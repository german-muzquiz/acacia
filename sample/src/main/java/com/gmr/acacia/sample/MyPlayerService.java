package com.gmr.acacia.sample;

import com.gmr.acacia.annotations.Service;

import rx.Observable;


@Service(MyPlayerServiceImpl.class)
public interface MyPlayerService
{

    void play(String aSongName);

    void pause();

    void stop();

    Observable<PlayerState> getPlayerState();

    TrackInfo getTrackInfo();

}
