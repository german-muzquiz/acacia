package com.gmr.acacia.sample;

import com.gmr.acacia.Service;

import rx.Observable;


@Service(value = PlayerImpl.class, androidService = PlayerService.class)
public interface Player
{

    void play(String aSongName);

    void pause();

    void stop();

    Observable<PlayerState> getPlayerState();

    TrackInfo getTrackInfo();

}
