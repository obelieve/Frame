package com.obelieve.thirdsdklib.imagepreview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.obelieve.thirdsdklib.R;
import com.obelieve.thirdsdklib.databinding.DialogViewSelectPictureActionBinding;

public class SelectPictureActionView extends FrameLayout implements View.OnClickListener {

    DialogViewSelectPictureActionBinding mBinding;

    Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public SelectPictureActionView(@NonNull Context context) {
        this(context, null);
    }

    public SelectPictureActionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mBinding = DialogViewSelectPictureActionBinding.inflate(LayoutInflater.from(getContext()));
        addView(mBinding.getRoot());
        mBinding.tv1.setOnClickListener(this);
        mBinding.tv2.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_1) {
            if (mCallback != null) {
                mCallback.onSavePicture();
            }
            dismiss();
        } else if (v.getId() == R.id.tv_2) {
            dismiss();
        }
    }


    private void dismiss() {
        if (mCallback != null) {
            mCallback.onCancel();
        }
    }


    public interface Callback {
        void onSavePicture();
        void onCancel();
    }
}
