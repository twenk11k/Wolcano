package com.wolcano.musicplayer.music.listener

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerViewScrollListener protected constructor() :
    RecyclerView.OnScrollListener() {
    private var scrolledDistance = 0
    private var loading = true
    var firstVisibleItem = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    private var controlsVisible = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val manager = recyclerView.layoutManager
        visibleItemCount = recyclerView.childCount
        if (manager is GridLayoutManager) {
            firstVisibleItem = manager.findFirstVisibleItemPosition()
            totalItemCount = manager.itemCount
        } else if (manager is LinearLayoutManager) {
            firstVisibleItem = manager.findFirstVisibleItemPosition()
            totalItemCount = manager.itemCount
        }
        val infiniteScrollingEnabled = true
        if (infiniteScrollingEnabled) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false
                    previousTotal = totalItemCount
                }
            }
            val visibleThreshold = 2
            if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
                onLoadMore()
                loading = true
            }
        }
        if (firstVisibleItem == 0) {
            if (!controlsVisible) {
                onScrollUp()
                controlsVisible = true
            }
            return
        }
        if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
            onScrollDown()
            controlsVisible = false
            scrolledDistance = 0
        } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
            onScrollUp()
            controlsVisible = true
            scrolledDistance = 0
        }
        if (controlsVisible && dy > 0 || !controlsVisible && dy < 0) {
            scrolledDistance += dy
        }
    }

    abstract fun onScrollUp()
    abstract fun onScrollDown()
    abstract fun onLoadMore()

    companion object {
        private const val HIDE_THRESHOLD = 20
        var previousTotal = 0
    }

}