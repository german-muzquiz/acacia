package com.gmr.acacia.impl;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;


/**
 * Creates a concrete PendingInvocation scanning the classpath to search for rx classes.
 */
public class PendingInvocationFactory
{

    public static PendingInvocation newInstance(Method invokedMethod, Object[] args)
    {
        try
        {
            Class.forName("rx.Observable");

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

}
