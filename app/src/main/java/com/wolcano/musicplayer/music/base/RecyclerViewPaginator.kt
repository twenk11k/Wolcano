package com.wolcano.musicplayer.music.base

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewPaginator(
    recyclerView: RecyclerView,
    private val isLoading: () -> Boolean,
    private val loadMore: () -> Unit,
) : RecyclerView.OnScrollListener() {

    init {
        recyclerView.addOnScrollListener(this)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        recyclerView.layoutManager?.let {
            if (dy > 0) { //check for scroll down
                val visibleItemCount = it.childCount
                val totalItemCount = it.itemCount
                if (it is LinearLayoutManager) {
                    val pastVisibleItems = it.findFirstVisibleItemPosition();
                    if (!isLoading()) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount && totalItemCount % 50 == 0) {
                            loadMore()
                        }
                    }
                }
            }
        }
    }

}
