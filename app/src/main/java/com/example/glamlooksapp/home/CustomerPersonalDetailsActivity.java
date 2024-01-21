package com.example.glamlooksapp.home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;


import com.example.glamlooksapp.R;


public class CustomerPersonalDetailsActivity extends AppCompatActivity {

    private Spinner spinner;
    private CheckBox checkBox1, checkBox2, checkBox3;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customer_personaldetails_page);

        spinner = findViewById(R.id.spinner);
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        loginButton = findViewById(R.id.login_button);

        setupSpinner();
        setupButtonClick();
    }

    private void setupSpinner() {
        String[] items = {"Item 1", "Item 2", "Item 3"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle spinner item selection if needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }

    private void setupButtonClick() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected item from the spinner
                String selectedSpinnerItem = spinner.getSelectedItem().toString();

                // Check the state of checkboxes
                boolean isChecked1 = checkBox1.isChecked();
                boolean isChecked2 = checkBox2.isChecked();
                boolean isChecked3 = checkBox3.isChecked();


            }
        });
    }
}

