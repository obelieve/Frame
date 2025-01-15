package com.obelieve.frame.ext.customviews.select;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.obelieve.frame.ext.ExtRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin
 * on 2020/5/25
 */
public class ListSelectView extends FrameLayout {
    /**
     * 单选类型
     */
    public static final int SINGLE_TYPE = 0;
    /**
     * 复选类型
     */
    public static final int MULTI_TYPE = 1;

    RecyclerView rvContent;

    List<IListSelectViewData> mList = new ArrayList<>();
    Callback mCallback;
    IListSelectView mIListSelectView;

    ListSelectAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    int mSelectType;
    int mCurSelectedPosition;

    public ListSelectView(@NonNull Context context) {
        this(context, null, 0);
    }

    public ListSelectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListSelectView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        rvContent = new RecyclerView(context);
        rvContent.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(rvContent);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void loadData(IListSelectView selectView, List<IListSelectViewData> list, int selectType) {
        loadData(null, new LinearLayoutManager(getContext()), selectView, list, selectType);
    }

    public void loadData(RecyclerView.LayoutManager layoutManager, IListSelectView selectView, List<IListSelectViewData> list, int selectType) {
        loadData(null, layoutManager, selectView, list, selectType);
    }

    /**
     * @param decorations
     * @param layoutManager
     * @param selectView
     * @param list
     * @param selectType    ListSelectView#SINGLE_TYPE,ListSelectView#MULTI_TYPE
     */
    public void loadData(RecyclerView.ItemDecoration[] decorations, RecyclerView.LayoutManager layoutManager, IListSelectView selectView, List<IListSelectViewData> list, int selectType) {
        mLayoutManager = layoutManager;
        mIListSelectView = selectView;
        mCurSelectedPosition = -1;
        mSelectType = selectType;
        mList = list != null ? list : new ArrayList<>();
        if (rvContent.getItemDecorationCount() > 0) {
            for (int i = 0; i < rvContent.getItemDecorationCount(); i++) {
                rvContent.removeItemDecorationAt(i);
            }
        }
        if (decorations != null) {
            for (RecyclerView.ItemDecoration decoration : decorations) {
                rvContent.addItemDecoration(decoration);
            }
        }
        rvContent.setLayoutManager(mLayoutManager);
        mAdapter = new ListSelectAdapter(getContext());
        mAdapter.setItemClickCallback(new ExtRecyclerViewAdapter.OnItemClickCallback<IListSelectViewData>() {
            @Override
            public void onItemClick(View view, IListSelectViewData data, int position) {
                if (mSelectType == SINGLE_TYPE) {
                    if (mCurSelectedPosition != position) {
                        if (mCurSelectedPosition >= 0 && mCurSelectedPosition < mAdapter.getDataHolder().getList().size()) {
                            mAdapter.getDataHolder().getList().get(mCurSelectedPosition).setSelected(false);
                        }
                        mAdapter.getDataHolder().getList().get(position).setSelected(true);
                        mAdapter.notifyDataSetChanged();
                        mCurSelectedPosition = position;
                        if (mCallback != null) {
                            mCallback.onSingleSelected(data);
                        }
                    }
                } else if (mSelectType == MULTI_TYPE) {
                    data.setSelected(!data.isSelected());
                    mIListSelectView.select(view, data.isSelected());
                }
            }
        });
        mAdapter.getDataHolder().setList(mList);
        rvContent.setAdapter(mAdapter);
    }

    public List<IListSelectViewData> getSelectedData() {
        List<IListSelectViewData> list = new ArrayList<>();
        if (mAdapter != null) {
            for (IListSelectViewData data : mAdapter.getDataHolder().getList()) {
                if (data != null && data.isSelected()) {
                    list.add(data);
                }
            }
        }
        return list;
    }

    public class ListSelectAdapter extends ExtRecyclerViewAdapter<IListSelectViewData> {


        public ListSelectAdapter(Context context) {
            super(context);
        }

        @Override
        public BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
            return mIListSelectView.genViewHolder(parent);
        }

        @Override
        public void loadViewHolder(BaseViewHolder holder, int position) {
            holder.bind(getDataHolder().getList().get(position),position,getDataHolder().getList());
        }
    }


    /**
     * 显示 选中/取消选中 视图接口
     */
    public interface IListSelectView {

        ExtRecyclerViewAdapter.BaseViewHolder genViewHolder(ViewGroup parent);

        void select(View view, boolean selected);
    }

    /**
     * 数据接口
     */
    public interface IListSelectViewData {

        String getName();

        boolean isSelected();

        void setSelected(boolean selected);
    }

    public interface Callback {

        void onSingleSelected(IListSelectViewData data);
    }

}
