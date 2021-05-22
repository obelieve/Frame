package com.obelieve.frame.utils.tab;

import android.view.View;

import androidx.viewpager.widget.ViewPager;


import com.google.android.material.tabs.TabLayout;

import java.util.List;

public interface ITabStatus<T> {

    void init(ViewPager viewPager, TabLayout tabLayout, List<T> list, int index);

    void selectTab(TabLayout.Tab tab, boolean selected);

    void setData(View view, T t);

    void refreshData();

    int getLayoutId();
}
