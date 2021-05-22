package com.obelieve.frame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.obelieve.frame.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static com.obelieve.frame.adapter.BaseRecyclerViewAdapter.LoadMoreStatus.*;
import static com.obelieve.frame.adapter.BaseRecyclerViewAdapter.LoadMoreStatus.LOADING;

public abstract class BaseRecyclerViewAdapter<DATA> extends RecyclerView.Adapter<BaseRecyclerViewAdapter.BaseViewHolder> {

    private final int LOAD_MORE_TYPE = 9999;
    private final int EMPTY_TYPE = 9998;
    private final int HEADER_TYPE = 9997;
    private final int FOOTER_TYPE = 9996;
    private final int NORMAL_TYPE = 0;

    private Context mContext;
    private View mEmptyView;
    private View mHeaderView;
    private View mFooterView;
    private boolean mEnableHeader = true;
    private boolean mEnableFooter = true;
    private boolean mEnableLoadMore = true;
    private volatile boolean mLockLoadMore = false;
    private LoadMoreStatus mLoadMoreState = END;
    private RecyclerView mRecyclerView;
    private RecyclerView.OnScrollListener mOnScrollListener;

    protected BaseDataHolder<DATA> mDataHolder;
    protected OnItemClickCallback<DATA> mItemClickCallback;
    protected OnItemLongClickCallback<DATA> mItemLongClickCallback;
    private OnLoadMoreListener mOnLoadMoreListener;

    public BaseRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public BaseDataHolder<DATA> getDataHolder() {
        if (mDataHolder == null) {
            mDataHolder = new BaseDataHolder<>(this);
        }
        return mDataHolder;
    }

    public abstract BaseViewHolder getViewHolder(ViewGroup parent, int viewType);

    public abstract void loadViewHolder(BaseViewHolder holder, int position);

    public int loadItemViewType(int position) {
        return NORMAL_TYPE;
    }

    public void setLoadMoreListener(RecyclerView rv, OnLoadMoreListener listener) {
        mRecyclerView = rv;
        mOnLoadMoreListener = listener;
    }

    public void loadMoreLoading() {
        if (mOnLoadMoreListener == null)
            return;
        mLoadMoreState = LOADING;
        mLockLoadMore = false;
        if(getDataHolder().getList().size()>0){
            //size=0，Called attach on a child which is not detached
            notifyItemChanged(getItemCount() - 1);
        }
    }

    public void loadMoreError() {
        if (mOnLoadMoreListener == null)
            return;
        mLoadMoreState = ERROR;
        mLockLoadMore = false;
        if (getDataHolder().getList().size() > 0) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    public void loadMoreEnd() {
        if (mOnLoadMoreListener == null)
            return;
        mLoadMoreState = END;
        mLockLoadMore = false;
        if (getDataHolder().getList().size() > 0) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    public void onSetListAfter() {

    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
    }

    public void setEmptyView(View emptyView, RecyclerView.LayoutParams params) {
        if (mEmptyView != null)
            mEmptyView.setLayoutParams(params);
        mEmptyView = emptyView;
    }

    public void setEmptyView(View emptyView, int width, int height) {
        if (mEmptyView != null)
            mEmptyView.setLayoutParams(new RecyclerView.LayoutParams(width, height));
        mEmptyView = emptyView;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
    }

    public void setFooterView(View footerView) {
        mFooterView = footerView;
    }

    public void setEnableHeader(boolean enableHeader) {
        mEnableHeader = enableHeader;
    }

    public void setEnableFooter(boolean enableFooter) {
        mEnableFooter = enableFooter;
    }

    public void setEnableLoadMore(boolean enableLoadMore) {
        mEnableLoadMore = enableLoadMore;
    }

    public void setItemClickCallback(OnItemClickCallback<DATA> itemClickCallback) {
        mItemClickCallback = itemClickCallback;
    }

    public void setItemLongClickCallback(OnItemLongClickCallback<DATA> itemLongClickCallback) {
        mItemLongClickCallback = itemLongClickCallback;
    }

    private boolean checkIsLoadingState() {
        if (mRecyclerView == null)
            return false;
        RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
        if (lm instanceof LinearLayoutManager) {
            /**
             * 1.判断是否到底部，底部直接显示 end/error状态.
             * 2.没有到底部判断内容是否充满整页，有存在加载中，否则end/error状态.
             */
            if (((LinearLayoutManager) lm).findLastCompletelyVisibleItemPosition() + 1 == getItemCount()) {
                return false;
            } else {
                return ((LinearLayoutManager) lm).findFirstCompletelyVisibleItemPosition() != 0 ||
                        ((LinearLayoutManager) lm).findLastCompletelyVisibleItemPosition() + 1 < getItemCount();
            }
        } else if (lm instanceof StaggeredGridLayoutManager) {
            int[] startPosArray = new int[((StaggeredGridLayoutManager) lm).getSpanCount()];
            int[] endPosArray = new int[((StaggeredGridLayoutManager) lm).getSpanCount()];
            ((StaggeredGridLayoutManager) lm).findFirstCompletelyVisibleItemPositions(startPosArray);
            ((StaggeredGridLayoutManager) lm).findLastCompletelyVisibleItemPositions(endPosArray);
            Arrays.sort(startPosArray);
            Arrays.sort(endPosArray);
            /**
             * 1.判断是否到底部，底部直接显示 end/error状态.
             * 2.没有到底部判断内容是否充满整页，有存在加载中，否则end/error状态.
             * 3.底部还未显示出来时，返回-1
             */
            if (endPosArray[endPosArray.length - 1] + 1 == getItemCount() || endPosArray[endPosArray.length - 1] == -1) {
                return false;
            } else {
                return endPosArray[endPosArray.length - 1] + 1 < getItemCount() || startPosArray[0] != 0;
            }
        }
        return false;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseViewHolder vh = null;
        switch (viewType) {
            case LOAD_MORE_TYPE:
                vh = new LoadMoreViewHolder(parent, 0);
                break;
            case EMPTY_TYPE:
                vh = new SimpleViewHolder(mEmptyView);
                break;
            case HEADER_TYPE:
                vh = new SimpleViewHolder(mHeaderView);
                break;
            case FOOTER_TYPE:
                vh = new SimpleViewHolder(mFooterView);
                break;
            default:
                vh = getViewHolder(parent, viewType);
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        boolean header = mEnableHeader && mHeaderView != null;
        final int pos = header ? position - 1 : position;
        if (!(holder instanceof SimpleViewHolder) && !(holder instanceof LoadMoreViewHolder)) {
            if (mItemClickCallback != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickCallback.onItemClick(v, getDataHolder().getList().get(pos), pos);
                    }
                });
            } else {
                holder.itemView.setOnClickListener(null);
            }
            if (mItemLongClickCallback != null) {
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return mItemLongClickCallback.onItemLongClick(v, getDataHolder().getList().get(pos), pos);
                    }
                });
            } else {
                holder.itemView.setOnLongClickListener(null);
            }
            loadViewHolder(holder, pos);
        } else {
            if (holder instanceof LoadMoreViewHolder) {
                LoadMoreViewHolder holder1 = (LoadMoreViewHolder) holder;
                switch (mLoadMoreState) {
                    case LOADING:
                        holder1.mFlLoading.setVisibility(View.VISIBLE);
                        holder1.mFlError.setVisibility(View.GONE);
                        holder1.mFlEnd.setVisibility(View.GONE);
                        break;
                    case END:
                        holder1.mFlLoading.setVisibility(View.GONE);
                        holder1.mFlError.setVisibility(View.GONE);
                        holder1.mFlEnd.setVisibility(View.VISIBLE);
                        break;
                    case ERROR:
                        holder1.mFlLoading.setVisibility(View.GONE);
                        holder1.mFlError.setVisibility(View.VISIBLE);
                        holder1.mFlEnd.setVisibility(View.GONE);
                        break;
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnLoadMoreListener != null&&!mLockLoadMore) {
                            mLoadMoreState = LOADING;
                            mLockLoadMore = true;
                            holder1.mFlLoading.setVisibility(View.VISIBLE);
                            holder1.mFlError.setVisibility(View.GONE);
                            holder1.mFlEnd.setVisibility(View.GONE);
                            mOnLoadMoreListener.onLoadMore();
                        }
                    }
                });
                holder.itemView.setOnLongClickListener(null);
            } else {
                holder.itemView.setOnClickListener(null);
                holder.itemView.setOnLongClickListener(null);
            }
        }
    }

    @Override
    public int getItemCount() {
        int size = getDataHolder().getList().size();
        int empty = mEmptyView != null ? 1 : 0;
        int header = (mEnableHeader && mHeaderView != null) ? 1 : 0;
        int footer = (mEnableFooter && mFooterView != null) ? 1 : 0;
        int loadMore = (mEnableLoadMore && mOnLoadMoreListener != null) ? 1 : 0;
        if (size == 0) {
            if (header + footer > 0) {
                return header + footer;
            } else {
                return empty;
            }
        } else {
            return header + size + footer + loadMore;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int size = getDataHolder().getList().size();
        boolean header = mEnableHeader && mHeaderView != null;
        boolean footer = mEnableFooter && mFooterView != null;
        if (size == 0) {
            switch (position) {
                case 0:
                    if (header) {
                        return HEADER_TYPE;
                    } else if (footer) {
                        return FOOTER_TYPE;
                    } else {
                        return EMPTY_TYPE;
                    }
                case 1:
                    return FOOTER_TYPE;
                default:
                    return EMPTY_TYPE;
            }
        } else {
            if (position == 0 && header) {
                return HEADER_TYPE;
            } else if (position == size && !header) {
                if (footer) {
                    return FOOTER_TYPE;
                } else {
                    return LOAD_MORE_TYPE;
                }
            } else if (position == size + 1) {
                if (header) {
                    if (footer) {
                        return FOOTER_TYPE;
                    } else {
                        return LOAD_MORE_TYPE;
                    }
                } else {
                    return LOAD_MORE_TYPE;
                }
            } else if (position == size + 2) {
                return LOAD_MORE_TYPE;
            } else {
                return loadItemViewType(header ? position - 1 : position);
            }
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int index = holder.getLayoutPosition();
        /**
         * 瀑布流布局，有特殊类型时显示一行
         */
        if (getItemViewType(index) == (HEADER_TYPE | FOOTER_TYPE | LOAD_MORE_TYPE | EMPTY_TYPE)) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p =
                        (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            /**
             * Grid布局，有特殊类型时显示一行
             */
            GridLayoutManager.SpanSizeLookup lookup = ((GridLayoutManager) recyclerView.getLayoutManager()).getSpanSizeLookup();
            ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    int span =  type == HEADER_TYPE | type == FOOTER_TYPE | type == LOAD_MORE_TYPE | type == EMPTY_TYPE ?
                            ((GridLayoutManager) recyclerView.getLayoutManager()).getSpanCount() : lookup.getSpanSize(position);
                    return span;
                }
            });
        }
        if (recyclerView == mRecyclerView && mOnLoadMoreListener != null) {
            if (mOnScrollListener == null) {
                mOnScrollListener = new RecyclerView.OnScrollListener() {
                    /**
                     * 标记是否正在向上滑动
                     */
                    boolean isSlidingUpward = false;

                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                        if (newState == RecyclerView.SCROLL_STATE_IDLE && manager != null) {
                            //当状态是不滑动的时候
                            int lastItemPosition = 0;
                            int itemCount = manager.getItemCount();
                            if (manager instanceof GridLayoutManager) {
                                lastItemPosition = ((GridLayoutManager) manager).findLastCompletelyVisibleItemPosition();
                            } else if (manager instanceof LinearLayoutManager) {
                                lastItemPosition = ((LinearLayoutManager) manager).findLastCompletelyVisibleItemPosition();
                            } else if (manager instanceof StaggeredGridLayoutManager) {
                                int[] lastPositions = new int[((StaggeredGridLayoutManager) manager).getSpanCount()];
                                ((StaggeredGridLayoutManager) manager).findLastVisibleItemPositions(lastPositions);
                                lastItemPosition = findMax(lastPositions);
                            }
                            if (mEnableLoadMore && lastItemPosition == (itemCount - 1) && isSlidingUpward && mLoadMoreState == LOADING && !mLockLoadMore) {
                                mLockLoadMore = true;
                                mOnLoadMoreListener.onLoadMore();
                            }
                        }
                    }

                    private int findMax(int[] lastPositions) {
                        int max = lastPositions[0];
                        for (int value : lastPositions) {
                            if (value > max) {
                                max = value;
                            }
                        }
                        return max;
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        // 大于0表示正在向上滑动，小于等于0表示停止或向下滑动
                        isSlidingUpward = dy > 0;
                    }
                };
                mRecyclerView.addOnScrollListener(mOnScrollListener);
            }
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (recyclerView == mRecyclerView && mOnLoadMoreListener != null) {
            mRecyclerView.removeOnScrollListener(mOnScrollListener);
        }
    }

    public interface OnItemClickCallback<DATA> {
        void onItemClick(View view, DATA t, int position);
    }

    public interface OnItemLongClickCallback<DATA> {
        boolean onItemLongClick(View view, DATA t, int position);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }


    public static class BaseDataHolder<DATA> {

        private List<DATA> mList = new ArrayList<>();
        private BaseRecyclerViewAdapter<DATA> mAdapter;

        public BaseDataHolder(BaseRecyclerViewAdapter adapter) {
            mAdapter = adapter;
        }

        public List<DATA> getList() {
            return mList;
        }

        public void setList(List<DATA> list) {
            if (list == null) {
                list = new ArrayList<>();
            }
            mList = list;
            mAdapter.onSetListAfter();
            notifyDataSetChanged();
        }

        public void addAll(List<DATA> list) {
            if (list != null) {
                mList.addAll(list);
            }
            notifyDataSetChanged();
        }

        public void notifyItemChanged(int position) {
            mAdapter.notifyItemChanged(position);
        }

        public void notifyDataSetChanged() {
            mAdapter.notifyDataSetChanged();
        }
    }

    public static class SimpleViewHolder extends BaseViewHolder {

        public SimpleViewHolder(View view) {
            super(view);
        }
    }

    public static class LoadMoreViewHolder extends BaseViewHolder {


        private FrameLayout mFlLoading;
        private FrameLayout mFlError;
        private FrameLayout mFlEnd;

        public LoadMoreViewHolder(ViewGroup parent, int layoutId) {
            super(parent, R.layout.view_load_more);
            mFlLoading = itemView.findViewById(R.id.fl_loading);
            mFlError = itemView.findViewById(R.id.fl_error);
            mFlEnd = itemView.findViewById(R.id.fl_end);
        }
    }

    public static class BaseViewHolder<DATA, VB extends ViewBinding> extends RecyclerView.ViewHolder {
        protected VB mViewBinding;

        private BaseViewHolder(View view) {
            super(view);
        }

        public BaseViewHolder(VB viewBinding) {
            super(viewBinding.getRoot());
            mViewBinding = viewBinding;
        }

        public BaseViewHolder(ViewGroup parent, int layoutId) {
            super(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
        }

        public void bind(DATA data,int position,List<DATA> list) {
        }

        public VB getViewBinding() {
            return mViewBinding;
        }
    }

    public enum LoadMoreStatus {
        LOADING, END, ERROR
    }


}
