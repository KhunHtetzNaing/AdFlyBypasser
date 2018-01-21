package com.htetznaing.adflyfucker;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.evgenii.jsevaluator.JsEvaluator;
import com.evgenii.jsevaluator.interfaces.JsCallback;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    JsEvaluator jsEvaluator;
    EditText editText;
    Button button;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    AdView banner;
    AdRequest adRequest;
    InterstitialAd interstitialAd;
    Switch what;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jsEvaluator = new JsEvaluator(this);

        adRequest = new AdRequest.Builder().build();
        banner = findViewById(R.id.adView);
        banner.loadAd(adRequest);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-1325188641119577/7051769374");
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                loadAD();
            }

            @Override
            public void onAdOpened() {
                loadAD();
            }
        });

        sharedPreferences = getSharedPreferences("Fuck",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        boolean wh = sharedPreferences.getBoolean("what",true);
        what = findViewById(R.id.what);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Extracting...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        editText = findViewById(R.id.edText);
        button = findViewById(R.id.btn);
        button.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null
                && intent.getAction() != null
                && intent.getData() != null
                && intent.getAction().equals(Intent.ACTION_VIEW)) {
            start(getIntent().getDataString());
        }


        if (wh==true){
            Intent startIntent = new Intent(MainActivity.this, Copy.class);
            startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            startService(startIntent);
        }

        what.setChecked(wh);
        what.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                showAD();
                if (b==true){
                    editor.putBoolean("what",b);
                    editor.commit();
                    editor.apply();
                    Intent startIntent = new Intent(MainActivity.this, Copy.class);
                    startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                    startService(startIntent);
                }

                if (b==false){
                    editor.putBoolean("what",b);
                    editor.commit();
                    editor.apply();
                    Intent stopIntent = new Intent(MainActivity.this, Copy.class);
                    stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
                    startService(stopIntent);
                }
            }
        });
    }

    public void loadAD(){
        if (!interstitialAd.isLoaded()){
            interstitialAd.loadAd(adRequest);
        }
    }

    public void showAD(){
        if (interstitialAd.isLoaded()){
            interstitialAd.show();
        }else{
            interstitialAd.loadAd(adRequest);
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn:
                String text = editText.getText().toString();
                start(text);
                break;
        }
    }

    public void start(String text){
        if (text.isEmpty() || text.equals(null)){
            Toast.makeText(this, "Please enter your link :)", Toast.LENGTH_SHORT).show();
        }else{
            new work().execute(text);
        }
    }

    class work extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(strings[0])
                    .build();

            try {
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
                    Log.d("Result", s);
                    getUrl(s);
                }catch (Exception e){
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Can't extract this url :(", Toast.LENGTH_LONG).show();
                }

            }else{
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Can't extract this url :(", Toast.LENGTH_LONG).show();
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
                Log.d("Result",result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Completed");
                        builder.setMessage(result);
                        builder.setPositiveButton("Copy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ClipboardManager copy = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                copy.setText(result);
                                if (copy.hasText()){
                                    Toast.makeText(MainActivity.this, "Copied", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        builder.setNegativeButton("Open", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        final AlertDialog dialog = builder.create();
                        dialog.show();
                        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent= new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(result));
                                startActivity(Intent.createChooser(intent,"Choose browser!"));
                                dialog.show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Can't extract this url :(", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ClipboardManager copy = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        switch (item.getItemId()){
            case R.id.paste:
                if (copy.hasText()){
                    editText.setText(copy.getText());
                }else{
                    Toast.makeText(this, "No copied text!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.clear:
                editText.setText("");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
