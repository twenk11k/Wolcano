package com.wolcano.musicplayer.music.ui.activity;


import androidx.test.rule.ActivityTestRule;

import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.ui.activity.main.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void init() {

        activityTestRule.getActivity();
    }

    @Test
    public void bottomNavigationViewMenuOnlineTest() throws Throwable {

        onView(withId(R.id.nav_onlineplayer)).perform(click());
        onView(withId(R.id.action_searchM)).perform(click());
        onView(withId(R.id.searchTextView)).perform(typeText("test song"));
        onView(withId(R.id.searchTextView)).perform(pressImeActionButton());

        Thread.sleep(6000);

    }

}