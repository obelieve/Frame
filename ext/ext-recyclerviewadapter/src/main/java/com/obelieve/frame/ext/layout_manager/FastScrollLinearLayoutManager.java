package com.obelieve.frame.ext.layout_manager;

import android.content.Context;
import android.graphics.PointF;
import android.util.DisplayMetrics;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;


public class FastScrollLinearLayoutManager extends LinearLayoutManager {

    public FastScrollLinearLayoutManager(Context context) {
        super(context);
    }
    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {

            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return FastScrollLinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return super.calculateSpeedPerPixel(displayMetrics);
            }

            @Override
            protected int calculateTimeForScrolling(int dx) {
                if (dx > 3000) {
                    dx = 3000;
                }
                return super.calculateTimeForScrolling(dx);
            }
        };

        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }
}
