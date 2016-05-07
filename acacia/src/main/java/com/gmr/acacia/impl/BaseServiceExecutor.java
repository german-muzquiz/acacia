package com.gmr.acacia.impl;


import android.util.Log;

import java.lang.reflect.Method;

abstract class BaseServiceExecutor implements ServiceExecutor {

    private ServiceThread serviceThread;
    private Object userService;

    /**
     * @param userService the user supplied implementation of the service.
     */
    BaseServiceExecutor(Object userService) {
        this.userService = userService;
    }

    @Override
    public void startServiceThread() {
        if (serviceThread != null)
        {
            return;
        }

        String threadName = userService.getClass().getSimpleName() + "Thread";
        serviceThread = new ServiceThread(threadName);
        serviceThread.start();
    }


    @Override
    public void stopServiceThread() {
        if (serviceThread != null)
        {
            serviceThread.quit();
        }
    }


    /**
     * Invoke user service implementation directly on the client calling thread.
     */
    Object handleDirectInvocation(final Method invokedMethod, final Object[] args) throws Throwable
    {
        return invokedMethod.invoke(userService, args);
    }


    /**
     * Post invocation of user service implementation to worker thread queue.
     */
    void handleVoidOnWorkerThread(final Method invokedMethod, final Object[] args) throws Throwable
    {
        serviceThread.getHandler().post(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    invokedMethod.invoke(userService, args);
                }
                catch (Throwable anError)
                {
                    Log.e(Constants.LOG_TAG, "Ignoring exception while executing " + invokedMethod
                            + " on the worker thread.", anError);
                }
            }
        });
    }


    public ServiceThread getServiceThread() {
        return serviceThread;
    }

    Object getUserService() {
        return userService;
    }
}
