package com.wolcano.musicplayer.music.ui.adapter;

import android.Manifest;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.models.SongOnline;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.provider.RemotePlay;
import com.wolcano.musicplayer.music.mvp.other.PlayModelLocal;
import com.wolcano.musicplayer.music.utils.Perms;
import com.wolcano.musicplayer.music.ui.dialog.Dialogs;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.ToastUtils;
import com.wolcano.musicplayer.music.utils.Utils;
import java.util.ArrayList;
import java.util.List;


public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<SongOnline> arraylist;
    private AppCompatActivity context;
    private boolean showLoader;
    private int dCount = 0;

    public MainAdapter(AppCompatActivity context, ArrayList<SongOnline> arraylist) {
        if (arraylist == null) {
            this.arraylist = new ArrayList<>();
        } else {
            this.arraylist = arraylist;

        }
        this.context = context;

    }


    private void setOnPopupMenuListener(ViewHolder viewHolder, final int pos) {
        viewHolder.more.setOnClickListener(v -> {
            try {
                ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(v.getContext(), R.style.PopupMenuToolbar);

                PopupMenu popup = new PopupMenu(contextThemeWrapper, v);
                popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup_song_copyto_clipboard:
                                Dialogs.copyDialog(context, arraylist.get(pos));
                                break;
                            case R.id.action_down:
                                Perms.with(context)
                                        .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        .result(new Perms.PermInterface() {
                                            @Override
                                            public void onPermGranted() {
                                                dCount++;
                                                SongUtils.downPerform(context,arraylist.get(pos));
                                            }

                                            @Override
                                            public void onPermUnapproved() {
                                                ToastUtils.show(context.getApplicationContext(),R.string.no_perm_save_file);
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
        final int size = arraylist.size();
        arraylist.clear();
        notifyItemRangeRemoved(0, size);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View v;
        switch (viewType) {
            case Type.TYPE_SONG:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
                viewHolder = new ViewHolder(v);
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
                    loaderViewHolder.mProgressBar.setVisibility(View.VISIBLE);
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    loaderViewHolder.mProgressBar.setIndeterminateTintList(ColorStateList.valueOf(Utils.getAccentColor(context)));
                } else {
                    loaderViewHolder.mProgressBar.setVisibility(View.GONE);
                }
            }

        } else {

            ViewHolder viewHolder = (ViewHolder) holder;

            SongOnline localItem;

            localItem = arraylist.get(position);

            viewHolder.line1.setText(localItem.getTitle());
            String dura = "";
            try {
                dura = Utils.getDura(localItem.getDuration());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            viewHolder.line2.setText((String.valueOf(arraylist.get(position).getDuration()).isEmpty() ? "" : String.valueOf(dura)) + "  |  " + localItem.getArtistName());

            setOnPopupMenuListener(viewHolder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (arraylist.size() == (position + 1) && arraylist.size() >= 50) {
            return Type.TYPE_FOOTER;
        } else {
            return Type.TYPE_SONG;

        }

    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }


    private static class Type {

        private static final int TYPE_SONG = 1;
        private static final int TYPE_FOOTER = 2;

    }

    public void showLoading(boolean status) {
        this.showLoader = status;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView line1;
        TextView line2;
        ImageView albumArt,more;

        public ViewHolder(View view) {
            super(view);
            this.line1 = view.findViewById(R.id.line1);
            this.line2 = view.findViewById(R.id.line2);
            this.albumArt = view.findViewById(R.id.albumArt);
            this.more = view.findViewById(R.id.more);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Utils.hideKeyboard(context);
            final Handler handler = new Handler();
            handler.postDelayed(() -> new PlayModelLocal(context, arraylist) {
                @Override
                public void onPrepare() {
                }

                @Override
                public void onTaskDone(List<Song> alist) {
                    if(getAdapterPosition()!=-1)
                    RemotePlay.get().playAdd(context,alist, alist.get(getAdapterPosition()));
                }

                @Override
                public void onTaskFail(Exception e) {
                    ToastUtils.show(context.getApplicationContext(),R.string.cannot_play);
                }
            }.onTask(), 100);

        }
    }
}
