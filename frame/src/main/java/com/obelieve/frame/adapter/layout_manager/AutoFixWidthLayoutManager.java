package com.obelieve.frame.adapter.layout_manager;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 自适应宽度 RecyclerView.LayoutManager
 * Created by zxy on 2019/2/12 11:57.
 */
public class AutoFixWidthLayoutManager extends RecyclerView.LayoutManager
{
    public AutoFixWidthLayoutManager()
    {

    }

    @Override
    public boolean isAutoMeasureEnabled()
    {
        //必须，防止recyclerview高度为wrap时测量item高度0
        return true;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams()
    {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state)
    {
        detachAndScrapAttachedViews(recycler);
        int parentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int curWidth = getPaddingLeft();
        int curHeight = getPaddingTop();
        int lastViewHeight = 0;
        for (int i = 0; i < getItemCount(); i++)
        {
            View view = recycler.getViewForPosition(i);
            addView(view);
            measureChildWithMargins(view, 0, 0);
            int viewWidth = getDecoratedMeasuredWidth(view);
            int viewHeight = getDecoratedMeasuredHeight(view);
            curWidth += viewWidth;
            if (curWidth <= parentWidth)
            {
                int l = curWidth - viewWidth;
                int t = curHeight;
                int r = l + viewWidth;
                int b = t + viewHeight;
                layoutDecorated(view, l, t, r, b);
                lastViewHeight = b;
            } else
            {
                int l = getPaddingLeft();
                int t = lastViewHeight;
                int r = l + viewWidth;
                int b = t + viewHeight;
                layoutDecorated(view, l, t, r, b);
                curWidth = r;
                curHeight = lastViewHeight;
                lastViewHeight = b;
            }
        }
    }
}
