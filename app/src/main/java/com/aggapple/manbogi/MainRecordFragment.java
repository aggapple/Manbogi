package com.aggapple.manbogi;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.aggapple.manbogi.base.BaseFragment;
import com.aggapple.manbogi.data.ManbogiData;
import com.aggapple.manbogi.utils.SocialUtils;

import java.util.ArrayList;

public class MainRecordFragment extends BaseFragment {

    private ListView mList;
    private RecordAdapter mAdapter;
    private Button mRefreshBtn;

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
        mRefreshBtn = (Button) getView().findViewById(R.id.refresh);
        mRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI();
            }
        });
        mList = (ListView) getView().findViewById(R.id.list);
        mAdapter = new RecordAdapter(getActivity());
        mAdapter.setLongClickListener(onLongClick);
        mList.setAdapter(mAdapter);
    }

    public void updateUI() {
        if (getView() == null)
            return;
        mAdapter.setData(((MainActivity) getActivity()).queryAllData());
        mAdapter.notifyDataSetChanged();
    }

    public static class RecordHolder {
        public View v;
        public int id;
        public TextView date;
        public TextView walk;
        public TextView distance;

        public View set(View v, View.OnLongClickListener listener) {
            this.v = v;
            date = (TextView) v.findViewById(R.id.date);
            walk = (TextView) v.findViewById(R.id.walk);
            distance = (TextView) v.findViewById(R.id.distance);

            if (listener != null) {
                v.setOnLongClickListener(listener);
            }
            v.setTag(this);
            return v;
        }
    }

    public class RecordAdapter extends BaseAdapter {
        protected Context mContext;
        protected ArrayList<ManbogiData> mDatas = new ArrayList<ManbogiData>();
        protected View.OnLongClickListener longClickListener;

        public RecordAdapter(Context context) {
            this.mContext = context;
        }

        public void setLongClickListener(View.OnLongClickListener longClickListener) {
            this.longClickListener = longClickListener;
        }

        public void setData(ArrayList<ManbogiData> datas) {
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
                convertView = new RecordHolder().set(LayoutInflater.from(mContext).inflate(R.layout.main_record_fragment_item, null, false), this.longClickListener);
            }

            final RecordHolder h = (RecordHolder) convertView.getTag();
            ManbogiData manData = (ManbogiData) getItem(position);

            h.id = manData.getId();
            h.date.setText("" + ((MainActivity) getActivity()).getDate(manData.getDate(), "yyyy.MM.dd"));
            h.walk.setText("" + manData.getWalk());
            h.distance.setText("" + SocialUtils.convertDistance(manData.getDistance()));

            return convertView;

        }
    }

    private View.OnLongClickListener onLongClick = new View.OnLongClickListener() {
        RecordHolder h = null;

        @Override
        public boolean onLongClick(View v) {
            h = (RecordHolder) v.getTag();
            ((MainActivity) getActivity()).showDialog("해당 기록을 삭제하시겠습니까?", "예", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // View의 Tag를 이용하는 방식
                    if (h == null)
                        return;
                    if (((MainActivity) getActivity()).deleteData(h.id)) {
                        ((MainActivity) getActivity()).showDialog("기록이 삭제되었습니다.", "확인", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateUI();
                            }
                        }, true);
                    }
                }
            }, "아니오", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            }, true);
            return false;
        }
    };

}