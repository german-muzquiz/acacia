package com.gmr.acacia.impl;


import android.util.Log;

import com.gmr.acacia.AcaciaService;

import java.lang.reflect.Method;


public class DefaultPendingInvocation implements PendingInvocation
{
    private Method invokedMethod;
    private Object[] args;
    private AcaciaService service;


    public DefaultPendingInvocation(Method invokedMethod, Object[] args)
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
            Log.w(Constants.LOG_TAG, "Ignoring result of " + invokedMethod + ", as it was" +
                        " called when the service was not connected yet. Result: " + result);
        }
        catch (Throwable throwable)
        {
            Log.e(Constants.LOG_TAG, "Ignoring exception while executing " + invokedMethod
                    + ", as it was called when the service was not connected yet.", throwable);
        }
    }
}
