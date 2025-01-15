package com.obelieve.frame;


import android.os.Bundle;

import com.obelieve.frame.base.ApiBaseActivity2;
import com.obelieve.frame.databinding.ActivityMainBinding;

public class MainActivity extends ApiBaseActivity2<ActivityMainBinding> {

    @Override
    protected void initCreateAfterView(Bundle savedInstanceState) {

    }

    @Override
    public int getStatusBarColor() {
        return getResources().getColor(R.color.colorPrimaryDark);
    }

    @Override
    public boolean isLightStatusBar() {
        return false;
    }
}



