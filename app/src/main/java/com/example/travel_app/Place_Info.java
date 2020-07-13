package com.example.travel_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Place_Info extends AppCompatActivity {
    public String id;
    public String addr;
    public String tim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place__info);

        Intent c=getIntent();
        String name=c.getStringExtra("Name");
        id=c.getStringExtra("id");
        TextView n=(TextView)findViewById(R.id.name);
        n.setText(name);
        Button b=(Button)findViewById(R.id.next);
        b.setText("Map");
        GetPlaces obj = new GetPlaces();
        obj.execute();
    }
    public void next(View v){
        Intent c=new Intent();
        c.setClass(this,MapsActivity.class);
        startActivity(c);
    }


    private class GetPlaces extends AsyncTask<String,Void,String> {

        public String text;
        public GetPlaces() {
            text=null;
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                //Place your Google Places API Key in the api_key variable below
                String api_key="";
                URL endpoint = new URL("https://maps.googleapis.com/maps/api/place/details/json?key="+api_key+"&place_id="+id);
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
                        if (key.equals("result"))
                        {
                            jsonReader.beginObject();
                            while(jsonReader.hasNext())
                            {
                                String tmp=jsonReader.nextName();
                                if(tmp.equals("formatted_address")){
                                    addr=jsonReader.nextString();
                                }
                                else if(tmp.equals("opening_hours")){
                                    jsonReader.beginObject();
                                    while(jsonReader.hasNext()){
                                        String t=jsonReader.nextName();
                                        if(t.equals("weekday_text")){
                                            jsonReader.beginArray();
                                            while (jsonReader.hasNext()){
                                                if(tim==null){
                                                    tim=(String)jsonReader.nextString();
                                                }
                                                else{
                                                    tim=tim+"\n"+(String)jsonReader.nextString();
                                                }
                                            }
                                            jsonReader.endArray();
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
                        else
                        {
                            jsonReader.skipValue(); // Skip values of other keys
                        }
                    }

                }
                else {
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
            TextView ad=(TextView)findViewById(R.id.addr);
//            Toast.makeText(Place_Info.this, tim, Toast.LENGTH_SHORT).show();
            ad.setText(addr);

            TextView timin=(TextView)findViewById(R.id.timing);
            timin.setText(tim);

        }
    }

}