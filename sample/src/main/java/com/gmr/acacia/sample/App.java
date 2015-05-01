package com.gmr.acacia.sample;

import android.app.Application;

import com.gmr.acacia.Acacia;


public class App extends Application
{
    private static Player player;

    @Override
    public void onCreate()
    {
        super.onCreate();
        player = Acacia.createService(getApplicationContext(), Player.class);
    }

    public static Player getPlayer()
    {
        return player;
    }
}
