package com.example.serwe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationRestaurantActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    double latitude, longitude;
    double destLat, destLong;

    Button getDirection;
    TextView distance, duration;
    LinearLayout linearLayoutText;

    private final int REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;
    private SupportMapFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_restaurant);

        linearLayoutText = findViewById( R.id.layoutToHide );
        linearLayoutText.setVisibility( View.GONE );


        distance = findViewById( R.id.distance_text );
        duration = findViewById( R.id.duration_text );




        getUserLocation();

        if(!checkPermission())
        {
            requestPermission();
        }
        else
        {
            fusedLocationProviderClient.requestLocationUpdates( locationRequest, locationCallback, Looper.myLooper() );
        }

        FragmentManager fm = getSupportFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById( R.id.mapDirection);


        fragment.getMapAsync( this );


        getDirection = findViewById( R.id.buttongetDirection );
        getDirection.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object[] dataTransfer;

                String direction_url = getDirectionUrl( latitude, longitude, destLat, destLong );
                Log.i("MainActivity", direction_url);
                dataTransfer = new Object[6];
                dataTransfer[0] = mMap;
                dataTransfer[1] = direction_url;

                dataTransfer[2] = new LatLng( destLat, destLong );
                dataTransfer[3] = new LatLng( latitude, longitude );
                dataTransfer[4] = distance;
                dataTransfer[5] = duration;

//                GetDirectionData getDirectionData = new GetDirectionData();
//                getDirectionData.execute( dataTransfer );
//                linearLayoutText.setVisibility( View.VISIBLE );
//                getDirection.setVisibility( View.GONE );
//                //Toast.makeText( this, "Distance", Toast.LENGTH_SHORT ).show();

            }
        } );
    }

    private String getDirectionUrl(double latitude, double longitude, double destlatitude, double destlongitude)
    {
        StringBuilder placeUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?"  );
        placeUrl.append( "origin=" + latitude + "," + longitude );
        placeUrl.append( "&destination=" + destlatitude + "," + destlongitude );

        //placeUrl.append( "&keyword=cruise" );
        placeUrl.append( "&key=" + getString(R.string.api_key_class ));
        return placeUrl.toString();
    }

//    private void getNoteLocation() {
//
//        notes = getIntent().getParcelableExtra( "mapdata" );
//
//        LatLng userLocation = new LatLng( notes.getLat(), notes.getLng());
//        destLat = notes.getLat();
//        destLong = notes.getLng();
//
//        Toast.makeText( getApplicationContext(), String.valueOf(  userLocation.latitude), Toast.LENGTH_SHORT ).show();
//
//        CameraPosition cameraPosition = CameraPosition.builder()
//                .target( userLocation )
//                .zoom( 15 )
//                .bearing( 0 )
//                .tilt( 45 )
//                .build();
//        mMap.animateCamera( CameraUpdateFactory.newCameraPosition( cameraPosition ) );
//        mMap.addMarker( new MarkerOptions().position( userLocation )
//                .title( "Note Location" )
//                .icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE) ));
//
//
//
//    }
//

    private Boolean checkPermission()
    {
        int permissionState = ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION );
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE );

    }

    private void getUserLocation()
    {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( this);
        locationRequest = new LocationRequest();
        locationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );
        locationRequest.setInterval( 5000 );
        locationRequest.setFastestInterval( 3000 );
        locationRequest.setSmallestDisplacement( 10 );
        setHomeMarker();
        //setFavouriteMarkers();


    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//
//        mMap = googleMap;
//
//    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType( GoogleMap.MAP_TYPE_NORMAL );
        mMap.setMyLocationEnabled(true);
        //getNoteLocation();

        mMap.setOnMapClickListener( new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                linearLayoutText.setVisibility( View.GONE );
                getDirection.setVisibility( View.VISIBLE );

                //adding home marker again
                LatLng userLocation = new LatLng( latitude, longitude);
                mMap.addMarker( new MarkerOptions().position( userLocation )
                        .title( "Your Location" ));
                //getNoteLocation();
            }
        } );


    }

    private void setHomeMarker(){
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations())
                {
                    //new
                    LatLng userLocation = new LatLng( location.getLatitude(), location.getLongitude());
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    CameraPosition cameraPosition = CameraPosition.builder()
                            .target( userLocation )
                            .zoom( 15 )
                            .bearing( 0 )
                            .tilt( 45 )
                            .build();
                    mMap.animateCamera( CameraUpdateFactory.newCameraPosition( cameraPosition ) );
                    mMap.addMarker( new MarkerOptions().position( userLocation )
                            .title( "Your Location" ));




                    // .icon( BitmapDescriptorFactory.fromResource( R.drawable.icon ) ));


                }
            }
        };
    }


}
