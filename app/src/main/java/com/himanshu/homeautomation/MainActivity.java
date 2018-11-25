package com.himanshu.homeautomation;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        databaseReference.child("RELAY1").addValueEventListener(relay1Listener);
        databaseReference.child("RELAY2").addValueEventListener(relay2Listener);
        databaseReference.child("RELAY3").addValueEventListener(relay3Listener);
        databaseReference.child("RELAY4").addValueEventListener(relay4Listener);

        relay1Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    databaseReference.child("RELAY1").setValue(1);
                }else{
                    databaseReference.child("RELAY1").setValue(0);
                }
            }
        });

        relay2Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    databaseReference.child("RELAY2").setValue(1);
                }else{
                    databaseReference.child("RELAY2").setValue(0);
                }
            }
        });

        relay3Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    databaseReference.child("RELAY3").setValue(1);
                }else{
                    databaseReference.child("RELAY3").setValue(0);
                }
            }
        });

        relay4Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    databaseReference.child("RELAY4").setValue(1);
                }else{
                    databaseReference.child("RELAY4").setValue(0);
                }
            }
        });


        boolean isServicesOK = isServicesOK();

        if(homeLocationImageView != null){
            if(isServicesOK) {
                homeLocationImageView.setOnClickListener(this);
            }
        }

        if(homeLocationTextView != null){
            if(isServicesOK) {
                homeLocationTextView.setOnClickListener(this);
            }
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
                startActivity(new Intent(this,MapsActivity.class));
            }else if(homeLocationTextView.getId() == v.getId()){
                startActivity(new Intent(this,MapsActivity.class));
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
}
