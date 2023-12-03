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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
            removeValues(activity);
            dialog.dismiss(); // Dismiss the dialog after initiating the removeValues action
        });

        no.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void removeValues(Activity activity) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            DatabaseReference diaryRef = FirebaseDatabase.getInstance().getReference().child("diary_food_total").child(uid);
            DatabaseReference weightRef = FirebaseDatabase.getInstance().getReference().child("Weight_management").child(uid);
            DatabaseReference diary = FirebaseDatabase.getInstance().getReference().child("diary");

            Task<Void> removeDiaryTotal = diaryRef.child("diary_total").removeValue();
            Task<Void> removeDailyCalorieIntake = weightRef.child("daily_calorie_intake").removeValue();
            Task<Void> removedDiary = diary.child(uid).removeValue();

            // Combine both tasks into a single composite task
            Task<Void> combinedTask = Tasks.whenAll(removeDiaryTotal, removeDailyCalorieIntake,removedDiary);

            combinedTask.addOnSuccessListener(aVoid -> {
                // Show toast message when both values are successfully removed
                Toast.makeText(activity, "Check in success", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(activity, "Failed to remove values", Toast.LENGTH_SHORT).show();
            });
        }
    }
}