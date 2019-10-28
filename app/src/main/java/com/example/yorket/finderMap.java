package com.example.yorket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class finderMap extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
     private Button logoutFinderMapbtn,showTargetLocationbtn;
     FirebaseAuth mAuth;
     private LocationManager locationManager;
     private LocationListener locationListener;
     ArrayList<String> targets;
     private int c=0;
     ArrayList<String> keys;
     private LatLng passengerLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finder_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        logoutFinderMapbtn=findViewById(R.id.logoutFinderMap);
        mAuth = FirebaseAuth.getInstance();
        targets=new ArrayList<>();
        keys=new ArrayList<>();
      //  targets.clear();
        logoutFinderMapbtn.setOnClickListener(this);
        showTargetLocationbtn=findViewById(R.id.showTargetLocationbtn);
        locationManager=(LocationManager) getSystemService(LOCATION_SERVICE);
        showTargetLocationbtn.setOnClickListener(this);
        Toast.makeText(finderMap.this,"update",Toast.LENGTH_SHORT).show();



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

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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
        if (ContextCompat.checkSelfPermission(finderMap.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(finderMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        }
        else
        {//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location currentPassengerLocation;

            currentPassengerLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(currentPassengerLocation!=null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                updateCameraPosition(currentPassengerLocation);

            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1000&&grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {if(ContextCompat.checkSelfPermission(finderMap.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location currentPassengerLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            updateCameraPosition(currentPassengerLocation);
        }
        }

    }
    public void updateCameraPosition(Location pLocation)
    { // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

             passengerLocation = new LatLng(pLocation.getLatitude(), pLocation.getLongitude()); //getting potion of passenger
        Map<String,Object> hashMap=new HashMap<>();
        hashMap.put("latitude",passengerLocation.latitude );
        hashMap.put("longitude",passengerLocation.longitude);
        FirebaseDatabase.getInstance().getReference().child("my_users").child(mAuth.getCurrentUser().getUid()).child("finderLocation").updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               // if(task.isComplete())
                //    Toast.makeText(finderMap.this,"Location Sussesfully Updated",Toast.LENGTH_SHORT).show();
               // else
             //       Toast.makeText(finderMap.this,task.getException()+"",Toast.LENGTH_SHORT).show();
            }
        });

      //  loca(passengerLocation);
          //  mMap.clear();
            //Adding the marker to postion

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.logoutFinderMap:mAuth.signOut();
            finish();
            break;
            case R.id.showTargetLocationbtn:
             //  Toast.makeText(finderMap.this,c+"",Toast.LENGTH_SHORT).show();
                FirebaseDatabase.getInstance().getReference().child("my_users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot snapshot:dataSnapshot.getChildren())
                        {
                            for(DataSnapshot snapshot1:snapshot.child("usersData").getChildren())
                            {
                                String str=(String) snapshot1.child("userType").getValue() ;
                                if(str.equals("target"))
                                {
                                    if(snapshot1.child("userFinder").getValue().equals(mAuth.getCurrentUser().getEmail()))
                                    {
                                        LatLng loc=new LatLng((Double) snapshot.child("targetLocation").child("latitude").getValue(),(Double) snapshot.child("targetLocation").child("longitude").getValue());

                                            mMap.clear();
                                        try {
                                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                            Marker driverMarker = mMap.addMarker(new MarkerOptions().position(passengerLocation).title("Finder").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                            Marker PassengerMarker = mMap.addMarker(new MarkerOptions().position(loc).title(snapshot1.child("email").getValue().toString()));
                                            ArrayList<Marker> myMarkers = new ArrayList();
                                            myMarkers.add(driverMarker);
                                            myMarkers.add(PassengerMarker);
                                            for (Marker marker : myMarkers) {
                                                builder.include(marker.getPosition());
                                            }
                                            LatLngBounds bounds = builder.build();
                                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,10);
                                            mMap.animateCamera(cameraUpdate);
                                        }
                                        catch (Exception e)
                                        {
                                            Toast.makeText(finderMap.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(passengerLocation, 17));  //updating the map
                                        //mMap.addMarker(new MarkerOptions().position(passengerLocation).title("You are here")).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                  //     mMap.addMarker(new MarkerOptions().position(loc).title(snapshot1.child("email").getValue().toString()));
                                    }
                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
//                FirebaseDatabase.getInstance().getReference().child("my_users").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
//                        {
//                          for(String key:keys)
//                          {
//                              if(dataSnapshot1.getKey().equals(key));
//                              {String str=(String) dataSnapshot1.child("targetLocation").child("Email").getValue();
//                                  Toast.makeText(finderMap.this,str,Toast.LENGTH_SHORT).show();
////                               LatLng loc=new LatLng((Double) dataSnapshot1.child("targetLocation").child("latitude").getValue(),(Double) dataSnapshot1.child("targetLocation").child("longitude").getValue());
////                                  mMap.addMarker(new MarkerOptions().position(loc).title("Finder is here"));
//                              }
//                          }
//                        }
//                    }

//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//                break;

        }
    }

}
