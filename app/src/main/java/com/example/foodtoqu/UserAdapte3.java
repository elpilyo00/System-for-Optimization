package com.example.foodtoqu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
public class UserAdapte3 extends RecyclerView.Adapter<UserAdapte3.ViewHolder> {
    private List<User4> userList;
    private Context context;
   // Firebase Auth UID

    public UserAdapte3(List<User4> userList, Context context) {
        this.userList = userList;
        this.context = context;// Initialize the Firebase Auth UID
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User4 user = userList.get(position);

        // Bind user data to ViewHolder views
        holder.usernameTextView.setText(user.getName());

        holder.cardView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.recycler_animation3));


        // Load the user image using Picasso
        Glide.with(context)  // Replace 'context' with your actual context
                .load(user.getImage())
                .into(holder.userImageView);

        // Set an OnClickListener to handle item clicks
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User4 user = userList.get(position);
                String name = user.getName();

                // Create a Firebase reference to the "users" node
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("User");

                // Query the database to find the UID associated with the name
                usersRef.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Check if there is a match
                        if (dataSnapshot.exists()) {
                            // Loop through the results (usually there should be only one result)
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String uid = snapshot.getKey(); // Get the UID
                                // Now, you have the UID associated with the name
                                // Start the DiaryListActivity3 or pass the UID to the intent
                                Intent intent = new Intent(context, DiaryListActivity3.class);
                                intent.putExtra("uid", uid);
                                context.startActivity(intent);
                                ((Activity) context).overridePendingTransition(0, 0); // Disable animation
                                ((Activity) context).finish();
                                return; // Exit the loop since we found a match
                            }
                        } else {
                            // Handle the case where there is no match (name not found)
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors here
                    }
                });
            }
        });
    }

        @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateList(List<User4> newList) {
        userList.clear();
        userList.addAll(newList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImageView;
        TextView usernameTextView;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.userImageView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            cardView = itemView.findViewById(R.id.cardView);

        }
    }
}
