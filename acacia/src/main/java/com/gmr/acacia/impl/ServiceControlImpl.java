package com.gmr.acacia.impl;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.gmr.acacia.AutoServiceException;
import com.gmr.acacia.Service;
import com.gmr.acacia.ServiceAware;
import com.gmr.acacia.ServiceControl;


/**
 * Handles communication between the user implementation and the android service instance.
 */
public class ServiceControlImpl implements ServiceControl, ServiceConnection
{
    private Service connectedService;
    private ServiceThread serviceThread;
    private final Object serviceImplementation;


    public ServiceControlImpl(Context aContext, Object aServiceImplementation, boolean useWorkerThread)
    {
        serviceImplementation = aServiceImplementation;

        if (serviceImplementation instanceof ServiceAware)
        {
            ((ServiceAware) serviceImplementation).setContext(aContext);
        }

        if (useWorkerThread)
        {
            serviceThread = new ServiceThread(aServiceImplementation.getClass().getSimpleName() + "Thread");
            serviceThread.start();
        }

        // Start service
        Intent intent = new Intent(aContext, Service.class);
        aContext.startService(intent);
        aContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder iBinder)
    {
        Log.d(Constants.LOG_TAG, Service.class.getName() + " connected");
        Service.LocalBinder myBinder = (Service.LocalBinder) iBinder;
        connectedService =  myBinder.getService();

        if (serviceImplementation instanceof ServiceAware)
        {
            ((ServiceAware) serviceImplementation).onServiceConnected(this);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name)
    {
        Log.d(Constants.LOG_TAG, Service.class.getName() + " disconnected");
        connectedService = null;

        if (serviceImplementation instanceof ServiceAware)
        {
            ((ServiceAware) serviceImplementation).onServiceDisconnected();
        }
    }

    public boolean isUsingWorkerThread()
    {
        return serviceThread != null;
    }

    public ServiceThread getServiceThread()
    {
        return serviceThread;
    }

    public Object getServiceImplementation()
    {
        return serviceImplementation;
    }

    public boolean reportInvocationException(Throwable anError)
    {
        if (serviceImplementation instanceof ServiceAware)
        {
            ((ServiceAware) serviceImplementation).onInvocationException(anError);
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void startForeground(int id, Notification notification)
    {
        if (connectedService == null)
        {
            throw new AutoServiceException("There's no connection to service at this moment.");
        }

        connectedService.startForeground(id, notification);
    }

    @Override
    public void stopForeground(boolean removeNotification)
    {
        if (connectedService == null)
        {
            throw new AutoServiceException("There's no connection to service at this moment.");
        }

        connectedService.stopForeground(removeNotification);
    }

    @Override
    public void shutdown()
    {
        if (connectedService != null)
        {
            connectedService.stopSelf();
        }

        if (serviceThread != null)
        {
            serviceThread.quit();
        }
    }
}
