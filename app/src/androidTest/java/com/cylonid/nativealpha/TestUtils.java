package com.cylonid.nativealpha;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.any;

import android.view.View;

import androidx.annotation.CheckResult;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.test.espresso.AmbiguousViewMatcherException;
import androidx.test.espresso.NoMatchingRootException;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.matcher.ViewMatchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestUtils {


    public static void acceptLicense() {
        onView(withId(R.id.btnNewsConfirm)).perform(click());
    }

    public static void alertDialogAccept() {
        onView(withId(android.R.id.button1)).perform(click());
    }

    public static void alertDialogDismiss() {
        onView(withId(android.R.id.button2)).perform(click());
    }

    public static boolean viewIsDisplayed(int viewId) {
        final boolean[] isDisplayed = {true};
        onView(withId(viewId)).withFailureHandler((error, viewMatcher) -> isDisplayed[0] = false).check(matches(isDisplayed()));
        return isDisplayed[0];
    }


    public static void waitFor(final long ms) {
        final CountDownLatch signal = new CountDownLatch(1);

        try {
            signal.await(ms, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
    }

    // Custom ViewAction to perform drag
    public static ViewAction dragFromTo(final int amountInPixels) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isDisplayed(); // Constraints to ensure the view is displayed
            }

            @Override
            public String getDescription() {
                return "Drag from a position given by resource ID and move by the specified amount of pixels";
            }

            @Override
            public void perform(androidx.test.espresso.UiController uiController, View view) {
                // Start position coordinates
                CoordinatesProvider startCoordinates = v -> {
                    int[] location = new int[2];
                    v.getLocationOnScreen(location);
                    return new float[]{location[0] + v.getWidth() / 2f, location[1] + v.getHeight() / 2f};
                };

                // End position coordinates (move down by a specified amount of pixels)
                CoordinatesProvider endCoordinates = v -> {
                    int[] location = new int[2];
                    v.getLocationOnScreen(location);
                    return new float[]{location[0] + v.getWidth() / 2f, location[1] + v.getHeight() / 2f + amountInPixels};
                };

                // Perform swipe action
                new GeneralSwipeAction(Swipe.SLOW, startCoordinates, endCoordinates, Press.FINGER)
                        .perform(uiController, view);
            }
        };
    }

    public static Matcher<View> getElementFromMatchAtPosition(final Matcher<View> matcher, final int position) {
        return new BaseMatcher<View>() {
            int counter = 0;

            @Override
            public boolean matches(final Object item) {
                if (matcher.matches(item)) {
                    if (counter == position) {
                        counter++;
                        return true;
                    }
                    counter++;
                }
                return false;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("Element at hierarchy position " + position);
            }
        };
    }

    public static void waitForElementWithText(@IdRes int stringId) {

        ViewInteraction element;
        do {
            waitFor(500);

            //simple example using withText Matcher.
            element = onView(withText(stringId));

        } while (!MatcherExtension.exists(element));

    }

    public static AppCompatActivity getCurrentActivity() {
        final AppCompatActivity[] activity = new AppCompatActivity[1];
        onView(isRoot()).check((view, noViewFoundException) -> activity[0] = (AppCompatActivity) view.getContext());
        return activity[0];
    }

    private static class MatcherExtension {
        @CheckResult
        public static boolean exists(ViewInteraction interaction) {
            try {
                interaction.perform(new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return any(View.class);
                    }

                    @Override
                    public String getDescription() {
                        return "check for existence";
                    }

                    @Override
                    public void perform(UiController uiController, View view) {
                        // no op, if this is run, then the execution will continue after .perform(...)
                    }
                });
                return true;
            } catch (AmbiguousViewMatcherException ex) {
                // if there's any interaction later with the same matcher, that'll fail anyway
                return true; // we found more than one
            } catch (NoMatchingViewException | NoMatchingRootException ex) {
                return false;
            } // optional depending on what you think "exists" means

        }

    }
}
