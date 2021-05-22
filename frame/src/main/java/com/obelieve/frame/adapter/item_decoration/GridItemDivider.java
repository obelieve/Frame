package com.obelieve.frame.adapter.item_decoration;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zxy
 * Notice:调用noDividerItem(leftRightNoDivider,topBottomNoDivider)在移除左右间距leftRightNoDivider=true设置时，会出现不等宽
 */
public class GridItemDivider extends RecyclerView.ItemDecoration {

    private boolean mIsFirst = true;
    private float mDensity;

    private boolean mIsDP;
    private int mDividerWidth;
    private int mColor;
    private Paint mPaint;
    private Paint mNoPaint;

    private boolean mDividerLeftToTop;
    private boolean mLeftRightNoDivider;
    private boolean mTopBottomNoDivider;
    private List<Integer> mNoDividers = new ArrayList<>();

    public GridItemDivider() {
        this(false, 1, Color.rgb(216, 216, 216));
    }

    public GridItemDivider(int color) {
        this(true, 1, color);
    }

    /**
     * @param dividerWidth unit dp
     * @param color
     */
    public GridItemDivider(boolean is_dp, int dividerWidth, int color) {
        mIsDP = is_dp;
        mDividerWidth = dividerWidth;
        mColor = color;
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mNoPaint = new Paint();
        mNoPaint.setColor(Color.TRANSPARENT);
    }

    public GridItemDivider dividerToLeftTop(boolean dividerToLeftTop) {
        mDividerLeftToTop = dividerToLeftTop;
        return this;
    }

    public GridItemDivider noDividerItem(int... args) {
        return noDividerItem(false, false, args);
    }

    public GridItemDivider noDividerItem(boolean leftRightNoDivider, boolean topBottomNoDivider, int... position) {
        mLeftRightNoDivider = leftRightNoDivider;
        mTopBottomNoDivider = topBottomNoDivider;
        if (position != null && position.length > 0) {
            for (int i = 0; i < position.length; i++)
                mNoDividers.add(position[i]);
        }
        return this;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (!(parent.getLayoutManager() instanceof GridLayoutManager)) {
            throw new RuntimeException("RecyclerView must be GridLayoutManager!");
        }
        if (parent.getAdapter() == null) {
            throw new RuntimeException("RecyclerView Adapter is null !");
        }
        if (mIsFirst) {
            mDensity = view.getResources().getDisplayMetrics().density;
            mDividerWidth = mIsDP ? (int) (mDividerWidth * mDensity) : mDividerWidth;
            mIsFirst = false;
        }
        GridLayoutManager lm = (GridLayoutManager) parent.getLayoutManager();
        int position = parent.getChildAdapterPosition(view);
        if (mDividerLeftToTop) {
            if (!(mLeftRightNoDivider && isLeftBorder(lm, position))) {
                outRect.left = mDividerWidth;
            }
            if (!(mTopBottomNoDivider && isTopRow(lm, position))) {
                outRect.top = mDividerWidth;
            }
            if (mNoDividers.size() > 0 && mNoDividers.contains(position)) {
                outRect.left = 0;
                outRect.top = 0;
            }
        } else {
            if (!(mLeftRightNoDivider && isRightBorder(lm, position))) {
                outRect.right = mDividerWidth;
            }
            if (!(mTopBottomNoDivider && isBottomRow(parent.getAdapter().getItemCount(), lm, position))) {
                outRect.bottom = mDividerWidth;
            }
            if (mNoDividers.size() > 0 && mNoDividers.contains(position)) {
                outRect.right = 0;
                outRect.bottom = 0;
            }
        }
    }

    private boolean isLeftBorder(GridLayoutManager lm, int position) {
        int spanCount = lm.getSpanCount();
        return lm.getSpanSizeLookup().getSpanIndex(spanCount, position) == 0;
    }

    private boolean isRightBorder(GridLayoutManager lm, int position) {
        int spanCount = lm.getSpanCount();
        int spanSize = lm.getSpanSizeLookup().getSpanSize(position);
        int spanIndex = lm.getSpanSizeLookup().getSpanIndex(position, spanCount);
        return spanSize == spanCount || (spanCount > spanSize && spanIndex == spanCount - 1);
    }

    private boolean isTopRow(GridLayoutManager lm, int position) {
        int spanCount = lm.getSpanCount();
        if (position >= 0 && position < spanCount) {
            int spanSize = 0;
            for (int i = 0; i <= position; i++) {//获取包含当前位置的之前所有SpanSize是否大于一行所显示的
                spanSize += lm.getSpanSizeLookup().getSpanSize(i);
            }
            return spanSize <= spanCount;
        } else {
            return false;
        }
    }

    private boolean isBottomRow(int itemCount, GridLayoutManager lm, int position) {
        int spanCount = lm.getSpanCount();
        int spanSize = lm.getSpanSizeLookup().getSpanSize(position);
        int spanIndex = lm.getSpanSizeLookup().getSpanIndex(position, spanCount);
        int leftSpanCount = spanCount / spanSize - (spanIndex + 1);//当前行还可以容纳多少项
        int leftSpanCounter = 0;//当前行的当前Item位置后面项数量计数
        int leftSpanSize = 0;//当前行的当前Item位置后面项占比SpanSize统计
        for (int i = position; i < itemCount; i++) {
            if (leftSpanCounter >= leftSpanCount) {
                break;
            }
            leftSpanSize += lm.getSpanSizeLookup().getSpanSize(i);
            leftSpanCounter++;
        }
        int rowSpanSize = leftSpanSize + spanSize * (spanIndex + 1);//当前行的Item位置预期SpanSize
        int leftItemCount = itemCount - (position + 1);//剩余多少项
        return rowSpanSize <= spanCount && leftSpanCount >= leftItemCount;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        GridLayoutManager lm = (GridLayoutManager) parent.getLayoutManager();
        int itemCount = parent.getAdapter().getItemCount();
        for (int i = 0; i < childCount; i++) {
            Paint paint = mPaint;
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
            int left = view.getLeft();
            int top = view.getTop();
            int right = view.getRight();
            int bottom = view.getBottom();
            if (mDividerLeftToTop) {
                if (!(mNoDividers.size() > 0 && mNoDividers.contains(position))) {
                    if (!(mLeftRightNoDivider && isLeftBorder(lm, position))) {
                        c.drawRect(left - mDividerWidth, top, left, bottom, paint);
                    }
                    if (!(mTopBottomNoDivider && isTopRow(lm, position))) {
                        c.drawRect(left - mDividerWidth, top - mDividerWidth, right, top, paint);
                    }
                }
            } else {
                if (!(mNoDividers.size() > 0 && mNoDividers.contains(position))) {
                    if (!(mLeftRightNoDivider && isRightBorder(lm, position))) {
                        c.drawRect(right, top, right + mDividerWidth, bottom, paint);
                    }
                    if (!(mTopBottomNoDivider && isBottomRow(itemCount, lm, position))) {
                        c.drawRect(left, bottom, right + mDividerWidth, bottom + mDividerWidth, paint);
                    }
                }
            }

        }
    }
}
