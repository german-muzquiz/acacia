package com.gmr.acacia;

/**
 * Provides the Android {@link android.app.Service} implementation.
 */
public interface ServiceAware<T extends AcaciaService>
{
     void setAndroidService(T androidService);
}
