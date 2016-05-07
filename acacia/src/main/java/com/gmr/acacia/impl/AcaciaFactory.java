package com.gmr.acacia.impl;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;


/**
 * Creates concrete classes scanning the classpath to search for rx classes.
 */
public class AcaciaFactory
{

    static PendingInvocation newPendingInvocation(Method invokedMethod, Object[] args)
    {
        try
        {
            Class.forName("rx.android.schedulers.AndroidSchedulers");

            // rx was found in classpath
            Class<?> myClass = Class.forName("com.gmr.acacia.impl.RxPendingInvocation");
            Constructor<?> myConstructor = myClass.getConstructor(Method.class, Object[].class);

            return (PendingInvocation) myConstructor.newInstance(invokedMethod, args);
        }
        catch (ClassNotFoundException anEx)
        {
            // rx not found in classpath
        }
        catch (Exception anEx)
        {
            Log.e(Constants.LOG_TAG, "Unable to instantiate com.gmr.acacia.impl.RxPendingInvocation, " +
                    "rx support will not be available.", anEx);
        }

        return new DefaultPendingInvocation(invokedMethod, args);
    }


    public static ServiceExecutor newServiceExecutor(Object userService)
    {
        try
        {
            Class.forName("rx.android.schedulers.AndroidSchedulers");

            // rx was found in classpath
            Class<?> myClass = Class.forName("com.gmr.acacia.impl.RxServiceExecutor");
            Constructor<?> myConstructor = myClass.getConstructor(Object.class);

            return (ServiceExecutor) myConstructor.newInstance(userService);
        }
        catch (ClassNotFoundException anEx)
        {
            // rx not found in classpath
        }
        catch (Exception anEx)
        {
            Log.e(Constants.LOG_TAG, "Unable to instantiate com.gmr.acacia.impl.RxServiceExecutor, " +
                    "rx support will not be available.", anEx);
        }

        return new DefaultServiceExecutor(userService);
    }

}
