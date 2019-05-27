package com.wolcano.musicplayer.music.provider.notifications;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.text.TextUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.provider.RemotePlay;
import com.wolcano.musicplayer.music.provider.MusicService;
import static com.wolcano.musicplayer.music.constants.Constants.ACTION_QUIT;
import static com.wolcano.musicplayer.music.constants.Constants.ACTION_REWIND;
import static com.wolcano.musicplayer.music.constants.Constants.ACTION_SKIP;
import static com.wolcano.musicplayer.music.constants.Constants.ACTION_TOGGLE_PAUSE;


public class NotificationLatestImpl extends Notification {
    public NotificationLatestImpl get() {
        return NotificationLatestImpl.SingletonHolder.singletonInstance;
    }

    private static class SingletonHolder {
        private static NotificationLatestImpl singletonInstance = new NotificationLatestImpl();
    }

    @Override
    public synchronized void update(Song song) {
        if (song == null) {
            return;
        }
        stopped = false;

        final String albumName = song.getAlbum();
        final String artistName = song.getArtist();
        final String text = TextUtils.isEmpty(albumName)
                ? artistName : artistName + " - " + albumName;

        if (service != null) {
            Intent action = service.getPackageManager()
                    .getLaunchIntentForPackage(service.getPackageName())
                    .setPackage(null)
                    .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            final PendingIntent clickIntent = PendingIntent.getActivity(service, 0, action, 0);

            final ComponentName serviceName = new ComponentName(service, MusicService.class);
            Intent intent = new Intent(ACTION_QUIT);
            intent.setComponent(serviceName);
            final PendingIntent deleteIntent = PendingIntent.getService(service, 0, intent, 0);

            service.runOnUiThread(new Runnable() {
                @Override
                public void run() {

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
                            boolean isPlaying = RemotePlay.get().isPlaying();
                            int playButtonResId = isPlaying
                                    ? R.drawable.baseline_pause_black_36 : R.drawable.baseline_play_arrow_black_36;

                            NotificationCompat.Action playPauseAction = new NotificationCompat.Action(playButtonResId,
                                    service.getString(R.string.action_play_pause),
                                    retrievePlaybackAction(ACTION_TOGGLE_PAUSE));
                            NotificationCompat.Action previousAction = new NotificationCompat.Action(R.drawable.baseline_skip_previous_black_36,
                                    service.getString(R.string.action_previous),
                                    retrievePlaybackAction(ACTION_REWIND));
                            NotificationCompat.Action nextAction = new NotificationCompat.Action(R.drawable.baseline_skip_next_black_36,
                                    service.getString(R.string.action_next),
                                    retrievePlaybackAction(ACTION_SKIP));
                            if(bitmap==null){
                                bitmap = BitmapFactory.decodeResource(service.getResources(), R.drawable.album_art);
                            }
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(service, NOTIFICATION_CHANNEL_ID)
                                    .setSmallIcon(R.drawable.baseline_music_note_white_24)
                                    .setLargeIcon(bitmap)
                                    .setContentIntent(clickIntent)
                                    .setDeleteIntent(deleteIntent)
                                    .setContentTitle(song.getTitle())
                                    .setContentText(text)
                                    .setOngoing(isPlaying)
                                    .setShowWhen(false)
                                    .addAction(previousAction)
                                    .addAction(playPauseAction)
                                    .addAction(nextAction);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(service.getSessionManager().getMediaSessionCompat().getSessionToken()).setShowActionsInCompactView(0, 1, 2))
                                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                            }


                            if (stopped)
                                return;
                            updateNotifyModeAndPostNotification(builder.build());
                        }
                    });

                }
            });
        }


    }

    private PendingIntent retrievePlaybackAction(final String action) {
        final ComponentName serviceName = new ComponentName(service, MusicService.class);
        Intent intent = new Intent(action);
        intent.setComponent(serviceName);
        return PendingIntent.getService(service, 0, intent, 0);
    }
}