package com.cylonid.nativealpha.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;

import com.cylonid.nativealpha.R;
import com.cylonid.nativealpha.model.DataManager;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utility {
    private static final String TAG = "XXX";

    public static void deleteShortcuts(List<Integer> removableWebAppIds) {
        ShortcutManager manager = App.getAppContext().getSystemService(ShortcutManager.class);
        for (ShortcutInfo info : manager.getPinnedShortcuts()) {
            int id = info.getIntent().getIntExtra(Const.INTENT_WEBAPPID, -1);
            if (removableWebAppIds.contains(id)) {
                manager.disableShortcuts(List.of(info.getId()), App.getAppContext().getString(R.string.webapp_already_deleted));
            }
        }
    }

    public static void showToast(Activity a, String text) {
        showToast(a, text, Toast.LENGTH_LONG);
    }

    public static void showToast(Activity a, String text, int toastDisplayDuration) {
        Toast toast = Toast.makeText(a, text, toastDisplayDuration);
        toast.setGravity(Gravity.TOP, 0, 100);
        toast.show();
    }

    public static void setViewAndChildrenEnabled(View view, boolean enabled) {

        view.setClickable(enabled);
        if (enabled) {
            view.setAlpha(1.0f);
        } else {
            view.setAlpha(0.75f);
        }

        if (view instanceof ViewGroup viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewAndChildrenEnabled(child, enabled);
            }
        }
    }

    public static Long getTimeInSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat getHourMinFormat() {
        return new SimpleDateFormat("HH:mm");
    }

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat getDayHourMinuteSecondsFormat() {
        return new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
    }


    public static Calendar convertStringToCalendar(String str) {
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(Objects.requireNonNull(getHourMinFormat().parse(str)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public static boolean isInInterval(Calendar low, Calendar time, Calendar high) {
        //Bring timestamp with day_current + HH:mm => day_unixZero + HH:mm by parsing it again...
        Calendar middle = Calendar.getInstance();
        try {
            middle.setTime(Objects.requireNonNull(getHourMinFormat().parse(Utility.getHourMinFormat().format(time.getTime()))));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //CASE: If the end of our timespan is after midnight, add one day to the end date to get a proper span.
        if (high.before(low)) {
            high.add(Calendar.DATE, 1);
            if (middle.before(low)) {
                middle.add(Calendar.DATE, 1);
            }
        }
        return middle.after(low) && middle.before(high);
    }
//        System.out.println("Low: " + Utility.getDayHourMinuteSecondsFormat().format(low.getTime()));
//        System.out.println("Middle: " + Utility.getDayHourMinuteSecondsFormat().format(middle.getTime()));
//        System.out.println("High: " + Utility.getDayHourMinuteSecondsFormat().format(high.getTime()));
//        System.out.println("Is Before high: " + (middle.before(high)));
//        System.out.println("Is after low: " + (middle.after(low)));


    public static void Assert(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static Integer getWidthFromIcon(String size_string) {
        int x_index = size_string.indexOf("x");
        if (x_index == -1)
            x_index = size_string.indexOf("×");

        if (x_index == -1)
            return 1;
        String width = size_string.substring(0, x_index);

        return Integer.parseInt(width);
    }


    public static void applyUITheme() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            int id = DataManager.getInstance().getSettings().getThemeId();
            switch (id) {
                case 0:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    break;
                case 1:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
                case 2:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
            }
        }
    }

    public static boolean isNightMode(Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    public static void writeFileOnInternalStorage(Context mcoContext, String sFileName, String sBody) {

        try {
            File gpxfile = new File(mcoContext.getExternalFilesDir(null), sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showInfoSnackbar(AppCompatActivity activity, String msg, int duration) {

        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), msg, duration);

        snackbar.setAction(App.getAppContext().getString(android.R.string.ok), v -> snackbar.dismiss());

        View snackBarView = snackbar.getView();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 30, 0, 20);


        snackBarView.setLayoutParams(params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            snackBarView.setForceDarkAllowed(false);

        TextView tv = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setMaxLines(10);
        snackbar.setBackgroundTint(ResourcesCompat.getColor(App.getAppContext().getResources(), R.color.snackbar_background, null));
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();

    }

    public static boolean URLEqual(String left, String right) {
        if (left == null || right == null)
            return false;
        String stripped_left = left.replace("/", "").replace("www.", "");
        String stripped_right = right.replace("/", "").replace("www.", "");
        return stripped_left.equals(stripped_right);
    }

    public static String getFileNameFromDownload(String url, String content_disposition, String mime_type) {
        String file_name = null;
        if (content_disposition != null && !content_disposition.equals("")) {
            Pattern pattern = Pattern.compile("attachment; filename=\"(.*)\"; filename\\*=UTF-8''(.*)", Pattern.CASE_INSENSITIVE);
            Matcher m = pattern.matcher(content_disposition);
            file_name = m.matches() ? m.group(2) : null;
        }
        if (file_name == null) {
            file_name = URLUtil.guessFileName(url, content_disposition, mime_type);
        }

        return file_name;
    }

    @ColorInt
    public static int getThemeColor
            (
                    @NonNull final Context context,
                    @AttrRes final int attributeColor
            ) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attributeColor, value, true);
        return value.data;
    }

    public static String getProcessName(Context context) {
        if (context == null) return null;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }


}
