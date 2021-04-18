package com.wolcano.musicplayer.music.binding

import android.view.animation.AnimationUtils
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import com.skydoves.whatif.whatIfNotNullOrEmpty
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.base.RecyclerViewPaginator
import com.wolcano.musicplayer.music.model.SongOnline
import com.wolcano.musicplayer.music.ui.adapter.OnlineAdapter
import com.wolcano.musicplayer.music.ui.fragment.online.OnlineViewModel

object RecyclerViewBinding {

    @JvmStatic
    @BindingAdapter("adapter")
    fun bindAdapter(view: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        view.adapter = adapter.apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }

    @JvmStatic
    @BindingAdapter("paginationOnlineList")
    fun paginationOnlineList(view: RecyclerView, viewModel: OnlineViewModel) {
        RecyclerViewPaginator(
            recyclerView = view,
            isLoading = { viewModel.isLoading.get() },
            loadMore = { viewModel.fetchNextOnlineList() },
        )
    }

    @JvmStatic
    @BindingAdapter("adapterOnlineList", "shouldClearList")
    fun bindAdapterOnlineList(
        view: FastScrollRecyclerView,
        onlineList: List<SongOnline>?,
        shouldClearList: Boolean
    ) {
        onlineList.whatIfNotNullOrEmpty { itemList ->
            view.adapter.whatIfNotNullAs<OnlineAdapter> { adapter ->
                if (shouldClearList) {
                    if (adapter.itemCount >= 15) {
                        view.setThumbEnabled(true)
                    } else {
                        view.setThumbEnabled(false)
                    }
                    adapter.clearAndSetOnlineList(itemList)
                } else {
                    adapter.setOnlineList(itemList)
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("runLayoutAnimation")
    fun bindLayoutAnimation(view: FastScrollRecyclerView, page: Int) {
        if (page == 1) {
            val context = view.context
            val controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
            view.layoutAnimation = controller
            view.adapter?.notifyDataSetChanged()
            view.scheduleLayoutAnimation()
        }
    }

}