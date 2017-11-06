package com.jeremy.imagecompress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by Jeremy on 2017/11/3.
 */

public class ImageUtils {

    public static final int TARGET_WIDTH = 800;
    public static final int DEFAULT_QUALITY = 30;

    static {
        System.loadLibrary("imgcompress-lib");
    }

    /**
     * 读取图片
     *
     * @param imgPath 图片路径
     * @return
     */
    public static Bitmap decodeFileWithInSampleSize(String imgPath) {
        return decodeFileWithInSampleSize(imgPath, TARGET_WIDTH);
    }

    /**
     * 读取图片
     *
     * @param imgPath     图片路径
     * @param targetWidth 期望宽度
     * @return
     */
    public static Bitmap decodeFileWithInSampleSize(String imgPath, int targetWidth) {
        return decodeFileWithInSampleSize(imgPath, targetWidth, new BitmapFactory.Options());
    }

    /**
     * *读取图片
     *
     * @param imgPath     图片路径
     * @param targetWidth 期望宽度
     * @param bmpOps      采样选择
     * @return
     */
    public static Bitmap decodeFileWithInSampleSize(String imgPath, int targetWidth, BitmapFactory.Options bmpOps) {

        bmpOps.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, bmpOps);

        int inSampleSize = 1;
        int bmpWidth = bmpOps.outWidth;

        if (bmpWidth > targetWidth) {
            inSampleSize = bmpWidth / targetWidth;
        }

        bmpOps.inSampleSize = inSampleSize;
        bmpOps.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(imgPath, bmpOps);

    }

    /**
     * jpeg图片压缩
     *
     * @param bitmap   图片
     * @param fileName 文件名
     * @return 1：成功 -1：失败
     */
    public static int compressBitmapWithNative(Bitmap bitmap, String fileName) {
        return compressBitmapWithNative(bitmap, DEFAULT_QUALITY, fileName);
    }

    /**
     * jpeg图片压缩
     *
     * @param bitmap   图片
     * @param quality  压缩质量
     * @param fileName 文件名
     * @return 1：成功 -1：失败
     */
    public static int compressBitmapWithNative(Bitmap bitmap, int quality, String fileName) {
        if (bitmap.getConfig() != Bitmap.Config.ARGB_8888) {
            Bitmap resultBmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(resultBmp);
            Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            canvas.drawBitmap(bitmap, null, rect, null);
            int result = compressBitmap(bitmap, quality, fileName);
            resultBmp.recycle();
            return result;
        } else {
            return compressBitmap(bitmap, quality, fileName);
        }
    }

    /**
     * jpeg图片压缩
     *
     * @param bitmap   图片
     * @param quality  压缩质量
     * @param fileName 文件名
     * @return 1：成功 -1：失败
     */
    private native static int compressBitmap(Bitmap bitmap, int quality, String fileName);

}
