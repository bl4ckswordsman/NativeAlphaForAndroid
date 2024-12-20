package com.cylonid.nativealpha.util;

import static com.cylonid.nativealpha.util.App.getAppContext;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Base64;
import android.util.Log;

import androidx.collection.LruCache;

import com.caverock.androidsvg.SVG;
import com.cylonid.nativealpha.model.WebApp;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class IconHelper {

    public static final int STANDARD_ICON_SIZE = 192;
    private static final String PREFS_NAME = "custom_icons";
    private static final LruCache<String, Bitmap> memoryCache;

    static {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        memoryCache = new LruCache<>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static void handleWebAppIcon(Context context, WebApp webapp, Bitmap icon, boolean isWebView) {
        if (icon == null || webapp.getTitle() == null) return;

        if (icon == null || webapp.getTitle() == null) return;

        // Check if custom icon already exists
        String customIcon = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(webapp.getTitle(), null);
        if (customIcon != null) {
            // If custom icon exists, load it instead
            Bitmap savedIcon = loadIconFromPreferences(context, webapp.getTitle());
            if (savedIcon != null && isWebView && context instanceof Activity) {
                ((Activity)context).setTaskDescription(
                    new ActivityManager.TaskDescription(
                        webapp.getTitle(),
                        savedIcon
                    )
                );
            }
            return;
        }

        Bitmap scaledIcon = createSquareBitmap(icon);

        // Save icon
        saveCustomIconToSharedPreferences(context, webapp.getTitle(), scaledIcon);
        
        // Cache in memory
        memoryCache.put(webapp.getTitle(), scaledIcon);

        // Update task description if in WebView
        if (isWebView && context instanceof Activity) {
            ((Activity)context).setTaskDescription(
                new ActivityManager.TaskDescription(
                    webapp.getTitle(),
                    scaledIcon
                )
            );
        }
    }

    public static Bitmap loadOrFetchIcon(Context context, String url, String title) {
        // Try memory cache first
        Bitmap cachedIcon = memoryCache.get(title);
        if (cachedIcon != null) return cachedIcon;

        // Try stored preferences
        Bitmap storedIcon = loadIconFromPreferences(context, title);
        if (storedIcon != null) {
            memoryCache.put(title, storedIcon);
            return storedIcon;
        }

        // Fetch from network
        if (url != null && !url.isEmpty()) {
            Bitmap fetchedIcon = fetchIconFromUrl(url);
            if (fetchedIcon != null) {
                Bitmap scaledIcon = createSquareBitmap(fetchedIcon);
                saveCustomIconToSharedPreferences(context, title, scaledIcon);
                memoryCache.put(title, scaledIcon);
                return scaledIcon;
            }
        }

        return null;
    }

    private static Bitmap fetchIconFromUrl(String strUrl) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            String contentType = con.getContentType();

            if (contentType != null && contentType.contains("svg")) {
                return loadSvg(strUrl);
            }

            InputStream is = con.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            return (bitmap != null && bitmap.getWidth() >= Const.FAVICON_MIN_WIDTH) ? bitmap : null;
        } catch (Exception e) {
            Log.e("IconHelper", "Error fetching icon", e);
            return null;
        }
    }

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
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        }

        // Always return scaled bitmap
        return bitmap != null ? createSquareBitmap(bitmap) : null;
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
    

    public static void saveCustomIconToSharedPreferences(Context context, String shortcutTitle, Bitmap bitmap) {
        if (bitmap != null) {
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
            getAppContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putString(shortcutTitle, encodedSvg)
                    .apply();
        }
    }

    public static Bitmap createSquareBitmap(Bitmap bitmap) {
        if (bitmap == null) return null;
    
        // Calculate target dimensions preserving aspect ratio
        float scale = (float) STANDARD_ICON_SIZE / Math.max(bitmap.getWidth(), bitmap.getHeight());
        int targetWidth = Math.round(bitmap.getWidth() * scale);
        int targetHeight = Math.round(bitmap.getHeight() * scale);
    
        // Create square bitmap with transparent background
        Bitmap result = Bitmap.createBitmap(STANDARD_ICON_SIZE, STANDARD_ICON_SIZE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        
        // Scale and center the bitmap
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
        float left = (STANDARD_ICON_SIZE - targetWidth) / 2f;
        float top = (STANDARD_ICON_SIZE - targetHeight) / 2f;
        canvas.drawBitmap(scaledBitmap, left, top, null);
    
        return result;
    }
}
