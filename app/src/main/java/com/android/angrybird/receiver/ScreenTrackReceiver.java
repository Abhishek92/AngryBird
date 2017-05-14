package com.android.angrybird.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.angrybird.activity.AuthenticationActivity;

/**
 * Created by hp pc on 14-05-2017.
 */

public class ScreenTrackReceiver extends BroadcastReceiver {
    public static boolean isScreenOff;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON))
            isScreenOff = false;
        else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            isScreenOff = true;
            Intent it = new Intent(context, AuthenticationActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(it);
        }
    }
}
