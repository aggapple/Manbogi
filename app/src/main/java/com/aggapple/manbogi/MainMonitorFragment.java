package com.aggapple.manbogi;

import com.aggapple.manbogi.base.BaseApplication;
import com.aggapple.manbogi.base.BaseFragment;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapContext;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainMonitorFragment extends BaseFragment {

	private CheckBox mServiceSwitch;
	private TextView mWalk, mDistance, mLoation;

	private NMapContext mMapContext;
	private NMapLocationManager mMapLocationManager;
	private static final String CLIENT_ID = "Narc0Em43EdZIvjgnLve";// 애플리케이션 클라이언트 아이디 값

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMapContext =  new NMapContext(super.getActivity());
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
	}

	private void initUI() {
		mWalk = (TextView) getView().findViewById(R.id.walk);
		mDistance = (TextView) getView().findViewById(R.id.distance);
		mLoation = (TextView) getView().findViewById(R.id.location);

		mServiceSwitch = (CheckBox) getView().findViewById(R.id.service_switch);
		mServiceSwitch.setOnCheckedChangeListener(onCheckedChange);
	}

	private void initMap()	 {
		// Fragment에 포함된 NMapView 객체 찾기
		NMapView mapView = findMapView(super.getView());
		if (mapView == null) {
			throw new IllegalArgumentException("NMapFragment dose not have an instance of NMapView.");
		}
		mapView.setClientId(CLIENT_ID);

		// NMapActivity를 상속하지 않는 경우에는 NMapView 객체 생성후 반드시 setupMapView()를 호출해야함.
		mMapContext.setupMapView(mapView);

		NMapLocationManager mLocationManager = new NMapLocationManager(getActivity());
		mLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);
		mLocationManager.enableMyLocation(true);

		// location manager
		mMapLocationManager = new NMapLocationManager(getActivity());
		mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);

		mMapContext.setMapDataProviderListener(new NMapActivity.OnDataProviderListener() {
			@Override
			public void onReverseGeocoderResponse(NMapPlacemark nMapPlacemark, NMapError nMapError) {
				if(nMapError!=null){
					Toast.makeText(getActivity(), ""+nMapError, Toast.LENGTH_SHORT).show();
				}else {
					mLoation.setText(nMapPlacemark.toString());
				}
			}
		});

	}
	private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {
		@Override
		public boolean onLocationChanged(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {
			//TODO:: 리소스를 다이렉트로 사용할 경우 앱 종료 시 접근오류가 발생할 수 있음

			mMapContext.findPlacemarkAtLocation(nGeoPoint.getLongitude(), nGeoPoint.getLatitude());
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
	}

	private OnCheckedChangeListener onCheckedChange = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
			if (isChecked) {
				mServiceSwitch.setText("STOP");
				mServiceSwitch.setBackgroundColor(0xFF0000FF);

				((MainActivity) getActivity()).startMiniService();

			} else {
				mServiceSwitch.setText("START");
				mServiceSwitch.setBackgroundColor(0xFFFF0000);

				((MainActivity) getActivity()).stopMiniService();
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

		ViewGroup vg = (ViewGroup)v;
		if (vg instanceof NMapView) {
			return (NMapView)vg;
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
	public void onStart(){
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
