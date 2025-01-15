package com.obelieve.frame.ext.customviews.select;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.obelieve.ext.customviews.R;
import com.obelieve.rvtools.BaseRecyclerViewAdapter;

import java.util.List;

/**
 * Created by zxy
 * on 2020/5/8
 */
public class ThreeLayerSelectView extends FrameLayout {

    RecyclerView rvLayer1;
    RecyclerView rvLayer2;
    RecyclerView rvLayer3;
    View viewEmpty;


    Layer1Adapter mLayer1Adapter;
    Layer2Adapter mLayer2Adapter;
    Layer3Adapter mLayer3Adapter;

    List<Select1Entity> mSelect1EntityList;
    int[] mPos1 = new int[1];
    int[] mPos2 = new int[2];
    int[] mPos3 = new int[3];
    Callback mCallback;

    public ThreeLayerSelectView(@NonNull Context context) {
        this(context, null, 0);
    }

    public ThreeLayerSelectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThreeLayerSelectView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_three_layer_select, this, true);
        rvLayer1 = view.findViewById(R.id.rv_layer1);
        rvLayer2 = view.findViewById(R.id.rv_layer2);
        rvLayer3 = view.findViewById(R.id.rv_layer3);
        viewEmpty = view.findViewById(R.id.view_empty);
        viewEmpty.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallback!=null){
                    mCallback.onClickEmpty();
                }
            }
        });
        mLayer1Adapter = new Layer1Adapter(context);
        mLayer2Adapter = new Layer2Adapter(context);
        mLayer3Adapter = new Layer3Adapter(context);
        rvLayer1.setLayoutManager(new LinearLayoutManager(context));
        rvLayer2.setLayoutManager(new LinearLayoutManager(context));
        rvLayer3.setLayoutManager(new LinearLayoutManager(context));
        rvLayer1.setAdapter(mLayer1Adapter);
        rvLayer2.setAdapter(mLayer2Adapter);
        rvLayer3.setAdapter(mLayer3Adapter);
        mLayer1Adapter.setItemClickCallback(new BaseRecyclerViewAdapter.OnItemClickCallback<Select1Entity>() {
            @Override
            public void onItemClick(View view, Select1Entity entity, int position) {
                if (mSelect1EntityList.get(mPos1[0]) != null &&
                        entity != mSelect1EntityList.get(mPos1[0])) {
                    resetSelectedPosition(1, position, 0, 0);
                    mLayer1Adapter.notifyDataSetChanged();
                    if (entity.getList() != null && entity.getList().size() > 0) {
                        mLayer2Adapter.getDataHolder().setList(entity.getList());
                        mLayer3Adapter.getDataHolder().setList(entity.getList().get(mPos2[1]).getList());
                    }
                }
            }
        });
        mLayer2Adapter.setItemClickCallback(new BaseRecyclerViewAdapter.OnItemClickCallback<Select2Entity>() {
            @Override
            public void onItemClick(View view, Select2Entity entity, int position) {
                if (mSelect1EntityList.get(mPos2[0]) != null &&
                        mSelect1EntityList.get(mPos2[0]).getList() != null &&
                        entity != mSelect1EntityList.get(mPos2[0]).getList().get(mPos2[1])) {
                    resetSelectedPosition(2, mPos2[0], position, 0);
                    mLayer2Adapter.notifyDataSetChanged();
                    if (entity.getList() != null && entity.getList().size() > 0) {
                        mLayer3Adapter.getDataHolder().setList(entity.getList());
                    }
                }
            }
        });
        mLayer3Adapter.setItemClickCallback(new BaseRecyclerViewAdapter.OnItemClickCallback<Select3Entity>() {
            @Override
            public void onItemClick(View view, Select3Entity entity, int position) {
                if (mSelect1EntityList.get(mPos3[0]) != null &&
                        mSelect1EntityList.get(mPos3[0]).getList() != null &&
                        mSelect1EntityList.get(mPos3[0]).getList().get(mPos3[1]) != null &&
                        mSelect1EntityList.get(mPos3[0]).getList().get(mPos3[1]).getList() != null &&
                        entity != mSelect1EntityList.get(mPos3[0]).getList().get(mPos3[1]).getList().get(mPos3[2])) {
                    resetSelectedPosition(3, mPos1[0], mPos2[1], position);
                    mLayer3Adapter.notifyDataSetChanged();
                    if (mCallback != null) {
                        Select1Entity select1Entity = mSelect1EntityList.get(mPos3[0]);
                        Select2Entity select2Entity = mSelect1EntityList.get(mPos3[0]).getList().get(mPos3[1]);
                        Select3Entity select3Entity = mSelect1EntityList.get(mPos3[0])
                                .getList().get(mPos3[1])
                                .getList().get(mPos3[2]);
                        mCallback.onSelectedItem(
                                select1Entity.getId(),select1Entity.getName(),
                                select2Entity.getId(),select2Entity.getName(),
                                select3Entity.getId(),select3Entity.getName());
                    }
                }
            }
        });
    }

    public void loadData(List<Select1Entity> list) {
        mSelect1EntityList = list;
        int[] posArr = getDefSelectedPosition(list);
        int pos1 = posArr[0];
        int pos2 = posArr[1];
        int pos3 = posArr[2];
        mPos1[0] = pos1;

        mPos2[0] = pos1;
        mPos2[1] = pos2;

        mPos3[0] = pos1;
        mPos3[1] = pos2;
        mPos3[2] = pos3;
        mLayer1Adapter.getDataHolder().setList(list);
        if (list != null && mPos2[0] < list.size()) {
            mLayer2Adapter.getDataHolder().setList(list.get(mPos2[0]).getList());
            if (list.get(mPos2[0]).getList() != null && mPos2[1] < list.get(mPos2[0]).getList().size()) {
                mLayer3Adapter.getDataHolder().setList(list.get(mPos2[0]).getList().get(mPos2[1]).getList());
            }
        }
    }

    public void setCurSelectedPosition(int pos1, int pos2, int pos3){
        try{
            mSelect1EntityList.get(mPos1[0]).setSelected(false);
            mSelect1EntityList.get(mPos2[0]).getList().get(mPos2[1]).setSelected(false);
            mSelect1EntityList.get(mPos3[0]).getList().get(mPos3[1]).getList().get(mPos3[2]).setSelected(false);
            Select1Entity select1Entity = mSelect1EntityList.get(pos1);
            Select2Entity select2Entity = mSelect1EntityList.get(pos1).getList().get(pos2);
            Select3Entity select3Entity = mSelect1EntityList.get(pos1).getList().get(pos2).getList().get(pos3);
            select1Entity.setSelected(true);
            select2Entity.setSelected(true);
            select3Entity.setSelected(true);
            mLayer1Adapter.notifyDataSetChanged();
            mLayer2Adapter.getDataHolder().setList(select1Entity.getList());
            mLayer3Adapter.getDataHolder().setList(select2Entity.getList());
            mPos1[0] = pos1;
            mPos2[0] = pos1;
            mPos2[1] = pos2;
            mPos3[0] = pos1;
            mPos3[1] = pos2;
            mPos3[2] = pos3;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void resetSelectedPosition(int layer, int pos1, int pos2, int pos3) {
        if (mSelect1EntityList != null) {
            try {
                if (layer == 1) {
                    mSelect1EntityList.get(mPos1[0]).setSelected(false);
                    mSelect1EntityList.get(pos1).setSelected(true);
                    mPos1[0] = pos1;
                    int tempPos2 = getSelectedPosition((List) mSelect1EntityList.get(mPos1[0]).getList());
                    mPos2[0] = pos1;
                    mPos2[1] = tempPos2;
                } else if (layer == 2) {
                    mSelect1EntityList.get(pos1).getList().get(mPos2[1]).setSelected(false);
                    mSelect1EntityList.get(pos1).getList().get(pos2).setSelected(true);
                    mPos2[0] = pos1;
                    mPos2[1] = pos2;
                } else if (layer == 3) {
                    mSelect1EntityList.get(mPos3[0]).getList().get(mPos3[1]).getList().get(mPos3[2]).setSelected(false);
                    mSelect1EntityList.get(pos1).getList().get(pos2).getList().get(pos3).setSelected(true);
                    mPos3[0] = pos1;
                    mPos3[1] = pos2;
                    mPos3[2] = pos3;
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (layer >= 1) {
                    mPos1[0] = 0;
                }
                if (layer >= 2) {
                    mPos2[0] = 0;
                    mPos2[1] = 0;
                }
                if (layer >= 3) {
                    mPos3[0] = 0;
                    mPos3[1] = 0;
                    mPos3[2] = 0;
                }
            }
        }
    }


    private int[] getDefSelectedPosition(List<Select1Entity> list) {
        int pos1 = 0;
        int pos2 = 0;
        int pos3 = 0;
        if (list != null) {
            pos1 = getSelectedPosition((List) list);
            if (pos1 < list.size()) {
                List<Select2Entity> list2 = list.get(pos1).getList();
                if (list2 != null) {
                    pos2 = getSelectedPosition((List) list2);
                    if (pos2 < list2.size()) {
                        List<Select3Entity> list3 = list2.get(pos2).getList();
                        if (list3 != null) {
                            pos3 = getSelectedPosition((List) list3);
                        }
                    }
                }
            }
        }
        return new int[]{pos1, pos2, pos3};
    }

    private int getSelectedPosition(List<Selectable> list) {
        int position = 0;
        boolean first = true;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (first && list.get(i) != null && list.get(i).isSelected()) {//查找第一个selected的position
                    position = i;
                    first = false;
                    continue;
                }
                list.get(i).setSelected(false);//其他的都为false
                if (first && i == list.size() - 1) {//如果全部都是false，就设置第一个为true
                    list.get(0).setSelected(true);
                }
            }
        }
        return position;
    }

    public static class Layer1Adapter extends BaseRecyclerViewAdapter<Select1Entity> {

        public Layer1Adapter(Context context) {
            super(context);
        }

        @Override
        public BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
            return new TypeLayer1ViewHolder(parent);
        }

        @Override
        public void loadViewHolder(BaseViewHolder holder, int position) {
            TypeLayer1ViewHolder viewHolder = (TypeLayer1ViewHolder) holder;
            viewHolder.tvName.setText(getDataHolder().getList().get(position).getName());
            viewHolder.tvName.setSelected(getDataHolder().getList().get(position).isSelected());
        }


        public class TypeLayer1ViewHolder extends BaseViewHolder {

            TextView tvName;

            public TypeLayer1ViewHolder(ViewGroup parent) {
                super(parent, R.layout.viewholder_type_layer1);
                tvName = itemView.findViewById(R.id.tv_name);
            }
        }
    }

    public static class Layer2Adapter extends BaseRecyclerViewAdapter<Select2Entity> {

        public Layer2Adapter(Context context) {
            super(context);
        }

        @Override
        public BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
            return new TypeLayer2ViewHolder(parent);
        }

        @Override
        public void loadViewHolder(BaseViewHolder holder, int position) {
            TypeLayer2ViewHolder viewHolder = (TypeLayer2ViewHolder) holder;
            viewHolder.tvName.setText(getDataHolder().getList().get(position).getName());
            viewHolder.tvName.setSelected(getDataHolder().getList().get(position).isSelected());
        }


        public class TypeLayer2ViewHolder extends BaseViewHolder {

            TextView tvName;

            public TypeLayer2ViewHolder(ViewGroup parent) {
                super(parent, R.layout.viewholder_type_layer2);
                tvName = itemView.findViewById(R.id.tv_name);
            }
        }
    }

    public static class Layer3Adapter extends BaseRecyclerViewAdapter<Select3Entity> {

        public Layer3Adapter(Context context) {
            super(context);
        }

        @Override
        public BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
            return new TypeLayer3ViewHolder(parent);
        }

        @Override
        public void loadViewHolder(BaseViewHolder holder, int position) {
            TypeLayer3ViewHolder viewHolder = (TypeLayer3ViewHolder) holder;
            viewHolder.tvName.setText(getDataHolder().getList().get(position).getName());
            viewHolder.tvName.setSelected(getDataHolder().getList().get(position).isSelected());
            if (getDataHolder().getList().get(position).isSelected()) {
                viewHolder.ivSelect.setVisibility(VISIBLE);
            } else {
                viewHolder.ivSelect.setVisibility(GONE);
            }
        }


        public class TypeLayer3ViewHolder extends BaseViewHolder {

            TextView tvName;
            ImageView ivSelect;

            public TypeLayer3ViewHolder(ViewGroup parent) {
                super(parent, R.layout.viewholder_type_layer3);
                tvName = itemView.findViewById(R.id.tv_name);
                ivSelect = itemView.findViewById(R.id.iv_select);
            }
        }
    }

    public static class Select1Entity extends BaseSelectEntity {

        private List<Select2Entity> list;

        public List<Select2Entity> getList() {
            return list;
        }

        public void setList(List<Select2Entity> list) {
            this.list = list;
        }
    }

    public static class Select2Entity extends BaseSelectEntity {

        private List<Select3Entity> list;

        public List<Select3Entity> getList() {
            return list;
        }

        public void setList(List<Select3Entity> list) {
            this.list = list;
        }
    }

    public static class Select3Entity extends BaseSelectEntity {

    }

    public static class BaseSelectEntity implements Selectable {

        private int id;
        private String name;
        private boolean selected;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean isSelected() {
            return selected;
        }

        @Override
        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

    public interface Selectable {
        String getName();

        boolean isSelected();

        void setSelected(boolean selected);
    }

    public interface Callback {
        void onClickEmpty();
        void onSelectedItem(int id1, String name1, int id2, String name2, int id3, String name3);
    }
}
