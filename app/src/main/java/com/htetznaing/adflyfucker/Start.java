package com.htetznaing.adflyfucker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by HtetzNaing on 1/19/2018.
 */

public class Start extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context,Copy.class));
    }
}
