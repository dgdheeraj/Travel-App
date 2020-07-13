package com.example.travel_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class list extends AppCompatActivity {
    public ArrayList<String> pl_id;
    public ArrayList<String> li;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent c=getIntent();
        li=(ArrayList<String>)c.getSerializableExtra("list_places");
        pl_id=(ArrayList<String>)c.getSerializableExtra("places_id");

        ListView lp=(ListView) findViewById(R.id.list_places);
        MyListAdapter itemsAdapter=new MyListAdapter(list.this,R.layout.custom_list,li,pl_id);
        lp.setAdapter(itemsAdapter);
//        Toast.makeText(list.this, li.size()+","+pl_id.size(), LENGTH_SHORT).show();
    }
    public void next(View v){
        Intent i=new Intent();
        i.setClass(this, Saved.class);
        i.putExtra("list_places", li);
        i.putExtra("places_id",pl_id);
        startActivity(i);

    }
}