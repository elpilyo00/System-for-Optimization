package com.example.foodtoqu.Dialog_utils;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.foodtoqu.R;
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
            checkOut(); // Call checkOut method when "Yes" button is clicked
            dialog.dismiss(); // Dismiss the dialog after performing the checkOut action
        });

        no.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void checkOut() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Weight_management").child(uid);

            // Removing specific child values
            userRef.child("daily_calorie_intake").removeValue();
//            userRef.child("daily_carbohydrate_intake").removeValue();
//            userRef.child("daily_fat_intake").removeValue();
//            userRef.child("daily_protein_intake").removeValue();
        }
    }
}
