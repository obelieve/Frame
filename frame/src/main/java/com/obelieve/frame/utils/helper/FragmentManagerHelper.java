package com.obelieve.frame.utils.helper;

import android.os.Bundle;
import android.util.SparseArray;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * 导航栏Tab管理帮助类：主要是维护Fragment列表，内部使用show/hide进行Fragment显示和隐藏
 * TabFragmentManagerHelper.activityRemoveFragmentInstanceState(Bundle) 移除Activity重建时Fragment实例恢复。
 * Created by Admin
 * on 2020/7/20
 */
public class FragmentManagerHelper {

    private Fragment mCurrentFragment;
    private FragmentManager mFragmentManager;
    private FragmentFactory mFragmentFactory;
    private SparseArray<Fragment> mFragmentArray = new SparseArray<>();
    @IdRes
    private int mContainer;

    public FragmentManagerHelper(FragmentManager fm, @IdRes int container, FragmentFactory factory) {
        mFragmentManager = fm;
        mContainer = container;
        mFragmentFactory = factory;
    }

    /**
     * 移除Activity重建时，onCreate()中,重新获取Fragment的实例
     *
     * @param savedInstanceState
     */
    public static void activityRemoveFragmentInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedInstanceState.remove("android:support:fragments");
            savedInstanceState.remove("android:fragments");
        }
    }

    public void switchFragment(int index) {
        Fragment fragment = getFragment(index);
        if (fragment == null) {
            fragment = mFragmentFactory.genFragment(index);
            mFragmentArray.put(index, fragment);
        }
        switchFragment(fragment);
    }

    public Fragment getFragment(int index) {
        return mFragmentArray.get(index);
    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (!fragment.isAdded()) {
            if (mCurrentFragment != null) {
                transaction.hide(mCurrentFragment);
            }
            transaction.add(mContainer, fragment, fragment.getClass().getName());
        } else {
            transaction.hide(mCurrentFragment).show(fragment);
        }
        mCurrentFragment = fragment;
        transaction.commitAllowingStateLoss();
    }


    public interface FragmentFactory {
        Fragment genFragment(int index);
    }

}
