package com.wolcano.musicplayer.music.mvp.db;

import android.content.Context;

import org.greenrobot.greendao.database.Database;


public class DatabaseManager {

    private AppDao appDao;
    private static final String DATABASE_NAME = "database_wolcano";

    public static DatabaseManager get() {
        return SingletonHolder.singletonInstance;
    }

    private static class SingletonHolder {
        private static DatabaseManager singletonInstance = new DatabaseManager();
    }

    public void init(Context context) {
        GreenDaoMaster.DevOpenHelper helperDao = new GreenDaoMaster.DevOpenHelper(context, DATABASE_NAME);
        Database database = helperDao.getWritableDb();
        GreenDaoSession daoSession = new GreenDaoMaster(database).newSession();
        appDao = daoSession.getAppDao();
    }
    public AppDao getAppDao() {
        return appDao;
    }

    private DatabaseManager() {
    }

}
