package com.cylonid.nativefier;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShortcutHelper {
    private String strBaseUrl;
    private Context c;
    private WebApp d;
    private Bitmap bitmap;
    private ImageView uiFavicon;
    private CircularProgressBar uiProgressBar;
    private EditText uiTitle;
    private final static String USER_AGENT = "Mozilla/5.0 (Android 10; Mobile; rv:68.0) Gecko/68.0 Firefox/68.0";
    private String shortcut_title;
    private Button uiBtnPositive;
    //TreeMap<Icon width in px, URL>
    TreeMap<Integer, String> found_icons;


    public ShortcutHelper(WebApp d, Context c) {
        this.d = d;
        this.strBaseUrl = d.getBaseUrl();
        this.c = c;
        this.bitmap = null;
        this.shortcut_title = "";
        this.found_icons = new TreeMap<>();
    }

    public void fetchFaviconURL() {
        buildShortcutDialog();
        new FaviconURLFetcher().execute();
    }
    private void loadFavicon(String url) {
        Target target = new Target() {

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                showFailedMessage();
                uiProgressBar.setVisibility(View.INVISIBLE);
                uiBtnPositive.setEnabled(true);
                uiTitle.requestFocus();
            }

            @Override
            public void onBitmapLoaded(Bitmap loaded, Picasso.LoadedFrom from) {
                bitmap = loaded;
                uiFavicon.setImageBitmap(bitmap);
                uiProgressBar.setVisibility(View.GONE);
                uiFavicon.setVisibility(View.VISIBLE);
                uiBtnPositive.setEnabled(true);
                uiTitle.requestFocus();
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        };

        if (url != null) {
            Picasso.get().load(url).into(target);
        }
        else {
            showFailedMessage();
            uiProgressBar.setVisibility(View.INVISIBLE);
            uiBtnPositive.setEnabled(true);
            uiTitle.requestFocus();
        }
    }

    private void addShortcutToHomeScreen(Bitmap bitmap)
    {
        Intent intent = Utility.createWebViewIntent(d, c);

        IconCompat icon;
        if (bitmap != null)
            icon = IconCompat.createWithBitmap(bitmap);
        else
            icon = IconCompat.createWithResource(c, R.mipmap.native_alpha_shortcut);
        String final_title = uiTitle.getText().toString();
        if (final_title.equals(""))
            final_title = d.getTitle();

        if (ShortcutManagerCompat.isRequestPinShortcutSupported(c)) {

            ShortcutInfoCompat pinShortcutInfo = new ShortcutInfoCompat.Builder(c, final_title)
                    .setIcon(icon)
                    .setShortLabel(final_title)
                    .setLongLabel(final_title)
                    .setIntent(intent)
                    .build();
            ShortcutManagerCompat.requestPinShortcut(c, pinShortcutInfo, null);

        }

    }
    private void showFailedMessage() {
        Toast toast = Toast.makeText(c,"We could not retrieve an icon for the selected website.", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 100);
        toast.show();

    }

    private Integer getWidthFromIcon(String size_string) {
        int x_index = size_string.indexOf("x");
        String width = size_string.substring(0, x_index);

        return Integer.parseInt(width);
    }

    private void addHardcodedIcons() {
        if (strBaseUrl.contains("amazon"))
            found_icons.put(300, "https://s3.amazonaws.com/prod-widgetSource/in-shop/pub/images/amzn_favicon_blk.png");

        if (strBaseUrl.contains("oebb"))
            found_icons.put(192, "https://www.oebb.at/.resources/pv-2017/themes/images/favicons/android-chrome-192x192.png");

    }

    private void buildShortcutDialog() {
        LayoutInflater li = LayoutInflater.from(c);
        final View inflated_view = li.inflate(R.layout.shortcut_dialog, null);
        uiTitle = (EditText) inflated_view.findViewById(R.id.websiteTitle);
        uiFavicon = (ImageView) inflated_view.findViewById(R.id.favicon);
        uiProgressBar =  (CircularProgressBar)inflated_view.findViewById(R.id.circularProgressBar);

        final AlertDialog dialog = new AlertDialog.Builder(c)
                .setView(inflated_view)
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                uiBtnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                uiBtnPositive.setEnabled(false);
                uiBtnPositive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        addShortcutToHomeScreen(bitmap);
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }


    private class FaviconURLFetcher extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                //Connect to the website
                Document doc = Jsoup.connect(strBaseUrl).userAgent(USER_AGENT).followRedirects(true).get();
                Elements metaTags = doc.select("meta[http-equiv=refresh]");

                //Step 1: Check for META Redirect
                if (!metaTags.isEmpty())
                {
                    Element metaTag = metaTags.first();
                    String content = metaTag.attr("content");
                    Pattern pattern = Pattern.compile(".*URL='?(.*)$", Pattern.CASE_INSENSITIVE);
                    Matcher m = pattern.matcher(content);
                    String redirectUrl = m.matches() ? m.group(1) : null;
                    if (redirectUrl != null) {
                        strBaseUrl = redirectUrl;
                        doc = Jsoup.connect(strBaseUrl).followRedirects(true).get();
                    }
                }
                //Step 2: Check PWA manifest
                Elements manifest = doc.select("link[rel=manifest]");
                if (!manifest.isEmpty()) {
                        Element mf = manifest.first();
                        String data = Jsoup.connect(mf.absUrl("href")).ignoreContentType(true).execute().body();
                        try {
                            JSONObject json = new JSONObject(data);
                            JSONArray manifest_icons = json.getJSONArray("icons");

                            shortcut_title = json.getString("name");

                            String start_url = json.getString("start_url");
                            if (!start_url.isEmpty()) {
                                URL base_url = new URL(mf.absUrl("href"));
                                URL fl_url = new URL(base_url, start_url);
                                d.setBase_url(fl_url.toString());

                            }

                            for (int i = 0; i < manifest_icons.length(); i++) {
                                String icon_href = manifest_icons.getJSONObject(i).getString("src");
                                String sizes = manifest_icons.getJSONObject(i).getString("sizes");
                                Integer width = getWidthFromIcon(sizes);
                                URL base_url = new URL(mf.absUrl("href"));
                                URL full_url = new URL(base_url, icon_href);
                                found_icons.put(width, full_url.toString());
                            }
                        }
                        catch(JSONException e) {
                            e.printStackTrace();
                        }
                }
                //Step 3: Fallback to PNG icons
                else {

                    Element html_title = doc.selectFirst("title");
                    shortcut_title = html_title.text();
                    Elements icons = doc.select("link[rel=icon]");
                    //If necessary, use apple icons
                    if (icons.isEmpty()) {
                        Elements apple_icons = doc.select("link[rel=apple-touch-icon]");
                        Elements apple_icons_prec = doc.select("link[rel=apple-touch-icon-precomposed]");
                        icons.addAll(apple_icons);
                        icons.addAll(apple_icons_prec);
                    }
                    for (Element icon : icons) {

                        String icon_href = icon.absUrl("href");
                        String sizes = icon.attr("sizes");
                        if (!sizes.equals("")) {
                            Integer width = getWidthFromIcon(sizes);
                            found_icons.put(width, icon_href);
                        }
                        else
                            found_icons.put(1, icon_href);

                    }
                }
                addHardcodedIcons();
                if (!found_icons.isEmpty()) {
                    Map.Entry<Integer, String> best_fit = found_icons.lastEntry();
                    if (best_fit.getKey() < 96)
                        return null;
                    else
                        return best_fit.getValue();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

            @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (!shortcut_title.equals(""))
                uiTitle.setText(shortcut_title);
            else
                uiTitle.setText(d.getTitle());
            loadFavicon(result);

//            imageView.setImageBitmap(bitmap);
//            textView.setText(title);
//            progressDialog.dismiss();
        }
    }
}
