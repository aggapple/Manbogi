package com.aggapple.manbogi;

import com.aggapple.manbogi.MainActivity.STATE.PAGE;
import com.aggapple.manbogi.base.BaseActivity;
import com.aggapple.manbogi.service.MiniModeService;
import com.aggapple.manbogi.utils.CheckerHelper;
import com.aggapple.manbogi.utils.IME;
import com.aggapple.manbogi.views.BaseTabFragmentPagerAdapter;
import com.aggapple.manbogi.views.CheckerHelperLinearLayout;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

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

//    private NMapContext mMapContext;
//    private static final String CLIENT_ID = "Narc0Em43EdZIvjgnLve";// 애플리케이션 클라이언트 아이디 값

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

//        mMapContext =  new NMapContext(this);
//        mMapContext.onCreate();
//        NMapView mapView = (NMapView)findViewById(R.id.mapView);
//        mapView.setApiKey(CLIENT_ID);// 클라이언트 아이디 설정
//        mMapContext.setupMapView(mapView);

		initUI();
		
//		NMapPlacemark mark = new NMapPlacemark();
//		Toast.makeText(this, mark.toString(), Toast.LENGTH_SHORT).show();
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
		if (isServiceRunningCheck()) {
			Intent intent = new Intent();
			intent.setClass(this, MiniModeService.class);
			stopService(intent);
			// if (mCounnection != null)
			// unbindService(mCounnection);
		}
	}

	@Override
	protected void onDestroy() {
		stopMiniService();
		super.onDestroy();
	}

	public boolean isServiceRunningCheck() {
		ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.aggapple.manbogi.service.MiniModeService".equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

}
