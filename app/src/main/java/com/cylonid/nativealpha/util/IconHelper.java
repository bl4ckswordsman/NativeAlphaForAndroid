package com.cylonid.nativealpha.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Base64;
import android.util.Log;
import com.caverock.androidsvg.SVG;

public class IconHelper {

    public static final int STANDARD_ICON_SIZE = 192;

    public static Bitmap loadIconFromPreferences(
        Context context,
        String title
    ) {
        String iconData = context
            .getSharedPreferences("custom_icons", Context.MODE_PRIVATE)
            .getString(title, null);

        if (iconData != null) {
            try {
                byte[] decodedData = Base64.decode(iconData, Base64.DEFAULT);
                return loadIconFromData(decodedData);
            } catch (Exception e) {
                Log.e("IconHelper", "Failed to load icon", e);
            }
        }
        return null;
    }

    public static Bitmap loadIconFromData(byte[] data) {
        Bitmap bitmap = null;

        // First try as SVG
        try {
            String content = new String(data, "UTF-8");
            if (
                content.trim().startsWith("<svg") ||
                content.trim().startsWith("<?xml")
            ) {
                SVG svg = SVG.getFromString(content);
                bitmap = Bitmap.createBitmap(
                    STANDARD_ICON_SIZE,
                    STANDARD_ICON_SIZE,
                    Bitmap.Config.ARGB_8888
                );
                Canvas canvas = new Canvas(bitmap);
                svg.renderToCanvas(canvas);
            }
        } catch (Exception e) {
            Log.d("IconHelper", "Not an SVG, trying as bitmap");
        }

        // If not SVG, try as regular bitmap
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        }

        // Ensure consistent size if bitmap exists
        if (bitmap != null) {
            bitmap = Bitmap.createScaledBitmap(
                bitmap,
                STANDARD_ICON_SIZE,
                STANDARD_ICON_SIZE,
                true
            );
        }

        return bitmap;
    }
}
