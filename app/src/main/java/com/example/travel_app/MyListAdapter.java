package com.example.travel_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyListAdapter extends ArrayAdapter<String> {

    List<String> list_places;
    List<String> pl_id;
    public Context context;
    int resource;
    SharedPreferences shared;
    public MyListAdapter(Context context, int resource, List<String> list_places,List<String> pl_id){
        super(context, resource, list_places);
        this.context = context;
        this.resource = resource;
        this.list_places = list_places;
        this.pl_id=pl_id;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(resource, null, false);
        TextView name =(TextView) view.findViewById(R.id.text);
        final Button save =(Button) view.findViewById(R.id.save);

        String current_place=list_places.get(position);
        name.setText(current_place);

        shared=context.getSharedPreferences("list_places", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=shared.edit();

        if(shared.contains("list")) {
            Set<String> yourSet = shared.getStringSet("list", null);
            if (yourSet.contains(list_places.get(position))==true){
                save.setText("Unsave");
            }
            else{
                save.setText("Save");
            }
        }
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(context, list_places.get(position)+" Will be saved/Unsaved",Toast.LENGTH_SHORT).show();
                String s= (String) save.getText();

                shared=context.getSharedPreferences("list_places", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit=shared.edit();
                //Store
                //Save
                if(s.equals("Save")){
                    if(shared.contains("list")){
                        Set<String> yourSet = shared.getStringSet("list", null);
                        yourSet.add(list_places.get(position));
                        edit.putStringSet("list", yourSet);
                        edit.commit();

                    }
                    else{
                        HashSet<String> t=new HashSet<String>();
                        t.add(list_places.get(position));
                        edit.putStringSet("list", t);
                        edit.commit();
                    }
                    save.setText("Unsave");
                }
                //Remove
                else{
                    if(shared.contains("list")) {
                        Set<String> yourSet = shared.getStringSet("list", null);
                        yourSet.remove(list_places.get(position));
                        if (yourSet.contains(list_places.get(position))==false){
                            Toast.makeText(context, "Removed",Toast.LENGTH_SHORT).show();
                        }
                        edit.putStringSet("list", yourSet);
                        edit.commit();
                    }
                    save.setText("Save");

                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, list_places.get(position)+" Details to be shown",Toast.LENGTH_SHORT).show();
                Intent go=new Intent();
                go.setClass(context,Place_Info.class);
                //Get place id from the place_id list in Trial.java
//                Trial obj=new Trial();
//                go.putExtra("id",obj.places_id.get(position));
                go.putExtra("Name",list_places.get(position));
                go.putExtra("id",pl_id.get(position));
                ContextCompat.startActivity(context,go,null);
            }
        });
        return view;
    }
}
