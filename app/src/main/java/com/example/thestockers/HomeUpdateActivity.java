package com.example.thestockers;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class HomeUpdateActivity extends AppCompatActivity {

    EditText product_input, quantity_input;
    Spinner uom_input;
    Button update_button, delete_button;
    String id, product, quantity, unit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_update);

        product_input = findViewById(R.id.productET_update);
        quantity_input = findViewById(R.id.quantityET_update);
        product_input.addTextChangedListener(UpdateInfoTextWatcher);
        quantity_input.addTextChangedListener(UpdateInfoTextWatcher);
        update_button = findViewById(R.id.update_button);
        delete_button = findViewById(R.id.delete_button);

        // Unit of Measure Spinner
        uom_input = findViewById(R.id.UoM_spinner_update);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.unit_of_measure, R.layout.support_simple_spinner_dropdown_item);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        uom_input.setAdapter(spinnerAdapter);
        uom_input.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                unit = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // 1. This is called first.
        getAndSetIntentData();

        ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setTitle(product);
        }

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 2. Then update is called.
                HomeDatabaseHelper myDB = new HomeDatabaseHelper(HomeUpdateActivity.this);
                product = product_input.getText().toString().trim();
                quantity = quantity_input.getText().toString().trim();
                RemoteDBHelper.updateInv(product, quantity, unit, id);
                myDB.updateData(id, product, quantity, unit);
                finish();
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeDatabaseHelper db = new HomeDatabaseHelper(HomeUpdateActivity.this);
                db.deleteOneRow(id);
                Toast.makeText(HomeUpdateActivity.this, "Deleted" , Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    void getAndSetIntentData(){
        if(getIntent().hasExtra("id") && getIntent().hasExtra("product")
            && getIntent().hasExtra("quantity") && getIntent().hasExtra("unit")){
            // Get data from intent
            id = getIntent().getStringExtra("id");
            product = getIntent().getStringExtra("product");
            quantity = getIntent().getStringExtra("quantity");
            unit = getIntent().getStringExtra("unit");

            // Set intent data into editTexts and spinner
            product_input.setText(product);
            quantity_input.setText(quantity);
            uom_input.setSelection(((ArrayAdapter<String>)uom_input.getAdapter()).getPosition(unit));
        }else{
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        }
    }

    // Enable update button when product & quantity input are non-empty.
    private TextWatcher UpdateInfoTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String productInput = product_input.getText().toString().trim();
            String quantityInput = quantity_input.getText().toString().trim();
            update_button.setEnabled(!productInput.isEmpty() && (!quantityInput.isEmpty() && !quantityInput.equals("0")));
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

}