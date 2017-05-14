package com.android.angrybird;

import android.app.Application;

import com.android.angrybird.prefs.PreferenceUtil;
import com.android.angrybird.util.ApplicationLifeCycleHandler;
import com.android.angrybird.util.FileUtils;


/**
 * Created by hp pc on 04-05-2017.
 */

public class AngryBirdApp extends Application {

    private static AngryBirdApp appInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        PreferenceUtil.getInstance().initPrefs(getApplicationContext());
        ApplicationLifeCycleHandler handler = new ApplicationLifeCycleHandler();
        registerActivityLifecycleCallbacks(handler);
        registerComponentCallbacks(handler);
        FileUtils.createImageDir(getApplicationContext());


    }

    public static AngryBirdApp getAppInstance()
    {
        return appInstance;
    }
}
