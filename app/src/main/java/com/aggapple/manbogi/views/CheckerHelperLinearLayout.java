package com.aggapple.manbogi.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.aggapple.manbogi.utils.CheckerHelper;

import java.util.ArrayList;

public class CheckerHelperLinearLayout extends LinearLayout {

    public CheckerHelperLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckerHelperLinearLayout(Context context) {
        super(context);
    }

    private CheckerHelper mCheckerHelper = new CheckerHelper();

    public CheckerHelper getCheckerHelper() {
        return mCheckerHelper;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCheckerHelper.set(this, (String) getTag());
    }

    public void setOnItemClickListener(CheckerHelper.OnItemClickListener listener) {
        mCheckerHelper.setOnItemClickListener(listener);
    }

    public void setOnItemSelectedListener(CheckerHelper.OnItemSelectedListener listener) {
        mCheckerHelper.setOnItemSelectedListener(listener);
    }

    public int getCheckedIndex() {
        return mCheckerHelper.getCheckedIndex();
    }

    public View getCheckedView() {
        return mCheckerHelper.getCheckedView();
    }

    public int getCount() {
        return mCheckerHelper.getCount();
    }

    public View getView(int index) {
        return mCheckerHelper.getView(index);
    }

    private ArrayList<Object> mObjects = new ArrayList<Object>();

    public void add(Object obj) {
        mObjects.add(obj);
        if (getCount() < mObjects.size())
            throw new ArrayIndexOutOfBoundsException("피자 조각은 사람 수 보다 많아야 싸움이 안난다.");
    }

    public Object getCurrentItem() {
        return getItem(mCheckerHelper.getCheckedIndex());
    }

    public Object getItem(int position) {
        return mObjects.get(position);
    }

}
