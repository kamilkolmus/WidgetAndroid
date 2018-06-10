package com.i7xaphe.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sheredpreferences;
        sheredpreferences = context.getSharedPreferences(MainActivity.sharePref, Context.MODE_PRIVATE);
        if (sheredpreferences.getBoolean("startUp", MySettings.startUp)) {
            context.startService(new Intent(context, Widget.class));
        }
    }

}
