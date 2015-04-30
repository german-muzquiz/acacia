package com.gmr.acacia;

import android.content.Context;


/**
 * Reports interesting events to a service implementing this interface.
 */
public interface ServiceAware
{

    /**
     * @param context used to create and start the service.
     */
    void setContext(Context context);

    /**
     * Called when the service has been created, started and a connection to it established.
     *
     * @param serviceControl used to invoke operations on the connected service.
     */
    void onServiceConnected(ServiceControl serviceControl);

    /**
     * Called when the connection to the service has been lost.
     */
    void onServiceDisconnected();

    /**
     * Called if there's an exception invoking a method of the service interface.
     *
     * @param throwable the error.
     */
    void onInvocationException(Throwable throwable);

}
