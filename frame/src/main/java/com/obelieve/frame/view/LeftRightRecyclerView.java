package com.obelieve.frame.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.obelieve.frame.R;
import com.obelieve.frame.adapter.BaseRecyclerViewAdapter;

import java.util.List;

/**
 * 左右RecyclerView联动 View
 * @param <LData>
 * @param <RData>
 */
public class LeftRightRecyclerView<LData extends LeftRightRecyclerView.ILeftData,RData extends LeftRightRecyclerView.IRightData> extends FrameLayout {

    RecyclerView mRvLeft;
    RecyclerView mRvRight;

    LeftAdapter<LData> mLeftAdapter;
    RightAdapter<RData> mRightAdapter;

    LeftViewHolderFactory<LData> mLeftViewHolderFactory;
    RightViewHolderFactory<RData> mRightViewHolderFactory;

    Callback<RData> mCallback;

    public LeftRightRecyclerView(@NonNull Context context) {
        this(context, null, 0);
    }

    public LeftRightRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeftRightRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.view_left_right_recyclerview, this, true);
        mRvLeft = view.findViewById(R.id.rv_left);
        mRvRight = view.findViewById(R.id.rv_right);
    }

    public void init(LeftViewHolderFactory<LData> leftViewHolderFactory, RightViewHolderFactory<RData> rightViewHolderFactory) {
        mLeftViewHolderFactory = leftViewHolderFactory;
        mRightViewHolderFactory = rightViewHolderFactory;
        mRvLeft.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvRight.setLayoutManager(new LinearLayoutManager(getContext()));
        mLeftAdapter = new LeftAdapter(getContext(), mLeftViewHolderFactory);
        mRightAdapter = new RightAdapter(getContext(), mRightViewHolderFactory);
        mRvLeft.setAdapter(mLeftAdapter);
        mRvRight.setAdapter(mRightAdapter);
        mLeftAdapter.setItemClickCallback(new BaseRecyclerViewAdapter.OnItemClickCallback<LData>() {
            @Override
            public void onItemClick(View view, ILeftData data, int position) {
                if (mLeftAdapter.getCurPosition() != position) {
                    mLeftAdapter.setCurrentPosition(data.LRRVTag());
                    int rightPos = mRightAdapter.scrollCurrentLRRVTag(data.LRRVTag());
                    if (rightPos != -1) {
                        mRvRight.scrollToPosition(rightPos);
                        if (mRvRight.getLayoutManager() instanceof LinearLayoutManager) {
                            ((LinearLayoutManager) mRvRight.getLayoutManager())
                                    .scrollToPositionWithOffset(rightPos, 0);
                        }
                    }
                }
            }
        });
        mRightAdapter.setItemClickCallback(new BaseRecyclerViewAdapter.OnItemClickCallback<RData>() {
            @Override
            public void onItemClick(View view, RData data, int position) {
                if (mCallback != null) {
                    mCallback.onRightItemClick(view, data, position);
                }
            }
        });
        mRvRight.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mRvRight.getLayoutManager() instanceof LinearLayoutManager) {
                    int firstVisiblePos = ((LinearLayoutManager) mRvRight.getLayoutManager()).findFirstVisibleItemPosition();
                    IRightData dataItem = mRightAdapter.getDataHolder().getList().get(firstVisiblePos);
                    mLeftAdapter.setCurrentPosition(dataItem.LRRVTag());
                }
            }
        });
    }

    public void setCallback(Callback<RData> callback) {
        mCallback = callback;
    }

    public void setLeftRightData(List<LData> leftData,
                                 List<RData> rightData) {
        mLeftAdapter.getDataHolder().setList(leftData);
        mRightAdapter.getDataHolder().setList(rightData);
    }

    public void setLeftSelectedPosition(int index){
        mRvLeft.post(new Runnable() {
            @Override
            public void run() {
                if(index>=0&&index<mRvLeft.getChildCount()){
                    mRvLeft.getChildAt(index).performClick();
                }
            }
        });
    }

    public LeftAdapter<LData> getLeftAdapter() {
        return mLeftAdapter;
    }

    public RightAdapter<RData> getRightAdapter() {
        return mRightAdapter;
    }

    public RecyclerView getRvLeft() {
        return mRvLeft;
    }

    public RecyclerView getRvRight() {
        return mRvRight;
    }

    public static class LeftAdapter<LData extends ILeftData> extends BaseRecyclerViewAdapter<LData> {

        private int mCurPosition = 0;
        private String mCurLRRVTAG="";

        private LeftViewHolderFactory<ILeftData> mFactory;

        public LeftAdapter(Context context, LeftViewHolderFactory<ILeftData> factory) {
            super(context);
            mFactory = factory;
        }

        @Override
        public BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
            return mFactory.genLeftViewHolder(parent);
        }

        @Override
        public void loadViewHolder(BaseViewHolder holder, int position) {
            if (holder instanceof LeftViewHolder) {
                ((LeftViewHolder) holder).bind(getDataHolder().getList().get(position));
                if (getDataHolder().getList().get(position).isSelected()) {
                    mCurLRRVTAG = getDataHolder().getList().get(position).LRRVTag();
                    mCurPosition = position;
                }
            }
        }

        public int getCurPosition() {
            return mCurPosition;
        }

        public void setCurrentPosition(String tag) {
            for (int i = 0; i < getDataHolder().getList().size(); i++) {
                if (mCurPosition != i&&!TextUtils.isEmpty(tag) &&
                        tag.equals(getDataHolder().getList().get(i).LRRVTag())) {
                    if (mCurPosition >= 0 && mCurPosition < getDataHolder().getList().size()) {
                        getDataHolder().getList().get(mCurPosition).setSelected(false);
                    }
                    getDataHolder().getList().get(i).setSelected(true);
                    mCurPosition = i;
                    mCurLRRVTAG = tag;
                    notifyDataSetChanged();
                    return;
                }
            }
        }

    }

    public static class RightAdapter<RData extends IRightData> extends BaseRecyclerViewAdapter<RData> {

        private RightViewHolderFactory<IRightData> mFactory;

        public RightAdapter(Context context, RightViewHolderFactory<IRightData> factory) {
            super(context);
            mFactory = factory;
        }

        @Override
        public BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
            return mFactory.genRightViewHolder(parent);
        }

        @Override
        public void loadViewHolder(BaseViewHolder holder, int position) {
            if (holder instanceof RightViewHolder) {
                ((RightViewHolder) holder).bind(getDataHolder().getList().get(position));
            }
        }

        public int scrollCurrentLRRVTag(String tag) {
            for (int i = 0; i < getDataHolder().getList().size(); i++) {
                final String LRRVTag = getDataHolder().getList().get(i).LRRVTag();
                if (getDataHolder().getList().get(i).isTop() && !TextUtils.isEmpty(LRRVTag)
                        && LRRVTag.equals(tag)) {
                    return i;
                }
            }
            return -1;
        }
    }

    public interface ILeftData {
        void setSelected(boolean selected);
        boolean isSelected();

        String LRRVTag();
    }

    public interface IRightData {
        boolean isTop();

        String LRRVTag();
    }

    public interface LeftViewHolderFactory<DATA extends ILeftData> {
        LeftViewHolder<DATA> genLeftViewHolder(ViewGroup parent);
    }

    public interface RightViewHolderFactory<DATA extends IRightData> {
        RightViewHolder<DATA> genRightViewHolder(ViewGroup parent);
    }

    public abstract static class LeftViewHolder<DATA extends ILeftData> extends BaseRecyclerViewAdapter.BaseViewHolder {

        public LeftViewHolder(ViewBinding viewBinding) {
            super(viewBinding);
        }

        public LeftViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
        }

        public abstract void bind(DATA data);
    }

    public abstract static class RightViewHolder<DATA extends IRightData> extends BaseRecyclerViewAdapter.BaseViewHolder {


        public RightViewHolder(ViewBinding viewBinding) {
            super(viewBinding);
        }

        public RightViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
        }

        public abstract void bind(DATA data);
    }

    public interface Callback<DATA extends IRightData> {
        void onRightItemClick(View view, DATA data, int position);
    }


}
