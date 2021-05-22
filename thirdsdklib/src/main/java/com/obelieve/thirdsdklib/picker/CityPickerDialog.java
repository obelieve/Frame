package com.obelieve.thirdsdklib.picker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.model.IPickerViewData;
import com.google.gson.Gson;
import com.obelieve.frame.utils.proguard.UnProguard;
import com.obelieve.thirdsdklib.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class CityPickerDialog {

    private Activity mActivity;
    private OptionsPickerView mOptionsPickerView;

    private List<CityBean.ProvinceData> mProvinceDataList;
    private List<List<CityBean.CityData>> mCityDataNestedList;
    private int mSelected1, mSelected2;

    private Callback mCallback;


    public CityPickerDialog(Activity activity, Callback callback) {
        mActivity = activity;
        mCallback = callback;
        initData(activity);
    }


    public void setSelectedData(String city) {
        if (city == null) return;
        try {
            if (mProvinceDataList != null && mCityDataNestedList != null) {
                for (int i = 0; i < mProvinceDataList.size(); i++) {
                    List<CityBean.CityData> cityDataList = mCityDataNestedList.get(i);
                    for (int j = 0; j < cityDataList.size(); j++) {
                        if (cityDataList.get(j).getPickerViewText().equals(city)) {
                            mSelected1 = i;
                            mSelected2 = j;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSelectedData(String province, String city) {
        if (province == null || city == null) return;
        try {
            if (mProvinceDataList != null && mCityDataNestedList != null) {
                for (int i = 0; i < mProvinceDataList.size(); i++) {
                    if (province.equals(mProvinceDataList.get(i).getPickerViewText())) {
                        List<CityBean.CityData> cityDataList = mCityDataNestedList.get(i);
                        for (int j = 0; j < cityDataList.size(); j++) {
                            if (cityDataList.get(j).getPickerViewText().equals(city)) {
                                mSelected1 = i;
                                mSelected2 = j;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 展示选择器
     */
    public void show() {// 弹出选择器
        if (!isExistData())
            return;
        if (mOptionsPickerView == null) {
            mOptionsPickerView = new OptionsPickerView.Builder(mActivity, new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    //返回的分别是三个级别的选中位置
                    mSelected1 = options1;
                    mSelected2 = options2;
                    mCallback.selected(mProvinceDataList.get(options1).province, mCityDataNestedList.get(options1).get(options2).city);
                }
            })
                    .setCancelText("取消")
                    .setSubmitText("确定")
                    .setTitleText("选择城市")
                    .setTitleSize(18)//标题文字大小
                    .setTitleColor(0xff43CD80)//标题文字颜色
                    .setSubmitColor(0xff43CD80)//确定按钮文字颜色
                    .setCancelColor(0xff43CD80)//取消按钮文字颜色
                    .setTitleBgColor(0xffffffff)//标题背景颜色 Night mode
                    .setBgColor(0xffffffff)//滚轮背景颜色 Night mode
                    .setDividerColor(Color.TRANSPARENT)
                    .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                    .setContentTextSize(15)
                    .build();
            mOptionsPickerView.setPicker(mProvinceDataList, mCityDataNestedList);//二级选择器
        }
        mOptionsPickerView.setSelectOptions(mSelected1, mSelected2);
        mOptionsPickerView.show();
    }

    public void dismiss() {
        if (mOptionsPickerView != null) {
            mOptionsPickerView.dismiss();
            mOptionsPickerView = null;
        }
        mActivity = null;
    }

    private boolean isExistData() {
        boolean exist = false;
        if (mProvinceDataList != null && mCityDataNestedList != null && mProvinceDataList.size() == mCityDataNestedList.size()) {
            exist = true;
        }
        return exist;
    }

    private void initData(Context context) {
        try {
            CityBean cityBean = CityBeanUtil.getInstance().getCityBean(context);
            mProvinceDataList = cityBean.item;
            mCityDataNestedList = new ArrayList<>();
            for (CityBean.ProvinceData data : mProvinceDataList) {
                mCityDataNestedList.add(data.cities.item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface Callback {
        void selected(String province, String city);
    }

    private static class CityBean {

        public ArrayList<ProvinceData> item;

        public static class ProvinceData implements UnProguard, Serializable, IPickerViewData {
            public String id;
            public String provinceid;
            public String province;
            public CitysData cities;

            @Override
            public String getPickerViewText() {
                return province;
            }
        }

        public static class CitysData implements UnProguard, Serializable {
            public ArrayList<CityData> item;
        }

        public static class CityData implements UnProguard, Serializable, IPickerViewData {
            public String id;
            public String cityid;
            public String city;
            public String provinceid;

            @Override
            public String getPickerViewText() {
                return city;
            }
        }
    }

    private static class CityBeanUtil {

        private static CityBeanUtil instance = new CityBeanUtil();
        private CityBean mCityBean;

        private CityBeanUtil(){}

        public static CityBeanUtil getInstance() {
            return instance;
        }

        public void initCityData(Context context) {
            try {
                InputStream inputStream = context.getResources().openRawResource(R.raw.city);
                String data = getString(inputStream);
                mCityBean = new Gson().fromJson(data, CityBean.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String getString(InputStream inputStream) {
            InputStreamReader inputStreamReader = null;
            try {
                inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            BufferedReader reader = new BufferedReader(inputStreamReader);
            StringBuffer sb = new StringBuffer("");
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }

        public CityBean getCityBean(Context context) {
            if (mCityBean == null) {
                initCityData(context);
            }
            return mCityBean;
        }
    }
}
