package com.gmr.acacia.impl;

import android.os.Looper;
import rx.Observable;
import rx.Scheduler;

import java.lang.reflect.Method;


/**
 * ServiceExecutor that can handle Rx Observables.
 */
public class RxServiceExecutor extends BaseServiceExecutor {

    private static Method ANDROID_SCHEDULERS_FROM;
    static
    {
        try
        {
            Class<?> ANDROID_SCHEDULERS_CLASS = Class.forName("rx.android.schedulers.AndroidSchedulers");
            ANDROID_SCHEDULERS_FROM = ANDROID_SCHEDULERS_CLASS.getMethod("from", Looper.class);
        }
        catch (Exception anEx)
        {
            throw new RuntimeException("Exception initializing Rx classes through reflection", anEx);
        }
    }

    /**
     * @param userService the user supplied implementation of the service.
     */
    public RxServiceExecutor(Object userService) {
        super(userService);
    }

    @Override
    public Object invoke(Method invokedMethod, Object[] args) throws Throwable {
        if (getServiceThread() == null)
        {
            return handleDirectInvocation(invokedMethod, args);
        }
        else if (invokedMethod.getReturnType().equals(Void.TYPE))
        {
            handleVoidOnWorkerThread(invokedMethod, args);
            return null;
        }
        else if (invokedMethod.getReturnType().equals(Observable.class))
        {
            return handleObservableOnWorkerThread(invokedMethod, args);
        }
        else
        {
            // method has a return type other than Observable, it cannot be executed on the worker thread
            return handleDirectInvocation(invokedMethod, args);
        }
    }


    /**
     * Subscribe observable invocation to the worker thread queue.
     */
    private Observable<?> handleObservableOnWorkerThread(final Method invokedMethod, final Object[] args) throws Throwable
    {
        Observable<?> result = (Observable<?>) invokedMethod.invoke(getUserService(), args);
        if (result != null)
        {
            Scheduler scheduler = (Scheduler) ANDROID_SCHEDULERS_FROM.invoke(null, getServiceThread().getHandler().getLooper());
            return result.subscribeOn(scheduler);
        }
        else
        {
            return null;
        }
    }

}
