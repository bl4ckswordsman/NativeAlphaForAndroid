package com.cylonid.nativealpha;

import com.cylonid.nativealpha.model.WebApp;
import com.cylonid.nativealpha.util.Const;
import com.cylonid.nativealpha.util.Utility;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UtilUnitTests {

    @org.junit.jupiter.api.Test
    public void getWidthFromHTMLElementString() {
        assertEquals((Integer)192, Utility.getWidthFromIcon("192x192"));
    }
    public void testShortcutHelper(String base_url, final String expected, final int result_index) {
        WebApp webapp = new WebApp(base_url, Integer.MAX_VALUE);
        ShortcutDialogFragment frag = ShortcutDialogFragment.newInstance(webapp);
        String[] result = frag.fetchWebappData();
        assertEquals(result[result_index], expected);
    }

    @Test
    public void faviconFromWebManifest() {
        testShortcutHelper("https://xda-developers.com", "https://www.xda-developers.com/android-chrome-512x512.png", Const.RESULT_IDX_FAVICON);
    }

    @Test
    public void faviconWithoutManifest() {
        testShortcutHelper("https://orf.at", "https://orf.at/mojo/1_4_1/storyserver//common/images/favicons/favicon-128x128.png", Const.RESULT_IDX_FAVICON);
    }

    @org.junit.jupiter.api.Test
    public void faviconNull() {
        testShortcutHelper("https://tugraz.at", null, Const.RESULT_IDX_FAVICON);
    }

    @org.junit.jupiter.api.Test
    public void faviconNonExistingSite() {
        testShortcutHelper("https://asdfasdfasdfasdf.asdfsdaf", null, Const.RESULT_IDX_FAVICON);
    }


    @org.junit.jupiter.api.Test
    public void getStartUrlFromWebManifest() {
        testShortcutHelper("https://online.tugraz.at", "https://online.tugraz.at/tug_online/ee/ui/ca2/app/desktop/#/login", Const.RESULT_IDX_NEW_BASEURL);
    }

    @org.junit.jupiter.api.Test
    public void getWebAppTitleFromManifest() {
        testShortcutHelper("https://online.tugraz.at", "TUGRAZonline Go", Const.RESULT_IDX_TITLE);
    }

}

