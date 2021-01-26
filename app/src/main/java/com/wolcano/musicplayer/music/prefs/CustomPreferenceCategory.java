package com.wolcano.musicplayer.music.prefs;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;

import com.wolcano.musicplayer.music.utils.Utils;


public class CustomPreferenceCategory extends PreferenceCategory {

    @TargetApi(21)
    public CustomPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context, attrs);
    }

    public CustomPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs);
    }

    public CustomPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs);
    }

    public CustomPreferenceCategory(Context context) {
        super(context);
        this.init(context, (AttributeSet) null);
    }

    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        TextView titleTextView = (TextView) holder.itemView;
        titleTextView.setTextColor(Utils.getAccentColor(getContext()));
    }

    private void init(Context context, AttributeSet attrs) {
        this.setLayoutResource(com.kabouzeid.appthemehelper.R.layout.ate_preference_category);
    }
}
