package com.aggapple.manbogi;

import com.aggapple.manbogi.base.BaseActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class Intro extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        checkPermission();
    }

    private static final int REQUEST_CODE_LOCATION = 0;

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
        } else {
            goMain();
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // success!
                goMain();
            } else {
                // Permission was denied or request was cancelled
                //TODO::퍼미션을 얻지 못했을 경우 다음 화면에서 문제가 발생하지 않도록 주의 요구!!
                goMain();
            }
        }
    }

    private void goMain() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                long mDelayTime = 1000;

                try {
                    Thread.sleep(mDelayTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                startActivity(new Intent(Intro.this, MainActivity.class));
                finish();
                return null;
            }

        }.execute(null, null, null);

    }
}
