package com.example.thestockers;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ManualAddFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    Spinner uom_spinner;
    String selected_uom;
    EditText productName_manAdd;
    EditText quantity_manAdd;
    private Button addButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manual_add, container, false);

        productName_manAdd = view.findViewById(R.id.productET_manualAdd);
        quantity_manAdd = view.findViewById(R.id.quantityET_manualAdd);
        addButton = view.findViewById(R.id.manualAdd_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeDatabaseHelper myDB = new HomeDatabaseHelper(ManualAddFragment.this.getActivity());
                System.out.println("Add String: " + productName_manAdd.getText().toString().trim());
//                RemoteDBHelper.insertDB("randomid", "randomdate",
//                        productName_manAdd.getText().toString(),
//                        quantity_manAdd.getText().toString(), selected_uom);
                myDB.addItem(productName_manAdd.getText().toString().trim(),
                        Integer.valueOf(quantity_manAdd.getText().toString().trim()), selected_uom);
                // clear editTexts & default spinner
                productName_manAdd.getText().clear();
                quantity_manAdd.getText().clear();
                uom_spinner.setSelection(0);
            }
        });

        productName_manAdd.addTextChangedListener(AddInfoTextWatcher);
        quantity_manAdd.addTextChangedListener(AddInfoTextWatcher);

        // Connect Unit of Measure spinner resource to adapter
        uom_spinner = view.findViewById(R.id.UoM_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.unit_of_measure, R.layout.support_simple_spinner_dropdown_item);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        uom_spinner.setAdapter(spinnerAdapter);
        uom_spinner.setOnItemSelectedListener(this);

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selected_uom = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        selected_uom = "count";
    }

    // Enable add button when product & quantity input are non-empty.
    private TextWatcher AddInfoTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String productInput = productName_manAdd.getText().toString().trim();
            String quantityInput = quantity_manAdd.getText().toString().trim();
            addButton.setEnabled(!productInput.isEmpty() && (!quantityInput.isEmpty() && !quantityInput.equals("0")));
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };
}