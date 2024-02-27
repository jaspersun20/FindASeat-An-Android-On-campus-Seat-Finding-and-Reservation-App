package com.example.finaaseat.map_black_box_test;
import android.content.Context;
import android.content.SharedPreferences;


import static android.app.PendingIntent.getActivity;
import static android.content.Context.MODE_PRIVATE;
import static android.os.SystemClock.sleep;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyBelow;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;


import static org.hamcrest.CoreMatchers.allOf;


import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.finaaseat.MainActivity;
import com.example.finaaseat.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class mapBlackBoxTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<MainActivity>(MainActivity.class);



    public void login(){
        onView(withId(R.id.uscid_login)).perform(click(),typeText("1111111111"),pressBack());
        // sleep(1000);

         onView(withId(R.id.password_login)).perform(click(),typeText("123123"),pressBack());
         onView(allOf(withId(R.id.password_login),withText("123123"))).check(matches(isDisplayed()));
        sleep(2000);
         onView(withId(R.id.register_button)).perform(click());

    }

    //check if the map displayed
    @Test
    public void detailIsBelowMap(){
        onView(withId(R.id.maps)).check(matches(isDisplayed()));
    }

    //check the default display is lvl
    @Test
    public void defaultBuilding(){
        onView(allOf(withId(R.id.buildingName),withText("Leavey Library"))).check(matches(isDisplayed()));
    }

    //since the user hasn't login, if we click reserve it show jump to the login page
    @Test
    public void checkUserLogin(){
        onView(withId(R.id.reserveButton)).perform(click());
        onView(withId(R.id.uscid_login)).check(matches(isDisplayed()));
    }

   //check after login, the user could make reservation
    @Test
    public void checkToReservation(){
        onView(withId(R.id.reserveButton)).perform(click());
        login();
        onView(withId(R.id.reserveButton)).perform(click());
        sleep(2000);
       onView(withId(R.id.Indoor_Availability)).check(matches(isDisplayed()));
    }

    //check we could reserve on the right building
    @Test
    public void checkIstheRightBuildingToReserve(){
        onView(withId(R.id.reserveButton)).perform(click());
        login();
        onView(withId(R.id.reserveButton)).perform(click());
        sleep(5000);
        onView(allOf(withId(R.id.buildingNameReservation),withText("LVL"))).check(matches(isDisplayed()));

    }


}
