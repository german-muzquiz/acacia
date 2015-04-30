package com.gmr.acacia;

import android.app.Notification;


/**
 * Invokes operations on the service.
 */
public interface ServiceControl
{

    /**
     * Proxy method to {@link android.app.Service#startForeground(int, android.app.Notification)}.
     */
    void startForeground(int id, Notification notification);

    /**
     * Proxy method to {@link android.app.Service#stopForeground(boolean)}.
     */
    void stopForeground(boolean removeNotification);

    /**
     * Used when you no longer need this service instance. After you call this method the service
     * is stopped and the worker thread finished.
     */
    void shutdown();

}
