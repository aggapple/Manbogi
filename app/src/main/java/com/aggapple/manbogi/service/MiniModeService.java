package com.aggapple.manbogi.service;

import com.aggapple.manbogi.R;
import com.aggapple.manbogi.base.BaseApplication;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

@SuppressLint({ "ClickableViewAccessibility", "InflateParams" })
public class MiniModeService extends Service implements OnClickListener, OnTouchListener {

	private LinearLayout mMiniFloatingLayout;

	private WindowManager.LayoutParams mParams;
	private WindowManager mWindowManager;
	

	// private ProgressBar otp_Time_Progress;
	//
	// private TextView otp_TimeOut, opt_pw_num1, opt_pw_num2, opt_pw_num3,
	// opt_pw_num4, opt_pw_num5, opt_pw_num6;

	// private Button btn_cancel, otp_num_auto_input;
	private Boolean isFloatingEnd = false;
	private float prevX, prevY;


	// private String otpnum;

	// protected int count, count_Text;

	@Override
	public void onCreate() {
		super.onCreate();
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMiniFloatingLayout = (LinearLayout) inflater.inflate(R.layout.mini_mode_service, null);
		initTest();
		mMiniFloatingLayout.setOnTouchListener(this);
		mMiniFloatingLayout.setOnClickListener(onClick);

		mParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
				// WindowManager.LayoutParams.TYPE_PHONE,
				// WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				WindowManager.LayoutParams.TYPE_TOAST, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSLUCENT);

		mParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mWindowManager.addView(mMiniFloatingLayout, mParams);
		// setOTPNumber();
		timeProgress();
	}

	private void viewControllTask() {
		
	}

	@Override
	public IBinder onBind(Intent intent) {
//		otpnum = intent.getStringExtra("OTPNUMBER");
		// android.Log.e("서비스 전달된 otp : "+ intent.getStringExtra("OTPNUMBER"));
//		setOTPNumber();
		return null;
	}

	private void setOTPNumber() {
//		if (otpnum != null && otpnum.length() == 6) {
//			for (int i = 0; otpnum.length() > i; i++) {
//				Character s = new Character(otpnum.charAt(i));
//				String num = s.toString();
//				switch (i) {
//				case 0:
//					opt_pw_num1.setText(num);
//					break;
//				case 1:
//					opt_pw_num2.setText(num);
//					break;
//				case 2:
//					opt_pw_num3.setText(num);
//					break;
//				case 3:
//					opt_pw_num4.setText(num);
//					break;
//				case 4:
//					opt_pw_num5.setText(num);
//					break;
//				case 5:
//					opt_pw_num6.setText(num);
//				}
//
//			}
//		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mWindowManager != null && mMiniFloatingLayout != null) {
			try {
				mWindowManager.removeView(mMiniFloatingLayout);
			} catch (Exception e) {
				// 예외처리
				Log.e("", "에러나쪄염~!!!!!!");

			}
		}

	}

	private void initTest() {

//		otp_Time_Progress = (ProgressBar) mMiniFloatingLayout.findViewById(R.id.otp_progressBar);
//
//		otp_TimeOut = (TextView) mMiniFloatingLayout.findViewById(R.id.otp_progressBar_Time);
//
//		otp_num_auto_input = (Button) mMiniFloatingLayout.findViewById(R.id.otp_num_auto_input);
//		otp_num_auto_input.setOnClickListener(this);
//		otp_num_auto_input.setVisibility(View.GONE);
//
//		btn_cancel = (Button) mMiniFloatingLayout.findViewById(R.id.btn_cancel);
//		btn_cancel.setOnClickListener(this);
//
//		opt_pw_num1 = (TextView) mMiniFloatingLayout.findViewById(R.id.opt_pw_num1);
//		opt_pw_num2 = (TextView) mMiniFloatingLayout.findViewById(R.id.opt_pw_num2);
//		opt_pw_num3 = (TextView) mMiniFloatingLayout.findViewById(R.id.opt_pw_num3);
//		opt_pw_num4 = (TextView) mMiniFloatingLayout.findViewById(R.id.opt_pw_num4);
//		opt_pw_num5 = (TextView) mMiniFloatingLayout.findViewById(R.id.opt_pw_num5);
//		opt_pw_num6 = (TextView) mMiniFloatingLayout.findViewById(R.id.opt_pw_num6);

	}

	private void timeProgress() {
//		count_Text = 60;
//		count = 0;
//
//		new CountDownTimer(61000, 100) { // 기준시간, 실행시간
//
//			@Override
//			public void onTick(long millisUntilFinished) {
//				count++;
//				otp_Time_Progress.setProgress(count);
//				if (!(count_Text <= 0) && (count % 10 == 0)) { // 시간 카운터
//
//					count_Text--;
//				}
//				otp_TimeOut.setText("" + count_Text);
//				if (isFloatingEnd) {
//					this.cancel();
//					// onFinish();
//				}
//			}
//
//			@Override
//			public void onFinish() {
//				otp_Time_Progress.setProgress(600);
//				otp_TimeOut.setText("" + 0);
//				onDestroy();
//				stopService(new Intent(MiniModeService.this, MiniModeService.class));
//			}
//		}.start();

	}

	@Override
	public void onClick(View v) {
//		count_Text = 60;
//
//		count = 0;
//		int btnId = v.getId();
//		switch (btnId) {
//		case R.id.otp_num_auto_input:
//			new CountDownTimer(61000, 100) { // 기준시간, 실행시간
//
//				@Override
//				public void onTick(long millisUntilFinished) {
//					otp_Time_Progress.setProgress(count);
//					if (!(count_Text <= 0) && (count % 10 == 0)) { // 시간 카운터
//
//						count_Text--;
//					}
//					otp_TimeOut.setText("" + count_Text);
//					count++;
//				}
//
//				@Override
//				public void onFinish() {
//
//				}
//			}.start();
//
//			break;
//
//		case R.id.btn_cancel:
//			isFloatingEnd = true;
//			stopService(new Intent(this, MiniModeService.class));
//			// onDestroy();
//			break;
//
//		}
	}

	private OnClickListener onClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {

			if(getApplication() instanceof BaseApplication){
				BaseApplication.AppStatus status = ((BaseApplication)getApplication()).getAppStatus();
				String statusVal = "NONE";
				switch (status) {
				case FOREGROUND:
					statusVal = "FOREGROUND";
					break;
				case RETURNED_TO_FOREGROUND:
					statusVal = "RETURNED_TO_FOREGROUND";
					break;
				case BACKGROUND:
					statusVal = "BACKGROUND";
					break;
				default:
					break;
				}
					Toast.makeText(getApplicationContext(), statusVal, Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// 플로팅UI 위치 이동
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			prevX = event.getRawX();
			prevY = event.getRawY();

			break;

		case MotionEvent.ACTION_MOVE:
			float rawX = event.getRawX();
			float rawY = event.getRawY();

			float x = rawX - prevX;
			float y = rawY - prevY;

			setCoordinateUpdate(x, y);

			prevX = rawX;
			prevY = rawY;

		}

		return false;
	}

	private void setCoordinateUpdate(float x, float y) {
		if (mParams != null) {
			mParams.x += (int) x;
			mParams.y += (int) y;

			mWindowManager.updateViewLayout(mMiniFloatingLayout, mParams);
		}

	}

}
