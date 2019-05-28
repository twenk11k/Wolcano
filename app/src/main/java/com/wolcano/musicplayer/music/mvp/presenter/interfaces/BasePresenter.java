package com.wolcano.musicplayer.music.mvp.presenter.interfaces;


import com.wolcano.musicplayer.music.mvp.view.BaseView;

public interface BasePresenter<T extends BaseView>{

    void attachView(T view);

    void subscribe();

    void unsubscribe();
}
