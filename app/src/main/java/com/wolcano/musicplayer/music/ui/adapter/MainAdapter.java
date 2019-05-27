package com.wolcano.musicplayer.music.ui.adapter;

import android.Manifest;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.constants.Type;
import com.wolcano.musicplayer.music.databinding.ItemSongOnlineBinding;
import com.wolcano.musicplayer.music.mvp.models.SongOnline;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.provider.RemotePlay;
import com.wolcano.musicplayer.music.mvp.other.PlayModelLocal;
import com.wolcano.musicplayer.music.utils.PermissionUtils;
import com.wolcano.musicplayer.music.ui.dialog.Dialogs;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.ToastUtils;
import com.wolcano.musicplayer.music.utils.Utils;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<SongOnline> songOnlineList;
    private AppCompatActivity activity;
    private boolean showLoader;
    private int downloadCount = 0;

    public MainAdapter(AppCompatActivity activity, ArrayList<SongOnline> songOnlineList) {
        if (songOnlineList == null) {
            this.songOnlineList = new ArrayList<>();
        } else {
            this.songOnlineList = songOnlineList;

        }
        this.activity = activity;

    }


    private void setOnPopupMenuListener(ViewHolder viewHolder, final int pos) {
        viewHolder.binding.more.setOnClickListener(v -> {
            try {
                ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(v.getContext(), R.style.PopupMenuToolbar);

                PopupMenu popup = new PopupMenu(contextThemeWrapper, v);
                popup.getMenuInflater().inflate(R.menu.popup_menu_main, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup_song_copyto_clipboard:
                                Dialogs.copyDialog(activity, songOnlineList.get(pos));
                                break;
                            case R.id.action_down:
                                PermissionUtils.with(activity)
                                        .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        .result(new PermissionUtils.PermInterface() {
                                            @Override
                                            public void onPermGranted() {
                                                downloadCount++;
                                                SongUtils.downPerform(activity,songOnlineList.get(pos));
                                            }

                                            @Override
                                            public void onPermUnapproved() {
                                                ToastUtils.show(activity.getApplicationContext(),R.string.no_perm_save_file);
                                            }
                                        })
                                        .reqPerm();

                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void clear() {
        final int size = songOnlineList.size();
        songOnlineList.clear();
        notifyItemRangeRemoved(0, size);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View v;
        switch (viewType) {
            case Type.TYPE_SONG:
                ItemSongOnlineBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.item_song_online,parent,false);
                viewHolder = new ViewHolder(binding);
                break;
            case Type.TYPE_FOOTER:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.thefooter_view, parent, false);
                viewHolder = new LoaderViewHolder(v);

                break;

        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == Type.TYPE_FOOTER) {
            if (holder instanceof LoaderViewHolder) {
                LoaderViewHolder loaderViewHolder = (LoaderViewHolder) holder;
                if (showLoader) {
                    loaderViewHolder.progressBar.setVisibility(View.VISIBLE);
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    loaderViewHolder.progressBar.setIndeterminateTintList(ColorStateList.valueOf(Utils.getAccentColor(activity)));
                } else {
                    loaderViewHolder.progressBar.setVisibility(View.GONE);
                }
            }

        } else {

            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.binding.setSongOnline(songOnlineList.get(position));
            viewHolder.binding.executePendingBindings();

            SongOnline songOnline = viewHolder.binding.getSongOnline();

            viewHolder.binding.line1.setText(songOnline.getTitle());
            String duration = "";
            try {
                duration = Utils.getDuration(songOnline.getDuration());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            viewHolder.binding.line2.setText((String.valueOf(songOnlineList.get(position).getDuration()).isEmpty() ? "" : String.valueOf(duration)) + "  |  " + songOnline.getArtistName());

            setOnPopupMenuListener(viewHolder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (songOnlineList.size() == (position + 1) && songOnlineList.size() >= 50) {
            return Type.TYPE_FOOTER;
        } else {
            return Type.TYPE_SONG;

        }

    }

    @Override
    public int getItemCount() {
        return (null != songOnlineList ? songOnlineList.size() : 0);
    }



    public void showLoading(boolean status) {
        this.showLoader = status;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemSongOnlineBinding binding;

        public ViewHolder(ItemSongOnlineBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.getRoot().setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            Utils.hideKeyboard(activity);
            final Handler handler = new Handler();
            handler.postDelayed(() -> new PlayModelLocal(activity, songOnlineList) {
                @Override
                public void onPrepare() {
                }

                @Override
                public void onTaskDone(List<Song> alist) {
                    if(getAdapterPosition()!=-1)
                    RemotePlay.get().playAdd(activity,alist, alist.get(getAdapterPosition()));
                }

                @Override
                public void onTaskFail(Exception e) {
                    ToastUtils.show(activity.getApplicationContext(),R.string.cannot_play);
                }
            }.onTask(), 100);

        }
    }
    public class LoaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progress_bar)
        ProgressBar progressBar;

        public LoaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
