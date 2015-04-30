package com.gmr.acacia;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.gmr.acacia.impl.Constants;


/**
 * This is a generic implementation of Android {@link android.app.Service}.
 *
 * It is a bound service meant to be run locally in the same process as the application, so there's
 * no AIDL handling.
 */
public class Service extends android.app.Service
{
    /**
     * Class for clients to access. Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder
    {
        public Service getService()
        {
            return Service.this;
        }
    }

    // This is the object that receives interactions from clients.
    private final IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent)
    {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(Constants.LOG_TAG, this.getClass().getSimpleName() + ": onStartCommand: " + intent);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate()
    {
        Log.d(Constants.LOG_TAG, "Creating " + this.getClass().getSimpleName());
        super.onCreate();
    }

    @Override
    public void onDestroy()
    {
        Log.d(Constants.LOG_TAG, "Destroying " + this.getClass().getSimpleName());
        super.onDestroy();
    }

}
