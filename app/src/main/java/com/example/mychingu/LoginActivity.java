package com.example.mychingu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log; // Required for Log.d

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextStudentIdLogin, editTextPasswordLogin;
    private Button buttonLogin;
    private TextView textViewRegisterPrompt;
    private DatabaseHelper db; // Declare DatabaseHelper

    // SharedPreferences constants (moved these to be proper member variables)
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "loggedInUserId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        editTextStudentIdLogin = findViewById(R.id.editTextStudentIdLogin);
        editTextPasswordLogin = findViewById(R.id.editTextPasswordLogin);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegisterPrompt = findViewById(R.id.textViewRegisterPrompt);

        // Initialize DatabaseHelper
        db = new DatabaseHelper(this);

        // Check if user is already logged in (using SharedPreferences)
        if (isLoggedIn()) {
            int userId = getLoggedInUserId();
            Log.d("MyChinguDebug", "User already logged in. Redirecting to MainActivity with userId: " + userId); // DEBUG LOG
            redirectToMainActivity(userId); // Use the helper method
            return; // Exit onCreate
        }

        // Set onClickListener for Login button
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String studentId = editTextStudentIdLogin.getText().toString().trim();
                String password = editTextPasswordLogin.getText().toString().trim();

                if (TextUtils.isEmpty(studentId)) {
                    editTextStudentIdLogin.setError(getString(R.string.error_empty_field));
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    editTextPasswordLogin.setError(getString(R.string.error_empty_field));
                    return;
                }

                // Authenticate user using DatabaseHelper, checkUser now returns int (userId or -1)
                int userId = db.checkUser(studentId, password);
                Log.d("MyChinguDebug", "Login attempt for Student ID: " + studentId + ", db.checkUser returned userId: " + userId); // DEBUG LOG

                if (userId != -1) { // If login successful (userId is not -1)
                    Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();

                    // Save login state and user_id to SharedPreferences
                    saveLoginState(true, userId);

                    // Navigate to MainActivity
                    redirectToMainActivity(userId); // Use the helper method
                } else {
                    Toast.makeText(LoginActivity.this, R.string.error_invalid_credentials, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set onClickListener for Register prompt
        textViewRegisterPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    // SharedPreferences methods
    private boolean isLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean loggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);
        Log.d("MyChinguDebug", "Checking isLoggedIn from SharedPreferences: " + loggedIn); // DEBUG LOG
        return loggedIn;
    }

    private int getLoggedInUserId() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int userId = prefs.getInt(KEY_USER_ID, -1); // Return -1 if no user_id found
        Log.d("MyChinguDebug", "Retrieved loggedInUserId from SharedPreferences: " + userId); // DEBUG LOG
        return userId;
    }

    private void saveLoginState(boolean isLoggedIn, int userId) {
        SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.putInt(KEY_USER_ID, userId);
        editor.apply(); // Apply changes asynchronously
        Log.d("MyChinguDebug", "Login state saved: isLoggedIn=" + isLoggedIn + ", userId=" + userId); // DEBUG LOG
    }

    // Method to clear login state (for logout) - Made static as it's called from MainActivity
    public static void clearLoginState(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        Log.d("MyChinguDebug", "Login state cleared from SharedPreferences."); // DEBUG LOG
    }

    // Helper method to redirect to MainActivity (also includes debugging log)
    private void redirectToMainActivity(int userId) {
        Log.d("MyChinguDebug", "Redirecting to MainActivity with userId: " + userId); // DEBUG LOG
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("user_id", userId); // Pass the user_id
        startActivity(intent);
        finish(); // Finish LoginActivity
    }
}