package com.example.mychingu;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button; // This import is still needed if you have other buttons, but button_logout will be removed
import android.widget.Toast;
import android.util.Log;

import java.lang.reflect.Method;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import androidx.appcompat.widget.SearchView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton add_button;
    // Button button_logout; // REMOVED: This button is being removed from the layout

    DatabaseHelper db;
    ArrayList<String> _id, gender, friend_name, friend_dob, friend_phone, friend_email;
    CustomAdapter customAdapter;

    private int currentUserId = -1; // To store the ID of the currently logged-in user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the current user ID from the Intent (passed from LoginActivity)
        if (getIntent().hasExtra("user_id")) {
            currentUserId = getIntent().getIntExtra("user_id", -1);
            Log.d("MyChinguDebug", "MainActivity onCreate: Intent has user_id, value: " + currentUserId); // DEBUG LOG
            if (currentUserId == -1) {
                Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_LONG).show();
                redirectToLogin();
                return;
            }
        } else {
            Log.d("MyChinguDebug", "MainActivity onCreate: Intent DOES NOT have user_id."); // DEBUG LOG
            Toast.makeText(this, "No user session found. Please log in.", Toast.LENGTH_LONG).show();
            redirectToLogin();
            return;
        }


        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        add_button = findViewById(R.id.add_button);

        // Initialize the ArrayLists
        _id = new ArrayList<>();
        gender = new ArrayList<>();
        friend_name = new ArrayList<>();
        friend_dob = new ArrayList<>();
        friend_phone = new ArrayList<>();
        friend_email = new ArrayList<>();

        // Set RecyclerView LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create a new DatabaseHelper instance
        db = new DatabaseHelper(MainActivity.this);

        // Initialize and set the CustomAdapter FIRST (This order is crucial for no NullPointerException)
        customAdapter = new CustomAdapter(MainActivity.this, this, _id, gender, friend_name, friend_dob, friend_phone, friend_email);
        recyclerView.setAdapter(customAdapter);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                storeDataInArray(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                storeDataInArray(newText);
                return true;
            }
        });

        // Load the data from the database NOW (AFTER adapter is initialized)
        storeDataInArray(null);

        // Set OnClickListener for Add button
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the AddActivity to add new data
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                intent.putExtra("user_id", currentUserId); // Pass the current user's ID
                startActivityForResult(intent, 1);  // Start Activity with request code 1
            }
        });

        // Add OnClickListener for the new Logout button
        // button_logout.setOnClickListener(new View.OnClickListener() { // REMOVED: Button no longer exists
        //     @Override
        //     public void onClick(View view) {
        //         confirmLogoutDialog(); // Call the existing logout confirmation dialog
        //     }
        // });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            storeDataInArray(null);  // Load the data again after adding/updating/deleting a friend
            // customAdapter.notifyDataSetChanged(); // This is already called inside storeDataInArray
        }
    }

    // Method to load data from the database, now taking an optional query
    private void storeDataInArray(String query) {
        Log.d("MyChinguDebug", "storeDataInArray called. currentUserId: " + currentUserId); // DEBUG LOG
        // Ensure currentUserId is valid before querying
        if (currentUserId == -1) {
            Toast.makeText(this, "Error: User not logged in. Cannot load data.", Toast.LENGTH_LONG).show();
            return;
        }

        Cursor cursor;
        if (query != null && !query.isEmpty()) {
            cursor = db.searchFriends(currentUserId, query); // Use search method
        } else {
            cursor = db.readAllData(currentUserId); // Use read all data method
        }

        _id.clear();  // Clear existing data to avoid duplication
        gender.clear();
        friend_name.clear();
        friend_dob.clear();
        friend_phone.clear();
        friend_email.clear();

        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(this, "No friends found for this user.", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                _id.add(cursor.getString(0));
                gender.add(cursor.getString(2));
                friend_name.add(cursor.getString(3));
                friend_dob.add(cursor.getString(4));
                friend_phone.add(cursor.getString(5));
                friend_email.add(cursor.getString(6));
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        customAdapter.notifyDataSetChanged(); // Important: Notify adapter after data change
    }

    // Delete friend and refresh the RecyclerView (this method is called from CustomAdapter)
    public void deleteFriend(String friendId) {
        db.deleteOneFriend(friendId);  // Delete the friend from the database
        storeDataInArray(null);  // Reload the data from the database (no search query)
        // customAdapter.notifyDataSetChanged(); // This is already called inside storeDataInArray
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.delete_all) {
            confirmDeleteAllDialog(); // Call a specific method for deleting all
            return true;
        } else if (itemId == R.id.logout) { // Handle logout
            confirmLogoutDialog();
            return true;
        } else if (itemId == R.id.birthdaywish) {
            Intent i = new Intent(MainActivity.this, FriendlistActivity.class);
            i.putExtra("user_id", currentUserId);
            startActivity(i);
            return true;
        } else if (itemId == R.id.viewreport) {
            Intent intent = new Intent(MainActivity.this, ReportActivity.class);
            intent.putExtra("user_id", currentUserId);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Dialog for deleting all friends for the current user
    void confirmDeleteAllDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete All Friends?");
        builder.setMessage("Are you sure you want to delete ALL friends for this user?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Use the new deleteAllFriendsForUser method
                db.deleteAllFriendsForUser(currentUserId);
                storeDataInArray(null); // Reload data after deletion
                // customAdapter.notifyDataSetChanged(); // Removed as storeDataInArray already calls it
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                // Do nothing
            }
        });
        builder.create().show();
    }

    // Dialog for logging out
    void confirmLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Clear SharedPreferences
                LoginActivity.clearLoginState(MainActivity.this);
                Toast.makeText(MainActivity.this, R.string.logout_message, Toast.LENGTH_SHORT).show();
                // Redirect to LoginActivity
                redirectToLogin();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });
        builder.create().show();
    }

    // Helper method to redirect to LoginActivity
    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
        startActivity(intent);
        finish();
    }
}