package com.htetznaing.droidxbg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by HtetzNaing on 9/13/2017.
 */

public class OnBootReceiver extends BroadcastReceiver {
    SharedPreferences sharedPreferences;
    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPreferences = context.getSharedPreferences("myFile",MODE_PRIVATE);
        int check = sharedPreferences.getInt("check",0);
        if (check!=0) {
            Intent serviceIntent = new Intent(context, Floating.class);
            context.startService(serviceIntent);
        }
    }
}
