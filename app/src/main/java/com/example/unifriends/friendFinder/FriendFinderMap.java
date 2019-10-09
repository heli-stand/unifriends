package com.example.unifriends.friendFinder;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.unifriends.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class FriendFinderMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;

    double[] selectedUserLocation;
    String selectedUserName;

    //redirect to verification step

    public void redirectToVerification(View view) {


        Intent intent = new Intent(this, Verification.class);
        startActivity(intent);
        finish();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // we have permission -> 0,0 every update

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_finder_map);

        //get values passed down of selected user from results

        Intent intent = getIntent();
        selectedUserLocation = intent.getDoubleArrayExtra("selectedUserLocation");
        selectedUserName = intent.getStringExtra("selectedUserName");


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Add a marker for selected user and move the camera

                Log.i("Location", location.toString());
               // Toast.makeText(FriendFinderMap.this, location.toString(), Toast.LENGTH_SHORT).show();
                LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(current).title("You are here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(current));

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //if we dont have permission, ask for it
            // request code , number that we can check at the other end to see which request was made

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


        } else {

            // you have permission

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            //LAST KNOWN LOCATION CODE
//
//            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//
//            if(lastKnownLocation != null) {
//                LatLng current = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
//                Log.i("last known location", current.toString());   //gets last known location successfully
//                mMap.clear();
//                mMap.addMarker(new MarkerOptions().position(current).title("You are here"));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
//            }
        }


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//                 LatLng selectedUser = new LatLng(selectedUserLocation[0], selectedUserLocation[2]);
//                mMap.addMarker(new MarkerOptions().position(selectedUser).title(selectedUserName+ " is here"));

    }
}
