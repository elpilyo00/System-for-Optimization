package com.example.foodtoqu.Dialog_utils;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.foodtoqu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
public class PlanDialog {
    //dialog to passed to UserActivity
    @SuppressLint("NotConstructor")
    public void PlanDialog(Activity activity) {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("User");
        DatabaseReference weightManagementReference = FirebaseDatabase.getInstance().getReference().child("Weight_management");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            DialogPlus dialog = DialogPlus.newDialog(activity)
                    .setContentHolder(new ViewHolder(R.layout.plan_layout))
                    .setContentWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setGravity(Gravity.CENTER)
                    .setCancelable(false)
                    .create();
            View dialogView = dialog.getHolderView();

            TextView currentWeight = dialogView.findViewById(R.id.current_weight);
            EditText targetWeight = dialogView.findViewById(R.id.target_weight);
            TextView targetDate = dialogView.findViewById(R.id.selectDate);
            targetDate.setOnClickListener(v -> showDatePickerDialog(activity, targetDate));
            userReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Double userWeightKg = dataSnapshot.child("weight").getValue(Double.class);
                        if (userWeightKg != null) {
                            String formattedWeight = String.format("%.2f lbs", userWeightKg);
                            currentWeight.setText(formattedWeight);
                            Log.e(TAG, "Formatted Weight in lbs: " + formattedWeight);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error: " + databaseError.getMessage());
                    Toast.makeText(activity, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


            Button save = dialogView.findViewById(R.id.saved);
            Button cancel = dialogView.findViewById(R.id.cancel);

            save.setOnClickListener(v -> {
                String targetWeightValue = targetWeight.getText().toString();
                String selectedDate = targetDate.getText().toString();

                if (targetWeightValue.isEmpty() || selectedDate.isEmpty()) {
                    // Display a toast message prompting the user to fill up the fields
                    Toast.makeText(activity, "Please fill up target weight and date to proceed", Toast.LENGTH_SHORT).show();
                } else {
                    weightManagementReference.child(uid).child("target_weight").setValue(targetWeightValue);
                    weightManagementReference.child(uid).child("target_date").setValue(selectedDate);

                    dialog.dismiss();
                }
            });


            cancel.setOnClickListener(v -> {
                dialog.dismiss();
            });

            dialog.show();
        }
    }
    //datepicker method
    private void showDatePickerDialog(Activity activity, TextView targetDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, (view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            String selectedDate = sdf.format(calendar.getTime());
            targetDate.setText(selectedDate);
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

}
