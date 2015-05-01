package com.gmr.acacia.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.gmr.acacia.AcaciaException;
import com.gmr.acacia.AcaciaService;
import com.gmr.acacia.Service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.Queue;

import rx.Observable;
import rx.Subscriber;


/**
 * Java dynamic proxy to a user defined implementation of a service.
 */
public class ServiceProxy implements InvocationHandler, ServiceConnection
{
    private AcaciaService service;
    private boolean useWorkerThread;
    private Class<?> userImplClass;
    // keep track of service invocations made before the service connection is established
    private Queue<PendingInvocation> pendingInvocations;


    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Context aContext, Class<T> aServiceInterface)
    {
        // Validate annotations
        Service serviceAnnotation = aServiceInterface.getAnnotation(Service.class);
        if (serviceAnnotation == null)
        {
            throw new AcaciaException("Interface " + aServiceInterface.getName() + " must be annotated " +
                    "with " + Service.class.getName() + " to create a service.");
        }
        if (!aServiceInterface.isAssignableFrom(serviceAnnotation.value()))
        {
            throw new AcaciaException("Class " + serviceAnnotation.value().getName() + " must implement " +
                    aServiceInterface.getName());
        }

        return (T) Proxy.newProxyInstance(aServiceInterface.getClassLoader(),
                new Class[]{aServiceInterface},
                new ServiceProxy(aContext, aServiceInterface));
    }


    public ServiceProxy(Context aContext, Class<?> aServiceInterface)
    {
        this.pendingInvocations = new LinkedList<>();

        Service serviceAnnotation = aServiceInterface.getAnnotation(Service.class);
        this.useWorkerThread = serviceAnnotation.useWorkerThread();
        this.userImplClass = serviceAnnotation.value();
        Class<? extends AcaciaService> androidService = serviceAnnotation.androidService();

        // Start Android service
        Intent intent = new Intent(aContext, androidService);
        aContext.startService(intent);
        aContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder iBinder)
    {
        Log.d(Constants.LOG_TAG, "Service connected.");
        service = ((AcaciaService.LocalBinder) iBinder).getService();
        service.setUserImplClass(userImplClass);
        if (useWorkerThread)
        {
            service.startServiceThread();
        }

        // execute pending invocations
        PendingInvocation pendingInvocation;
        while ((pendingInvocation = pendingInvocations.poll()) != null)
        {
            pendingInvocation.setService(service);
            pendingInvocation.run();
        }
    }


    @Override
    public void onServiceDisconnected(ComponentName name)
    {
        Log.d(Constants.LOG_TAG, "Service disconnected.");
        service = null;
    }
    

    @Override
    public Object invoke(Object proxy, Method invokedMethod, Object[] args) throws Throwable
    {
        if (service == null)
        {
            return handleDisconnectedInvocation(invokedMethod, args);
        }
        else
        {
            return handleConnectedInvocation(invokedMethod, args);
        }
    }


    private Object handleDisconnectedInvocation(final Method invokedMethod, final Object[] args) throws Throwable
    {
        final PendingInvocation pendingInvocation = new PendingInvocation(invokedMethod, args);
        pendingInvocations.add(pendingInvocation);

        if (invokedMethod.getReturnType().equals(Void.TYPE))
        {
            return null;
        }
        else if (invokedMethod.getReturnType().equals(Observable.class))
        {
            return Observable.merge(Observable.create(pendingInvocation));
        }
        else
        {
            Log.w(Constants.LOG_TAG, "Failed to deliver result of " + invokedMethod + ", service is" +
                    " not connected yet.");
            return null;
        }
    }


    private Object handleConnectedInvocation(Method invokedMethod, Object[] args) throws Throwable
    {
        return service.invoke(invokedMethod, args);
    }


    private static class PendingInvocation implements Runnable, Observable.OnSubscribe<Observable<?>>
    {
        private Method invokedMethod;
        private Object[] args;
        private AcaciaService service;
        private Subscriber<? super Observable<?>> subscriber;


        private PendingInvocation(Method invokedMethod, Object[] args)
        {
            this.invokedMethod = invokedMethod;
            this.args = args;
        }

        public void setService(AcaciaService service)
        {
            this.service = service;
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

}
