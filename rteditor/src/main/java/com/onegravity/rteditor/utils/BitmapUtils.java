package com.onegravity.rteditor.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bumptech.glide.Glide;

public class BitmapUtils {

    public static Bitmap downloadToScaledBitmap(Context context, String url, int width, int height) {
        try {
            Bitmap bitmap = Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .submit()
                    .get();

            return Bitmap.createScaledBitmap(bitmap, width, height, true);
        } catch (Exception e) {
            return null;
        }
    }

}