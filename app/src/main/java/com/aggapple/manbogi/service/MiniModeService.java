package com.aggapple.manbogi.service;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aggapple.manbogi.MainActivity;
import com.aggapple.manbogi.R;
import com.aggapple.manbogi.base.BaseApplication;

@SuppressLint({"ClickableViewAccessibility", "InflateParams"})
public class MiniModeService extends Service implements OnTouchListener {

    private LinearLayout mMiniFloatingLayout;

    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;

    private TextView mMiniWalk, mMiniDistance;

    public IBinder mBinder = new MiniModeBinder();

    public class MiniModeBinder extends Binder {
        public MiniModeService getService() {
            return MiniModeService.this;
        }
    }

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

        initUI();
    }

    private void initUI() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMiniFloatingLayout = (LinearLayout) inflater.inflate(R.layout.mini_mode_service, null);
        mMiniFloatingLayout.setOnTouchListener(this);
        mMiniFloatingLayout.setOnLongClickListener(onLongClick);

        mParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                // WindowManager.LayoutParams.TYPE_PHONE,
                // WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.TYPE_TOAST, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSLUCENT);

        mParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mMiniFloatingLayout, mParams);

        mMiniWalk = (TextView) mMiniFloatingLayout.findViewById(R.id.mini_walk);
        mMiniDistance = (TextView) mMiniFloatingLayout.findViewById(R.id.mini_distance);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
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
                e.printStackTrace();
            }
        }

    }

    public void setMiniWalk(long walk) {
        mMiniWalk.setText("" + walk);
    }

    public void setMiniDistance(String distance) {
        mMiniDistance.setText(distance);
    }

    private View.OnLongClickListener onLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            try {
                intent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
            return false;
        }
    };


    private OnClickListener onClick = new OnClickListener() {

        @Override
        public void onClick(View v) {

            if (getApplication() instanceof BaseApplication) {
                BaseApplication.AppStatus status = ((BaseApplication) getApplication()).getAppStatus();
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
