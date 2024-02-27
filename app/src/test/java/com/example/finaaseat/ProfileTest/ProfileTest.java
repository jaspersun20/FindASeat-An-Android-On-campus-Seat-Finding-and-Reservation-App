package com.example.finaaseat.ProfileTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.finaaseat.Building;
import com.example.finaaseat.Profile;
import com.example.finaaseat.Reservation;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ProfileTest {
    private List<Profile.Reservations> ReservationList;
    Profile.User user = new Profile.User();
    Profile.Reservations res = new Profile.Reservations();

    @Before
    public void setup() {
        user.setTotalReservation(4);
        user.setCurrentDayReservation(2);
        user.setDate("11/25");
        user.setBuildingName("LVL");
        user.setTimeslot("11:00am");
        user.setUscid(1111111111);
    }
    @Test
    public void testOnRepeatedUSCID() {
        assertTrue(ReservationList==null);
        Profile.User newuser = new Profile.User();
        newuser.setTotalReservation(4);
        newuser.setCurrentDayReservation(2);
        newuser.setDate("11/26");
        newuser.setBuildingName("LVL");
        newuser.setTimeslot("11:00am");
        newuser.setUscid(1111111111);
        assertTrue(newuser.getRegistered()==1);
    }

    @Test
    public void testOnDateFormat() {
        assertTrue(ReservationList==null);
        res.setKey("2");
        res.setDate("11/27");
        res.setBuildingName("LVL");
        res.setTimeslot("2:00PM");
        assertTrue(Profile.changeDateFormat(res.getDate()).equals("November 27"));
    }

    @Test
    public void testOnPassedReservation() {
        assertTrue(ReservationList==null);
        res.setKey("2");
        res.setDate("12/10");
        res.setBuildingName("LVL");
        res.setTimeslot("2:00PM");
        assertFalse(res.isPastReservation());
    }
    @Test
    public void testOnReservationList() {
        Profile.Reservations res1 = new Profile.Reservations();
        Profile.Reservations res2 = new Profile.Reservations();
        Profile.Reservations res3 = new Profile.Reservations();
        Profile.Reservations res4 = new Profile.Reservations();
        Profile.Reservations res5 = new Profile.Reservations();
        res1.setKey("1");
        res1.setDate("12/11");
        res1.setBuildingName("LVL");
        res1.setTimeslot("2:00PM");
        res2.setKey("2");
        res2.setDate("12/12");
        res2.setBuildingName("LVL");
        res2.setTimeslot("2:00PM");
        res3.setKey("3");
        res3.setDate("12/13");
        res3.setBuildingName("LVL");
        res3.setTimeslot("2:00PM");
        res4.setKey("4");
        res4.setDate("12/14");
        res4.setBuildingName("LVL");
        res4.setTimeslot("2:00PM");
        res5.setKey("5");
        res5.setDate("12/15");
        res5.setBuildingName("LVL");
        res5.setTimeslot("2:00PM");
        user.getActiveReservationList().add(res1);
        user.getActiveReservationList().add(res2);
        user.getActiveReservationList().add(res3);
        user.getActiveReservationList().add(res4);
        user.getActiveReservationList().add(res5);
        res2.setCanceled(1);
        assertTrue(user.getTotalReservation() == 4);
    }

    @Test
    public void testOnDefaultImage() {
        Profile.User newUser = new Profile.User();
        newUser.setTotalReservation(4);
        newUser.setCurrentDayReservation(2);
        newUser.setDate("11/26");
        newUser.setBuildingName("LVL");
        newUser.setTimeslot("11:00am");
        newUser.setUscid(1111111111);
        assertTrue(user.getImageUrl() == "https://firebasestorage.googleapis.com/v0/b/findaseat-a81b4.appspot.com/o/trojan.jpg?alt=media&token=32d699ea-5fc8-41a5-9905-c87529b2e9b3");
    }











}
