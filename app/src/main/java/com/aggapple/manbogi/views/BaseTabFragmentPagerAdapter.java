package com.aggapple.manbogi.views;

import java.util.ArrayList;

import com.aggapple.manbogi.utils.CheckerHelper;
import com.aggapple.manbogi.utils.CheckerHelper.OnItemSelectedListener;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BaseTabFragmentPagerAdapter extends FragmentPagerAdapter implements OnPageChangeListener, OnItemSelectedListener {
	private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();
	CheckerHelper ch;
	protected ViewPager pager;

	public BaseTabFragmentPagerAdapter(FragmentManager fm, CheckerHelper ch, ViewPager pager) {
		super(fm);
		this.ch = ch;
		this.pager = pager;
		pager.setPageMargin(5);

		ch.setOnItemSelectedListener(this);
		pager.setOnPageChangeListener(this);
	}

	public BaseTabFragmentPagerAdapter(FragmentManager fm, CheckerHelperLinearLayout ch, ViewPager pager) {
		this(fm, ch.getCheckerHelper(), pager);
	}

	public void add(Fragment fragment) {
		mFragments.add(fragment);
		if (ch.getCount() < mFragments.size())
			throw new ArrayIndexOutOfBoundsException("피자 조각은 사람 수 보다 많아야 싸움이 안난다.");
	}

	public void add(Context context, Fragment fr, int resid_layout_tab_button) {
		final View tabButton = LayoutInflater.from(context).inflate(resid_layout_tab_button, ch.getGroup(), false);
		add(fr, tabButton);
	}

	public void add(Context context, Class<?> fragment, int resid_layout_tab_button) {
		final Fragment fr = Fragment.instantiate(context, fragment.getName());
		final View tabButton = LayoutInflater.from(context).inflate(resid_layout_tab_button, ch.getGroup(), false);
		add(fr, tabButton);
	}

	public void add(Context context, Class<?> fragment, View tabButton) {
		final Fragment fr = Fragment.instantiate(context, fragment.getName());
		add(fr, tabButton);
	}

	public void add(Fragment fr, View tabButton) {
		ch.getGroup().addView(tabButton);
		ch.updateOnClickListener();
		mFragments.add(fr);
	}

	@Override
	public Fragment getItem(int position) {
		return mFragments.get(position);
	}

	@Override
	public int getCount() {
		return mFragments.size();
	}

	@Override
	public void onItemSelected(ViewGroup parent, View view, int position, long id) {
		pager.setCurrentItem(position, true);
	}

	@Override
	public void onNothingSelected(ViewGroup parent) {
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		ch.onItemClicked(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}
}