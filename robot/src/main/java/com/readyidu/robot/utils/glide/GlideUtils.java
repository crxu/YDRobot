package com.readyidu.robot.utils.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.makeramen.roundedimageview.RoundedImageView;
import com.readyidu.robot.R;
import com.readyidu.robot.utils.view.FastBlurUtil;

/**
 * @Autour: wlq
 * @Description: Glide工具类
 * @Date: 2017/10/12 12:00
 * @Update: 2017/10/12 12:00
 * @UpdateRemark:
 * @Version: V1.0
 */
public class GlideUtils {
    public static void loadImage(Context context, String imageUrl, ImageView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_default_picture1)
                .error(R.drawable.ic_default_picture1)
                .thumbnail(0.3f)
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void loadImageRes(Context context, int resId, ImageView imageView) {
        Glide.with(context)
                .load(resId)
                .placeholder(R.drawable.ic_default_picture1)
                .error(R.drawable.ic_default_picture1)
                .thumbnail(0.3f)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void loadImageNet(Context context, String imageUrl, ImageView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_default_picture1)
                .error(R.drawable.ic_default_picture1)
                .thumbnail(0.3f)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void loadHeadImageRes(Context context, int resId, ImageView imageView) {
        Glide.with(context)
                .load(resId)
                .placeholder(R.drawable.ic_robot_2)
                .error(R.drawable.ic_robot_2)
                .thumbnail(0.3f)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void loadHeadImageNet(Context context, String imageUrl, int radius, ImageView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_robot_2)
                .error(R.drawable.ic_robot_2)
                .thumbnail(0.3f)
                .bitmapTransform(new CenterCrop(context), new RoundedCornersTransformation(context, radius, 0, RoundedCornersTransformation.CornerType.ALL))
                .dontAnimate()
                .into(imageView);
    }

    public static void loadMusicHeadImageRes(Context context, int resId, ImageView imageView) {
        Glide.with(context)
                .load(resId)
                .placeholder(R.drawable.ic_music_play_head_default_background)
                .error(R.drawable.ic_music_play_head_default_background)
                .thumbnail(0.3f)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void loadMusicHeadImageNet(Context context, String imageUrl, int radius, ImageView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_music_play_head_default_background)
                .error(R.drawable.ic_music_play_head_default_background)
                .thumbnail(0.3f)
                .bitmapTransform(new CenterCrop(context), new RoundedCornersTransformation(context, radius, 0, RoundedCornersTransformation.CornerType.ALL))
                .dontAnimate()
                .into(imageView);
    }

    public static void loadRoundedImageNet(Context context, String imageUrl, ImageView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_default_picture1)
                .error(R.drawable.ic_default_picture1)
                .centerCrop()
                .dontAnimate()
                .into(imageView);
    }

    public static void loadRoundedImageVideoNewsNet(Context context, String imageUrl, int radius, ImageView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_video_news_default)
                .error(R.drawable.ic_video_news_default)
                .thumbnail(0.4f)
                .bitmapTransform(new CenterCrop(context), new RoundedCornersTransformation(context, radius, 0, RoundedCornersTransformation.CornerType.ALL))
                .dontAnimate()
                .into(imageView);
    }

    public static void loadRoundedImageRes(Context context, int resId, int radius, ImageView imageView) {
        Glide.with(context)
                .load(resId)
                .placeholder(R.drawable.ic_default_picture1)
                .error(R.drawable.ic_default_picture1)
                .thumbnail(0.3f)
                .bitmapTransform(new CenterCrop(context), new RoundedCornersTransformation(context, radius, 0, RoundedCornersTransformation.CornerType.ALL))
                .dontAnimate()
                .into(imageView);
    }

    public static void loadMusicSingerImageRes(Context context, int resId, ImageView imageView) {
        Glide.with(context)
                .load(resId)
                .placeholder(R.drawable.ic_music_play_head_default_background)
                .error(R.drawable.ic_music_play_head_default_background)
                .thumbnail(0.3f)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(imageView);
    }

    public static void loadMusicSingerImageNet(Context context, final String imageUrl, final ImageView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_music_play_head_default_background)
                .error(R.drawable.ic_music_play_head_default_background)
                .bitmapTransform(new BitmapTransformation(context) {
                    @Override
                    public String getId() {
                        return imageUrl;
                    }

                    @Override
                    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
                        return ss(toTransform);
                    }
                })
                .into(imageView);
    }

    public static void loadMusicSingerImageNet2(Context context, final String imageUrl, final ImageView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_music_play_head_default_background)
                .error(R.drawable.ic_music_play_head_default_background)
                .bitmapTransform(new BitmapTransformation(context) {
                    @Override
                    public String getId() {
                        return imageUrl;
                    }

                    @Override
                    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
                        return ss(toTransform);
                    }
                })
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        imageView.setImageDrawable(resource);
                        if (imageView instanceof RoundedImageView) {
                            imageView.postInvalidate();
                        }
                    }
                });
    }

    public static void loadBlurImageRes(Context context, int resId, final ImageView imageView) {
        Glide.with(context)
                .load(resId)
                .asBitmap()
                .placeholder(R.drawable.ic_music_play_head_default_background)
                .error(R.drawable.ic_music_play_head_default_background)
                .thumbnail(0.3f)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        setBlurBitmap(drawable2Bitmap(placeholder), imageView);
                    }

                    @Override
                    public void onResourceReady(Bitmap originBitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        setBlurBitmap(originBitmap, imageView);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable drawable) {
                        setBlurBitmap(drawable2Bitmap(drawable), imageView);
                    }
                });
    }

    public static void loadBlurImageNet(final Context context, String imageUrl, final ImageView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .asBitmap()
                .placeholder(R.drawable.ic_music_play_head_default_background)
                .error(R.drawable.ic_music_play_head_default_background)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        setBlurBitmap(drawable2Bitmap(placeholder), imageView);
                    }

                    @Override
                    public void onResourceReady(Bitmap originBitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        setBlurBitmap(originBitmap, imageView);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable drawable) {
                        setBlurBitmap(drawable2Bitmap(drawable), imageView);
                    }
                });
    }

    private static void setBlurBitmap(Bitmap originBitmap, ImageView imageView) {
        int scaleRatio = 10;
        int blurRadius = 8;
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                originBitmap,
                originBitmap.getWidth() / scaleRatio,
                originBitmap.getHeight() / scaleRatio,
                false);
        Bitmap blurBitmap = FastBlurUtil.doBlur(scaledBitmap, blurRadius, true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(blurBitmap);
    }

    private static Bitmap drawable2Bitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    public static void loadSing(Context context, final String url, final ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.ic_music_play_head_default_background)
                .error(R.drawable.ic_music_play_head_default_background)
                .bitmapTransform(new BitmapTransformation(context) {
                    @Override
                    public String getId() {
                        return url;
                    }

                    @Override
                    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
                        return ss(toTransform);
                    }
                })
                .into(imageView);
    }

    private static Bitmap ss(Bitmap bitmap) {
        if (null != bitmap) {
            int max = bitmap.getWidth();
            int height = bitmap.getHeight();

            if (max < height) {
                max = height;
            }
            return GlideUtils.toRoundBitmap(Bitmap.createScaledBitmap(bitmap, max, max, true));
        }
        return null;
    }

    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }
}