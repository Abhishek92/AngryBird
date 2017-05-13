package com.android.angrybird;

import android.app.Application;

import com.android.angrybird.prefs.PreferenceUtil;


/**
 * Created by hp pc on 04-05-2017.
 */

public class AngryBirdApp extends Application {

    private boolean wasInBackground;
    private static AngryBirdApp appInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        PreferenceUtil.getInstance().initPrefs(getApplicationContext());
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        wasInBackground = true;
    }

    public boolean wasInBackground()
    {
        return wasInBackground;
    }

    public void setWasInBackground(boolean wasInBackground)
    {
        this.wasInBackground = wasInBackground;
    }

    public static AngryBirdApp getAppInstance()
    {
        return appInstance;
    }
}
