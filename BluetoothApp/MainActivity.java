package com.example.gpsapp;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.List;

/* loaded from: classes3.dex */
public class MainActivity extends AppCompatActivity {
    public static final int DEFAULT_UPDATE_INTERVAL = 1;
    public static final int FAST_UPDATE_INTERVAL = 1;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    Button btn_newWaypoint;
    Button btn_showMap;
    Button btn_showWaypointList;
    private LocationRequest.Builder builder;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallBack;
    LocationRequest locationRequest;
    List<Location> savedLocations;
    Button showBTDevices;
    Switch sw_gps;
    Switch sw_locationupdates;
    TextView tv_WaypointCount;
    TextView tv_accuracy;
    TextView tv_address;
    TextView tv_altitude;
    TextView tv_lat;
    TextView tv_lon;
    TextView tv_sensor;
    TextView tv_speed;
    TextView tv_updates;

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.tv_lat = (TextView) findViewById(R.id.tv_lat);
        this.tv_lon = (TextView) findViewById(R.id.tv_lon);
        this.tv_altitude = (TextView) findViewById(R.id.tv_altitude);
        this.tv_accuracy = (TextView) findViewById(R.id.tv_accuracy);
        this.tv_speed = (TextView) findViewById(R.id.tv_speed);
        this.tv_sensor = (TextView) findViewById(R.id.tv_sensor);
        this.tv_updates = (TextView) findViewById(R.id.tv_updates);
        this.tv_address = (TextView) findViewById(R.id.tv_address);
        this.sw_gps = (Switch) findViewById(R.id.sw_gps);
        this.sw_locationupdates = (Switch) findViewById(R.id.sw_locationsupdates);
        this.btn_newWaypoint = (Button) findViewById(R.id.btn_newWaypoint);
        this.btn_showWaypointList = (Button) findViewById(R.id.btn_showWaypointList);
        this.tv_WaypointCount = (TextView) findViewById(R.id.tv_countOfCrumbs);
        this.btn_showMap = (Button) findViewById(R.id.btn_showMap);
        this.showBTDevices = (Button) findViewById(R.id.showBTDevices);
        LocationRequest.Builder priority = new LocationRequest.Builder(1000L).setMinUpdateIntervalMillis(1000L).setPriority(102);
        this.builder = priority;
        this.locationRequest = priority.build();
        this.locationCallBack = new LocationCallback() { // from class: com.example.gpsapp.MainActivity.1
            @Override // com.google.android.gms.location.LocationCallback
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                MainActivity.this.updateUIValues(locationResult.getLastLocation());
            }
        };
        this.btn_newWaypoint.setOnClickListener(new View.OnClickListener() { // from class: com.example.gpsapp.MainActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MyApplication myApplication = (MyApplication) MainActivity.this.getApplicationContext();
                MainActivity.this.savedLocations = myApplication.getMyLocations();
                MainActivity.this.savedLocations.add(MainActivity.this.currentLocation);
            }
        });
        this.btn_showWaypointList.setOnClickListener(new View.OnClickListener() { // from class: com.example.gpsapp.MainActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, (Class<?>) ShowSavedLocationsList.class);
                MainActivity.this.startActivity(i);
            }
        });
        this.showBTDevices.setOnClickListener(new View.OnClickListener() { // from class: com.example.gpsapp.MainActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, (Class<?>) BluetoothPage.class);
                MainActivity.this.startActivity(i);
            }
        });
        this.btn_showMap.setOnClickListener(new View.OnClickListener() { // from class: com.example.gpsapp.MainActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, (Class<?>) MapsActivity.class);
                MainActivity.this.startActivity(i);
            }
        });
        this.sw_gps.setOnClickListener(new View.OnClickListener() { // from class: com.example.gpsapp.MainActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (MainActivity.this.sw_gps.isChecked()) {
                    MainActivity.this.builder.setPriority(100);
                    MainActivity mainActivity = MainActivity.this;
                    mainActivity.locationRequest = mainActivity.builder.build();
                    MainActivity.this.tv_sensor.setText("Using GPS sensors");
                    return;
                }
                MainActivity.this.builder.setPriority(102);
                MainActivity mainActivity2 = MainActivity.this;
                mainActivity2.locationRequest = mainActivity2.builder.build();
                MainActivity.this.tv_sensor.setText("Using Cell Towers + WIFI");
            }
        });
        this.sw_locationupdates.setOnClickListener(new View.OnClickListener() { // from class: com.example.gpsapp.MainActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (MainActivity.this.sw_locationupdates.isChecked()) {
                    MainActivity.this.startLocationUpdates();
                } else {
                    MainActivity.this.stopLocationUpdates();
                }
            }
        });
        updateGPS();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopLocationUpdates() {
        this.tv_updates.setText("Location is NOT being tracked");
        this.tv_lat.setText("Not tracking location");
        this.tv_lon.setText("Not tracking location");
        this.tv_speed.setText("Not tracking location");
        this.tv_address.setText("Not tracking location");
        this.tv_accuracy.setText("Not tracking location");
        this.tv_altitude.setText("Not tracking location");
        this.tv_sensor.setText("Not tracking location");
        this.fusedLocationProviderClient.removeLocationUpdates(this.locationCallBack);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startLocationUpdates() {
        this.tv_updates.setText("Location is being tracked");
        if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != 0 && ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
            return;
        }
        this.fusedLocationProviderClient.requestLocationUpdates(this.locationRequest, this.locationCallBack, (Looper) null);
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 99:
                if (grantResults[0] != 0) {
                    Toast.makeText(this, "This app requires permission in order to work properly", 0).show();
                    finish();
                    break;
                } else {
                    updateGPS();
                    break;
                }
        }
    }

    private void updateGPS() {
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient((Activity) this);
        if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0) {
            this.fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() { // from class: com.example.gpsapp.MainActivity.8
                @Override // com.google.android.gms.tasks.OnSuccessListener
                public void onSuccess(Location location) {
                    MainActivity.this.updateUIValues(location);
                    MainActivity.this.currentLocation = location;
                }
            });
        } else {
            requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 99);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUIValues(Location location) {
        this.tv_lat.setText(String.valueOf(location.getLatitude()));
        this.tv_lon.setText(String.valueOf(location.getLongitude()));
        this.tv_accuracy.setText(String.valueOf(location.getAccuracy()));
        if (location.hasAltitude()) {
            this.tv_altitude.setText(String.valueOf(location.getAltitude()));
        } else {
            this.tv_altitude.setText("Not Available");
        }
        if (location.hasSpeed()) {
            this.tv_speed.setText(String.valueOf(location.getSpeed()));
        } else {
            this.tv_speed.setText("Not Available");
        }
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            this.tv_address.setText(addresses.get(0).getAddressLine(0));
        } catch (Exception e) {
            this.tv_address.setText("Unable to get street address");
        }
        List<Location> list = this.savedLocations;
        if (list == null) {
            this.tv_WaypointCount.setText("0");
        } else {
            this.tv_WaypointCount.setText(Integer.toString(list.size()));
        }
    }
}