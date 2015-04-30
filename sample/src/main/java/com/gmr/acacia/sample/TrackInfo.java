package com.gmr.acacia.sample;

/**
 * Created by german on 28/04/15.
 */
public class TrackInfo
{
    private String artist;
    private int durationMillis;
    private String songName;

    public TrackInfo(String artist, int durationMillis, String songName)
    {
        this.artist = artist;
        this.durationMillis = durationMillis;
        this.songName = songName;
    }

    public String getArtist()
    {
        return artist;
    }

    public int getDurationMillis()
    {
        return durationMillis;
    }

    public String getSongName()
    {
        return songName;
    }

    @Override
    public String toString()
    {
        return "TrackInfo{" +
                "artist='" + artist + '\'' +
                ", durationMillis=" + durationMillis +
                ", songName='" + songName + '\'' +
                '}';
    }
}
