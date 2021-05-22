package com.obelieve.frame.utils.image;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.util.Util;
import com.obelieve.frame.R;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class GlideRoundTransform extends BitmapTransformation {

    private final float radius;
    private static final String ID = "com.zxy.frame.utils";
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);
    boolean isGif;
    Context context;

    public GlideRoundTransform(Context context, boolean isGif) {
        this(context, isGif, 7);
    }

    public GlideRoundTransform(Context context, boolean isGif, int dp) {
        this.isGif = isGif;
        this.context = context;
        this.radius = Resources.getSystem().getDisplayMetrics().density * dp;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return roundCrop(pool, toTransform);
    }

    private Bitmap roundCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
        canvas.drawRoundRect(rectF, radius, radius, paint);
        if (isGif) {
            paintGif(source, canvas);
        }
        return result;
    }

    private void paintGif(Bitmap oldbitmap, Canvas canvas) {
        int width = oldbitmap.getWidth();
        int height = oldbitmap.getHeight();
        Bitmap gifbmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_gif);
        int gifbmpWidth = gifbmp.getWidth();
        int gifbmpHeight = gifbmp.getHeight();

        canvas.drawBitmap(gifbmp, width - gifbmpWidth - 9, height - gifbmpHeight - 9, null);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof GlideRoundTransform) {
            GlideRoundTransform other = (GlideRoundTransform) obj;
            return radius == other.radius;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Util.hashCode(ID.hashCode(), Util.hashCode(radius));
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
        byte[] radiusData = ByteBuffer.allocate(4).putInt((int) radius).array();
        messageDigest.update(radiusData);
    }
}

