package com.obelieve.frame.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewbinding.ViewBinding;

import com.obelieve.frame.R;
import com.obelieve.frame.utils.info.SystemInfoUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public abstract class ApiBaseFragment<T extends ViewBinding,VM extends ViewModel> extends Fragment implements ICommonToolbar {

    protected boolean mInitOnce = true;
    protected T mViewBinding;
    protected VM mViewModel;
    protected Context mContext;
    protected Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createLayoutView(container);
        createViewModel();
        return mViewBinding.getRoot();
    }

    private void createLayoutView(@Nullable ViewGroup container) {
        Type superclass = getClass().getGenericSuperclass();
        Class<?> aClass = (Class<?>) ((ParameterizedType) superclass).getActualTypeArguments()[0];
        try {
            Method method = aClass.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
            mViewBinding = (T) method.invoke(null, getLayoutInflater(), container, false);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void createViewModel() {
        Type superclass = getClass().getGenericSuperclass();
        Type[] types = ((ParameterizedType) superclass).getActualTypeArguments();
        if(types.length==2){
            Class<VM> aClass = (Class<VM>) types[1];
            mViewModel = (VM) ViewModelProviders.of(this).get(aClass);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        if(mInitOnce){
            mInitOnce = false;
            initViewOnce();
        }
        initViewModel();
    }

    protected abstract void initView();

    protected void initViewModel(){}

    protected void initViewOnce(){}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void setNavigateStyle(Style style) {
        if (getView() == null) return;
        ImageView left_icon = getView().findViewById(R.id.left_icon);
        TextView title = getView().findViewById(R.id.title);
        TextView right_icon = getView().findViewById(R.id.right_icon);
        TextView right_tip = getView().findViewById(R.id.right_tip);
        if (style == Style.DARK) {
            if (left_icon != null) {
                left_icon.setImageResource(R.drawable.frame_ic_back_black);
            }
            if (title != null) {
                title.setTextColor(getResources().getColor(R.color.common_black));
            }
            if (right_icon != null) {
                right_icon.setTextColor(getResources().getColor(R.color.common_black));
            }
            if (right_tip != null) {
                right_tip.setTextColor(getResources().getColor(R.color.common_black));
            }
        } else if (style == Style.LIGHT) {
            if (left_icon != null) {
                left_icon.setImageResource(R.drawable.frame_ic_back_white);
            }
            if (title != null) {
                title.setTextColor(getResources().getColor(R.color.white));
            }
            if (right_icon != null) {
                right_icon.setTextColor(getResources().getColor(R.color.white));
            }
            if (right_tip != null) {
                right_tip.setTextColor(getResources().getColor(R.color.white));
            }
        }
    }

    @Override
    public void setNavigateBackgroundResource(int resId) {
        if (getView() == null) return;
        View toolbar_layout = getView().findViewById(R.id.toolbar_layout);
        if (toolbar_layout != null) {
            toolbar_layout.setBackgroundResource(resId);
        }
    }

    @Override
    public void setNavigateBackgroundColor(int color) {
        if (getView() == null) return;
        View toolbar_layout = getView().findViewById(R.id.toolbar_layout);
        if (toolbar_layout != null) {
            toolbar_layout.setBackgroundColor(color);
        }
    }

    @Override
    public void setNeedNavigate() {
        if (getView() == null) return;
        View left_layout = getView().findViewById(R.id.left_layout);
        if (left_layout != null) {
            left_layout.setOnClickListener(v -> {
                if (mActivity != null) mActivity.finish();
            });
        }
    }

    @Override
    public void setNeedNavigate(View.OnClickListener onClickListener) {
        if (getView() == null) return;
        View left_layout = getView().findViewById(R.id.left_layout);
        if (left_layout != null) {
            ImageView left_icon = getView().findViewById(R.id.left_icon);
            left_icon.setImageResource(R.drawable.frame_ic_back_black);
            left_layout.setOnClickListener(onClickListener);
        }
    }

    @Override
    public void setMyTitle(int titleId) {
        if (getView() == null) return;
        TextView tvTitle = getView().findViewById(R.id.title);
        if (tvTitle != null) {
            tvTitle.setText(titleId);
        }
    }

    @Override
    public void setMyTitle(String title) {
        if (getView() == null) return;
        TextView tvTitle = getView().findViewById(R.id.title);
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    @Override
    public void setRightNavigate(int drawableId, View.OnClickListener onClickListener) {
        if (getView() == null) return;
        View icon_layout = getView().findViewById(R.id.right_icon_layout_2);
        if (icon_layout != null) {
            ImageView rightIcon = getView().findViewById(R.id.right_icon_2);
            rightIcon.setImageResource(drawableId);
            icon_layout.setVisibility(View.VISIBLE);
            icon_layout.setOnClickListener(onClickListener);
        }
    }

    @Override
    public void setRightNavigate(int drawableGravity, int drawableId, String text, View.OnClickListener onClickListener) {
        if (getContext() != null)
            setRightNavigate(drawableGravity, drawableId, SystemInfoUtil.dp2px(getContext().getApplicationContext(), 4), text, onClickListener);
    }

    @Override
    public void setRightNavigate(int drawableGravity, int drawableId, int drawablePaddingDp, String text, View.OnClickListener onClickListener) {
        if (getView() == null || getContext() == null) return;
        View right_tip_layout = getView().findViewById(R.id.right_tip_layout);
        if (right_tip_layout != null) {
            TextView right_tip = getView().findViewById(R.id.right_tip);
            Drawable tempDrawable = ContextCompat.getDrawable(getContext(), drawableId);
            Drawable left = null, top = null, right = null, bottom = null;
            if (drawableGravity == Gravity.START) {
                left = tempDrawable;
            } else if (drawableGravity == Gravity.TOP) {
                top = tempDrawable;
            } else if (drawableGravity == Gravity.END) {
                right = tempDrawable;
            } else if (drawableGravity == Gravity.BOTTOM) {
                bottom = tempDrawable;
            }
            right_tip.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
            right_tip.setCompoundDrawablePadding(drawablePaddingDp);
            right_tip.setText(text);
            right_tip_layout.setVisibility(View.VISIBLE);
            right_tip_layout.setOnClickListener(onClickListener);
        }
    }
}
