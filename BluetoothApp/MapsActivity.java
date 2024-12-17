package com.example.gpsapp;

import android.location.Location;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import com.example.gpsapp.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;

/* loaded from: classes3.dex */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private ActivityMapsBinding binding;
    private GoogleMap mMap;
    List<Location> savedLocations;

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMapsBinding inflate = ActivityMapsBinding.inflate(getLayoutInflater());
        this.binding = inflate;
        setContentView(inflate.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        MyApplication myApplication = (MyApplication) getApplicationContext();
        this.savedLocations = myApplication.getMyLocations();
    }

    @Override // com.google.android.gms.maps.OnMapReadyCallback
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        for (Location location : this.savedLocations) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Lat: " + location.getLatitude() + "Lon: " + location.getLongitude());
            this.mMap.addMarker(markerOptions);
        }
    }
}