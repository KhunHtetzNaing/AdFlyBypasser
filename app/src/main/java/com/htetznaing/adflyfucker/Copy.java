package com.htetznaing.adflyfucker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.evgenii.jsevaluator.JsEvaluator;
import com.evgenii.jsevaluator.interfaces.JsCallback;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by HtetzNaing on 1/19/2018.
 */

public class Copy extends Service {
    private NotificationManager notificationManager;
    ClipboardManager clipboardManager;
    JsEvaluator jsEvaluator;
    String fuckLINK = "Error";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        runInBackground(intent);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        jsEvaluator = new JsEvaluator(this);
        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                start(clipboardManager.getText().toString());
            }
        });
        return START_STICKY;
    }

    public void start(String text){
        if (text.isEmpty() || text.equals(null)){
        }else{
            if (text.startsWith("http") || text.startsWith("https") || text.startsWith("www")) {
                new work().execute(text);
            }
        }
    }

    class work extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... strings) {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            try {
                Request request = new Request.Builder()
                        .url(strings[0])
                        .build();
                response = client.newCall(request).execute();
                if (response.isSuccessful())
                    return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s!=null) {
                try {
                    s = s.substring(s.indexOf("var ysmm"), s.indexOf("var easyUrl"));
                    s = s.replace("ysmm", "val");
                    getUrl(s);
                }catch (Exception e){
                }

            }else{

            }
        }
    }

    public void getUrl(String s){
        String js = s +
                "var T3 = val,key,I = '',X = '';\n" +
                "for (var m = 0; m < T3.length; m++) {\n" +
                "if (m % 2 == 0) {\n" +
                "I += T3.charAt(m);\n" +
                "} else {\n" +
                "X = T3.charAt(m) + X;\n" +
                "}\n" +
                "}\n" +
                "\n" +
                "T3 = I + X;\n" +
                "var U = T3.split('');\n" +
                "for (var m = 0; m < U.length; m++) {\n" +
                "if (!isNaN(U[m])) {\n" +
                "for (var R = m + 1; R < U.length; R++) {\n" +
                "if (!isNaN(U[R])) {\n" +
                "var S = U[m]^U[R];\n" +
                "if (S < 10) {\n" +
                "U[m] = S;\n" +
                "}\n" +
                "m = R;\n" +
                "R = U.length;\n" +
                "}\n" +
                "}\n" +
                "}\n" +
                "}\n" +
                "T3 = U.join('');\n" +
                "T3 = window.atob(T3);\n" +
                "T3 = T3.substring(T3.length - (T3.length - 16));\n" +
                "T3 = T3.substring(0, T3.length - 16);\n" +
                "\n" +
                "key = T3;\n" +
                "return key;";
        Log.d("Result",js);
        jsEvaluator.evaluate("function hello(){"+js+"} hello();", new JsCallback() {
            @Override
            public void onResult(final String result) {
                fuckLINK = result;
                Log.d("FuckingResult",result);
                showActionButtonsNotification(result);
            }

            @Override
            public void onError(String s) {

            }
        });
    }

    private Intent getNotificationIntent() {
        Intent intent = new Intent(this, Done.class);
        intent.putExtra("url",fuckLINK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    private void showActionButtonsNotification(String link) {

        Notification notification = new NotificationCompat.Builder(this)
                .setContentIntent(PendingIntent.getActivity(this, 0, getNotificationIntent(), PendingIntent.FLAG_UPDATE_CURRENT))
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Extracted AdFly Link :)")
                .setContentText(link)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .build();

        notificationManager.notify(100, notification);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
    }

    public void runInBackground(Intent intent){
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,notificationIntent, 0);
            Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.icon);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("AdFlyBypasser")
                    .setTicker("AdFlyBypasser")
                    .setContentText("Running...")
                    .setSmallIcon(R.drawable.icon)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .build();
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            stopForeground(true);
            stopSelf();
        }
    }
}
