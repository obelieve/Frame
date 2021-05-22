package com.obelieve.frame.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.obelieve.frame.utils.SelectionManage;

/**
 * Created by admin on 2018/11/18.
 */

public class BottomTabView extends LinearLayout implements View.OnClickListener {

    SelectionManage mSelectionManage = new SelectionManage();

    private boolean mInitial;
    private Callback mCallback;
    private View[] mItemViews;
    private ViewPager mViewPager;

    public BottomTabView(@NonNull Context context) {
        this(context, null, 0);
    }

    public BottomTabView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomTabView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
    }

    public void setup(Callback callback) {
        init(0, null, callback);
    }

    public void setup(int curIndex, Callback callback) {
        init(curIndex, null, callback);
    }

    public void setupWithViewPager(ViewPager viewPager) {
        init(0, viewPager, null);
    }

    public void setupWithViewPager(ViewPager viewPager, Callback callback) {
        init(0, viewPager, callback);
    }

    public void setupWithViewPager(int curIndex, ViewPager viewPager, Callback callback) {
        init(curIndex, viewPager, callback);
    }

    public void setCurrentIndex(int currentIndex) {
        if (mItemViews != null && currentIndex >= 0 && currentIndex < mItemViews.length) {
            mSelectionManage.setCurrentItem(currentIndex);
        }
    }

    private void init(int curIndex, ViewPager viewPager, Callback callback) {
        if (mInitial) {
            throw new IllegalStateException("BottomTabView is already initialized !");
        }
        mInitial = true;
        if (curIndex < 0 || curIndex >= getChildCount()) {
            curIndex = 0;
        }
        mViewPager = viewPager;
        mCallback = callback;
        mItemViews = new View[getChildCount()];
        for (int i = 0; i < getChildCount(); i++) {
            mItemViews[i] = getChildAt(i);
            mItemViews[i].setOnClickListener(this);
        }
        mSelectionManage.setMode(SelectionManage.Mode.SINGLE_MUST_ONE);
        mSelectionManage.setItems(mItemViews);
        mSelectionManage.setOnSelectChangeListener(new SelectionManage.OnSelectChangeListener() {

            @Override
            public void onSelectChange(int index, View view, boolean select) {
                if (mViewPager != null && select) {
                    mViewPager.setCurrentItem(index);
                }
                if (mCallback != null)
                    mCallback.onSelectChange(index, view, select);
            }
        });
        if (mViewPager != null) {
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    mSelectionManage.setCurrentItem(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            if (curIndex == 0) {
                mSelectionManage.setCurrentItem(0);
            }
            mViewPager.setCurrentItem(curIndex);
        } else {
            mSelectionManage.setCurrentItem(curIndex);
        }
    }

    @Override
    public void onClick(View v) {
        for (int index = 0; index < mItemViews.length; index++) {
            if (v.getId() == mItemViews[index].getId()) {
                if (mCallback != null && !mCallback.onPreClick(index)) {
                    mSelectionManage.setCurrentItem(index);
                }
                break;
            }
        }
    }

    public interface Callback {
        default boolean onPreClick(int index){
            return false;
        }
        void onSelectChange(int index, View view, boolean select);
    }
}
