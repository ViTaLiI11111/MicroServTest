package com.example.ukrainianstylerestaurant.util;

import android.graphics.Bitmap;
import android.util.Base64;
import java.io.ByteArrayOutputStream;

public final class ImageUtil {
    public static String toBase64(Bitmap bmp) {
        if (bmp == null) return null; // бекенд сприйме як відсутнє
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
        return Base64.encodeToString(os.toByteArray(), Base64.NO_WRAP);
    }
    private ImageUtil() {}
}

