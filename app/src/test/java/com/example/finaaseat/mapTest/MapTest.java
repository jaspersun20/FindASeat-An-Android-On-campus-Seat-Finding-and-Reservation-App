package com.example.finaaseat.mapTest;

import android.graphics.Color;

import com.example.finaaseat.Building;
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


public class MapTest extends TestCase {

    private List<Building> buildingList;

    private Building currBuilding;

    @Before
    public void setup(){

        buildingList = new ArrayList<>();

        Polygon lvlPolygon = null;


        Building buildingLvl = new Building("Leavey Library","https://libraries.usc.edu/sites/default/files/styles/16_9_large_2x/public/2019-07/leavey-dsc_0106.jpg?itok=5SPgA4w-","651 W 35th St, Los Angeles, CA 90089",150,18,6,lvlPolygon);
        currBuilding= buildingLvl;
        buildingList.add(buildingLvl);


        Polygon dohenyPolygon = null;

        Building buildingDoheny = new Building("Doheny Memorial Library","https://libraries.usc.edu/sites/default/files/styles/16_9_large_2x/public/2019-08/dml-front.jpg?itok=nPHy_km1","3550 Trousdale Pkwy, Los Angeles, CA 90089",150,17,9,dohenyPolygon);
        buildingList.add(buildingDoheny);


        Polygon thhPolygon = null;


        Building buildingThh = new Building("Taper Hall","https://www.hmdb.org/Photos4/467/Photo467523.jpg?412019103900PM","3501 Trousdale Pkwy, Los Angeles, CA 90089",150,21,9,thhPolygon);
        buildingList.add(buildingThh);


        Polygon dmcPolygon = null;

        Building buildingDmc = new Building("Center for International and Public Affairs","https://www.hmdb.org/Photos7/739/Photo739882.jpg?86202314100AM","3518 Trousdale Pkwy, Los Angeles, CA 90089",150,21,9,dmcPolygon);
        buildingList.add(buildingDmc);

        Polygon sosPolygon = null;

        Building buildingSos = new Building("Social Sciences Building","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ-QC5GgyMBwXtPTm-J7RK3bwIkheqtpcNp0U_iBUcIPim4GoA0JHw_gaWZk9XRzRwO30c&usqp=CAU","Allyson Felix Field, Los Angeles, CA 90089",150,18,9,sosPolygon);
        buildingList.add(buildingSos);

    }

    // check the default building is lvl
    @Test
    public void testOnDefaultBuilding() {
        setup();
        assertTrue(currBuilding!=null);
        assertTrue(currBuilding.getName().equals("Leavey Library"));
        assertTrue(currBuilding.getImg().equals("https://libraries.usc.edu/sites/default/files/styles/16_9_large_2x/public/2019-07/leavey-dsc_0106.jpg?itok=5SPgA4w-"));
        assertTrue(currBuilding.getLocation().equals("651 W 35th St, Los Angeles, CA 90089"));
        assertTrue(currBuilding.getCapacity()==150);
        assertTrue(currBuilding.getTimeOpen()==6);
        assertTrue(currBuilding.getTimeClose()==18);
    }


    // check if the buildingList has correct size
    @Test
    public void testOnBuildingList() {
        setup();
        assertTrue(buildingList.size()==5);

    }

    // check none of the building has an empty name
    @Test
    public void testBuildingNames() {
        setup();
        for(int i=0; i<buildingList.size();i++){
            assertTrue(!buildingList.get(i).getName().equals(""));
        }
    }

    // check if we could switch the current building
    @Test
    public void testSwitchCurrentBuilding() {
        setup();
        switchCurrent();
        assertTrue(currBuilding!=null);
        assertTrue(currBuilding.getName().equals("Doheny Memorial Library"));
        assertTrue(currBuilding.getImg().equals("https://libraries.usc.edu/sites/default/files/styles/16_9_large_2x/public/2019-08/dml-front.jpg?itok=nPHy_km1"));
        assertTrue(currBuilding.getLocation().equals("3550 Trousdale Pkwy, Los Angeles, CA 90089"));
        assertTrue(currBuilding.getCapacity()==150);
        assertTrue(currBuilding.getTimeOpen()==9);
        assertTrue(currBuilding.getTimeClose()==17);
    }

    public void switchCurrent(){
        currBuilding = buildingList.get(1);
    }

   // check if current building exist in the building list
    @Test
    public void checkBuildingExisted() {
        setup();
        String buildingName = currBuilding.getName();
        for(int i =0; i<buildingList.size();i++){
            if(buildingName==buildingList.get(i).getName()){
                assertTrue(buildingName.equals(currBuilding.getName()));
            }
        }

    }

    @Test
    //check if we could switch current building based on its name
    public void testUpdateBuildingLocation() {
        setup();
        switchCurrent("Taper Hall");
        assertTrue(checkBuilding("Taper Hall"));

    }
    public void switchCurrent(String name){
       for(int i=0; i<buildingList.size();i++){
           if(buildingList.get(i).getName().equals(name)){
               currBuilding = buildingList.get(i);
           }
       }
    }

    // based on name check if the currentBuilding is right with the corresponding name
    public boolean checkBuilding(String name){

        for(int i=0; i<buildingList.size();i++){
            if(buildingList.get(i).getName().equals(name)){
                Building curr = buildingList.get(i);
                if(!curr.getName().equals(currBuilding.getName())) return false;
                if(!curr.getImg().equals(currBuilding.getImg())) return false;
                if(!curr.getLocation().equals(currBuilding.getLocation())) return false;
                if(curr.getTimeOpen() != currBuilding.getTimeOpen()) return false;
                if(curr.getTimeClose() != currBuilding.getTimeClose())return false;
            }
        }
        return true;
    }

}