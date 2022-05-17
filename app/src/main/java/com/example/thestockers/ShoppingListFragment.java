package com.example.thestockers;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ShoppingListFragment extends Fragment {

    RecyclerView recyclerView;
    FloatingActionButton add_button;

    ShoppingListDatabase myDB;
    ArrayList<String> list_id, list_title;
    ShoppingCustomAdapter customAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        // Inflate the layout for this fragment
        recyclerView = view.findViewById(R.id.shopping_recyclerView);
        add_button = view.findViewById(R.id.add_list_button);
        add_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getActivity(), ShoppingAddActivity.class);
                startActivity(intent);
            }
        });

        myDB = new ShoppingListDatabase(getActivity());
        list_id = new ArrayList<>();
        list_title = new ArrayList<>();

        storeDataInArrays();

        customAdapter = new ShoppingCustomAdapter(getActivity(), list_id, list_title);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }
    void storeDataInArrays(){
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0){
            //Toast.makeText(this.getActivity(), "No data.", Toast.LENGTH_SHORT).show();
        }
        else{
            while(cursor.moveToNext()){
                list_id.add(cursor.getString(0));
                list_title.add(cursor.getString(1));
            }
        }
    }

}