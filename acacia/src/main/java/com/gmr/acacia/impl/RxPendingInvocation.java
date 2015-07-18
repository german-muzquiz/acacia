package com.gmr.acacia.impl;


import android.util.Log;

import com.gmr.acacia.AcaciaService;

import java.lang.reflect.Method;

import rx.Observable;
import rx.Subscriber;


public class RxPendingInvocation implements PendingInvocation, Observable.OnSubscribe<Observable<?>>
{
    private Method invokedMethod;
    private Object[] args;
    private AcaciaService service;
    private Subscriber<? super Observable<?>> subscriber;


    public RxPendingInvocation(Method invokedMethod, Object[] args)
    {
        this.invokedMethod = invokedMethod;
        this.args = args;
    }

    @Override
    public void setService(AcaciaService service)
    {
        this.service = service;
    }

    @Override
    public Object returnOnDisconnectedInvocation()
    {
        if (invokedMethod.getReturnType().equals(Void.TYPE))
        {
            return null;
        }
        else if (invokedMethod.getReturnType().equals(Observable.class))
        {
            return Observable.merge(Observable.create(this));
        }
        else
        {
            Log.w(Constants.LOG_TAG, "Failed to deliver result of " + invokedMethod + ", service is" +
                    " not connected yet.");
            return null;
        }
    }

    @Override
    public void run()
    {
        try
        {
            Object result = this.service.invoke(invokedMethod, args);
            if (invokedMethod.getReturnType().equals(Observable.class))
            {
                Observable<?> resultObservable = (Observable<?>) result;
                if (subscriber != null)
                {
                    subscriber.onNext(resultObservable);
                    subscriber.onCompleted();
                }
            }
            else
            {
                Log.w(Constants.LOG_TAG, "Ignoring result of " + invokedMethod + ", as it was" +
                        " called when the service was not connected yet.");
            }
        }
        catch (Throwable throwable)
        {
            if (invokedMethod.getReturnType().equals(Observable.class))
            {
                if (subscriber != null)
                {
                    subscriber.onError(throwable);
                }
            }
            else
            {
                Log.e(Constants.LOG_TAG, "Ignoring exception while executing " + invokedMethod
                        + ", as it was called when the service was not connected yet.", throwable);
            }
        }
    }

    @Override
    public void call(Subscriber<? super Observable<?>> subscriber)
    {
        this.subscriber = subscriber;
    }

}
