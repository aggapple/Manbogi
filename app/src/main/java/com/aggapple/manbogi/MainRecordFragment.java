package com.aggapple.manbogi;

import java.util.ArrayList;

import com.aggapple.manbogi.base.BaseFragment;
import com.aggapple.manbogi.data.ManbogiData;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainRecordFragment extends BaseFragment {

	private ListView mList;
	private RecordAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.main_record_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initUI();
		updateUI();
	}

	private void initUI() {
		mList = (ListView) getView().findViewById(R.id.list);
		// TODO::만보기데이터 로드 후 아래 어답터에 적용
		mAdapter = new RecordAdapter(getActivity(), new ArrayList<ManbogiData>());
		mList.setAdapter(mAdapter);
	}

	public void updateUI() {
		if (getView() == null)
			return;
		mAdapter.notifyDataSetChanged();
	}

	public static class RecordHolder {
		public View v;
		public TextView date;
		public TextView walk;
		public TextView distance;

		public View set(View v) {
			this.v = v;
			date = (TextView) v.findViewById(R.id.date);
			walk = (TextView) v.findViewById(R.id.walk);
			distance = (TextView) v.findViewById(R.id.distance);

			v.setTag(this);
			return v;
		}
	}

	public class RecordAdapter extends BaseAdapter {
		protected Context mContext;
		protected ArrayList<ManbogiData> mDatas = new ArrayList<ManbogiData>();

		public RecordAdapter(Context context, ArrayList<ManbogiData> datas) {
			this.mContext = context;
			this.mDatas = datas;
		}

		@Override
		public int getCount() {
			return mDatas.size();
		}

		@Override
		public Object getItem(int position) {
			return mDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = new RecordHolder().set(LayoutInflater.from(mContext).inflate(R.layout.main_record_fragment_item, null, false));
			}

			final RecordHolder h = (RecordHolder) convertView.getTag();
			ManbogiData manData = (ManbogiData) getItem(position);

			// TODO::형변환 후 정리
			h.date.setText("" + manData.date);
			h.walk.setText("" + manData.walk);
			h.distance.setText("" + manData.distance);

			return convertView;

		}
	}

}