package com.obelieve.frame.view.select;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.obelieve.frame.R;

import java.util.List;

/**
 * Created by Admin
 * on 2020/5/25
 */
public class ListSelectPopupView extends FrameLayout {

    private ListSelectView mListSelectView;
    private View mViewEmpty;
    private Callback mCallback;

    public ListSelectPopupView(@NonNull Context context) {
        this(context, null, 0);
    }

    public ListSelectPopupView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListSelectPopupView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_list_select_popup, this, true);
        mListSelectView = view.findViewById(R.id.lsv_content);
        mViewEmpty = view.findViewById(R.id.view_empty);
        mViewEmpty.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onClickEmpty();
                }
            }
        });
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
        mListSelectView.setCallback(new ListSelectView.Callback() {
            @Override
            public void onSingleSelected(ListSelectView.IListSelectViewData data) {
                if (mCallback != null) {
                    mCallback.onSingleSelected(data);
                }
            }
        });
    }

    public void loadData(ListSelectView.IListSelectView selectView, List<ListSelectView.IListSelectViewData> list, int selectType) {
        loadData(null, new LinearLayoutManager(getContext()), selectView, list, selectType);
    }

    public void loadData(RecyclerView.LayoutManager layoutManager, ListSelectView.IListSelectView selectView, List<ListSelectView.IListSelectViewData> list, int selectType) {
        loadData(null, layoutManager, selectView, list, selectType);
    }

    /**
     * @param decorations
     * @param layoutManager
     * @param selectView
     * @param list
     * @param selectType    ListSelectView#SINGLE_TYPE,ListSelectView#MULTI_TYPE
     */
    public void loadData(RecyclerView.ItemDecoration[] decorations, RecyclerView.LayoutManager layoutManager, ListSelectView.IListSelectView selectView, List<ListSelectView.IListSelectViewData> list, int selectType) {
        mListSelectView.loadData(decorations, layoutManager, selectView, list, selectType);
    }

    public List<ListSelectView.IListSelectViewData> getSelectedData() {
        return mListSelectView.getSelectedData();
    }


    public interface Callback {

        void onSingleSelected(ListSelectView.IListSelectViewData data);

        void onClickEmpty();
    }

}
