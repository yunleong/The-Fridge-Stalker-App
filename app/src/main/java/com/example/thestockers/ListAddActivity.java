package com.example.thestockers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ListAddActivity extends AppCompatActivity {

    EditText name_input, quantity_input;
    Button add_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_add);

        int groupID = getIntentData();

        name_input = findViewById(R.id.item_name_input);
        quantity_input = findViewById(R.id.item_quantity);
        add_button = findViewById(R.id.item_add_button);
        add_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                ListDatabase myDB = new ListDatabase(ListAddActivity.this);
                myDB.addItem(name_input.getText().toString().trim(),
                        groupID,
                        Integer.valueOf(quantity_input.getText().toString().trim()));
            }
        });
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
}