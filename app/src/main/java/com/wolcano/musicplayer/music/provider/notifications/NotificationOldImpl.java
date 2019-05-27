package com.wolcano.musicplayer.music.provider.notifications;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import androidx.core.app.NotificationCompat;
import android.widget.RemoteViews;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.provider.RemotePlay;
import com.wolcano.musicplayer.music.provider.MusicService;
import static com.wolcano.musicplayer.music.Constants.ACTION_QUIT;
import static com.wolcano.musicplayer.music.Constants.ACTION_REWIND;
import static com.wolcano.musicplayer.music.Constants.ACTION_SKIP;
import static com.wolcano.musicplayer.music.Constants.ACTION_STOP;
import static com.wolcano.musicplayer.music.Constants.ACTION_TOGGLE_PAUSE;

public class NotificationOldImpl extends Notification {

    public NotificationOldImpl get() {
        return NotificationOldImpl.SingletonHolder.singletonInstance;
    }

    private static class SingletonHolder {
        private static NotificationOldImpl singletonInstance = new NotificationOldImpl();
    }

    private int getPrevIcon() {
        return R.drawable.baseline_skip_previous_black_36;

    }


    private int getPlayIcon(boolean isPlaying) {
        if (isPlaying) {
            return R.drawable.baseline_pause_black_36;
        } else {
            return R.drawable.baseline_play_arrow_black_36;
        }
    }

    private int getNextIcon() {
        return R.drawable.baseline_skip_next_black_36;
    }
    private int getCloseIcon() {
        return R.drawable.baseline_close_black_36;
    }

    @Override
    public void update(Song song) {

        stopped = false;


        final boolean isPlaying = RemotePlay.get().isPlaying();

        final RemoteViews notificationLayout = new RemoteViews(service.getPackageName(), R.layout.thenotification_old_2);
        final RemoteViews notificationLayoutExpanded = new RemoteViews(service.getPackageName(), R.layout.thenotification_old_2_2);

        notificationLayout.setTextViewText(R.id.line1, song.getTitle());
        notificationLayout.setTextViewText(R.id.line2, song.getArtist());
        notificationLayoutExpanded.setTextViewText(R.id.line1, song.getTitle());
        notificationLayoutExpanded.setTextViewText(R.id.line2, song.getArtist());
        notificationLayoutExpanded.setTextViewText(R.id.line3, song.getAlbum());


        linkButtons(notificationLayout);
        linkButtons(notificationLayoutExpanded);

        Intent action = service.getPackageManager()
                .getLaunchIntentForPackage(service.getPackageName())
                .setPackage(null)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        final PendingIntent clickIntent = PendingIntent.getActivity(service, 0, action, 0);
        final PendingIntent deleteIntent = buildPendingIntent(service, ACTION_QUIT, null);

        final android.app.Notification notification = new NotificationCompat.Builder(service, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(clickIntent)
                .setDeleteIntent(deleteIntent)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContent(notificationLayout)
                .setOngoing(isPlaying)
                .build();


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
                    public void update(Bitmap bitmap){
                        if(bitmap==null){
                            bitmap = BitmapFactory.decodeResource(service.getResources(), R.drawable.album_art);
                        }
                        notificationLayout.setImageViewBitmap(R.id.albumArt, bitmap);
                        notificationLayoutExpanded.setImageViewBitmap(R.id.albumArt, bitmap);
                        setNotificationContent();

                        if (stopped)
                            return;
                        updateNotifyModeAndPostNotification(notification);
                    }
                });

            }


            private void setNotificationContent() {


                notificationLayout.setImageViewResource(R.id.prev, getPrevIcon());
                notificationLayout.setImageViewResource(R.id.next, getNextIcon());
                notificationLayout.setImageViewResource(R.id.close,getCloseIcon());
                notificationLayout.setImageViewResource(R.id.playpause, getPlayIcon(isPlaying));

                notificationLayoutExpanded.setImageViewResource(R.id.prev, getPrevIcon());
                notificationLayoutExpanded.setImageViewResource(R.id.next, getNextIcon());
                notificationLayoutExpanded.setImageViewResource(R.id.close,getCloseIcon());
                notificationLayoutExpanded.setImageViewResource(R.id.playpause, getPlayIcon(isPlaying));

            }
        });

    }

    private PendingIntent buildPendingIntent(Context context, final String action, final ComponentName serviceName) {
        Intent intent = new Intent(action);
        intent.setComponent(serviceName);
        return PendingIntent.getService(context, 0, intent, 0);
    }

    private void linkButtons(final RemoteViews notificationLayout) {
        PendingIntent pendingIntent;

        final ComponentName serviceName = new ComponentName(service, MusicService.class);

        // Previous track
        pendingIntent = buildPendingIntent(service, ACTION_REWIND, serviceName);
        notificationLayout.setOnClickPendingIntent(R.id.prev, pendingIntent);

        // Play and pause
        pendingIntent = buildPendingIntent(service, ACTION_TOGGLE_PAUSE, serviceName);
        notificationLayout.setOnClickPendingIntent(R.id.playpause, pendingIntent);

        // Next track
        pendingIntent = buildPendingIntent(service, ACTION_SKIP, serviceName);
        notificationLayout.setOnClickPendingIntent(R.id.next, pendingIntent);
        // close
        pendingIntent = buildPendingIntent(service, ACTION_STOP,serviceName);
        notificationLayout.setOnClickPendingIntent(R.id.close,pendingIntent);


    }
}

