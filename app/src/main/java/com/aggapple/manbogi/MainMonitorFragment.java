package com.aggapple.manbogi;

import com.aggapple.manbogi.base.BaseApplication;
import com.aggapple.manbogi.base.BaseFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainMonitorFragment extends BaseFragment {

	private CheckBox mServiceSwitch;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.main_monitor_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initUI();
		updateUI();
	}

	private void initUI() {
		mServiceSwitch = (CheckBox) getView().findViewById(R.id.service_switch);
		mServiceSwitch.setOnCheckedChangeListener(onCheckedChange);
	}

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

}
