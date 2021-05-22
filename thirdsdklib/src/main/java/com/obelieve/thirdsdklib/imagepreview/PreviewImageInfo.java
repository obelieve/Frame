package com.obelieve.thirdsdklib.imagepreview;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.previewlibrary.enitity.IThumbViewInfo;

/**
 * Created by Admin
 * on 2020/9/10
 */
public class PreviewImageInfo implements IThumbViewInfo {
    private String url;  //图片地址
    private Rect mBounds; // 记录坐标

    public PreviewImageInfo(String url, Rect bounds) {
        this.url = url;
        mBounds = bounds;
    }

    protected PreviewImageInfo(Parcel in) {
        url = in.readString();
        this.mBounds = in.readParcelable(Rect.class.getClassLoader());
    }

    public static final Parcelable.Creator<PreviewImageInfo> CREATOR = new Parcelable.Creator<PreviewImageInfo>() {
        @Override
        public PreviewImageInfo createFromParcel(Parcel in) {
            return new PreviewImageInfo(in);
        }

        @Override
        public PreviewImageInfo[] newArray(int size) {
            return new PreviewImageInfo[size];
        }
    };


    @Override
    public String getUrl() {//将你的图片地址字段返回
        return url;
    }

    @Override
    public Rect getBounds() {
        return mBounds;
    }

    @Nullable
    @Override
    public String getVideoUrl() {
        return null;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(url);
        parcel.writeParcelable(this.mBounds, i);
    }
}
