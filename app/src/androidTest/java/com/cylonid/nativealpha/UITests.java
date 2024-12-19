package com.cylonid.nativealpha;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.web.webdriver.Locator;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.cylonid.nativealpha.model.DataManager;
import com.cylonid.nativealpha.model.WebApp;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.actionWithAssertions;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static androidx.test.espresso.web.model.Atoms.getCurrentUrl;
import static androidx.test.espresso.web.sugar.Web.onWebView;
import static androidx.test.espresso.web.webdriver.DriverAtoms.findElement;
import static androidx.test.espresso.web.webdriver.DriverAtoms.getText;
import static com.cylonid.nativealpha.TestUtils.dragFromTo;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;

import android.view.View;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UITests {
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);
//    public ActivityScenarioRule<MainActivity> scenarioRule = new ActivityScenarioRule<>(MainActivity.class);
    @Test
    public void addWebsite() {
        TestUtils.acceptLicense();
        onView(withId(R.id.websiteUrl)).perform(clearText(), typeText("github.com"));
        onView(withId(R.id.switchCreateShortcut)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        assertEquals(DataManager.getInstance().getWebApp(0).getBaseUrl(), "https://github.com");
        onView(allOf(withId(R.id.btnOpenWebview), isDisplayed())).perform(click());
    }

    @Test
    public void addMultipleWebsiteAndTestLoadedUrl() {
        initMultipleWebsites(List.of("github.com", "orf.at"));
        onView(TestUtils.getElementFromMatchAtPosition(withId(R.id.btnOpenWebview), 1)).perform(click());
        onWebView(Matchers.allOf(withId(R.id.webview))).withNoTimeout().check(webMatches(getCurrentUrl(), containsString("orf.at")));
    }

    @Test
    public void setCustomOrderOfWebapps() {
        initMultipleWebsites(List.of("github.com", "orf.at"));
        onView(TestUtils.getElementFromMatchAtPosition(withId(R.id.dragAnchor), 0)).perform(swipeDown());
        Matcher<View> anchor = TestUtils.getElementFromMatchAtPosition(withId(R.id.dragAnchor), 0);
 
        onView(anchor)
                .perform(actionWithAssertions(dragFromTo(500)));
        assertEquals(DataManager.getInstance().getWebApp(0).getOrder(), 1);

    }

    @Test
    public void startWebView() {
        initSingleWebsite("https://github.com");
        onView(allOf(withId(R.id.btnOpenWebview))).perform(click());
        onView(withId(R.id.webview)).check(matches(isDisplayed()));
    }

    @Test(expected = NoMatchingViewException.class)
    public void deleteWebsite() {
        initSingleWebsite("https://github.com");
        onView(allOf(withId(R.id.btnDelete))).perform(click());
        TestUtils.alertDialogAccept();

        onView(allOf(withId(R.id.btnDelete))).check(matches(not(isDisplayed()))); //Throws exception
    }

    @Test
    public void changeWebAppSettings() {
        initSingleWebsite("https://whatismybrowser.com/detect/are-third-party-cookies-enabled");
        onView(withId(R.id.btnSettings)).perform(click());
        onView(withId(R.id.switch3PCookies)).perform(scrollTo()).perform(click());
        onView(withId(R.id.btnSave)).perform(click());
        onView(allOf(withId(R.id.btnOpenWebview), isDisplayed())).perform(click());
        onWebView(Matchers.allOf(withId(R.id.webview))).withNoTimeout().withElement(findElement(Locator.ID, "detected_value")).check(webMatches(getText(), containsString("Yes")));

    }
    @Test
    public void badSSLAccept() {
        initSingleWebsite("https://untrusted-root.badssl.com/");
        onView(allOf(withId(R.id.btnOpenWebview), isDisplayed())).perform(click());
        TestUtils.waitForElementWithText(R.string.load_anyway);
        onView(withText(R.string.load_anyway)).perform(click());
        onWebView(Matchers.allOf(withId(R.id.webview))).withNoTimeout().withElement(findElement(Locator.ID, "content")).check(webMatches(getText(), containsString("untrusted-root")));
    }

    @Test(expected = java.lang.RuntimeException.class)
    public void badSSLDismiss() {
        initSingleWebsite("https://untrusted-root.badssl.com/");
        onView(allOf(withId(R.id.btnOpenWebview), isDisplayed())).perform(click());
        TestUtils.waitForElementWithText(android.R.string.cancel);
        onView(withText(android.R.string.cancel)).perform(click());
        onWebView(Matchers.allOf(withId(R.id.webview))).withTimeout(3, TimeUnit.SECONDS).withElement(findElement(Locator.ID, "content")).check(webMatches(getText(), containsString("untrusted-root")));

    }

    @Test
    public void openHTTPSite() {
        initSingleWebsite("http://httpforever.com/");
        onView(allOf(withId(R.id.btnOpenWebview), isDisplayed())).perform(click());
        TestUtils.waitForElementWithText(android.R.string.cancel);
        onView(withId(android.R.id.button2)).perform(scrollTo()).perform(click());

        onView(allOf(withId(R.id.btnOpenWebview), isDisplayed())).perform(click());
        TestUtils.waitForElementWithText(android.R.string.cancel);
        onView(withId(android.R.id.button1)).perform(scrollTo()).perform(click());
        onWebView(Matchers.allOf(withId(R.id.webview))).withTimeout(6, TimeUnit.SECONDS).check(webMatches(getCurrentUrl(), containsString("httpforever.com")));
    }


    private void initSingleWebsite(final String base_url) {
        activityTestRule.getActivity().runOnUiThread(() -> {
            DataManager.getInstance().addWebsite(new WebApp(base_url, DataManager.getInstance().getIncrementedID()));
            activityTestRule.getActivity().updateWebAppList();
        });

        TestUtils.acceptLicense();
        //Get rid of welcome message
        TestUtils.alertDialogDismiss();
    }

    private void initMultipleWebsites(final List<String> urls) {
        activityTestRule.getActivity().runOnUiThread(() -> {
            for (String base_url : urls) {
                DataManager.getInstance().addWebsite(new WebApp(base_url, DataManager.getInstance().getIncrementedID()));
            }
            activityTestRule.getActivity().updateWebAppList();
        });
        TestUtils.acceptLicense();
        //Get rid of welcome message
        TestUtils.alertDialogDismiss();
    }



}
