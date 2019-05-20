package com.example.traveltools.control;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by waynamigo on 18-8-17.
 */

public class PictureTool {
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getPicFromUri(Context context, Uri uri){
        if (Build.VERSION.SDK_INT >= 19) {
            return handleImageOnKitKat(context,uri);
        } else {
            return handleImageBeforeKitKat(context, uri);
        }
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String handleImageOnKitKat(Context context, Uri uri) {
        String imagePath=null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(context,contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(context,uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        return imagePath;
    }
    private String handleImageBeforeKitKat(Context context, Uri uri) {
        return  getImagePath(context,uri, null);

    }
    private String getImagePath(Context context, Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor =context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    public static Bitmap zoomBimtap(Bitmap src, int width, int height) {
        return Bitmap.createScaledBitmap(src, width, height, true);
    }
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
    public static Bitmap decodeSampledBitmapFromResource(String path, int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap= BitmapFactory.decodeFile(path,options);
        File outputFile=new File(path);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputFile);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    public static Bitmap showImage(String path){
        return BitmapFactory.decodeFile(path);
    }
}
