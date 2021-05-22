package com.obelieve.frame.dialog;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.obelieve.frame.R;

public class LoadingDialog extends BaseDialog {

    public LoadingDialog(@NonNull Activity activity) {
        super(activity, R.style.LoadingDialog);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_loading, null);
        setContentView(view);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        setGravity(Gravity.CENTER);
    }

}
