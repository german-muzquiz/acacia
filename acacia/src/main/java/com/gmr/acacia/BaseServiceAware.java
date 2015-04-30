package com.gmr.acacia;

import android.content.Context;


/**
 * Base, empty implementation of {@link com.gmr.acacia.ServiceAware}.
 *
 * Useful to prevent breaking client code if the interface changes.
 */
public class BaseServiceAware implements ServiceAware
{
    @Override
    public void setContext(Context context)
    {
        /* nothing to do */
    }

    @Override
    public void onServiceConnected(ServiceControl serviceControl)
    {
        /* nothing to do */
    }

    @Override
    public void onServiceDisconnected()
    {
        /* nothing to do */
    }

    @Override
    public void onInvocationException(Throwable throwable)
    {
        /* nothing to do */
    }
}
