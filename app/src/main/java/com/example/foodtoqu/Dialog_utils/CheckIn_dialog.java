package com.example.foodtoqu.Dialog_utils;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.foodtoqu.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class CheckIn_dialog {

    public void checkIn(Activity activity) {
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.check_in))
                .setContentWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setCancelable(false)
                .create();
        View dialogView = dialog.getHolderView();
        Button yes = dialogView.findViewById(R.id.Yes);
        Button no = dialogView.findViewById(R.id.no);

        yes.setOnClickListener(v -> {
            checkOut(activity); // Call checkOut method when "Yes" button is clicked
            dialog.dismiss(); // Dismiss the dialog after performing the checkOut action
        });

        no.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void checkOut(Activity activity) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Weight_management").child(uid);

            // Removing specific child values
            userRef.child("daily_calorie_intake").removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Show toast message when the value is successfully removed
                            Toast.makeText(activity, "Check in success", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(activity, "Failed to remove value", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}