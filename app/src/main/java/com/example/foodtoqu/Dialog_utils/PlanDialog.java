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

import java.text.ParseException;
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

                            Button save = dialogView.findViewById(R.id.saved);
                            save.setOnClickListener(v -> {
                                String targetWeightValue = targetWeight.getText().toString();
                                String selectedDate = targetDate.getText().toString();

                                if (targetWeightValue.isEmpty() || selectedDate.isEmpty()) {
                                    // Display a toast message prompting the user to fill up the fields
                                    Toast.makeText(activity, "Please fill up target weight and date to proceed", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Calculate weight to lose
                                    double targetWeightValueDouble = Double.parseDouble(targetWeightValue);
                                    double weightToLose = userWeightKg - targetWeightValueDouble;

                                    // Convert selected date to Calendar object
                                    Calendar selectedCalendar = Calendar.getInstance();
                                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                                    try {
                                        selectedCalendar.setTime(sdf.parse(selectedDate));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    // Calculate target days based on selected date
                                    int targetDays = calculateTotaldAYS(selectedCalendar);

                                    // Calculate daily calorie intake
                                    double caloricDeficit = weightToLose * 3500.0;
                                    double dailyCaloricDeficit = caloricDeficit / targetDays;
                                    double dailyCalorieIntake = 2000.0 - dailyCaloricDeficit;

                                    // Save target weight and date to Firebase
                                    weightManagementReference.child(uid).child("target_weight").setValue(targetWeightValue);
                                    weightManagementReference.child(uid).child("target_date").setValue(selectedDate);

                                    // Save daily calorie intake to Firebase
                                    weightManagementReference.child(uid).child("daily_calorie_intake").setValue(dailyCalorieIntake);
                                    weightManagementReference.child(uid).child("recent_calorie_intake").setValue(dailyCalorieIntake);
                                    DatabaseReference dailyCalorieIntakeTotalReference = FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("daily_calorie_intake_total")
                                            .child(uid)
                                            .child("total");

                                    dailyCalorieIntakeTotalReference.setValue(dailyCalorieIntake);
                                    calculateAndSaveNutritionalIntake(uid, dailyCalorieIntake, userWeightKg);
                                    dialog.dismiss();
                                }
                            });

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error: " + databaseError.getMessage());
                    Toast.makeText(activity, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            Button cancel = dialogView.findViewById(R.id.cancel);
            cancel.setOnClickListener(v -> {
                dialog.dismiss();
            });

            dialog.show();
        }
    }

    private void calculateAndSaveNutritionalIntake(String uid, double dailyCalorieIntake, double userWeightKg) {
        // Calculate daily protein intake
        double caloriesToProtein = dailyCalorieIntake * 0.20;
        double dailyProteinIntake = caloriesToProtein / 4;

        // Calculate daily fat intake
        double caloriesToFat = dailyCalorieIntake * 0.30;
        double dailyFatIntake = caloriesToFat / 9;

        // Calculate daily carbohydrate intake
        double caloriesToCarbs = dailyCalorieIntake - (caloriesToProtein + caloriesToFat);
        double dailyCarbohydrateIntake = caloriesToCarbs / 4;

        // Save daily nutritional intake to Firebase
        DatabaseReference weightManagementReference = FirebaseDatabase.getInstance().getReference().child("Weight_management");
        weightManagementReference.child(uid).child("daily_protein_intake").setValue(dailyProteinIntake);
        weightManagementReference.child(uid).child("daily_fat_intake").setValue(dailyFatIntake);
        weightManagementReference.child(uid).child("daily_carbohydrate_intake").setValue(dailyCarbohydrateIntake);
    }
    //datepicker method
    private void showDatePickerDialog(Activity activity, TextView targetDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, (view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            String selectedDate = sdf.format(calendar.getTime());
            targetDate.setText(selectedDate);

            int totalDays = calculateTotaldAYS(calendar); // Calculate total days based on selected date
            Log.d("TotalDays", "Total Days: " + totalDays);

            // Save total days to Firebase
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String uid = currentUser.getUid();
                saveTotalDaysToFirebase(uid, totalDays);
            }

        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void saveTotalDaysToFirebase(String uid, int totalDays) {
        DatabaseReference weightManagementReference = FirebaseDatabase.getInstance().getReference().child("Weight_management");
        weightManagementReference.child(uid).child("total_days").setValue(totalDays);
    }

    // Placeholder method for calculating target days
    private int calculateTotaldAYS(Calendar selectedCalendar) {
        Calendar currentCalendar = Calendar.getInstance();
        long differenceInMillis = selectedCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis();
        return (int) (differenceInMillis / (1000 * 60 * 60 * 24)); // Convert milliseconds to days
    }
}