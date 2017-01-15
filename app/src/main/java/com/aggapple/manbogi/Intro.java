package com.aggapple.manbogi;

import com.aggapple.manbogi.base.BaseActivity;
import com.aggapple.manbogi.utils.BaseP;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class Intro extends BaseActivity {

    interface EXTRA {
        enum PERMISSION {
            ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, ACCESS_WIFI_STATE, CHANGE_WIFI_STATE;

            boolean[] confirm = {true, true, true, true};

            boolean isConfirm() {
                return this.confirm[this.ordinal()];
            }

            void setConfirm(boolean confirm) {
                this.confirm[this.ordinal()] = confirm;
            }
        }
    }

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
            goMain(new Intent(Intro.this, MainActivity.class));
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        Intent intent = new Intent();
        intent.setClass(Intro.this, MainActivity.class);

        if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // success!
                BaseP.c().set(EXTRA.PERMISSION.ACCESS_FINE_LOCATION.name(), true);
            } else {
                // Permission was denied or request was cancelled
                BaseP.c().set(EXTRA.PERMISSION.ACCESS_FINE_LOCATION.name(), false);
            }
        }

        goMain(intent);
    }

    private void goMain(final Intent intent) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                long mDelayTime = 1000;

                try {
                    Thread.sleep(mDelayTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                startActivity(intent);
                finish();
                return null;
            }

        }.execute(null, null, null);

    }
}
