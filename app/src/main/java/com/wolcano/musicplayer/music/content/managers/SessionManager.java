package com.wolcano.musicplayer.music.content.managers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.GeneralCache;
import com.wolcano.musicplayer.music.provider.MusicService;
import com.wolcano.musicplayer.music.provider.RemotePlay;

public class SessionManager {

    private MusicService service;
    private MediaSessionCompat mediaSessionCompat;

    public static SessionManager get() {
        return SingletonHolder.singletonInstance;
    }

    private static class SingletonHolder {
        private static SessionManager singletonInstance = new SessionManager();
    }

    private SessionManager() {
    }

    public void setSessionMng(MusicService musicService) {
        this.service = musicService;
        initMedia();
    }


    public void updateSessionPlaybackState() {
        int state = (RemotePlay.get().isPlaying() || RemotePlay.get().isPreparing()) ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
        long MEDIA_SESSION_ACTIONS = PlaybackStateCompat.ACTION_PLAY
                | PlaybackStateCompat.ACTION_SEEK_TO
                | PlaybackStateCompat.ACTION_PLAY_PAUSE
                | PlaybackStateCompat.ACTION_PAUSE
                | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                | PlaybackStateCompat.ACTION_STOP;
        mediaSessionCompat.setPlaybackState(
                new PlaybackStateCompat.Builder()
                        .setActions(MEDIA_SESSION_ACTIONS)
                        .setState(state, RemotePlay.get().getSoundPos(), 1)
                        .build());
    }
    private void initMedia() {
        mediaSessionCompat = new MediaSessionCompat(service, "SessionManager");
        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSessionCompat.setCallback(sessionCallback);
        mediaSessionCompat.setActive(true);
    }

    public MediaSessionCompat getMediaSessionCompat() {
        return mediaSessionCompat;
    }

    private MediaSessionCompat.Callback sessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            RemotePlay.get().buttonClick(service);
        }

        @Override
        public void onPause() {
            RemotePlay.get().buttonClick(service);
        }

        @Override
        public void onSkipToNext() {
            RemotePlay.get().next(service,false);
        }

        @Override
        public void onSkipToPrevious() {
            RemotePlay.get().prev(service);
        }

        @Override
        public void onStop() {
            RemotePlay.get().stopRemotePlay(service);
        }

        @Override
        public void onSeekTo(long pos) {
            RemotePlay.get().seekTo((int) pos);
        }
    };
    public void updateSessionMetaData(Song song) {
        if (song == null) {
            mediaSessionCompat.setMetadata(null);
            return;
        }
        String contentURI = "content://media/external/audio/media/" + song.getSongId() + "/albumart";
        Picasso.get().load(contentURI).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                update(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                update(null);
            }
            private void update(Bitmap bitmap){
                if(bitmap==null){
                    bitmap = BitmapFactory.decodeResource(service.getResources(), R.drawable.album_art);
                }
                MediaMetadataCompat.Builder metaData = new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getTitle())
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getArtist())
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.getAlbum())
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, song.getArtist())
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.getDura())
                        .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    metaData.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, GeneralCache.get().getArrayList().size());
                }

                mediaSessionCompat.setMetadata(metaData.build());
            }
        });

    }


}