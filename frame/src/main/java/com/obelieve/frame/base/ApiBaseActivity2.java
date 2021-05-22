package com.obelieve.frame.base;

import androidx.lifecycle.ViewModel;
import androidx.viewbinding.ViewBinding;

public abstract class ApiBaseActivity2<T extends ViewBinding> extends ApiBaseActivity<T,ViewModel> {
    @Override
    protected void initViewModel() {

    }
}
