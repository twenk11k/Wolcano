package com.wolcano.musicplayer.music.mvp.listener;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


public abstract class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {


    private int scrolledDistance = 0;
    private static final int HIDE_THRESHOLD = 20;

    public static int previousTotal = 0;
    private boolean loading = true;
    private int visThreshold = 2;
    int firstVisItem, visItemCount, totalItemCount;

    private boolean infiniteScrollingEnabled = true;

    private boolean controlsVisible = true;

    protected RecyclerViewScrollListener(){
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();

        visItemCount = recyclerView.getChildCount();
        if (manager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager)manager;
            firstVisItem = gridLayoutManager.findFirstVisibleItemPosition();
            totalItemCount = gridLayoutManager.getItemCount();
        } else if (manager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager)manager;
            firstVisItem = linearLayoutManager.findFirstVisibleItemPosition();
            totalItemCount = linearLayoutManager.getItemCount();
        }


        if (infiniteScrollingEnabled) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }

            if (!loading && (totalItemCount -  visItemCount <= firstVisItem + visThreshold)) {
                onLoadMore();
                loading = true;
            }
        }

        if (firstVisItem == 0) {
            if (!controlsVisible) {
                onScrollUp();
                controlsVisible = true;
            }

            return;
        }

        if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
            onScrollDown();
            controlsVisible = false;
            scrolledDistance = 0;
        } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
            onScrollUp();
            controlsVisible = true;
            scrolledDistance = 0;
        }

        if ((controlsVisible && dy>0) || (!controlsVisible && dy <0)) {
            scrolledDistance+=dy;
        }
    }

    public abstract void onScrollUp();
    public abstract void onScrollDown();
    public abstract void onLoadMore();

}

