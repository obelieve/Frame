package com.obelieve.thirdsdklib.picker;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.bigkoo.pickerview.TimePickerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Admin
 * on 2020/9/30
 */
public class TimerPickerDialog {

    TimePickerView mTimePickerView = null;

    public TimerPickerDialog(Context context, final Callback callback){
        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 1, 1);//设置起始年份
        Calendar endDate = Calendar.getInstance();
        mTimePickerView = new TimePickerView.Builder(context, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String format = simpleDateFormat.format(date);
                if(callback!=null){
                    callback.onTimeSelect(format);
                }

            }
        }).setType(new boolean[]{true, true, true, false, false, false})//默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setContentSize(15)//滚轮文字大小
                .setTitleSize(18)//标题文字大小
                .setTitleText("请选择时间")//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(false)//是否循环滚动
                .setTitleColor(0xff43CD80)//标题文字颜色
                .setSubmitColor(0xff43CD80)//确定按钮文字颜色
                .setCancelColor(0xff43CD80)//取消按钮文字颜色
                .setTitleBgColor(0xffffffff)//标题背景颜色 Night mode
                .setBgColor(0xffffffff)//滚轮背景颜色 Night mode
//                .setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR) + 20)//默认是1900-2100年
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "秒")
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)//是否显示为对话框样式
                .build();
    }

    public void setSelectedData(String date) {
        Calendar selectedDate = Calendar.getInstance();
        try {
            if (TextUtils.isEmpty(date)) {
                selectedDate.set(1999, 9, 9);
            } else {
                String[] split = date.split("-");
                selectedDate.set(Integer.parseInt(split[0]), Integer.parseInt(split[1]) - 1, Integer.parseInt(split[2]));//设置起始年份
            }
        } catch (Exception e) {
            selectedDate.set(1999, 9, 9);
        }
        mTimePickerView.setDate(selectedDate);
    }


    public  void show(){
        mTimePickerView.show();
    }

    public void dismiss(){
        mTimePickerView.dismiss();
    }

    public interface Callback {
        void onTimeSelect(String date);
    }
}
