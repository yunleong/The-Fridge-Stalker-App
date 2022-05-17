package com.example.thestockers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ShoppingAddActivity extends AppCompatActivity {

    EditText title_input;
    Button add_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_add);

        title_input = findViewById(R.id.list_title_input);
        add_button = findViewById(R.id.list_add_button);
        add_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ShoppingListDatabase myDB = new ShoppingListDatabase(ShoppingAddActivity.this);
                myDB.addList(title_input.getText().toString().trim());
            }
        });
    }

}