package com.aggapple.manbogi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.aggapple.manbogi.base.AppConstants;
import com.aggapple.manbogi.base.BaseFragment;
import com.aggapple.manbogi.data.ManbogiData;
import com.aggapple.manbogi.utils.BaseP;
import com.aggapple.manbogi.utils.DeviceUtils;
import com.aggapple.manbogi.utils.SocialUtils;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapContext;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;

import java.lang.ref.WeakReference;

public class MainMonitorFragment extends BaseFragment {

    private CheckBox mServiceSwitch;
    private TextView mWalk, mDistance, mLoation;

    private NMapContext mMapContext;
    private NMapLocationManager mMapLocationManager;

    private static final int WALK_REST_LIMIT = 3;
    private double mTotDistance = 0.0d;
    private double mPrevLat = 0.0d;
    private double mPrevLng = 0.0d;
    private long mTotWalk = 0l;

    private CountDownTimer mConuntDownTimer;
    private boolean mIsWalking = false;

    public long getTotWalk() {
        return mTotWalk;
    }

    public double getTotDistance() {
        return mTotDistance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapContext = new NMapContext(super.getActivity());
        mMapContext.onCreate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_monitor_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initUI();
        initMap();
        updateUI();
        isRunningCheck();
    }

    private void initUI() {
        mWalk = (TextView) getView().findViewById(R.id.walk);
        mDistance = (TextView) getView().findViewById(R.id.distance);
        mLoation = (TextView) getView().findViewById(R.id.location);

        mServiceSwitch = (CheckBox) getView().findViewById(R.id.service_switch);
        mServiceSwitch.setOnCheckedChangeListener(onCheckedChange);
    }

    private void initValue() {
        mTotDistance = 0.0d;
        mTotWalk = 0l;
    }

    private void isRunningCheck(){
        if(BaseP.c().getBoolean(AppConstants.PREFERENCES.IS_RUN_PREFERENCES, false)){
            mServiceSwitch.setChecked(true);
        }
    }

    private void initMap() {
        // Fragment에 포함된 NMapView 객체 찾기
        NMapView mapView = findMapView(super.getView());
        if (mapView == null) {
            throw new IllegalArgumentException("NMapFragment dose not have an instance of NMapView.");
        }
        mapView.setClientId(AppConstants.CLIENT_ID);

        // NMapActivity를 상속하지 않는 경우에는 NMapView 객체 생성후 반드시 setupMapView()를 호출해야함.
        mMapContext.setupMapView(mapView);

        NMapLocationManager mLocationManager = new NMapLocationManager(getActivity());
        mLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);
        if (BaseP.c().getBoolean(Intro.EXTRA.PERMISSION.ACCESS_FINE_LOCATION.name())) {
            mLocationManager.enableMyLocation(true);
            DeviceUtils.chkGpsService(getActivity());
        } else {
            mLoation.setText("권한이 설정되지 않아 위치를 표시할 수 없습니다.");
        }

        // location manager
        mMapLocationManager = new NMapLocationManager(getActivity());
        mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);

        mMapContext.setMapDataProviderListener(new NMapActivity.OnDataProviderListener() {
            @Override
            public void onReverseGeocoderResponse(NMapPlacemark nMapPlacemark, NMapError nMapError) {
                if (nMapError != null) {
                    Toast.makeText(getActivity(), "" + nMapError, Toast.LENGTH_SHORT).show();
                } else {
                    mLoation.setText(nMapPlacemark.toString());
                }
            }
        });

    }

    private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {
        @Override
        public boolean onLocationChanged(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {
            MainMonitorObserver.getInstance().notifyObservers(nGeoPoint);

//			Toast.makeText(getActivity(), ""+nGeoPoint.getLatitude()+", "+nGeoPoint.getLongitude(), Toast.LENGTH_SHORT).show();
            return true;
        }


        @Override
        public void onLocationUpdateTimeout(NMapLocationManager nMapLocationManager) {

        }

        @Override
        public void onLocationUnavailableArea(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {

        }
    };

    public void updateUI() {
        if (getView() == null)
            return;

        loadPreferences();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWalk.setText("" + mTotWalk);
                mDistance.setText(SocialUtils.convertDistance(mTotDistance));
            }
        });
    }

    public void loadPreferences() {
        ManbogiData data = ((MainActivity) getActivity()).loadPreferences();
        if (data != null) {
            if (data.isRunning()) {
                mTotWalk = data.getWalk();
                mTotDistance = data.getDistance();
            }
        }
    }

    public void updateUI(Object data) {
        if (getView() == null)
            return;
        if (data instanceof NGeoPoint) {
            mMapContext.findPlacemarkAtLocation(((NGeoPoint) data).getLongitude(), ((NGeoPoint) data).getLatitude());
            double calcDistance = SocialUtils.calcDistance(mPrevLat, mPrevLng, ((NGeoPoint) data).getLatitude(), ((NGeoPoint) data).getLongitude());

            if (((MainActivity) getActivity()).mCurrentMode == MainActivity.STATE.MODE.STANDARD_DISTANCE) {
                if (((MainActivity) getActivity()).isStart()) {
                    if (mIsWalking && (mPrevLat != 0 || mPrevLng != 0)) {
                        mTotDistance += calcDistance;
                        updateUI();
                        ((MainActivity) getActivity()).savePreferences(System.currentTimeMillis(), mTotWalk, mTotDistance);
                        if (((MainActivity) getActivity()).getMiniModeService() != null) {
                            ((MainActivity) getActivity()).getMiniModeService().setMiniDistance(SocialUtils.convertDistance(mTotDistance));
                        }
                    }
                }
                mPrevLat = ((NGeoPoint) data).getLatitude();
                mPrevLng = ((NGeoPoint) data).getLongitude();
            }

        } else if (data instanceof Float) {
            if (((MainActivity) getActivity()).isStart()) {
                mTotWalk += 1;
                if (((MainActivity) getActivity()).mCurrentMode == MainActivity.STATE.MODE.STANDARD_WALK) {
                    mTotDistance += (double) BaseP.c().getInt(AppConstants.PREFERENCES.STEP_PREFERENCES) * 0.1d;
                } else {
                    mIsWalking = true;

                    if (mConuntDownTimer != null)
                        mConuntDownTimer.cancel();
                    mConuntDownTimer = new CountDownTimer(WALK_REST_LIMIT * 1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            // 2초의 시간동안 걸음의 수가 증가하지 않으면 멈춰있는것으로 간주한다.
                            // 멈춰있는 시간동안 이동거리는 증가하지 않는다.
                            mIsWalking = false;
                        }
                    };
                    mConuntDownTimer.start();
                }
                updateUI();

                if (((MainActivity) getActivity()).getMiniModeService() != null) {
                    ((MainActivity) getActivity()).getMiniModeService().setMiniWalk(mTotWalk);
                    if (((MainActivity) getActivity()).mCurrentMode == MainActivity.STATE.MODE.STANDARD_WALK) {
                        ((MainActivity) getActivity()).getMiniModeService().setMiniDistance(SocialUtils.convertDistance(mTotDistance));
                    }
                }
                ((MainActivity) getActivity()).savePreferences(System.currentTimeMillis(), mTotWalk, mTotDistance);

            }
        }
    }

    private OnCheckedChangeListener onCheckedChange = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
            if (isChecked) { // State is start
                mServiceSwitch.setText("STOP");
                mServiceSwitch.setBackgroundColor(0xFF0000FF);

                ((MainActivity) getActivity()).startWalkService();
                ((MainActivity) getActivity()).startMiniService();
                ((MainActivity) getActivity()).setStart(true);
                ((MainActivity) getActivity()).updateRunningState(true);
                BaseP.c().set(AppConstants.PREFERENCES.IS_RUN_PREFERENCES, true);

                updateUI();

            } else { // State is stop
                mServiceSwitch.setText("START");
                mServiceSwitch.setBackgroundColor(0xFFFF0000);

                ((MainActivity) getActivity()).stopWalkService();
                ((MainActivity) getActivity()).stopMiniService();
                ((MainActivity) getActivity()).setStart(false);
                ((MainActivity) getActivity()).updateRunningState(false);
                BaseP.c().set(AppConstants.PREFERENCES.IS_RUN_PREFERENCES, false);

                ((MainActivity) getActivity()).saveDB();
                initValue();
            }
        }
    };

    /**
     * Fragment에 포함된 NMapView 객체를 반환함
     */
    private NMapView findMapView(View v) {

        if (!(v instanceof ViewGroup)) {
            return null;
        }

        ViewGroup vg = (ViewGroup) v;
        if (vg instanceof NMapView) {
            return (NMapView) vg;
        }

        for (int i = 0; i < vg.getChildCount(); i++) {

            View child = vg.getChildAt(i);
            if (!(child instanceof ViewGroup)) {
                continue;
            }

            NMapView mapView = findMapView(child);
            if (mapView != null) {
                return mapView;
            }
        }
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();

        mMapContext.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        mMapContext.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        mMapContext.onPause();
    }

    @Override
    public void onStop() {

        mMapContext.onStop();

        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mMapContext.onDestroy();

        super.onDestroy();
    }

}
