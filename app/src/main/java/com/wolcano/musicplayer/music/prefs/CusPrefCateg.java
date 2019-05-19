package com.wolcano.musicplayer.music.prefs;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.TextView;

import com.wolcano.musicplayer.music.utils.Utils;


public class CusPrefCateg extends PreferenceCategory {

    @TargetApi(21)
    public CusPrefCateg(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context, attrs);
    }

    public CusPrefCateg(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs);
    }

    public CusPrefCateg(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs);
    }

    public CusPrefCateg(Context context) {
        super(context);
        this.init(context, (AttributeSet)null);
    }

    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        TextView mTitle = (TextView)holder.itemView;
        mTitle.setTextColor(Utils.getAccentColor(getContext()));
    }

    private void init(Context context, AttributeSet attrs) {
        this.setLayoutResource(com.kabouzeid.appthemehelper.R.layout.ate_preference_category);
    }
}
