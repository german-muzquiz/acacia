package com.gmr.acacia;


/**
 * Base, empty implementation of {@link com.gmr.acacia.ServiceAware}.
 *
 * Useful to prevent breaking client code if the interface changes.
 */
public class BaseServiceAware implements ServiceAware
{
    @Override
    public void setAndroidService(AcaciaService androidService)
    {
        // empty
    }
}
