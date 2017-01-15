package com.aggapple.manbogi.utils;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * <pre>
 * child view에 대하여 라디오 그룹의 버튼처럼 작동하게 만든다
 * 3가지모드가 있으며
 *   NONE 체크상태의 것이 없음
 *   RADIO 체크상태의 것이1개있음
 *   SINGLE_CHECK  체크상태의 것이 1개 또는 없음
 * setFilter()를 이용하여
 *   클릭이 가능한것을 지정할수 있다.
 *   지정하지 않으면 첫번째 Child를 대상으로 한다.
 *
 * Checked 항목은 setSelected() 함수를 이용하여 상태를 변경한다.
 *
 * </pre>
 */
public class CheckerHelper {
    private ArrayList<View> mChilds = new ArrayList<View>();
    private ViewGroup mParent;
    private OnItemClickListener onItemClickListener;
    private OnItemSelectedListener onItemSelectedListener;

    public static interface OnItemClickListener {
        public void onItemClick(ViewGroup parent, View view, int position, long id);
    }

    public static interface OnItemSelectedListener {
        public void onItemSelected(ViewGroup parent, View view, int position, long id);

        public void onNothingSelected(ViewGroup parent);
    }

    public enum MODE {
        NONE, RADIO, SINGLE_CHECK
    }

    private MODE mMode = MODE.RADIO;

    public static final String TAG_FILTER = "checkable";
    private String mTagFilter;

    /**
     * @param parent child view에 대하여 라디오 그룹의 버튼처럼 작동하게 만든다
     */
    // SINGLESELECTED default select
    public void setMode(MODE mode) {
        mMode = mode;
    }

    public void set(ViewGroup parent) {
        set(parent, null);
    }

    public void set(ViewGroup parent, String tagFilter) {
        set(parent, tagFilter, mMode);
    }

    public void set(ViewGroup parent, String tagFilter, MODE mode) {
        if (parent == null)
            throw new NullPointerException();

        setParent(parent, tagFilter);
        setMode(mode);

        clearOnClickListener();

        addOnClickListener();

        updateMode();
    }

    public void setParent(ViewGroup parent, String tagFilter) {
        if (parent == null)
            throw new NullPointerException();
        mParent = parent;
        mTagFilter = tagFilter;
    }

    private void updateMode() {
        final ArrayList<View> childs = mChilds;
        if (childs.size() > 0 && mMode == MODE.RADIO && getCheckedIndex() < 0)
            childs.get(0).setSelected(true);
    }

    public void updateOnClickListener() {
        clearOnClickListener();
        addOnClickListener();
    }

    public void clearOnClickListener() {
        final ArrayList<View> childs = mChilds;
        for (View view : childs)
            view.setOnClickListener(null);
    }

    public void addOnClickListener() {
        if (isEmpty(mTagFilter))
            addChildsFirst();
        else
            addChildsByTag(mParent);
    }

    private void addChildsFirst() {
        if (mParent == null)
            return;

        int N = mParent.getChildCount();
        for (int i = 0; i < N; i++) {
            final View child = mParent.getChildAt(i);
            mChilds.add(child);
            child.setOnClickListener(onSelectedClickListener);
        }
    }

    // 재귀함수
    private void addChildsByTag(ViewGroup parent) {
        int childCount = parent.getChildCount();
        for (int index = 0; index < childCount; index++) {
            View child = parent.getChildAt(index);
            if (child instanceof ViewGroup) {// ViewGroup일경우는 재귀호출
                addChildsByTag((ViewGroup) child);
            } else if (mTagFilter.equals(child.getTag())) {
                mChilds.add(child);
                child.setOnClickListener(onSelectedClickListener);
            }
        }
    }

    private OnClickListener onSelectedClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onItemClicked(v);
        }
    };

    private void fireNothingSelected() {
        if (onItemSelectedListener != null) {
            onItemSelectedListener.onNothingSelected(mParent);
        }
    }

    private void fireItemSelected(View v) {
        if (onItemSelectedListener != null) {
            onItemSelectedListener.onItemSelected(mParent, v, mChilds.indexOf(v), v.getId());
        }
    }

    private void fireItemClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(mParent, v, mChilds.indexOf(v), v.getId());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        onItemSelectedListener = listener;
    }

    public void onItemClicked(int i) {
        if (i < 0 || i >= getCount())
            return;
        View v = mChilds.get(i);

        onItemClicked(v);
    }

    public void onItemClicked(View v) {

        if (mMode != MODE.NONE) {
            if (mMode == MODE.SINGLE_CHECK && v.isSelected()) {
                v.setSelected(false);
                fireNothingSelected();
            } else {
                for (View child : mChilds) {
                    child.setSelected(child == v);
                    if (child == v)
                        fireItemSelected(v);
                }
            }
        }
        fireItemClick(v);
    }

    public int getCheckedIndex() {
        for (View child : mChilds) {
            if (child.isSelected()) {
                return mChilds.indexOf(child);
            }
        }
        return -1;
    }

    public View getCheckedView() {
        for (View child : mChilds) {
            if (child.isSelected()) {
                return child;
            }
        }
        return null;
    }

    public int getCount() {
        return mChilds.size();
    }

    public View getView(int index) {
        return mChilds.get(index);
    }

    public ViewGroup getGroup() {
        return mParent;
    }

    public boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

}
