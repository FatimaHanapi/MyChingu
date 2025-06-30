package com.example.mychingu;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton add_button;

    DatabaseHelper db;
    ArrayList<String> _id, gender, friend_name, friend_dob, friend_phone, friend_email;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Set OnClickListener for Add button
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the AddActivity to add new data
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(intent, 1);  // Start Activity with request code 1
            }
        });

        // Create a new DatabaseHelper instance
        db = new DatabaseHelper(MainActivity.this);

        // Load the data from the database
        storeDataInArray();

        // Set the CustomAdapter to the RecyclerView
        customAdapter = new CustomAdapter(MainActivity.this, this, _id, gender, friend_name, friend_dob, friend_phone, friend_email);
        recyclerView.setAdapter(customAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            storeDataInArray();  // Load the data again after adding a new friend
            customAdapter.notifyDataSetChanged();  // Notify adapter to refresh RecyclerView
        }
    }

    // Method to load data from the database
    private void storeDataInArray() {
        Cursor cursor = db.readAllData();
        _id.clear();  // Clear existing data to avoid duplication
        gender.clear();
        friend_name.clear();
        friend_dob.clear();
        friend_phone.clear();
        friend_email.clear();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                _id.add(cursor.getString(0));
                gender.add(cursor.getString(1));
                friend_name.add(cursor.getString(2));
                friend_dob.add(cursor.getString(3));
                friend_phone.add(cursor.getString(4));
                friend_email.add(cursor.getString(5));
            }
        }
    }

    // Delete friend and refresh the RecyclerView
    public void deleteFriend(String friendId) {
        db.deleteOneFriend(friendId);  // Delete the friend from the database
        storeDataInArray();  // Reload the data from the database
        customAdapter.notifyDataSetChanged();  // Refresh the RecyclerView
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.delete_all) {
            confirmDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete All?");
        builder.setMessage("Are you want to delete all Data?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseHelper db = new DatabaseHelper(MainActivity.this);
                db.deleteAllData();

                Intent intent = new Intent (MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i){

            }
        });
        builder.create().show();
    }
}