package com.android.angrybird.util;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by hp pc on 13-05-2017.
 */

public class ApplicationLifeCycleHandler implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {
    private static final String TAG = ApplicationLifeCycleHandler.class.getSimpleName();
    private int numStarted = 0;
    private boolean isBackground;
    private Activity mActivity;

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        Log.d(TAG, "app went to foreground");
        /*if(isBackground && activity instanceof AuthenticationActivity)
        {
            isBackground = false;
            activity.finish();
        }*/
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d(TAG, "app went to foreground");
        isBackground = false;
        numStarted++;
        mActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {

        Log.d(TAG, "app went to foreground");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d(TAG, "app went to foreground");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d(TAG, "app went to foreground");
        mActivity = activity;
        numStarted--;
        /*if(numStarted == 0 && activity instanceof BaseActivity)
        {
            Intent intent = new Intent(activity, AuthenticationActivity.class);
            activity.startActivity(intent);
        }*/
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(TAG, "app went to foreground");
    }

    @Override
    public void onTrimMemory(int i) {
        Log.d(TAG, "app went to foreground");
        if(i == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            isBackground = true;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        Log.d(TAG, "app went to foreground");
    }

    @Override
    public void onLowMemory() {
        Log.d(TAG, "app went to foreground");
    }
}
