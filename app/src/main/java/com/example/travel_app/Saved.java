package com.example.travel_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Saved extends AppCompatActivity {
    public ArrayList<String> pl_id;
    public ArrayList<String> li;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        ListView lp=(ListView) findViewById(R.id.list_places);
        Intent i=getIntent();
        li=(ArrayList<String>)i.getSerializableExtra("list_places");
        pl_id=(ArrayList<String>)i.getSerializableExtra("places_id");
        SharedPreferences shared=getSharedPreferences("list_places", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=shared.edit();
        Set<String> yourSet = shared.getStringSet("list", null);
        List<String> s=new ArrayList<String>();
        s.addAll(yourSet);
        MyListAdapter itemsAdapter=new MyListAdapter(Saved.this,R.layout.custom_list,s,pl_id);
        lp.setAdapter(itemsAdapter);
    }
    public void next(View v){
        Intent i=new Intent();
        i.setClass(this, list.class);
        i.putExtra("list_places", li);
        i.putExtra("places_id",pl_id);
        startActivity(i);

    }
}