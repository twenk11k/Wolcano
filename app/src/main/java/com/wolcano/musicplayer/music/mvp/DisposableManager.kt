package com.wolcano.musicplayer.music.mvp

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

object DisposableManager {

    private var compositeDisposable: CompositeDisposable? = null

    private fun getCompositeDisposable(): CompositeDisposable? {
        if (compositeDisposable == null || compositeDisposable!!.isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
        return compositeDisposable
    }

    fun add(disposable: Disposable) {
        getCompositeDisposable()?.add(disposable)
    }

    fun dispose() {
        getCompositeDisposable()?.dispose()
    }

}