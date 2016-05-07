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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;


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
    private Context context;
    private boolean isServiceStopped;


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
                new Class[]{aServiceInterface, ServiceControl.class},
                new ServiceProxy(aContext, aServiceInterface));
    }


    public ServiceProxy(Context aContext, Class<?> aServiceInterface)
    {
        this.pendingInvocations = new LinkedList<>();
        this.context = aContext;
        this.isServiceStopped = false;

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
        if (isServiceStopped)
        {
            throw new IllegalStateException("Service has been stopped, you must create a new instance.");
        }

        // Handle ServiceControl invocations
        if (Arrays.asList(ServiceControl.class.getDeclaredMethods()).contains(invokedMethod))
        {
            return handleServiceControlInvocation(invokedMethod);
        }
        else if (service == null)
        {
            return handleDisconnectedInvocation(invokedMethod, args);
        }
        else
        {
            return handleConnectedInvocation(invokedMethod, args);
        }
    }


    private Object handleServiceControlInvocation(Method invokedMethod)
    {
        if (invokedMethod.getName().equals("stop"))
        {
            isServiceStopped = true;

            if (service != null)
            {
                service.stop();
            }

            context.unbindService(this);
        }
        else
        {
            Log.w(Constants.LOG_TAG, "Unhandled ServiceControl method " + invokedMethod);
        }

        return null;
    }


    private Object handleDisconnectedInvocation(final Method invokedMethod, final Object[] args) throws Throwable
    {
        final PendingInvocation pendingInvocation = AcaciaFactory.newPendingInvocation(invokedMethod, args);
        pendingInvocations.add(pendingInvocation);
        return pendingInvocation.returnOnDisconnectedInvocation();
    }


    private Object handleConnectedInvocation(Method invokedMethod, Object[] args) throws Throwable
    {
        return service.invoke(invokedMethod, args);
    }

}
