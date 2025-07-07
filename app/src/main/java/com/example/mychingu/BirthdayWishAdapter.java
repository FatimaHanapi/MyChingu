package com.example.mychingu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BirthdayWishAdapter extends RecyclerView.Adapter<BirthdayWishAdapter.MyViewHolder> {

    private Context context;
    private Activity activity;
    private ArrayList<String> id, gender, name, dob, phone, email;

    public BirthdayWishAdapter(Activity activity, Context context,
                               ArrayList<String> id, ArrayList<String> gender, ArrayList<String> name,
                               ArrayList<String> dob, ArrayList<String> phone, ArrayList<String> email) {
        this.activity = activity;
        this.context = context;
        this.id = id;
        this.gender = gender;
        this.name = name;
        this.dob = dob;
        this.phone = phone;
        this.email = email;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_friendlistitem, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BirthdayWishAdapter.MyViewHolder holder, int position) {
        holder.friendNameTextView.setText(name.get(position));
        holder.friendPhoneTextView.setText(phone.get(position));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BirthdayWishActivity.class);
            intent.putExtra("friend_name", name.get(position));
            intent.putExtra("friend_phone", phone.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return id.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView friendNameTextView, friendPhoneTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            friendNameTextView = itemView.findViewById(R.id.showfriend_name);
            friendPhoneTextView = itemView.findViewById(R.id.showfriend_phone);
        }
    }
}