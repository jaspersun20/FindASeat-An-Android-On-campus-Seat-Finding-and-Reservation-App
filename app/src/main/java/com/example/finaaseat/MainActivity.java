package com.example.finaaseat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private UiSettings mUiSettings;
    private Polygon currentPolygon;

    private Building currBuilding;

    private List<Building> buildingList=new ArrayList<>();;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        //SupportMapFragment mapFragment =SupportMapFragment.newInstance(new GoogleMapOptions().mapId("44358275093249f0"));
        mapFragment.getMapAsync(this);

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


//         Clear cookie when logged out
        if (isTaskRoot()) {
            // If it's the first Activity of the app, clear the logged-in status
            editor.putString("userId", "");
            editor.putBoolean("isLoggedIn", false);
            editor.apply();
        }

        // Set the listener for the profile element
        View profileButton = findViewById(R.id.profile);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // To check if the user is logged in
                boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
                if (isLoggedIn) {
                    // Create an Intent to start the Profile activity
                    Intent intent = new Intent(MainActivity.this, Profile.class);
                    startActivity(intent);
                } else {
                    // The user is not logged in, redirect to the Login screen
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                    Toast Toast = null;
                    Toast.makeText(MainActivity.this, "Please log in to continue", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set the listener for the profile element
        View reserveButton = findViewById(R.id.reserveButton);
        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // To check if the user is logged in
                boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
                if (isLoggedIn) {
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    //When logging in
                    Log log = null;
                    log.d("buildingugiugsofige: ", currBuilding.getName());
                    myEdit.putString("buildingtxt", currBuilding.getName());
                    myEdit.apply();
                    // Create an Intent to start the Profile activity
                    Intent intent = new Intent(MainActivity.this, Reservation.class);
                    startActivity(intent);
                } else {
                    // The user is not logged in, redirect to the Login screen
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                    Toast Toast = null;
                    Toast.makeText(MainActivity.this, "Please log in to continue", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
//        map.setMinZoomPreference(6.0f);
//        map.setMaxZoomPreference(14.0f);

        CameraUpdate point = CameraUpdateFactory.newLatLng(new LatLng(34.02115, -118.28404));
        map.moveCamera(point);
        map.setMinZoomPreference(16);
        mUiSettings = map.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        // create buildings
        Polygon lvlPolygon = map.addPolygon(new PolygonOptions().clickable(true)
                .add(new LatLng(34.022051, -118.283222), new LatLng(34.021749, -118.283382),new LatLng(34.021394, -118.282566), new LatLng(34.021682,-118.282371)
                )
                .strokeColor(Color.BLUE)
                .fillColor(Color.BLUE)
        );
        lvlPolygon.setTag("lvl");
        currentPolygon = lvlPolygon;
        Building buildingLvl = new Building("Leavey Library","https://libraries.usc.edu/sites/default/files/styles/16_9_large_2x/public/2019-07/leavey-dsc_0106.jpg?itok=5SPgA4w-","651 W 35th St, Los Angeles, CA 90089",150,18,6,lvlPolygon);
        currBuilding= buildingLvl;
        buildingList.add(buildingLvl);

        UpdateBuilding();


        Polygon dohenyPolygon = map.addPolygon(new PolygonOptions().clickable(true)
                .add(new LatLng(34.020535, -118.283936), new LatLng(34.020210, -118.283205),new LatLng(34.019781, -118.283484), new LatLng(34.02010,-118.28420)
                )
                .strokeColor(Color.RED)
                .fillColor(Color.RED)
        );
        dohenyPolygon.setTag("doheny");

        Building buildingDoheny = new Building("Doheny Memorial Library","https://libraries.usc.edu/sites/default/files/styles/16_9_large_2x/public/2019-08/dml-front.jpg?itok=nPHy_km1","3550 Trousdale Pkwy, Los Angeles, CA 90089",150,17,9,dohenyPolygon);
        buildingList.add(buildingDoheny);


        Polygon thhPolygon = map.addPolygon(new PolygonOptions().clickable(true)
                .add(new LatLng(34.022752, -118.284404), new LatLng(34.021798, -118.285011), new LatLng(34.021640,-118.284657),new LatLng(34.022574, -118.283989)
                )
                .strokeColor(Color.RED)
                .fillColor(Color.RED)
        );
        thhPolygon.setTag("thh");

        Building buildingThh = new Building("Taper Hall","https://www.hmdb.org/Photos4/467/Photo467523.jpg?412019103900PM","3501 Trousdale Pkwy, Los Angeles, CA 90089",150,21,9,thhPolygon);
        buildingList.add(buildingThh);


        Polygon dmcPolygon = map.addPolygon(new PolygonOptions().clickable(true)
                .add(new LatLng(34.021563, -118.2844280), new LatLng(34.021205, -118.283443), new LatLng(34.020754,-118.283738),new LatLng(34.021109, -118.284562)
                )
                .strokeColor(Color.RED)
                .fillColor(Color.RED)
        );
        dmcPolygon.setTag("dmc");

        Building buildingDmc = new Building("Center for International and Public Affairs","https://www.hmdb.org/Photos7/739/Photo739882.jpg?86202314100AM","3518 Trousdale Pkwy, Los Angeles, CA 90089",150,21,9,dmcPolygon);
        buildingList.add(buildingDmc);

        Polygon sosPolygon = map.addPolygon(new PolygonOptions().clickable(true)
                .add(new LatLng(34.021728, -118.283816), new LatLng(34.02162, -118.28357), new LatLng(34.02142,-118.28370),new LatLng(34.02153, -118.28394)
                )
                .strokeColor(Color.RED)
                .fillColor(Color.RED)
        );
        sosPolygon.setTag("sos");

        Building buildingSos = new Building("Social Sciences Building","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ-QC5GgyMBwXtPTm-J7RK3bwIkheqtpcNp0U_iBUcIPim4GoA0JHw_gaWZk9XRzRwO30c&usqp=CAU","Allyson Felix Field, Los Angeles, CA 90089",150,18,9,sosPolygon);
        buildingList.add(buildingSos);

        Polygon wphPolygon = map.addPolygon(new PolygonOptions().clickable(true)
                .add(new LatLng(34.02210, -118.28387), new LatLng(34.02199, -118.28365), new LatLng(34.02181,-118.28377),new LatLng(34.02190, -118.28399)
                )
                .strokeColor(Color.RED)
                .fillColor(Color.RED)
        );
        wphPolygon.setTag("wph");

        Building buildingWph = new Building("Waite Phillips Hall","https://upload.wikimedia.org/wikipedia/commons/thumb/3/3c/Waite_Phillips_Hall%2C_University_of_Southern_California.jpg/1600px-Waite_Phillips_Hall%2C_University_of_Southern_California.jpg","3470 Trousdale Pkwy, Los Angeles, CA 90089",150,18,9,wphPolygon);
        buildingList.add(buildingWph);


        Polygon jffPolygon = map.addPolygon(new PolygonOptions().clickable(true)
                .add(new LatLng(34.01885, -118.28246), new LatLng(34.01876, -118.28226), new LatLng(34.01861,-118.28237),new LatLng(34.01869, -118.28256)
                )
                .strokeColor(Color.RED)
                .fillColor(Color.RED)
        );
        jffPolygon.setTag("jff");

        Building buildingJff = new Building("Fertitta Hall","https://today.usc.edu/wp-content/uploads/2016/09/Fertitta_toned_web2-1280x720.jpg","Registration Building, 610 Childs Way, Los Angeles, CA 90089",150,20,9,jffPolygon);
        buildingList.add(buildingJff);

        Polygon accPolygon = map.addPolygon(new PolygonOptions().clickable(true)
                .add(new LatLng(34.019420, -118.285652), new LatLng(34.01910, -118.28584), new LatLng(34.01894,-118.28549),new LatLng(34.01924, -118.28531)
                )
                .strokeColor(Color.RED)
                .fillColor(Color.RED)
        );
        accPolygon.setTag("acc");

        Building buildingAcc = new Building("Leventhal School of Accounting","https://dailytrojan.com/wp-content/uploads/2017/09/sabrinafinal-768x509.jpg","3660 Trousdale Pkwy #105, Los Angeles, CA 90089",150,17,9,accPolygon);
        buildingList.add(buildingAcc);

        Polygon salPolygon = map.addPolygon(new PolygonOptions().clickable(true)
                .add(new LatLng(34.01972, -118.28960), new LatLng(34.01952, -118.289116), new LatLng(34.01947,-118.28981),new LatLng(34.01926, -118.28932)
                )
                .strokeColor(Color.RED)
                .fillColor(Color.RED)
        );
        salPolygon.setTag("sal");


        Building buildingSal = new Building("Salvatori Computer Science Center","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSvAQF04_v0i9SKCbihkMo98gL-P3ncWQFt_qGK8kxQ1B6i8qm8ZrV1nc-Is-4kuEmLi0A&usqp=CAU","941 Bloom Walk, Los Angeles, CA 90089",150,21,9,salPolygon);
        buildingList.add(buildingSal);

        Polygon selPolygon = map.addPolygon(new PolygonOptions().clickable(true)
                .add(new LatLng(34.01978, -118.28888), new LatLng(34.01955, -118.28902), new LatLng(34.01938,-118.28865),new LatLng(34.01962, -118.28850)
                )
                .strokeColor(Color.RED)
                .fillColor(Color.RED)
        );
        selPolygon.setTag("sel");


        Building buildingSel = new Building("Science & Engineering Library","https://scontent-sjc3-1.xx.fbcdn.net/v/t1.18169-9/22148_314860229067_2300518_n.jpg?_nc_cat=107&ccb=1-7&_nc_sid=7a1959&_nc_ohc=v8yR6M55E4kAX9BLRb_&_nc_ht=scontent-sjc3-1.xx&oh=00_AfC21i2QQCDe27gAYO8eYFmkSJgFx1r-USGVLJdjxPEQdw&oe=656E4454","910 Bloom Walk, Los Angeles, CA 90089",150,21,9,selPolygon);
        buildingList.add(buildingSel);





        //map click listener
        map.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                if(polygon!=currentPolygon){
                    currentPolygon.setFillColor(Color.RED);
                    currentPolygon.setStrokeColor(Color.RED);
                    currentPolygon = polygon;
                    //fill in blue, update currentPolygon
                    currentPolygon.setStrokeColor(Color.BLUE);
                    currentPolygon.setFillColor(Color.BLUE);
                    UpdateBuilding();
                }

            }
        });

    }

    //function use to update the name of the building
    public void UpdateBuildingName(Building curr){



        TextView tv1 = (TextView)findViewById(R.id.buildingName);
        tv1.setText(curr.getName());

    }
    public void UpdateBuildingImage(Building curr){

        ImageView imageView = findViewById(R.id.buildingImage);
        Glide.with(this).load(curr.getImg()).into(imageView);

    }
    public void UpdateBuildingTime(Building curr){
        String time = String.valueOf(curr.getTimeOpen())+":00-"+String.valueOf(curr.getTimeClose())+":00";
        TextView tv1 = (TextView)findViewById(R.id.buildingTimeDetail);
        tv1.setText(time);
    }
    public void UpdateBuildingLocation(Building curr){
        TextView tv1 = (TextView)findViewById(R.id.buildingLocationDetail);
        tv1.setText(curr.getLocation());
    }

    public void UpdateBuildingCapacity(Building curr){
        TextView tv1 = (TextView)findViewById(R.id.buildingCapacityDetail);
        tv1.setText(String.valueOf(curr.getCapacity()));
    }

    public void UpdateBuilding(){
        Building curr = null;
        for(int i=0; i<buildingList.size(); i++){
            if(buildingList.get(i).getPolygon().getTag().toString()==currentPolygon.getTag().toString())
                curr = buildingList.get(i);
            currBuilding = curr;
        }
        UpdateBuildingName(curr);
        UpdateBuildingImage(curr);
        UpdateBuildingTime(curr);
        UpdateBuildingLocation(curr);
        UpdateBuildingCapacity(curr);

    }



}