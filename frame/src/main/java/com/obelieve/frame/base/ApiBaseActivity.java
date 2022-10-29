package com.obelieve.frame.base;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewbinding.ViewBinding;

import com.obelieve.frame.R;
import com.obelieve.frame.dialog.LoadingDialog;
import com.obelieve.frame.utils.StatusBarUtil;
import com.obelieve.frame.utils.SystemIntentUtil;
import com.obelieve.frame.utils.info.SystemInfoUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;


public abstract class ApiBaseActivity<T extends ViewBinding,VM extends ViewModel> extends AppCompatActivity implements ICommonToolbar, IStatusBar {

    private static final int PERMISSION_REQUEST_CODE = 10240;

    private int mStatusBarColor = Color.WHITE;
    protected boolean mNeedInsetStatusBar = true;
    protected boolean mLightStatusBar = true;
    protected boolean mFullScreen = false;

    protected T mViewBinding;
    protected VM mViewModel;

    private LoadingDialog mLoadingDialog;
    /**
     * Activity 实例.
     */
    protected AppCompatActivity mActivity;
    private OnRequestPermissionListener mPermissionListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        if(isFullScreen()){
            StatusBarUtil.setWindowLightStatusBar(this, isLightStatusBar());
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //检测系统UI显示变化，设置系统UI不显示
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    StatusBarUtil.setFullScreen(ApiBaseActivity.this);
                }
            });
        }else{
            if (mNeedInsetStatusBar) {
                StatusBarUtil.setStatusBarColor(this, getStatusBarColor());
            } else {
                StatusBarUtil.setStatusBarTranslucentStatus(this);
            }
            StatusBarUtil.setWindowLightStatusBar(this, isLightStatusBar());
        }

        createLayoutView();
        createViewModel();
        initCreateAfterView(savedInstanceState);
        initViewModel();
    }


    private void createLayoutView() {
        Type superclass = getClass().getGenericSuperclass();
        Class<?> aClass = (Class<?>) ((ParameterizedType) superclass).getActualTypeArguments()[0];
        try {
            Method method = aClass.getDeclaredMethod("inflate", LayoutInflater.class);
            mViewBinding = (T) method.invoke(null, getLayoutInflater());
            setContentView(mViewBinding.getRoot());
        } catch (NoSuchMethodException | IllegalAccessException| InvocationTargetException e) {
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

    protected abstract void initCreateAfterView(Bundle savedInstanceState);

    protected abstract  void initViewModel();

    /**
     * 设置竖屏
     */
    protected void setScreenVerOrientation() {

        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_BEHIND);
        }
    }

    public int getStatusBarColor() {
        return mStatusBarColor;
    }

    public boolean isFullScreen() {
        return mFullScreen;
    }

    public boolean isLightStatusBar() {
        return mLightStatusBar;
    }

    public void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);
        }
        mLoadingDialog.show();
    }

    public void dismissLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 请求权限BaseActivity$OnRequestPermissionListener
     *
     * @param permissions
     * @param listener
     */
    public void requestPermission(final String[] permissions, OnRequestPermissionListener listener) {
        this.mPermissionListener = listener;
        //收集未授权或者拒绝过的权限
        ArrayList<String> deniedPermissionList = new ArrayList<>();
        for (String per : permissions) {
            int checkSelfPermission = ContextCompat.checkSelfPermission(this, per);
            if (checkSelfPermission == PackageManager.PERMISSION_DENIED) {
                deniedPermissionList.add(per);
            }
        }

        if (deniedPermissionList.isEmpty()) {
            if (mPermissionListener != null) {
                mPermissionListener.onSuccess();
            }
        } else {
            String[] permissionArray = deniedPermissionList.toArray(new String[deniedPermissionList.size()]);
            ActivityCompat.requestPermissions(this, permissionArray, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PERMISSION_REQUEST_CODE == requestCode) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissions[i])) {
                            showTipsDialog(permissions);
                        } else {
                            showSettingDialog();
                        }
                    }
                    return;
                }
            }

            if (mPermissionListener != null) {
                mPermissionListener.onSuccess();
            }
        }
    }

    /**
     * 显示提示对话框
     */
    private void showTipsDialog(final String[] permissions) {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("需要授权相关权限")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mPermissionListener != null) {
                            mPermissionListener.onFailure();
                        }
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
                        }
                    }
                }).show();
    }

    private void showSettingDialog() {
        new AlertDialog.Builder(this)
                .setTitle("提示信息")
                .setMessage("当前应用缺少必要权限，该功能暂时无法使用。如若需要，请单击【确定】按钮前往设置中心进行权限授权。")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mPermissionListener != null) {
                            mPermissionListener.onFailure();
                        }
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SystemIntentUtil.openAppSettings(mActivity);

                    }
                }).show();
    }

    @Override
    public void setNavigateStyle(Style style) {
        ImageView left_icon = findViewById(R.id.left_icon);
        TextView title = findViewById(R.id.title);
        TextView right_icon = findViewById(R.id.right_icon);
        TextView right_tip = findViewById(R.id.right_tip);
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
        View toolbar_layout = findViewById(R.id.toolbar_layout);
        if (toolbar_layout != null) {
            toolbar_layout.setBackgroundResource(resId);
        }
    }

    @Override
    public void setNavigateBackgroundColor(int color) {
        View toolbar_layout = findViewById(R.id.toolbar_layout);
        if (toolbar_layout != null) {
            toolbar_layout.setBackgroundColor(color);
        }
    }

    @Override
    public void setNeedNavigate() {
        View left_layout = findViewById(R.id.left_layout);
        if (left_layout != null) {
            left_layout.setOnClickListener(v -> finish());
        }
    }

    @Override
    public void setNeedNavigate(View.OnClickListener onClickListener) {
        View left_layout = findViewById(R.id.left_layout);
        if (left_layout != null) {
            ImageView left_icon = findViewById(R.id.left_icon);
            left_icon.setImageResource(R.drawable.frame_ic_back_black);
            left_layout.setOnClickListener(onClickListener);
        }

    }

    @Override
    public void setMyTitle(int titleId) {
        TextView tvTitle = findViewById(R.id.title);
        if (tvTitle != null) {
            tvTitle.setText(titleId);
        }
    }

    @Override
    public void setMyTitle(String title) {
        TextView tvTitle = findViewById(R.id.title);
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    @Override
    public void setRightNavigate(int drawableId, View.OnClickListener onClickListener) {
        View icon_layout = findViewById(R.id.right_icon_layout_2);
        if (icon_layout != null) {
            ImageView rightIcon = findViewById(R.id.right_icon_2);
            rightIcon.setImageResource(drawableId);
            icon_layout.setVisibility(View.VISIBLE);
            icon_layout.setOnClickListener(onClickListener);
        }
    }

    @Override
    public void setRightNavigate(int drawableGravity, int drawableId, String text, View.OnClickListener onClickListener) {
        setRightNavigate(drawableGravity, drawableId, SystemInfoUtil.dp2px(getApplicationContext(), 4), text, onClickListener);
    }

    @Override
    public void setRightNavigate(int drawableGravity, int drawableId, int drawablePaddingDp, String text, View.OnClickListener onClickListener) {
        View right_tip_layout = findViewById(R.id.right_tip_layout);
        if (right_tip_layout != null) {
            TextView right_tip = findViewById(R.id.right_tip);
            Drawable tempDrawable = getResources().getDrawable(drawableId);
            Drawable left = null, top = null, right = null, bottom = null;
            if (drawableGravity == Gravity.LEFT) {
                left = tempDrawable;
            } else if (drawableGravity == Gravity.TOP) {
                top = tempDrawable;
            } else if (drawableGravity == Gravity.RIGHT) {
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

    @Override
    public void setStatusBarHeight(View view) {
        if (view != null && view.getLayoutParams() != null) {
            view.getLayoutParams().height = StatusBarUtil.getStatusBarHeight(mActivity);
            view.setLayoutParams(view.getLayoutParams());
        }
    }

    public interface OnRequestPermissionListener {

        void onSuccess();

        void onFailure();
    }
}
