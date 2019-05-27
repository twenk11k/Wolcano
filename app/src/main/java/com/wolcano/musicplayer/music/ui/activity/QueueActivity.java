package com.wolcano.musicplayer.music.ui.activity;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.kabouzeid.appthemehelper.common.ATHToolbarActivity;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.listener.GetDisposable;
import com.wolcano.musicplayer.music.mvp.listener.OnServiceListener;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.provider.RemotePlay;
import com.wolcano.musicplayer.music.ui.activity.base.BaseActivity;
import com.wolcano.musicplayer.music.ui.dialog.SleepTimerDialog;
import com.wolcano.musicplayer.music.utils.ToastUtils;
import com.wolcano.musicplayer.music.widgets.StatusBarView;
import com.wolcano.musicplayer.music.ui.adapter.QueueAdapter;
import com.wolcano.musicplayer.music.ui.dialog.Dialogs;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.Utils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class QueueActivity extends BaseActivity implements AdapterView.OnItemClickListener, OnServiceListener,GetDisposable {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview)
    FastScrollRecyclerView recyclerView;
    @BindView(R.id.statusBarCustom)
    StatusBarView statusBarView;
    @BindView(android.R.id.empty)
    TextView empty;

    private QueueAdapter adapter;
    private int primaryColor = -1;
    private Disposable queueDisposable;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.clear();
        getMenuInflater().inflate(R.menu.menu_sleeptimer, menu);
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(this, toolbar, menu, ATHToolbarActivity.getToolbarBackgroundColor(toolbar));

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onServiceConnection() {
        primaryColor = Utils.getPrimaryColor(this);
        setStatusbarColorAuto(statusBarView, primaryColor);

        if (Build.VERSION.SDK_INT < 21 && findViewById(R.id.statusBarCustom) != null) {
            findViewById(R.id.statusBarCustom).setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= 19) {
                int statusBarHeight = Utils.getStatHeight(this);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) findViewById(R.id.toolbar).getLayoutParams();
                layoutParams.setMargins(0, statusBarHeight, 0, 0);
                findViewById(R.id.toolbar).setLayoutParams(layoutParams);
            }
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setBackgroundColor(primaryColor);
        toolbar.setTitle(getResources().getString(R.string.nowplaying));
        Utils.setUpFastScrollRecyclerViewColor(recyclerView, Utils.getAccentColor(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));


        if (RemotePlay.get().getSongList().size() <= 30) {
            recyclerView.setThumbEnabled(false);
        } else {
            recyclerView.setThumbEnabled(true);
        }
        adapter = new QueueAdapter(this, RemotePlay.get().getSongList(), this);
        controlIfEmpty();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                controlIfEmpty();
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(RemotePlay.get().getRemotePlayPos(musicService));
        adapter.setIsPlaylist(true);
        RemotePlay.get().onListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_sleeptimer){
            new SleepTimerDialog().show(getSupportFragmentManager(), "SET_SLEEP_TIMER");
        }
        return super.onOptionsItemSelected(item);
    }
    private void controlIfEmpty() {
        if (empty != null) {
            empty.setText(R.string.no_queue);
            empty.setVisibility(adapter == null || adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onChangeSong(Song song) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPlayStart() {
    }

    @Override
    public void onPlayPause() {
    }

    @Override
    public void onProgressChange(int progress) {
    }

    @Override
    public void onBufferingUpdate(int percent) {
    }

    @Override
    protected void onDestroy() {
        RemotePlay.get().removeListener(this);

        if (queueDisposable != null && !queueDisposable.isDisposed()) {
            queueDisposable.dispose();
        }

        super.onDestroy();
    }

    @Override
    public void handlePlaylistDialog(Song song) {
        queueDisposable = Observable.fromCallable(() -> SongUtils.scanPlaylist(QueueActivity.this)).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(playlists -> Dialogs.addPlaylistDialog(QueueActivity.this, song, playlists));
    }
}
