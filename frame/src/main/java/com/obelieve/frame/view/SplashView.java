package com.obelieve.frame.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.obelieve.frame.R;
import com.obelieve.frame.utils.info.SystemInfoUtil;

public class SplashView extends FrameLayout {

    ViewPager vpContent;
    IndicatorView viewIndicator;
    TextView tvNext;

    PagerAdapter mAdapter;
    Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public SplashView(@NonNull Context context) {
        this(context, null);
    }

    public SplashView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_splash, this, true);
        vpContent = view.findViewById(R.id.vp_content);
        viewIndicator = view.findViewById(R.id.view_indicator);
        tvNext = view.findViewById(R.id.tv_next);
        tvNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onNext();
                }
            }
        });
    }

    public void loadData(@DrawableRes int[] resInts, @DrawableRes int selectedRes, @DrawableRes int unSelectedRes, @ColorInt int nextColor) {
        ImageView[] views = new ImageView[resInts.length];
        for (int i = 0; i < views.length; i++) {
            views[i] = new ImageView(getContext());
            views[i].setLayoutParams(new ViewPager.LayoutParams());
            views[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
            views[i].setImageResource(resInts[i]);
        }
        viewIndicator.setCountAndIndex(resInts.length, 0)
                .setIndicatorGap(5)
                .setSelectedDrawable(selectedRes)
                .setUnSelectedDrawable(unSelectedRes)
                .build();
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(nextColor);
        drawable.setCornerRadius(SystemInfoUtil.dp2px(getContext(),20));
        tvNext.setBackground(drawable);
        mAdapter = new PagerAdapter() {

            @Override
            public int getCount() {
                return resInts.length;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                container.addView(views[position]);
                return views[position];
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        };
        vpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewIndicator.refreshCurrentIndex(position);
                if (position == resInts.length - 1) {
                    tvNext.setVisibility(VISIBLE);
                    viewIndicator.setVisibility(GONE);
                } else {
                    tvNext.setVisibility(GONE);
                    viewIndicator.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vpContent.setAdapter(mAdapter);
    }

    public interface Callback {
        void onNext();
    }
}
