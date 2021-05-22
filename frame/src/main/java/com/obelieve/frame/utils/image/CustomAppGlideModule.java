package com.obelieve.frame.utils.image;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

/**
 * Glide4.0之后load(url)之后就不能调用.placeholder()等方法
 * 查看4.0+的文档发现需要通过GlideApp来调用一系列方法,生成GlideApp即可调用
 */
@GlideModule
public final class CustomAppGlideModule extends AppGlideModule {

}
