package com.gmr.acacia.sample;

import android.app.Application;

import com.gmr.acacia.AutoService;


public class App extends Application
{
    private static MyPlayerService playerService;

    @Override
    public void onCreate()
    {
        super.onCreate();
        playerService = AutoService.createService(getApplicationContext(), MyPlayerService.class);
    }

    public static MyPlayerService getPlayerService()
    {
        return playerService;
    }
}
