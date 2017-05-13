package com.android.angrybird.prefs;

import android.content.Context;
import android.content.SharedPreferences;



/**
 * Created by Deepesh Sharma on 11/10/2016.
 */
public final class PreferenceUtil
{



    private static String PREF_KEY = "PREF_KEY";
    private static String PREF_KEY_PIN_REG_COMPLETE = "PREF_KEY_PIN_REG_COMPLETE";
    private static SharedPreferences pref;
    private static PreferenceUtil prefrences = new PreferenceUtil();

    private PreferenceUtil()
    {
    }

    public static PreferenceUtil getInstance()
    {
        return prefrences;
    }

    public void initPrefs(Context context)
    {
        pref = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getWriteInstance()
    {
        return pref.edit();
    }

    private SharedPreferences getReadInstance()
    {
        return pref;
    }

    public void setPrefKeyPinRegComplete(boolean isRegComplete)
    {
        getWriteInstance().putBoolean(PREF_KEY_PIN_REG_COMPLETE, isRegComplete).commit();
    }

    public boolean isRegComplete()
    {
        return getReadInstance().getBoolean(PREF_KEY_PIN_REG_COMPLETE, false);
    }
}
