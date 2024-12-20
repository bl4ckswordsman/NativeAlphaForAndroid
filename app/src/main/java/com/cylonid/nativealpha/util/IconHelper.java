package com.cylonid.nativealpha.util;

import static com.cylonid.nativealpha.util.App.getAppContext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Base64;
import android.util.Log;
import com.caverock.androidsvg.SVG;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

    public static Bitmap loadSvg(String strUrl) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            InputStream is = con.getInputStream();

            // Read SVG content as string
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String svgContent = result.toString("UTF-8");

            // Save original SVG content
            saveCustomSvgToSharedPreferences(strUrl, svgContent);

            // Create bitmap for display
            SVG svg = SVG.getFromString(svgContent);
            Bitmap bitmap = Bitmap.createBitmap(STANDARD_ICON_SIZE, STANDARD_ICON_SIZE, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            svg.renderToCanvas(canvas);

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap loadBitmap(String strUrl) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            String contentType = con.getContentType();

            // Handle SVG content
            if (contentType != null && contentType.contains("svg")) {
                return loadSvg(strUrl);
            }

            // Handle regular image formats
            InputStream is = con.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            if (bitmap == null || bitmap.getWidth() < Const.FAVICON_MIN_WIDTH) return null;
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveCustomIconToSharedPreferences(Context context, String shortcutTitle, Bitmap bitmap) {
        if (bitmap != null) {
            // Resize bitmap to standard size for consistent display
            bitmap = Bitmap.createScaledBitmap(bitmap, STANDARD_ICON_SIZE, STANDARD_ICON_SIZE, true);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            String encodedBitmap = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            context.getSharedPreferences("custom_icons", Context.MODE_PRIVATE)
                    .edit()
                    .putString(shortcutTitle, encodedBitmap)
                    .apply();
        }
    }

    public static void saveCustomSvgToSharedPreferences(String shortcutTitle, String svgContent) {
        if (svgContent != null) {
            String encodedSvg = Base64.encodeToString(svgContent.getBytes(), Base64.DEFAULT);
            getAppContext().getSharedPreferences("custom_icons", Context.MODE_PRIVATE)
                    .edit()
                    .putString(shortcutTitle, encodedSvg)
                    .apply();
        }
    }
}
