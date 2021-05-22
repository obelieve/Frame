package com.obelieve.frame.utils.image;

import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.obelieve.frame.R;

/**
 * Created by Admin
 * on 2020/10/14
 */
@GlideExtension
public class CustomGlideExtension {


    //图片重新加载时，闪烁问题
    //Glide.with(MainActivity.this).load(mUrl).dontAnimate().placeholder(iv.getDrawable())

    /**
     * 过渡动画
     */
    private static DrawableCrossFadeFactory sDrawableCrossFadeFactory = new DrawableCrossFadeFactory.Builder(300).setCrossFadeEnabled(true).build();

    private static RequestOptions sAvatarRequestOptions = new RequestOptions().placeholder(R.drawable.ic_def_avatar).error(R.drawable.ic_def_avatar);
    private static RequestOptions sImgRequestOptions = new RequestOptions().placeholder(R.drawable.ic_load_loading).error(R.drawable.ic_load_error);

    private CustomGlideExtension() {
    }

    @GlideOption
    public static BaseRequestOptions<?> defAvatar(BaseRequestOptions<?> options) {
        options.apply(sAvatarRequestOptions);
        return options;
    }

    @GlideOption
    public static BaseRequestOptions<?> defImage(BaseRequestOptions<?> options) {
        options.apply(sImgRequestOptions);
        return options;
    }
}
