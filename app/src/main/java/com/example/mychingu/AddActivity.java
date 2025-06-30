package com.example.mychingu;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Toast;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddActivity extends AppCompatActivity {

    // Declare EditText, RadioGroup, RadioButtons, and Button
    EditText friend_name, friend_dob, friend_phone, friend_email;
    RadioGroup radioGroupGender;
    RadioButton radioMale, radioFemale;
    Button add_button;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // Initialize views by matching IDs with the ones in XML
        friend_name = findViewById(R.id.friend_name);
        friend_dob = findViewById(R.id.friend_dob);
        friend_phone = findViewById(R.id.friend_phone);
        friend_email = findViewById(R.id.friend_email);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);

        add_button = findViewById(R.id.add_button);

        // Instantiate DatabaseHelper
        db = new DatabaseHelper(AddActivity.this);

        // Set onClickListener for the Save button
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get data entered by the user
                String name = friend_name.getText().toString();
                String dob = friend_dob.getText().toString();
                String phone = friend_phone.getText().toString();
                String email = friend_email.getText().toString();

                // Get selected gender from RadioGroup
                int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
                RadioButton selectedGender = findViewById(selectedGenderId);
                String gender = selectedGender != null ? selectedGender.getText().toString() : "";

                // Validate phone number, email, and gender before saving data
                if (isPhoneValid(phone) && isEmailValid(email) && !gender.isEmpty()) {
                    // Proceed with saving the data
                    db.addFriend(gender, name, dob, phone, email);  // Add friend to the database
                    Toast.makeText(AddActivity.this, "Friend added successfully!", Toast.LENGTH_SHORT).show();

                    // Set result to notify MainActivity to refresh the data
                    setResult(RESULT_OK);  // Notify MainActivity to refresh its data
                    finish(); // Close the AddActivity and go back to the previous screen
                } else {
                    // Show error message if validation fails
                    Toast.makeText(AddActivity.this, "Invalid phone number, email, or gender", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set onClickListener for Date of Birth EditText to show DatePickerDialog
        friend_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the date picker dialog when user clicks on the EditText
                showDatePickerDialog();
            }
        });
    }

    // Method to show DatePickerDialog
    private void showDatePickerDialog() {
        // Initialize calendar instance
        Calendar calendar = Calendar.getInstance();

        // Create DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Format the selected date as "DD/MM/YYYY"
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String selectedDate = dateFormat.format(calendar.getTime());

                        // Set the selected date to the EditText
                        friend_dob.setText(selectedDate);
                    }
                },
                calendar.get(Calendar.YEAR),  // Initial year
                calendar.get(Calendar.MONTH), // Initial month
                calendar.get(Calendar.DAY_OF_MONTH) // Initial day
        );

        // Show the date picker dialog
        datePickerDialog.show();
    }

    // Method to validate phone number using a custom regular expression
    private boolean isPhoneValid(String phone) {
        if (phone.isEmpty()) {
            friend_phone.setError("Phone number is required");
            return false;
        }

        // Regular expression for a valid phone number (at least 10 digits)
        String phonePattern = "^[+]?[0-9]{10,13}$";  // At least 10 digits, and optionally start with a "+" (e.g., +1234567890)

        if (!phone.matches(phonePattern)) {
            friend_phone.setError("Invalid phone number");
            return false;
        }
        return true;
    }

    // Method to validate email address using Patterns.EMAIL_ADDRESS
    private boolean isEmailValid(String email) {
        if (email.isEmpty()) {
            friend_email.setError("Email is required");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            friend_email.setError("Invalid email address");
            return false;
        }
        return true;
    }
}
