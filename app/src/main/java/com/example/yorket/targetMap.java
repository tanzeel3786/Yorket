package com.example.yorket;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class targetMap extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
     private Button logoutTargetMapbtn,updateLocationbtn;
     private FirebaseAuth mAuth;
     private LocationManager locationManager;
     private LocationListener locationListener;
     Location location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        logoutTargetMapbtn=findViewById(R.id.logoutTargetMap);
        mAuth = FirebaseAuth.getInstance();
      //  updateLocationbtn=findViewById(R.id.updateLocation);
      //  updateLocationbtn.setOnClickListener(this);
        logoutTargetMapbtn.setOnClickListener(this);
        locationManager=(LocationManager) getSystemService(LOCATION_SERVICE);


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
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateCameraPosition(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (ContextCompat.checkSelfPermission(targetMap.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(targetMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2000);
        }
        else
        {locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location currentPassengerLocation;

            currentPassengerLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(currentPassengerLocation!=null) {
              //  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                updateCameraPosition(currentPassengerLocation);

            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==2000&&grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED)
        {if(ContextCompat.checkSelfPermission(targetMap.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location currentPassengerLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            updateCameraPosition(currentPassengerLocation);
        }
        }

    }

    public void updateCameraPosition(Location pLocation)
    { // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        LatLng passengerLocation = new LatLng(pLocation.getLatitude(), pLocation.getLongitude()); //getting potion of passenger

        Map<String,Object> hashMap=new HashMap<>();
        hashMap.put("latitude",passengerLocation.latitude );
        hashMap.put("longitude",passengerLocation.longitude);
        hashMap.put("Email",mAuth.getCurrentUser().getEmail());
        FirebaseDatabase.getInstance().getReference().child("my_users").child(mAuth.getCurrentUser().getUid()).child("targetLocation").updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
           if(task.isComplete())
               Toast.makeText(targetMap.this,"Location Sussesfully Updated",Toast.LENGTH_SHORT).show();
           else
               Toast.makeText(targetMap.this,task.getException()+"",Toast.LENGTH_SHORT).show();
            }
        });
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(passengerLocation, 17));  //updating the map
        mMap.addMarker(new MarkerOptions().position(passengerLocation).title("You are here"));  //Adding the marker to postion

    }
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));



    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.logoutTargetMap:mAuth.signOut();
                finish();
                break;

        }
    }
}
