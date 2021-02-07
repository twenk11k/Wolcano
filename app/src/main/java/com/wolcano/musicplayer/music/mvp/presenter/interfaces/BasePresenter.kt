package com.wolcano.musicplayer.music.mvp.presenter.interfaces

import com.wolcano.musicplayer.music.mvp.view.BaseView

interface BasePresenter<T : BaseView?> {
    fun attachView(view: T)
    fun subscribe()
    fun unsubscribe()
}