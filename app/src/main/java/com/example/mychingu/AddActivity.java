package com.example.mychingu;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddActivity extends AppCompatActivity {

    EditText friend_name, friend_dob, friend_phone, friend_email;
    RadioGroup radioGroupGender;
    RadioButton radioMale, radioFemale;
    Button add_button;

    private int currentUserId = -1; // To store the ID of the currently logged-in user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // Get the current user ID from the Intent (passed from MainActivity)
        if (getIntent().hasExtra("user_id")) {
            currentUserId = getIntent().getIntExtra("user_id", -1);
            if (currentUserId == -1) {
                // Handle error: user_id not passed correctly
                Toast.makeText(this, "Error: User ID not found. Please log in again.", Toast.LENGTH_LONG).show();
                finish(); // Close AddActivity
                return;
            }
        } else {
            // This should not happen if MainActivity passes the user_id correctly.
            Toast.makeText(this, "Error: User ID not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish(); // Close AddActivity
            return;
        }

        // Initialize views
        friend_name = findViewById(R.id.friend_name);
        friend_dob = findViewById(R.id.friend_dob);
        friend_phone = findViewById(R.id.friend_phone);
        friend_email = findViewById(R.id.friend_email);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);
        add_button = findViewById(R.id.add_button);

        // Set up DatePickerDialog for friend_dob
        friend_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Set OnClickListener for Add button
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gender, name, dob, phone, email;

                // Get selected gender
                int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
                RadioButton selectedGender = findViewById(selectedGenderId);
                gender = selectedGender != null ? selectedGender.getText().toString() : "";

                // Get other values
                name = friend_name.getText().toString().trim();
                dob = friend_dob.getText().toString().trim();
                phone = friend_phone.getText().toString().trim();
                email = friend_email.getText().toString().trim();

                // Validation
                if (TextUtils.isEmpty(gender)) {
                    Toast.makeText(AddActivity.this, "Please select gender", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    friend_name.setError(getString(R.string.error_empty_field));
                    return;
                }
                if (TextUtils.isEmpty(dob)) {
                    friend_dob.setError(getString(R.string.error_empty_field));
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    friend_phone.setError(getString(R.string.error_empty_field));
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    friend_email.setError(getString(R.string.error_empty_field));
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    friend_email.setError("Invalid email address");
                    return;
                }

                // Create a new DatabaseHelper instance
                DatabaseHelper db = new DatabaseHelper(AddActivity.this);
                // Now pass the currentUserId to addFriend
                db.addFriend(currentUserId, gender, name, dob, phone, email);

                // Set result to notify MainActivity to refresh the data
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);

                finish(); // Close the AddActivity and return to MainActivity
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Display selected date in DD/MM/YYYY format
                        String day = String.format("%02d", dayOfMonth);
                        String month = String.format("%02d", monthOfYear + 1); // Month is 0-indexed
                        friend_dob.setText(day + "/" + month + "/" + year);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }
}