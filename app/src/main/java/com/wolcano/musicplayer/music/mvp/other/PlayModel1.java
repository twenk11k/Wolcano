package com.wolcano.musicplayer.music.mvp.other;

import android.app.Activity;

import com.wolcano.musicplayer.music.mvp.models.Model1;
import com.wolcano.musicplayer.music.mvp.models.Song;

import java.util.ArrayList;
import java.util.List;


public abstract class PlayModel1 extends PlayModel0 {

    private List<Model1> model1List;

    public PlayModel1(Activity activity, List<Model1> model1List) {
        super(activity);
        this.model1List = model1List;
    }
    @Override
    protected void setModel1() {

        songList = new ArrayList<>();
        for(int i=0; i<model1List.size(); i++){
            String artist = model1List.get(i).getArtistName();
            String title = model1List.get(i).getTitle();
            song = new Song();
            song.setTip(Song.Tip.MODEL1);
            song.setTitle(title);
            song.setArtist(artist);
            song.setPath(model1List.get(i).getPath());
            song.setDura(model1List.get(i).getDuration()*1000);
            songList.add(song);
            if(i==(model1List.size()-1)){
                onTaskDone(songList);
            }
        }
    }
}
