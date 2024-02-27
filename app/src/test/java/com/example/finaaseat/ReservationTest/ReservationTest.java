package com.example.finaaseat.ReservationTest;

import junit.framework.TestCase;
import android.graphics.Color;
import android.util.Log;

import com.example.finaaseat.Building;
import com.example.finaaseat.Profile.Reservations;
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
import com.example.finaaseat.Reservation;
import com.example.finaaseat.Reservation;

import com.example.finaaseat.Profile;



public class ReservationTest extends TestCase {

    public Reservation.User user;
    public Reservation.Buildings buildings;
    @Before
    public void setup() {
        user = new Reservation.User();
        user.setTotalReservation(4);
        user.setCurrentDayReservation(2);
        user.setDate("11/25");
        user.setBuildingName("LVL");
        user.setTimeslot("11:00am");
        buildings = new Reservation.Buildings();
        buildings.setDate("11/25");
        buildings.setOperationHour("12:00pm-4:00pm");
        buildings.setCapacity(200);
        buildings.setCurrent_indoor_count(100);
        buildings.setCurrent_outdoor_count(100);


    }

    //checking if the updateUser function is working
    @Test
    public void testUpdateUser(){
        user = new Reservation.User();
        setup();
        String uscID = "1231231231";
        int new_total = user.updateUser(uscID, user.getBuildingName(), user.getDate(), user.getTimeslot(), user.getCurrentDayReservation(), user.getTotalReservation());
        assertEquals(5, new_total);
    }


    //checking if the updateBuilding function is working
    @Test
    public void testUpdatebBuilding(){
        buildings = new Reservation.Buildings();
        setup();
        String BuildingName = "LVL";
        buildings.setName(BuildingName);
        int new_available_count = buildings.updateBuilding_count(buildings.getName(), buildings.getTimeslot(), buildings.getDate(), "indoor", 100, 1);
        assertEquals(99, new_available_count);

    }

    @Test
    public void testEditReservation(){
        buildings = new Reservation.Buildings();
        user = new Reservation.User();
        setup();
        String uscID = "1231231231";
        String BuildingName = "LVL";
        buildings.setName(BuildingName);
        Boolean edit_building = buildings.editReservation_building(buildings.getName(), buildings.getTimeslot(), buildings.getDate(), "indoor", 100);
        Boolean edit_user = user.editReservation_user(uscID, user.getBuildingName(), user.getDate(), user.getTimeslot(), user.getCurrentDayReservation(), user.getTotalReservation());
        assertTrue(edit_building ==true);
        assertTrue(edit_user ==true);

    }

    //check if the selected time is within the operation hour
    @Test
    public void testOperationHour(){
        String timeSlot = "11:00am";
        String opening = "09:00am";
        String closing = "05:00pm";
        boolean result = Reservation.isWithinOperationHours(timeSlot, opening, closing);
        assertTrue(result ==true);
    }

    @Test
    public void testReachedMaximum(){
        user = new Reservation.User();
        setup();
        String uscID = "3563018064";
        Boolean overflow = user.reachMaximum(uscID, user.getBuildingName(), user.getDate(), user.getTimeslot(), user.getCurrentDayReservation(), user.getTotalReservation());
        assertTrue(overflow == false);

    }

}