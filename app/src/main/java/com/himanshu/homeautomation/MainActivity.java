package com.himanshu.homeautomation;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private ImageView homeLocationImageView;
    private TextView homeLocationTextView;

    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private Switch relay1Switch;
    private Switch relay2Switch;
    private Switch relay3Switch;
    private Switch relay4Switch;


    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private double latitude;
    private double longitude;

    private double homeLatitude;
    private double homeLongitude;

    private boolean homeLocationSet = false;
    private boolean homeLocationClicked = false;

    private TextView homeLatitudeTV;
    private TextView homeLongitudeTV;
    private TextView latituteTV;
    private TextView longitudeTV;
    private TextView distanceInMeterTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        homeLocationImageView = findViewById(R.id.content_main_home_location_image_view);
        homeLocationTextView = findViewById(R.id.content_main_home_location_text_view);

        relay1Switch = findViewById(R.id.content_main_relay_1_switch);
        relay2Switch = findViewById(R.id.content_main_relay_2_switch);
        relay3Switch = findViewById(R.id.content_main_relay_3_switch);
        relay4Switch = findViewById(R.id.content_main_relay_4_switch);

        homeLatitudeTV = findViewById(R.id.content_main_home_latitude_text_view);
        homeLongitudeTV = findViewById(R.id.content_main_home_longitude_text_view);
        latituteTV = findViewById(R.id.content_main_latitude_text_view);
        longitudeTV = findViewById(R.id.content_main_longitude_text_view);
        distanceInMeterTV = findViewById(R.id.content_main_distance_from_home_text_view);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        databaseReference.child("RELAY1").addValueEventListener(relay1Listener);
        databaseReference.child("RELAY2").addValueEventListener(relay2Listener);
        databaseReference.child("RELAY3").addValueEventListener(relay3Listener);
        databaseReference.child("RELAY4").addValueEventListener(relay4Listener);

        relay1Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    databaseReference.child("RELAY1").setValue(1);
                } else {
                    databaseReference.child("RELAY1").setValue(0);
                }
            }
        });

        relay2Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    databaseReference.child("RELAY2").setValue(1);
                } else {
                    databaseReference.child("RELAY2").setValue(0);
                }
            }
        });

        relay3Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    databaseReference.child("RELAY3").setValue(1);
                } else {
                    databaseReference.child("RELAY3").setValue(0);
                }
            }
        });

        relay4Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    databaseReference.child("RELAY4").setValue(1);
                } else {
                    databaseReference.child("RELAY4").setValue(0);
                }
            }
        });


        boolean isServicesOK = isServicesOK();

        if (homeLocationImageView != null) {
            if (isServicesOK) {
                homeLocationImageView.setOnClickListener(this);
            }
        }

        if (homeLocationTextView != null) {
            if (isServicesOK) {
                homeLocationTextView.setOnClickListener(this);
            }
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null){
                    return;
                }
                else{
                    latitude = locationResult.getLastLocation().getLatitude();
                    longitude = locationResult.getLastLocation().getLongitude();

                    longitudeTV.setText("Longitude - "+longitude);
                    latituteTV.setText("Latitude - "+latitude);

                    if(!homeLocationClicked) {
                        homeLatitude = latitude;
                        homeLongitude = longitude;
                    }

                    Log.i("Locationxd","Latitude "+latitude+" Longitude "+longitude);
                    Log.i("Locationxd", "Home Latitude "+homeLatitude+" Longitude "+homeLongitude);


                    if(homeLocationSet){
                        turnOffRelays(latitude,longitude);
                    }
                }
            }
        };

        locationRequest = LocationRequest.create();
        locationRequest.
                setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).
                setInterval(2000).
                setFastestInterval(2000).
                setNumUpdates(10000);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,null);
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_manage){

        }

        if(id == R.id.nav_send){
            startActivity(new Intent(this,Splash.class));
            finish();
            FirebaseAuth.getInstance().signOut();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v != null){
            if(homeLocationImageView.getId() == v.getId()){
                setHomeLocation();
            }else if(homeLocationTextView.getId() == v.getId()){
                setHomeLocation();
            }
        }
    }

    public boolean isServicesOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this,"You cant make maps request",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private ValueEventListener relay1Listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                int value = dataSnapshot.getValue(Integer.class);
                if(value == 0){
                    relay1Switch.setChecked(false);
                }else if(value == 1){
                    relay1Switch.setChecked(true);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener relay2Listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                int value = dataSnapshot.getValue(Integer.class);
                if(value == 0){
                    relay2Switch.setChecked(false);
                }else if(value == 1){
                    relay2Switch.setChecked(true);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener relay3Listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                int value = dataSnapshot.getValue(Integer.class);
                if(value == 0){
                    relay3Switch.setChecked(false);
                }else if(value == 1){
                    relay3Switch.setChecked(true);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener relay4Listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                int value = dataSnapshot.getValue(Integer.class);
                if(value == 0){
                    relay4Switch.setChecked(false);
                }else if(value == 1){
                    relay4Switch.setChecked(true);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void setHomeLocation(){
        homeLocationSet = true;
        homeLocationClicked = true;
        homeLongitudeTV.setText("Longitude - "+homeLongitude);
        homeLatitudeTV.setText("Latitude - "+homeLatitude);
    }

    private float computeDistance(double latitude,double longitude){
        float[] distance = new float[2];
        Location.distanceBetween(homeLatitude, homeLongitude, latitude, longitude, distance);
        return distance[0];
    }

    private void turnOffRelays(double latitude,double longitude){
        float distance = computeDistance(latitude,longitude);
        distanceInMeterTV.setText(distance+"m");
        Log.i("Locationxd","Distance " +distance);
        if(distance > 10f){
            homeLocationSet = false;
            homeLocationClicked = false;
            if(relay1Switch.isChecked()){
                relay1Switch.setChecked(false);
            }
            if(relay2Switch.isChecked()){
                relay2Switch.setChecked(false);
            }
            if(relay3Switch.isChecked()){
                relay3Switch.setChecked(false);
            }
            if(relay4Switch.isChecked()){
                relay4Switch.setChecked(false);
            }
        }
    }

}

