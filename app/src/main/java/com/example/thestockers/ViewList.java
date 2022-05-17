package com.example.thestockers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ViewList extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton add_button;

    ListDatabase myDB;
    ArrayList<String> item_id, item_name, item_group, item_quantity;
    ItemCustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);

        int groupID = getIntentData();
        Activity activity = this;

        recyclerView = findViewById(R.id.list_recyclerView);
        add_button = findViewById(R.id.add_item_button);
        add_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewList.this, ListAddActivity.class);
                intent.putExtra("GROUP_ID", groupID);
                activity.startActivityForResult(intent,1);
            }
        });

        myDB = new ListDatabase(ViewList.this);
        item_id = new ArrayList<>();
        item_name = new ArrayList<>();
        item_quantity = new ArrayList<>();

        storeDataInArrays(groupID);
        customAdapter = new ItemCustomAdapter(ViewList.this, item_id, item_name, item_group, item_quantity);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewList.this));

    }

    int getIntentData(){
        if(getIntent().hasExtra("GROUP_ID")){
            return getIntent().getIntExtra("GROUP_ID", 0);
        }
        else{
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
            return 0;
        }
    }

    void storeDataInArrays(int groupID){
        Cursor cursor = myDB.readAllData(groupID);
        if(cursor.getCount() == 0){
            Toast.makeText(ViewList.this, "No data.", Toast.LENGTH_SHORT).show();
        }
        else{
            while(cursor.moveToNext()){
                item_id.add(cursor.getString(0));
                item_name.add(cursor.getString(1));
                item_quantity.add(cursor.getString(3));
            }
        }
    }
}