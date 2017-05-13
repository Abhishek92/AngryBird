package com.android.angrybird.database;

import android.content.Context;

import com.android.angrybird.util.FileUtils;

import org.greenrobot.greendao.database.Database;

/**
 * Created by hp pc on 05-05-2017.
 */

public enum DBManager {
    INSTANCE;
    private DaoSession daoSession;
    public void initDatabase(Context context)
    {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "angrybird-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
        if(FileUtils.checkIfDbExist())
        {
            FileUtils.copyDatabaseToInternalStorage(context);
        }

    }

    public DaoSession getDaoSession()
    {
        return daoSession;
    }
}
