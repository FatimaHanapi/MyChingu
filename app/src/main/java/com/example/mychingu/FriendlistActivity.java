package com.example.mychingu;

import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FriendlistActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseHelper db;
    ArrayList<String> _id, gender, friend_name, friend_dob, friend_phone, friend_email;
    BirthdayWishAdapter adapter;
    int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);

        currentUserId = getIntent().getIntExtra("user_id", -1);
        recyclerView = findViewById(R.id.recyclerView);

        db = new DatabaseHelper(this);
        _id = new ArrayList<>();
        gender = new ArrayList<>();
        friend_name = new ArrayList<>();
        friend_dob = new ArrayList<>();
        friend_phone = new ArrayList<>();
        friend_email = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BirthdayWishAdapter(this, this, _id, gender, friend_name, friend_dob, friend_phone, friend_email);
        recyclerView.setAdapter(adapter);

        loadFriends();
    }

    private void loadFriends() {
        Cursor cursor = db.readAllData(currentUserId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                _id.add(cursor.getString(0));
                gender.add(cursor.getString(2));
                friend_name.add(cursor.getString(3));
                friend_dob.add(cursor.getString(4));
                friend_phone.add(cursor.getString(5));
                friend_email.add(cursor.getString(6));
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        adapter.notifyDataSetChanged();
    }
}
