package com.example.travel_app;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static android.widget.Toast.LENGTH_SHORT;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    public ArrayList<String> lat_long;
    public ArrayList<String> places_id;
    public ArrayList<String> list_places;
    Double[] my_pos;
    public LocationManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        lat_long = new ArrayList<String>();
        places_id = new ArrayList<String>();
        list_places = new ArrayList<String>();
        my_pos = new Double[]{1.288849, 103.862845};


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        get_loc();
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

        //        my_pos[0]=1.288849;
//        my_pos[1]=103.862845;
    }

    void get_loc() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(MapsActivity.this, "Error", LENGTH_SHORT).show();
            return;
        }

        Location myLocation = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        my_pos[0] = myLocation.getLatitude();
        my_pos[1] = myLocation.getLongitude();
//        Toast.makeText(MapsActivity.this, "Before", LENGTH_SHORT).show();
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//                mMap.clear();
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Toast.makeText(MapsActivity.this, "Insufficient Permissions", LENGTH_SHORT).show();
                    return;
                }
                mMap.setMyLocationEnabled(true);
                LatLng me = new LatLng(my_pos[0], my_pos[1]);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 12));
                Toast.makeText(MapsActivity.this, "Your Location:"+location.getLatitude()+","+location.getLongitude(), LENGTH_SHORT).show();
                my_pos[0]=location.getLatitude();
                my_pos[1]=location.getLongitude();
                GetPlaces obj = new GetPlaces();
                obj.execute();
                lm.removeUpdates(this);
//                LatLng sydney = new LatLng(my_pos[0], my_pos[1]);
//                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(MapsActivity.this, "Enabled", LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(MapsActivity.this, "Disabled", LENGTH_SHORT).show();

            }
        });
//        Toast.makeText(MapsActivity.this, "After"+my_pos[0]+","+my_pos[1], LENGTH_SHORT).show();
    }

    public void next(View v){
        Button b=(Button)findViewById(R.id.next);
        Intent go=new Intent();
        go.setClass(this,list.class);
        go.putExtra("list_places", list_places);
        go.putExtra("places_id", places_id);
        startActivity(go);
    }

    private class GetPlaces extends AsyncTask<String,Void,String>{

        public String text;
        public GetPlaces() {
            text=null;
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                //Place your Google Places API Key in the api_key variable below
                String api_key="";
                URL endpoint = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?key="+api_key+"&location="+my_pos[0]+","+my_pos[1]+"&radius=5000&type=tourist_attraction");
                HttpsURLConnection myConnection = (HttpsURLConnection) endpoint.openConnection();
                if (myConnection.getResponseCode() == 200) {
                    // Success
                    // Further processing here
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                    JsonReader jsonReader = new JsonReader(responseBodyReader);

                    jsonReader.beginObject(); // Start processing the JSON object
                    while (jsonReader.hasNext())
                    { // Loop through all keys
                        String key = jsonReader.nextName(); // Fetch the next key
                        if (key.equals("results"))
                        { // Check if desired key
                            // Fetch the value as a String
//                            String value = jsonReader.nextString();
                            jsonReader.beginArray();
                            while(jsonReader.hasNext())
                            {
                                jsonReader.beginObject();
                                while(jsonReader.hasNext()){
                                    String n=jsonReader.nextName();
                                    if (n.equals("name")){
                                        text=jsonReader.nextString();
                                        list_places.add(text);
                                    }
                                    else if (n.equals("place_id")){
                                        text=jsonReader.nextString();
                                        places_id.add(text);
                                    }
                                    else if (n.equals("geometry")){
                                        jsonReader.beginObject();
                                        while(jsonReader.hasNext()){
                                            String tmp=jsonReader.nextName();
                                            if (tmp.equals("location"))
                                            {
                                                jsonReader.beginObject();
                                                String lat=jsonReader.nextName();
                                                lat=jsonReader.nextString();
                                                String lon=jsonReader.nextName();
                                                lon=jsonReader.nextString();
                                                lat_long.add(lat+","+lon);
                                                jsonReader.endObject();
                                            }
                                            else{
                                                jsonReader.skipValue();
                                            }
                                        }
                                        jsonReader.endObject();
                                    }
                                    else{
                                        jsonReader.skipValue();
                                    }
                                }
                                jsonReader.endObject();

                            }

                        } else {
                            jsonReader.skipValue(); // Skip values of other keys
                        }
                    }

                } else {
                    text="Invalid";
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return text;
        }

        @Override
        protected void onPostExecute(String val) {
//            Toast.makeText(MapsActivity.this, lat_long.get(0)+" Details to be shown", LENGTH_SHORT).show();
            LatLng t;
            for(int i=0;i<lat_long.size();i++){
                String[] coords=lat_long.get(i).split(",",2);
                t=new LatLng(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]) );
                mMap.addMarker(new MarkerOptions().position(t).title(list_places.get(i)));
            }
        }
    }



}