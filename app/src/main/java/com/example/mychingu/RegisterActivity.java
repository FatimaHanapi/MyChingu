package com.example.mychingu;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextStudentIdRegister, editTextNameRegister, editTextEmailRegister,
            editTextPasswordRegister, editTextConfirmPasswordRegister;
    private Button buttonRegister;
    private TextView textViewLoginPrompt;
    private DatabaseHelper db; // Declare DatabaseHelper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        editTextStudentIdRegister = findViewById(R.id.editTextStudentIdRegister);
        editTextNameRegister = findViewById(R.id.editTextNameRegister);
        editTextEmailRegister = findViewById(R.id.editTextEmailRegister);
        editTextPasswordRegister = findViewById(R.id.editTextPasswordRegister);
        editTextConfirmPasswordRegister = findViewById(R.id.editTextConfirmPasswordRegister);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLoginPrompt = findViewById(R.id.textViewLoginPrompt);

        // Initialize DatabaseHelper
        db = new DatabaseHelper(this);

        // Set onClickListener for Register button
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String studentId = editTextStudentIdRegister.getText().toString().trim();
                String name = editTextNameRegister.getText().toString().trim();
                String email = editTextEmailRegister.getText().toString().trim();
                String password = editTextPasswordRegister.getText().toString().trim();
                String confirmPassword = editTextConfirmPasswordRegister.getText().toString().trim();

                // Validation checks
                if (TextUtils.isEmpty(studentId)) {
                    editTextStudentIdRegister.setError(getString(R.string.error_empty_field));
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    editTextNameRegister.setError(getString(R.string.error_empty_field));
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    editTextEmailRegister.setError(getString(R.string.error_empty_field));
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmailRegister.setError("Invalid email address");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    editTextPasswordRegister.setError(getString(R.string.error_empty_field));
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    editTextConfirmPasswordRegister.setError(getString(R.string.error_empty_field));
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    editTextConfirmPasswordRegister.setError(getString(R.string.error_passwords_mismatch));
                    Toast.makeText(RegisterActivity.this, R.string.error_passwords_mismatch, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if Student ID already exists
                if (db.checkStudentIdExists(studentId)) {
                    editTextStudentIdRegister.setError(getString(R.string.error_student_id_exists));
                    Toast.makeText(RegisterActivity.this, R.string.error_student_id_exists, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add user to database
                boolean registrationSuccess = db.addUser(studentId, name, email, password);

                if (registrationSuccess) {
                    Toast.makeText(RegisterActivity.this, R.string.registration_success, Toast.LENGTH_SHORT).show();
                    // Navigate back to LoginActivity
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Finish RegisterActivity
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set onClickListener for Login prompt
        textViewLoginPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to LoginActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Finish RegisterActivity
            }
        });
    }
}