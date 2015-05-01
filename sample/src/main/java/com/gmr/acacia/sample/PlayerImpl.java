package com.gmr.acacia.sample;

import android.app.Notification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gmr.acacia.ServiceAware;

import rx.Observable;
import rx.Subscriber;
import rx.observables.ConnectableObservable;


public class PlayerImpl implements Player, ServiceAware<PlayerService>
{
    private SubscribablePlayerState subscribablePlayerState;
    private ConnectableObservable<PlayerState> observablePlayerState;
    private String playingSong;
    private PlayerService playerService;


    public PlayerImpl()
    {
        subscribablePlayerState = new SubscribablePlayerState();
        observablePlayerState = Observable.create(subscribablePlayerState).replay(1);
        observablePlayerState.connect();
        subscribablePlayerState.onPlayerStateChanged(PlayerState.stopped);
    }

    @Override
    public void play(String aSongName)
    {
        Log.d("PlayerService", "[" + Thread.currentThread().getName() + "] Playing " + aSongName);
        playingSong = aSongName;
        startForeground();
        subscribablePlayerState.onPlayerStateChanged(PlayerState.playing);
    }

    @Override
    public void pause()
    {
        Log.d("PlayerService", "[" + Thread.currentThread().getName() + "] Pausing player");
        stopForeground();
        subscribablePlayerState.onPlayerStateChanged(PlayerState.paused);
    }

    @Override
    public void stop()
    {
        Log.d("PlayerService", "[" + Thread.currentThread().getName() + "] Stopping player");
        playingSong = null;
        stopForeground();
        subscribablePlayerState.onPlayerStateChanged(PlayerState.stopped);
    }

    @Override
    public Observable<PlayerState> getPlayerState()
    {
        return observablePlayerState;
    }

    @Override
    public TrackInfo getTrackInfo()
    {
        if (playingSong != null)
        {
            return new TrackInfo("some artist", 3600, playingSong);
        }
        else
        {
            return null;
        }
    }

    private void startForeground()
    {
        Notification notification = new NotificationCompat.Builder(playerService)
                .setContentTitle(playerService.getString(R.string.app_name))
                .setContentText(playerService.getString(R.string.playing))
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        playerService.startForeground(1, notification);
    }

    private void stopForeground()
    {
        playerService.stopForeground(true);
    }

    @Override
    public void setAndroidService(PlayerService androidService)
    {
        playerService = androidService;
    }


    private static class SubscribablePlayerState implements Observable.OnSubscribe<PlayerState>
    {
        private Subscriber<? super PlayerState> subscriber;

        @Override
        public void call(Subscriber<? super PlayerState> subscriber)
        {
            Log.d("PlayerService", "[" + Thread.currentThread().getName() + "] Subscribing " + subscriber);
            this.subscriber = subscriber;
        }

        public void onPlayerStateChanged(PlayerState aNewState)
        {
            if (this.subscriber != null)
            {
                Log.d("PlayerService", "[" + Thread.currentThread().getName() + "] Notifying new player state " + aNewState);
                this.subscriber.onNext(aNewState);
            }
        }
    }

}
