package com.aggapple.manbogi;

import com.aggapple.manbogi.base.BaseActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class Intro extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro);

		goMain();
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
