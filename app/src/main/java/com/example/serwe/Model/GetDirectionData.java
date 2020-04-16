package com.example.serwe.Model;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.HashMap;

public class GetDirectionData extends AsyncTask<Object, String, String> {


    GoogleMap googleMap;
    LinearLayout mylayout;
    TextView distanceText, durationText;
    String directionData;
    String url;

    String distance;
    String duration;

    LatLng latLng;

    LatLng latLngUser;




    @Override
    protected String doInBackground(Object... objects) {

        googleMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        latLng = (LatLng) objects[2];
        latLngUser = (LatLng) objects[3];
        distanceText = (TextView) objects[4];
        durationText = (TextView) objects[5];
        try {
            directionData = FetchURL.readURL( url );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return directionData;



    }

    @Override
    protected void onPostExecute(String s) {
        HashMap<String, String> distanceData = null;
        DataParser distanceParser = new DataParser();
        distanceData = distanceParser.parseDistance(s);
        distance = distanceData.get("distance");
        duration = distanceData.get( "duration" );
        distanceText.setText( "Distance: " + distance );
        durationText.setText( "Duration " + duration );




        googleMap.clear();
        // create new marker with new title and snippet

        MarkerOptions options = new MarkerOptions().position( latLng )
                .draggable( true )
                .title( "Duration" + duration)
                .snippet( "Distance: " + distance )
                .icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE ));



        googleMap.addMarker( options );

        googleMap.addMarker( new MarkerOptions().position( latLngUser )
                .title( "Your Location" ));

        CameraPosition cameraPosition = CameraPosition.builder()
                .target( latLng )
                .zoom( 12 )
                .bearing( 0 )
                .tilt( 45 )
                .build();
        googleMap.animateCamera( CameraUpdateFactory.newCameraPosition( cameraPosition ) );





        String[] directionsList;
        DataParser parserDirection = new DataParser();
        directionsList = parserDirection.parseDirections(s);
        displayDirections(directionsList);


    }

    private void displayDirections(String[] directionsList) {

        int count = directionsList.length;
        for(int i = 0;i<count;i++)
        {
            PolylineOptions optionsline = new PolylineOptions()
                    .addAll( PolyUtil.decode( directionsList[i] ) )
                    .color( Color.BLUE)
                    .width(20);

            googleMap.addPolyline(optionsline);

        }


    }
}

