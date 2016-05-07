package com.gmr.acacia.impl;

import java.lang.reflect.Method;


/**
 * ServiceExecutor that cannot handle Rx Observables.
 */
class DefaultServiceExecutor extends BaseServiceExecutor {

    /**
     * @param userService the user supplied implementation of the service.
     */
    DefaultServiceExecutor(Object userService) {
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
        else
        {
            // method has a return type other than Observable, it cannot be executed on the worker thread
            return handleDirectInvocation(invokedMethod, args);
        }
    }
}
