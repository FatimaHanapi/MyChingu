package com.example.mychingu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    Activity activity;
    private ArrayList<String> _id, gender, friend_name, friend_dob, friend_phone, friend_email;


    CustomAdapter(Activity activity, Context context, ArrayList<String> _id, ArrayList<String> gender, ArrayList<String> friend_name,
                  ArrayList<String> friend_dob, ArrayList<String> friend_phone, ArrayList<String> friend_email) {
        this.activity = activity;
        this.context = context;
        this._id = _id;
        this.gender = gender;
        this.friend_name = friend_name;
        this.friend_dob = friend_dob;
        this.friend_phone = friend_phone;
        this.friend_email = friend_email;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder._id_text.setText(_id.get(position));
        holder.gender_text.setText(gender.get(position));
        holder.friend_name.setText(friend_name.get(position));
        holder.friend_dob.setText(friend_dob.get(position));
        holder.friend_phone.setText(friend_phone.get(position));
        holder.friend_email.setText(friend_email.get(position));

        // Use a standard OnClickListener to avoid the issue with lambdas
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateActivity.class);
                intent.putExtra("id", String.valueOf(_id.get(position)));
                intent.putExtra("gender", String.valueOf(gender.get(position)));
                intent.putExtra("name", String.valueOf(friend_name.get(position)));
                intent.putExtra("dob", String.valueOf(friend_dob.get(position)));
                intent.putExtra("phone", String.valueOf(friend_phone.get(position)));
                intent.putExtra("email", String.valueOf(friend_email.get(position)));

                // Correct method to start activity
                activity.startActivityForResult(intent, 1);
            }
        });
    }
    @Override
    public int getItemCount() {
        return _id.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView _id_text, gender_text, friend_name, friend_dob, friend_phone, friend_email;
        LinearLayout mainLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            _id_text = itemView.findViewById(R.id._id_txt);
            gender_text = itemView.findViewById(R.id.label_gender);
            friend_name = itemView.findViewById(R.id.friend_name);
            friend_dob = itemView.findViewById(R.id.friend_dob);
            friend_phone = itemView.findViewById(R.id.friend_phone);
            friend_email = itemView.findViewById(R.id.friend_email);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}