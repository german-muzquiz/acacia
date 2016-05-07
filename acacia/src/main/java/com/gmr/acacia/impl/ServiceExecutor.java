package com.gmr.acacia.impl;

import java.lang.reflect.Method;


/**
 * Encapsulates execution of service methods when the service connection has been established.
 */
public interface ServiceExecutor
{

    /**
     * @param invokedMethod method invoked on the Service interface.
     * @param args args passed to method invocation.
     * @return result from method invocation.
     * @throws Throwable
     */
    Object invoke(Method invokedMethod, Object[] args) throws Throwable;

    /**
     * Starts a worker thread for the service if it doesn't have one yet.
     */
    void startServiceThread();

    /**
     * Stop the worker thread, if any.
     */
    void stopServiceThread();

    ServiceThread getServiceThread();

}
