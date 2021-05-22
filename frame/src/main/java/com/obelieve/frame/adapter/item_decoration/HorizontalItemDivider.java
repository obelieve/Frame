package com.obelieve.frame.adapter.item_decoration;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zxy
 */
public class HorizontalItemDivider extends RecyclerView.ItemDecoration {

    private boolean mIsFirst = true;
    private boolean mIsDP;
    private boolean mDividerToLeft;
    private int mDividerWidth;
    private int mColor;
    private Paint mPaint;
    private float mDensity;

    private Paint mNoPaint;
    private boolean mLeftNoDivider = false;
    private boolean mRightNoDivider = false;
    private List<Integer> mNoDividers = new ArrayList<>();
    private int mMarginTop;
    private int mMarginBottom;

    public HorizontalItemDivider() {
        this(false, 1, Color.rgb(216, 216, 216));
    }

    public HorizontalItemDivider(int color) {
        this(true, 1, color);
    }

    /**
     * @param dividerWidth unit dp
     * @param color
     */
    public HorizontalItemDivider(boolean is_dp, int dividerWidth, int color) {
        mIsDP = is_dp;
        mDividerWidth = dividerWidth;
        mColor = color;
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mNoPaint = new Paint();
        mNoPaint.setColor(Color.TRANSPARENT);
    }

    public HorizontalItemDivider dividerToLeft(boolean dividerToLeft) {
        mDividerToLeft = dividerToLeft;
        return this;
    }

    public HorizontalItemDivider noDividerItem(int... args) {
        return noDividerItem(false, false, args);
    }

    public HorizontalItemDivider noDividerItem(boolean leftNoDivider, boolean rightNoDivider, int... position) {
        mLeftNoDivider = leftNoDivider;
        mRightNoDivider = rightNoDivider;
        if (position != null && position.length > 0) {
            for (int i = 0; i < position.length; i++)
                mNoDividers.add(position[i]);
        }
        return this;
    }

    public HorizontalItemDivider marginTB(int top, int bottom) {
        mMarginTop = top;
        mMarginBottom = bottom;
        return this;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (mIsFirst && mIsDP) {
            mDensity = view.getResources().getDisplayMetrics().density;
            mDividerWidth = (int) (mDividerWidth * mDensity);
            mIsFirst = false;
        }
        int position = parent.getChildAdapterPosition(view);
        if (!((mLeftNoDivider && position == 0) ||
                (mRightNoDivider && position == parent.getAdapter().getItemCount() - 1) ||
                mNoDividers.size() > 0 && mNoDividers.contains(position))) {
            if (mDividerToLeft)
                outRect.left = mDividerWidth;
            else
                outRect.right = mDividerWidth;
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        int top = parent.getPaddingTop() + (int) (mIsDP ? mDensity * mMarginTop : mMarginTop);
        int bottom = parent.getHeight() - parent.getPaddingBottom() - (int) (mIsDP ? mDensity * mMarginBottom : mMarginBottom);
        for (int i = 0; i < childCount; i++) {
            Paint paint = mPaint;
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
            if ((mLeftNoDivider && position == 0) || (mRightNoDivider && position == parent.getAdapter().getItemCount() - 1) ||
                    mNoDividers.size() > 0 && mNoDividers.contains(position)) {
                paint = mNoPaint;
            }
            int left;
            int right;
            if (mDividerToLeft) {
                left = view.getLeft() - mDividerWidth;
                right = view.getLeft();
            } else {
                left = view.getRight();
                right = left + mDividerWidth;
            }
            c.drawRect(left, top, right, bottom, paint);
        }
    }
}
