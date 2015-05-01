package com.gmr.acacia.impl;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;


/**
 * Worker thread executing methods on the service implementation.
 */
public class ServiceThread extends Thread
{
    private Handler handler;

    public ServiceThread(String name)
    {
        super(name);
    }

    @Override
    public void run()
    {
        // preparing a looper on current thread
        // the current thread is being detected implicitly
        Looper.prepare();

        while(!Thread.interrupted())
        {
            try
            {
                Log.d(Constants.LOG_TAG, "Starting thread " + this.getName());

                // now, the handler will automatically bind to the
                // Looper that is attached to the current thread
                // You don't need to specify the Looper explicitly
                handler = new Handler();

                // Notify handler ready
                synchronized (ServiceThread.this)
                {
                    ServiceThread.this.notifyAll();
                }

                // After the following line the thread will start
                // running the message loop and will not normally
                // exit the loop unless a problem happens or you
                // quit() the looper
                Looper.loop();

                Log.d(Constants.LOG_TAG, "Thread " + this.getName() + " exiting gracefully");
                return;
            }
            catch (Throwable t)
            {
                Log.d(Constants.LOG_TAG, "Thread " + this.getName() + " got an error, resuming", t);
            }
        }

        Log.d(Constants.LOG_TAG, "Thread " + this.getName() + " interrupted, exiting gracefully");
    }

    public Handler getHandler()
    {
        // Wait until the thread is started and the handler is ready
        if (handler == null)
        {
            synchronized (ServiceThread.this)
            {
                while(handler == null)
                {
                    try
                    {
                        ServiceThread.this.wait();
                    }
                    catch (InterruptedException e)
                    {
                        // finished waiting
                    }
                }
            }

        }

        return handler;
    }

    public void quit()
    {
        getHandler().post(new Runnable()
        {
            @Override
            public void run()
            {
                Looper.myLooper().quit();
            }
        });
    }
}
