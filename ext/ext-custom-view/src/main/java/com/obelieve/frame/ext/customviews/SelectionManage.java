package com.obelieve.frame.ext.customviews;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

class SelectionManage {

    private Mode mMode = Mode.SINGLE_MUST_ONE;
    private List<Integer> mCurrentIndexes = new ArrayList<>();

    private List<View> mViews = new ArrayList<>();
    private List<Boolean> mBooleans = new ArrayList<>();
    private OnSelectChangeListener mOnSelectChangeListener;

    public void setMode(Mode mode) {
        clear();
        mMode = mode;
    }

    public void setItems(View... views) {
        clear();
        for (View view : views) {
            mViews.add(view);
            mBooleans.add(false);
        }
    }

    public void setOnSelectChangeListener(OnSelectChangeListener onSelectChangeListener) {
        mOnSelectChangeListener = onSelectChangeListener;
    }

    public void reset() {
        mCurrentIndexes.clear();
        for (int i = 0; i < mBooleans.size(); i++) {
            mBooleans.set(i, false);
            mViews.get(i).setSelected(false);
        }
    }

    public void setCurrentItem(int index) {
        if (index >= 0 && index < mViews.size()) {
            switch (mMode) {
                case SINGLE:
                    int lastIndex = mCurrentIndexes.size() > 0 ? mCurrentIndexes.get(0) : -1;
                    if (index != lastIndex) {
                        setNormalItem(lastIndex);
                        setSelectedItem(index);
                    } else {
                        setNormalItem(index);
                    }
                    break;
                case SINGLE_MUST_ONE:
                    int lastIndex_must = mCurrentIndexes.size() > 0 ? mCurrentIndexes.get(0) : -1;
                    if (index != lastIndex_must) {
                        setNormalItem(lastIndex_must);
                        setSelectedItem(index);
                    }
                    break;
                case MULTI:
                    if (mCurrentIndexes.contains(index)) {
                        setNormalItem(index);
                    } else {
                        setSelectedItem(index);
                    }
                    break;
                case MULTI_MUST_ONE:
                    int size = mCurrentIndexes.size();
                    boolean contain = mCurrentIndexes.contains(index);
                    if (size == 0 || (size > 0 && !contain)) {
                        setSelectedItem(index);
                    } else if (size > 1 && contain) {
                        setNormalItem(index);
                    }
                    break;
            }
        }
    }

    private void setNormalItem(int index) {
        if (index == -1)
            return;
        mBooleans.set(index, false);
        mViews.get(index).setSelected(false);
        mCurrentIndexes.remove((Integer) index);
        if (mOnSelectChangeListener != null) {
            mOnSelectChangeListener.onSelectChange(index, mViews.get(index), false);
        }
    }

    private void setSelectedItem(int index) {
        if (index == -1)
            return;
        mBooleans.set(index, true);
        mViews.get(index).setSelected(true);
        mCurrentIndexes.add(index);
        if (mOnSelectChangeListener != null) {
            mOnSelectChangeListener.onSelectChange(index, mViews.get(index), true);
        }
    }


    public void setCurrentItem(View view) {
        setCurrentItem(mViews.indexOf(view));
    }

    public List<Integer> getSelectedIndexes() {
        return mCurrentIndexes;
    }

    public List<View> getSelectedViews() {
        List<View> list = new ArrayList<>();
        for (Integer i : mCurrentIndexes) {
            list.add(mViews.get(i));
        }
        return list;
    }

    private void clear() {
        mCurrentIndexes.clear();
        mViews.clear();
        mBooleans.clear();
    }

    public interface OnSelectChangeListener {

        void onSelectChange(int index, View view, boolean select);
    }

    public enum Mode {
        SINGLE,
        SINGLE_MUST_ONE,
        MULTI,
        MULTI_MUST_ONE
    }
}
