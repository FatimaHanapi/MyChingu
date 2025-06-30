package com.example.mychingu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateActivity extends AppCompatActivity {

    EditText friend_name, friend_dob, friend_phone, friend_email;
    RadioGroup radioGroupGender;
    RadioButton radioMale, radioFemale;
    Button update_button, delete_button;

    String id, gender, name, dob, phone, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        // Initialize views by matching IDs with the ones in XML
        friend_name = findViewById(R.id.friend_name2);
        friend_dob = findViewById(R.id.friend_dob2);
        friend_phone = findViewById(R.id.friend_phone2);
        friend_email = findViewById(R.id.friend_email2);
        radioGroupGender = findViewById(R.id.radioGroupGender2);
        radioMale = findViewById(R.id.radioMale2);
        radioFemale = findViewById(R.id.radioFemale2);
        update_button = findViewById(R.id.update_button);
        delete_button = findViewById(R.id.delete_button);

        // Get the data from the Intent and set it in the EditText fields
        getAndSetIntentData();

        // Set action bar title
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(name); // Set action bar title to the friend's name
        }

        // Set OnClickListener for the Update button
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get values from the EditText fields
                name = friend_name.getText().toString();
                dob = friend_dob.getText().toString();
                phone = friend_phone.getText().toString();
                email = friend_email.getText().toString();

                // Get the selected gender
                int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
                RadioButton selectedGender = findViewById(selectedGenderId);
                gender = selectedGender != null ? selectedGender.getText().toString() : "";

                // Validate the data before updating
                if (!name.isEmpty() && !dob.isEmpty() && !phone.isEmpty() && !email.isEmpty() && !gender.isEmpty()) {
                    // Update the database with the new data
                    DatabaseHelper db = new DatabaseHelper(UpdateActivity.this);
                    db.updateFriend(id, gender, name, dob, phone, email);

                    Toast.makeText(UpdateActivity.this, "Friend updated successfully!", Toast.LENGTH_SHORT).show();

                    // Set result to notify MainActivity to refresh the data
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);

                    finish(); // Close the UpdateActivity and return to the previous screen
                } else {
                    Toast.makeText(UpdateActivity.this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set OnClickListener for the Delete button
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog(); // Show confirmation dialog before deleting
            }
        });
    }

    // Method to get the data from Intent and set it in the views
    void getAndSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("name") &&
                getIntent().hasExtra("gender") && getIntent().hasExtra("dob") &&
                getIntent().hasExtra("phone") && getIntent().hasExtra("email")) {

            // Getting Data from Intent
            id = getIntent().getStringExtra("id");
            name = getIntent().getStringExtra("name");
            gender = getIntent().getStringExtra("gender");
            dob = getIntent().getStringExtra("dob");
            phone = getIntent().getStringExtra("phone");
            email = getIntent().getStringExtra("email");

            // Setting the values in the EditText and RadioButton views
            friend_name.setText(name);
            friend_dob.setText(dob);
            friend_phone.setText(phone);
            friend_email.setText(email);

            // Set gender selection (RadioButton)
            if ("Male".equals(gender)) {
                radioMale.setChecked(true);
            } else if ("Female".equals(gender)) {
                radioFemale.setChecked(true);
            }
        } else {
            Toast.makeText(this, "No data received", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to confirm before deleting the friend
    void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + name + "?");
        builder.setMessage("Are you sure you want to delete " + name + "?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseHelper db = new DatabaseHelper(UpdateActivity.this);
                db.deleteOneFriend(id); // Delete the friend from the database

                Toast.makeText(UpdateActivity.this, "Friend deleted successfully!", Toast.LENGTH_SHORT).show();

                // Set result to notify MainActivity to refresh the data
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);

                finish(); // Close the UpdateActivity and return to MainActivity
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do nothing, just close the dialog
            }
        });

        builder.create().show();
    }
}
