package com.example.mychingu;

import static com.example.mychingu.R.id.tvFriendName1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BirthdayWishActivity extends AppCompatActivity {

    TextView nameText, phoneText;
    EditText etWishMessage;
    Button btnOpenWhatsApp;

    String friendName, friendPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish);

        nameText = findViewById(tvFriendName1);
        phoneText = findViewById(R.id.tvFriendPhone1);
        etWishMessage = findViewById(R.id.etWishMessage1);
        btnOpenWhatsApp = findViewById(R.id.btnOpenWhatsApp1);

        friendName = getIntent().getStringExtra("friend_name");
        friendPhone = getIntent().getStringExtra("friend_phone");

        nameText.setText("Name: " + friendName);
        phoneText.setText("Phone: " + friendPhone);

        btnOpenWhatsApp.setOnClickListener(v -> {
            String message = etWishMessage.getText().toString().trim();
            if (message.isEmpty()) {
                Toast.makeText(this, "Please write a birthday wish first", Toast.LENGTH_SHORT).show();
                return;
            }

            String rawPhone = friendPhone.trim();
            String formattedPhone;

            if (rawPhone.startsWith("0")) {
                if (rawPhone.startsWith("08")) {
                    // Indonesia
                    formattedPhone = "62" + rawPhone.substring(1);
                } else if (rawPhone.startsWith("01") || rawPhone.startsWith("02")) {
                    // Malaysia
                    formattedPhone = "60" + rawPhone.substring(1);
                } else {
                    Toast.makeText(this, "Please use international format or valid number.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else if (rawPhone.startsWith("+")) {
                formattedPhone = rawPhone.substring(1); // remove "+"
            } else {
                formattedPhone = rawPhone;
            }

            String url = "https://wa.me/" + formattedPhone + "?text=" + Uri.encode(message);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

    }
}