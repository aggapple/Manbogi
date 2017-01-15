package com.aggapple.manbogi;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aggapple.manbogi.MainActivity.STATE.PAGE;
import com.aggapple.manbogi.base.AppConstants;
import com.aggapple.manbogi.base.BaseActivity;
import com.aggapple.manbogi.data.ManbogiData;
import com.aggapple.manbogi.provider.DBHelper;
import com.aggapple.manbogi.provider.DBProvider;
import com.aggapple.manbogi.service.MiniModeService;
import com.aggapple.manbogi.service.WalkSensorService;
import com.aggapple.manbogi.utils.AppManager;
import com.aggapple.manbogi.utils.BaseP;
import com.aggapple.manbogi.utils.CheckerHelper;
import com.aggapple.manbogi.utils.IME;
import com.aggapple.manbogi.utils.SocialUtils;
import com.aggapple.manbogi.views.BaseTabFragmentPagerAdapter;
import com.aggapple.manbogi.views.CheckerHelperLinearLayout;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends BaseActivity implements Observer {

    public interface STATE {
        enum PAGE {
            MONITOR, RECORD
        }

        enum MODE {
            STANDARD_WALK, STANDARD_DISTANCE
        }
    }

    public PAGE mCurrentPage = PAGE.MONITOR;
    public STATE.MODE mCurrentMode = STATE.MODE.STANDARD_WALK;

    private ViewPager mPager;
    private ChargeTabFragmentPagerAdapter mAdapter;
    private CheckerHelperLinearLayout mTab;

    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerView;

    private TextView mStepValue, mSensorVelue;
    private SeekBar mStepController, mSensorController;
    private Button mSetDefault;

    private long mBackPressedTime = 0;
    private ServiceConnection mConnection;

    private boolean mIsMiniMode = false;

    private boolean mIsStart = false;

    private MiniModeService mMiniModeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initUI();
        initSettings();
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        mDrawerLayout.isDrawerOpen(mDrawerView);
        return super.onPrepareOptionsMenu(menu);
    }

    private void initUI() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(onDrawerStateChanged);
        mDrawerView = (LinearLayout) findViewById(R.id.drawer_view);

        mStepValue = (TextView) mDrawerView.findViewById(R.id.step_value);
        mSensorVelue = (TextView) mDrawerView.findViewById(R.id.sensor_value);
        mStepController = (SeekBar) mDrawerView.findViewById(R.id.step_controller);
        mStepController.setOnSeekBarChangeListener(stepListner);
        mSensorController = (SeekBar) mDrawerView.findViewById(R.id.sensor_controller);
        mSensorController.setOnSeekBarChangeListener(sensorListner);
        mSetDefault = (Button) mDrawerView.findViewById(R.id.set_default);
        mSetDefault.setOnClickListener(onSetDefualtClick);

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

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MiniModeService.MiniModeBinder mb = (MiniModeService.MiniModeBinder) service;
                mMiniModeService = mb.getService();
                mMiniModeService.setMiniWalk(
                        ((MainMonitorFragment) mAdapter.getItem(PAGE.MONITOR.ordinal())).getTotWalk());
                mMiniModeService.setMiniDistance(
                        SocialUtils.convertDistance(((MainMonitorFragment) mAdapter.getItem(PAGE.MONITOR.ordinal())).getTotDistance()));
                mIsMiniMode = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mIsMiniMode = false;
            }
        };
    }

    public void initSettings() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStepValue.setText("" + BaseP.c().getInt(AppConstants.PREFERENCES.STEP_PREFERENCES));
                mSensorVelue.setText("" + BaseP.c().getInt(AppConstants.PREFERENCES.SENSOR_PREFERENCES));
                mStepController.setProgress(BaseP.c().getInt(AppConstants.PREFERENCES.STEP_PREFERENCES) * 10);
                mSensorController.setProgress(BaseP.c().getInt(AppConstants.PREFERENCES.SENSOR_PREFERENCES) / 5 - 165 / 5);
            }
        });
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

    public void setStart(boolean state) {
        mIsStart = state;
        mIsMiniMode = state == true ? true : false;
    }

    public boolean isStart() {
        return mIsStart;
    }

    public void startMiniService() {
        mIsMiniMode = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mIsMiniMode) {
            if (mConnection != null)
                unbindService(mConnection);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mIsMiniMode) {
            Intent intent = new Intent();
            intent.setClass(this, MiniModeService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void stopMiniService() {
        if (AppManager.isServiceRunningCheck(this, "com.aggapple.manbogi.service.MiniModeService")) {
            if (mConnection != null)
                unbindService(mConnection);
        }
    }

    public void startWalkService() {
        if (!AppManager.isServiceRunningCheck(this, "com.aggapple.manbogi.service.WalkSensorService")) {
            Intent intent = new Intent();
            intent.setClass(this, WalkSensorService.class);
            startService(intent);
        }
    }

    public void stopWalkService() {
        if (AppManager.isServiceRunningCheck(this, "com.aggapple.manbogi.service.WalkSensorService")) {
            Intent intent = new Intent();
            intent.setClass(this, WalkSensorService.class);
            stopService(intent);
        }
    }

    public MiniModeService getMiniModeService() {
        if (mIsMiniMode)
            return mMiniModeService;
        else
            return null;
    }

    @Override
    protected void onDestroy() {
        stopMiniService();
        stopWalkService();
        MainMonitorObserver.getInstance().deleteObserver(this);
        super.onDestroy();
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof MainMonitorObserver) {
            if (data != null) {
                ((MainMonitorFragment) mAdapter.getItem(PAGE.MONITOR.ordinal())).updateUI(data);
            }
        }
    }

    public void savePreferences(long date, long walk, double distance) {
        ManbogiData data = new ManbogiData(date, walk, distance);
        Gson gson = new Gson();
        String json = gson.toJson(data);
        BaseP.c().set(AppConstants.PREFERENCES.SAVE_PREFERENCES, json);
    }

    public ManbogiData loadPreferences() {
        Gson gson = new Gson();
        return gson.fromJson(BaseP.c().getString(AppConstants.PREFERENCES.SAVE_PREFERENCES), ManbogiData.class);
    }

    public void updateRunningState(boolean isRunning) {
        Gson gson = new Gson();
        ManbogiData data = gson.fromJson(BaseP.c().getString(AppConstants.PREFERENCES.SAVE_PREFERENCES), ManbogiData.class);
        if (data != null) {
            data.setRunState(isRunning);
            BaseP.c().set(AppConstants.PREFERENCES.SAVE_PREFERENCES, gson.toJson(data));
        }
    }

    public void saveDB() {
        String json = BaseP.c().getString(AppConstants.PREFERENCES.SAVE_PREFERENCES);
        Gson gson = new Gson();
        ManbogiData manbogiData = gson.fromJson(json, ManbogiData.class);


        Cursor c = getContentResolver().query(DBProvider.CONTENT_URI, new String[]{DBHelper._ID, DBHelper._DATE, DBHelper._WALK, DBHelper._DISTANCE}, null, null, "_id ASC");

        boolean isInsertData = true;
        if (c != null) {
            while (c.moveToNext()) {
                String date = c.getString(1);
                if (getDate(Long.parseLong(date), "yyyy.MM.dd").equals(getDate(manbogiData.getDate(), "yyyy.MM.dd"))) {
                    isInsertData = false;
                    updateData(c, manbogiData);
                    break;
                }
            }
            c.close();
        }

        if (isInsertData) {
            insertData(manbogiData);
        }

        ((MainRecordFragment) mAdapter.getItem(PAGE.RECORD.ordinal())).updateUI();
        BaseP.c().set(AppConstants.PREFERENCES.SAVE_PREFERENCES, gson.toJson(new ManbogiData()));
    }

    public void insertData(ManbogiData data) {
        if (data != null) {
            ContentValues cv = new ContentValues();
            cv.put(DBHelper._DATE, "" + data.getDate());
            cv.put(DBHelper._WALK, "" + data.getWalk());
            cv.put(DBHelper._DISTANCE, "" + data.getDistance());

            Uri retUri = getContentResolver().insert(DBProvider.CONTENT_URI, cv);
        }
    }

    public void updateData(Cursor c, ManbogiData data) {
        ContentValues cv = new ContentValues();
        data.setWalk((long) Long.parseLong(c.getString(2)) + data.getWalk());
        data.setDistance((double) Double.parseDouble(c.getString(3)) + data.getDistance());

        if (deleteData(c.getInt(0))) {
            insertData(data);
        }
    }

    public ArrayList<ManbogiData> queryAllData() {
        ArrayList<ManbogiData> dataList = new ArrayList<ManbogiData>();
        Cursor c = getContentResolver().query(DBProvider.CONTENT_URI, new String[]{DBHelper._ID, DBHelper._DATE, DBHelper._WALK, DBHelper._DISTANCE}, null, null, "_id ASC");

        boolean isInserData = true;
        int index = 0;
        if (c != null) {
            while (c.moveToNext()) {
                int id = c.getInt(0);
                String date = c.getString(1);
                String walk = c.getString(2);
                String distance = c.getString(3);
                ManbogiData data = new ManbogiData(id, Long.parseLong(date), Long.parseLong(walk), Double.parseDouble(distance));
                dataList.add(index, data);
                index++;
            }
            c.close();
        }

        return dataList;
    }

    public boolean deleteData(int index) {
        int delCnt = getContentResolver().delete(DBProvider.CONTENT_URI, DBHelper._ID + "=" + index, null);
        return delCnt > 0;
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private DrawerLayout.DrawerListener onDrawerStateChanged = new DrawerLayout.DrawerListener() {

        @Override
        public void onDrawerStateChanged(int arg0) {
        }

        @Override
        public void onDrawerSlide(View arg0, float arg1) {
        }

        @Override
        public void onDrawerOpened(View arg0) {
        }

        @Override
        public void onDrawerClosed(View arg0) {
        }
    };

    public SeekBar.OnSeekBarChangeListener stepListner = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mStepValue.setText("" + progress / 10);
                }
            });
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            BaseP.c().set(AppConstants.PREFERENCES.STEP_PREFERENCES, seekBar.getProgress() / 10);
        }
    };

    public SeekBar.OnSeekBarChangeListener sensorListner = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSensorVelue.setText("" + (165 + 5 * progress));
                }
            });
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            BaseP.c().set(AppConstants.PREFERENCES.SENSOR_PREFERENCES, (165 + 5 * seekBar.getProgress()));
        }
    };
    public View.OnClickListener onSetDefualtClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mStepValue.setText("" + 5);
                    mStepController.setProgress(50);
                    mSensorVelue.setText("" + 415);
                    mSensorController.setProgress(50);
                    BaseP.c().set(AppConstants.PREFERENCES.STEP_PREFERENCES, 5);
                    BaseP.c().set(AppConstants.PREFERENCES.SENSOR_PREFERENCES, 415);

                }
            });
        }
    };

}
