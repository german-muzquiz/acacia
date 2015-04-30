package com.gmr.acacia.impl;

import android.content.Context;
import android.util.Log;

import com.gmr.acacia.AutoServiceException;
import com.gmr.acacia.annotations.Service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;


/**
 * Java dynamic proxy to a user defined implementation of a service.
 */
public class ServiceProxy implements InvocationHandler
{
    private ServiceControlImpl serviceControl;


    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Context aContext, Class<T> aServiceInterface)
    {
        try
        {
            // Validate annotations
            Service serviceAnnotation = aServiceInterface.getAnnotation(Service.class);
            if (serviceAnnotation == null)
            {
                throw new AutoServiceException("Interface " + aServiceInterface.getName() + " must be annotated " +
                        "with " + Service.class.getName() + " to create a service.");
            }

            boolean useWorkerThread = serviceAnnotation.useWorkerThread();
            Class<?> implementation = serviceAnnotation.value();

            // Create instance of user defined service implementation
            Object serviceImpl = implementation.newInstance();

            return (T) Proxy.newProxyInstance(aServiceInterface.getClassLoader(),
                    new Class[]{aServiceInterface},
                    new ServiceProxy(aContext, serviceImpl, useWorkerThread));
        }
        catch (InstantiationException | IllegalAccessException anEx)
        {
            throw new AutoServiceException("Cannot instantiate " + aServiceInterface.getAnnotation(
                    Service.class).value().getName() + ". Does it" +
                    " have an empty default constructor?", anEx);
        }
    }


    public ServiceProxy(Context aContext, Object aServiceImpl, boolean useWorkerThread)
    {
        serviceControl = new ServiceControlImpl(aContext, aServiceImpl, useWorkerThread);
    }


    @Override
    public Object invoke(Object proxy, Method invokedMethod, Object[] args)
    {
        try
        {
            if (!serviceControl.isUsingWorkerThread())
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
                return handleDirectInvocation(invokedMethod, args);
            }
        }
        catch (Throwable anError)
        {
            handleError(anError, invokedMethod, args);
            return null;
        }
    }


    /**
     * Invoke user service implementation directly on the client calling thread.
     */
    private Object handleDirectInvocation(final Method invokedMethod, final Object[] args) throws Throwable
    {
        return invokedMethod.invoke(serviceControl.getServiceImplementation(), args);
    }


    /**
     * Post invocation of user service implementation to worker thread queue.
     */
    private void handleVoidOnWorkerThread(final Method invokedMethod, final Object[] args) throws Throwable
    {
        serviceControl.getServiceThread().getHandler().post(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    invokedMethod.invoke(serviceControl.getServiceImplementation(), args);
                }
                catch (Throwable anError)
                {
                    handleError(anError, invokedMethod, args);
                }
            }
        });
    }


    /**
     * Subscribe observable invocation to the worker thread queue.
     */
    private Observable<?> handleObservableOnWorkerThread(final Method invokedMethod, final Object[] args) throws Throwable
    {
        Observable<?> result = (Observable<?>) invokedMethod.invoke(serviceControl.getServiceImplementation(), args);
        return result.subscribeOn(AndroidSchedulers.handlerThread(serviceControl.getServiceThread().getHandler()));
    }


    private void handleError(Throwable anError, Method invokedMethod, Object[] args)
    {
        if (!serviceControl.reportInvocationException(anError))
        {
            Log.e(Constants.LOG_TAG, "Exception executing method " + invokedMethod +
                    " with arguments " + Arrays.toString(args), anError);
        }
    }

}
