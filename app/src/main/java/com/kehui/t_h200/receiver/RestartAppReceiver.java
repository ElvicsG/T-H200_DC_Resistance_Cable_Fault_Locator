package com.kehui.t_h200.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kehui.t_h200.activity.MainActivity;

/**
 * Created by jwj on 2018/4/9.
 * app重启广播
 */

public class RestartAppReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentSplash = new Intent(context, MainActivity.class);
        intentSplash.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentSplash);
        MainActivity.instance.finish();
        System.exit(0);
    }
}
