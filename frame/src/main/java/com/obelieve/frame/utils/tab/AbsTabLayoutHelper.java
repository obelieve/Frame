package com.obelieve.frame.utils.tab;

import android.widget.LinearLayout;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

public abstract class AbsTabLayoutHelper<T> implements ITabStatus<T> {

    protected ViewPager mViewPager;
    protected TabLayout mTabLayout;
    protected List<T> mList;
    protected int mCurrentSelectedPosition;

    @Override
    public void init(ViewPager viewPager, TabLayout tabLayout, List<T> list, int index) {
        mViewPager = viewPager;
        mTabLayout = tabLayout;
        mList = list;
        if (mViewPager == null || mTabLayout == null || mList == null || index < 0 || index >= mList.size()) {
            throw new IllegalArgumentException("初始化TabLayout 参数错误！");
        }
        mCurrentSelectedPosition = index;
        mTabLayout.removeAllTabs();
        mViewPager.clearOnPageChangeListeners();
        mTabLayout.clearOnTabSelectedListeners();
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectTab(tab, true);
                if (tab.getPosition() >= 0 && tab.getPosition() < mList.size()) {
                    mCurrentSelectedPosition = tab.getPosition();
                    mViewPager.setCurrentItem(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                selectTab(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        for (int i = 0; i < mList.size(); i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            if (null == tab.getCustomView()) {
                tab.setCustomView(getLayoutId());
            }
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tab.getCustomView().getLayoutParams();
            tab.getCustomView().setLayoutParams(layoutParams);
            setData(tab.getCustomView(), mList.get(i));
            tabLayout.addTab(tab, i == index);
        }
        for (int i = 0; i < mList.size(); i++) {
            selectTab(tabLayout.getTabAt(i), i == mCurrentSelectedPosition);
        }
    }

    public int getCurrentSelectedPosition() {
        return mCurrentSelectedPosition;
    }

    public T getCurrentSelectedData() {
        return mList.get(mCurrentSelectedPosition);
    }

    @Override
    public void refreshData() {
        if (mTabLayout != null) {
            for (int i = 0; i < mTabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = mTabLayout.getTabAt(i);
                if (tab != null) {
                    setData(tab.getCustomView(), mList.get(i));
                }
            }
        }
    }

    public void setTabPadding(int left, int top, int right, int bottom) {
        if (mTabLayout != null) {
            for (int i = 0; i < mTabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = mTabLayout.getTabAt(i);
                if (tab != null && tab.getCustomView() != null) {
                    tab.getCustomView().setPadding(left, top, right, bottom);
                }
            }
        }
    }
}
