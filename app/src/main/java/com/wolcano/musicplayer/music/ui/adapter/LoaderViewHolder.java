package com.wolcano.musicplayer.music.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.wolcano.musicplayer.music.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoaderViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    public LoaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
