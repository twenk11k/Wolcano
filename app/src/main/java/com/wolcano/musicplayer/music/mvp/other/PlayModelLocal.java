package com.wolcano.musicplayer.music.mvp.other;

import android.app.Activity;

import com.wolcano.musicplayer.music.mvp.models.SongOnline;
import com.wolcano.musicplayer.music.mvp.models.Song;

import java.util.ArrayList;
import java.util.List;


public abstract class PlayModelLocal extends PlayModelOnline {

    private List<SongOnline> songOnlineList;

    public PlayModelLocal(Activity activity, List<SongOnline> songOnlineList) {
        super(activity);
        this.songOnlineList = songOnlineList;
    }
    @Override
    protected void setPlayModel() {

        songList = new ArrayList<>();
        for(int i = 0; i< songOnlineList.size(); i++){
            String artist = songOnlineList.get(i).getArtistName();
            String title = songOnlineList.get(i).getTitle();
            song = new Song();
            song.setType(Song.Tip.MODEL1);
            song.setTitle(title);
            song.setArtist(artist);
            song.setPath(songOnlineList.get(i).getPath());
            song.setDuration(songOnlineList.get(i).getDuration()*1000);
            songList.add(song);
            if(i==(songOnlineList.size()-1)){
                onTaskDone(songList);
            }
        }
    }
}
