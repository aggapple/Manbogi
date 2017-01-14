package com.aggapple.manbogi;

import com.aggapple.manbogi.MainActivity.STATE.PAGE;
import com.aggapple.manbogi.base.BaseActivity;
import com.aggapple.manbogi.service.MiniModeService;
import com.aggapple.manbogi.service.WalkSensorService;
import com.aggapple.manbogi.utils.CheckerHelper;
import com.aggapple.manbogi.utils.IME;
import com.aggapple.manbogi.views.BaseTabFragmentPagerAdapter;
import com.aggapple.manbogi.views.CheckerHelperLinearLayout;
import com.nhn.android.maps.NMapContext;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends BaseActivity implements Observer {

    public interface STATE {
        enum PAGE {
            MONITOR, RECORD
        }
    }

    public PAGE mCurrentPage = PAGE.MONITOR;

    private ViewPager mPager;
    private ChargeTabFragmentPagerAdapter mAdapter;
    private CheckerHelperLinearLayout mTab;

    private long mBackPressedTime = 0;
    // private ServiceConnection mCounnection;

    private boolean mIsMiniMode = false;

    private boolean mIsStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initUI();
    }

    private void initUI() {
        mTab = (CheckerHelperLinearLayout) findViewById(R.id.tab);
        mPager = (ViewPager) super.findViewById(R.id.pager);

        mAdapter = new ChargeTabFragmentPagerAdapter(getSupportFragmentManager(), mTab, mPager);
        final MainMonitorFragment monitor = (MainMonitorFragment) Fragment.instantiate(this, MainMonitorFragment.class.getName(), new Bundle());
        final MainRecordFragment record = (MainRecordFragment) Fragment.instantiate(this, MainRecordFragment.class.getName(), new Bundle());
        mAdapter.add(monitor);
        mAdapter.add(record);
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(onPageChanged);

        MainMonitorObserver.getInstance().addObserver(this);

        // mCounnection = new ServiceConnection() {
        // @Override
        // public void onServiceDisconnected(ComponentName name) {
        // }
        //
        // @Override
        // public void onServiceConnected(ComponentName name, IBinder service) {
        // }
        // };
    }

    public class ChargeTabFragmentPagerAdapter extends BaseTabFragmentPagerAdapter {

        public ChargeTabFragmentPagerAdapter(FragmentManager fm, CheckerHelper ch, ViewPager pager) {
            super(fm, ch, pager);
        }

        public ChargeTabFragmentPagerAdapter(FragmentManager fm, CheckerHelperLinearLayout ch, ViewPager pager) {
            super(fm, ch, pager);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            Fragment fr = getItem(position);
            InputMethodManager inputManager = (InputMethodManager) fr.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(pager.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    private OnPageChangeListener onPageChanged = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            IME.hideKeyboard(MainActivity.this);

            mTab.getCheckerHelper().onItemClicked(position);
            mCurrentPage = PAGE.values()[position];

            switch (mCurrentPage) {
                case MONITOR:
                    ((MainMonitorFragment) mAdapter.getItem(PAGE.MONITOR.ordinal())).updateUI();
                    break;
                case RECORD:
                    ((MainRecordFragment) mAdapter.getItem(PAGE.RECORD.ordinal())).updateUI();
                    break;
            }
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() - mBackPressedTime < 3000) {
            finish();

            return;
        }
        Toast.makeText(this, "뒤로가기를 한번 더 누르면 종료 됩니다.", Toast.LENGTH_SHORT).show();
        mBackPressedTime = System.currentTimeMillis();
    }

    public void setStart(boolean state){
        mIsStart = state;
    }

    public boolean isStart(){
        return mIsStart;
    }

    public void startMiniService() {
        mIsMiniMode = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mIsMiniMode) {
            Intent intent = new Intent();
            intent.setClass(this, MiniModeService.class);
            stopService(intent);
            // if (mCounnection != null)
            // unbindService(mCounnection);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mIsMiniMode) {
            Intent intent = new Intent();
            intent.setClass(this, MiniModeService.class);
            startService(intent);
            // bindService(intent, mCounnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void stopMiniService() {
        mIsMiniMode = false;
        if (isServiceRunningCheck("com.aggapple.manbogi.service.MiniModeService")) {
            Intent intent = new Intent();
            intent.setClass(this, MiniModeService.class);
            stopService(intent);
            // if (mCounnection != null)
            // unbindService(mCounnection);
        }
    }

    public void startWalkService() {
        if (!isServiceRunningCheck("com.aggapple.manbogi.service.WalkSensorService")) {
            Intent intent = new Intent();
            intent.setClass(this, WalkSensorService.class);
            startService(intent);
        }
    }

    public void stopWalkService() {
        if (isServiceRunningCheck("com.aggapple.manbogi.service.WalkSensorService")) {
            Intent intent = new Intent();
            intent.setClass(this, WalkSensorService.class);
            stopService(intent);
        }
    }

    @Override
    protected void onDestroy() {
        stopMiniService();
        stopWalkService();
        MainMonitorObserver.getInstance().deleteObserver(this);
        super.onDestroy();
    }

    public boolean isServiceRunningCheck(String serviceName) {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof MainMonitorObserver) {
            if (data != null) {
                ((MainMonitorFragment) mAdapter.getItem(PAGE.MONITOR.ordinal())).updateUI(data);
            }
        }
    }

}
