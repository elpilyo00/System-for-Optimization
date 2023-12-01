package com.example.foodtoqu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.example.foodtoqu.Dialog_utils.Dialog;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class FoodDetailActivityU extends AppCompatActivity {

    MaterialRatingBar ratingBar;
    TextView rate23;
    private ImageView detailImage;
    private TextView detailTitle, detailCalorie, detailFat, detailCholesterol,
            detailSodium, detailCarbo, detailSugar, detailProtein, detailDescription;
    private String foodName;
    private PieChart pieChart;
    private AppCompatButton addDiary;
    private DatabaseReference databaseReference;
    private String imageUrl;
    private FirebaseAuth auth;
    private DatabaseReference userDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail_u);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        pieChart = findViewById(R.id.pieChart);
        // Initialize views
        detailImage = findViewById(R.id.detailImage);
        detailTitle = findViewById(R.id.detailTitle);
        detailCalorie = findViewById(R.id.detailCalorie);
        detailFat = findViewById(R.id.detailFat);
        detailCholesterol = findViewById(R.id.detailCholesterol);
        detailSodium = findViewById(R.id.detailSodium);
        detailCarbo = findViewById(R.id.detailCarbo);
        detailSugar = findViewById(R.id.detailSugar);
        rate23 = findViewById(R.id.ratenum);
        detailProtein = findViewById(R.id.detailProtein);
        detailDescription = findViewById(R.id.detailDescription);
        ratingBar = findViewById(R.id.starRatingBar);
        addDiary = findViewById(R.id.add_btn_diary);

        // Configure the rating bar color based on the rating value
        float rating = getIntent().getFloatExtra("rating", 0.0f);
        int ratingColor = getRatingColor(rating);
        ratingBar.setProgressTintList(ColorStateList.valueOf(ratingColor));

        configurePieChart();
        populatePieChart();
        auth = FirebaseAuth.getInstance();

        // Retrieve data from intent extras
        Intent intent = getIntent();
        foodName = intent.getStringExtra("foodName");
        String calorie = intent.getStringExtra("calorie");
        String totalFat = intent.getStringExtra("totalFat");
        String cholesterol = intent.getStringExtra("cholesterol");
        String sodium = intent.getStringExtra("sodium");
        String carbo = intent.getStringExtra("carbo");
        String totalSugar = intent.getStringExtra("totalSugar");
        String protein = intent.getStringExtra("protein");
        imageUrl = intent.getStringExtra("imageUrl");
        rating = intent.getFloatExtra("rating", 0.0f);
        String description = intent.getStringExtra("description");

        // Set data to views
        detailTitle.setText(foodName);
        detailCalorie.setText(calorie);
        detailFat.setText(totalFat);
        detailCholesterol.setText(cholesterol);
        detailSodium.setText(sodium);
        detailCarbo.setText(carbo);
        detailSugar.setText(totalSugar);
        detailProtein.setText(protein);
        ratingBar.setRating(rating);
        rate23.setText(String.valueOf(rating));
        detailDescription.setText(description);

        // Load the image using Picasso
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.ic_baseline_local_pizza_24)
                .error(R.drawable.ic_baseline_local_pizza_24)
                .into(detailImage);



        addDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a dialog to select the meal
                dialog();
            }
        });

        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("diary");
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("User");


        // Delete button click listener
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),UserActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void dialog() {
        Dialog dialog = new Dialog(this); // Passing the instance of the Activity
        dialog.showMealSelectionDialog(this);
    }
        public void saveFoodToDatabase(String selectedMeal, String userUid) {
        // Get a reference to the "diary" node in your Firebase Realtime Database for the user
        DatabaseReference userDiaryRef = databaseReference.child(userUid);

        // Create a new entry (child node) under the selected meal with a unique key
        String entryKey = userDiaryRef.child(selectedMeal).push().getKey();

        // Prepare the data to be saved
        FoodItem2 foodItem = new FoodItem2();
        foodItem.setFoodName(foodName);
        foodItem.setUid(userUid);
        foodItem.setCalorie(detailCalorie.getText().toString());
        foodItem.setTotalFat(detailFat.getText().toString());
        foodItem.setCholesterol(detailCholesterol.getText().toString());
        foodItem.setSodium(detailSodium.getText().toString());
        foodItem.setCarbo(detailCarbo.getText().toString());
        foodItem.setTotalSugar(detailSugar.getText().toString());
        foodItem.setProtein(detailProtein.getText().toString());
        foodItem.setRating(ratingBar.getRating());
        foodItem.setDescription(detailDescription.getText().toString());
        foodItem.setImageUrl(imageUrl);
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);
        foodItem.setDate(formattedDate);

        // Set meal type based on user selection
        foodItem.setMealType(selectedMeal);

        // Retrieve user's name and image based on UID
        userDatabaseReference.child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String userImage = dataSnapshot.child("image").getValue(String.class);

                    // Add user's name and image to the food item
                    foodItem.setName(name);
                    foodItem.setUserImage(userImage);

                    // Save the data to the database under the unique key
                    userDiaryRef.child(selectedMeal).child(entryKey).setValue(foodItem)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Data saved successfully
                                        Toast.makeText(getApplicationContext(), "Food item saved to diary", Toast.LENGTH_SHORT).show();
                                        dailyIntake();
                                    } else {
                                        // Handle the error
                                        Toast.makeText(getApplicationContext(), "Failed to save food item", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    public void dailyIntake() {
        // Get the current user's UID using Firebase Auth
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            DatabaseReference weightManagementRef = FirebaseDatabase.getInstance().getReference("Weight_management").child(currentUserId);
            weightManagementRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve each daily intake value separately
                        Double currentCalorieIntake = dataSnapshot.child("daily_calorie_intake").getValue(Double.class);
                        Double currentCarbIntake = dataSnapshot.child("daily_carbohydrate_intake").getValue(Double.class);
                        Double currentFatIntake = dataSnapshot.child("daily_fat_intake").getValue(Double.class);
                        Double currentProteinIntake = dataSnapshot.child("daily_protein_intake").getValue(Double.class);

                        // Decrement each value by 1 if they exist
                        if (currentCalorieIntake != null && currentCarbIntake != null && currentFatIntake != null && currentProteinIntake != null) {
                            Double updatedCalorieIntake = currentCalorieIntake - 1;
                            Double updatedCarbIntake = currentCarbIntake - 1;
                            Double updatedFatIntake = currentFatIntake - 1;
                            Double updatedProteinIntake = currentProteinIntake - 1;

                            // Update the values in the database
                            dataSnapshot.getRef().child("daily_calorie_intake").setValue(updatedCalorieIntake);
                            dataSnapshot.getRef().child("daily_carbohydrate_intake").setValue(updatedCarbIntake);
                            dataSnapshot.getRef().child("daily_fat_intake").setValue(updatedFatIntake);
                            dataSnapshot.getRef().child("daily_protein_intake").setValue(updatedProteinIntake)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                // Handle the error
                                                Toast.makeText(getApplicationContext(), "Failed to update daily intake", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors
                }
            });
        }
    }

    private int getRatingColor(float rating) {
        if (rating >= 4.0) {
            // Return green color for high ratings (4.0 and above)
            return ContextCompat.getColor(getApplicationContext(), R.color.yellow);
        } else if (rating >= 3.0) {
            // Return yellow color for medium ratings (3.0 to 3.9)
            return ContextCompat.getColor(getApplicationContext(), R.color.yellow);
        } else {
            // Return red color for low ratings (below 3.0)
            return ContextCompat.getColor(getApplicationContext(), R.color.yellow);
        }
    }

    private void populatePieChart() {
        // Retrieve nutritional information from intent extras
        Intent intent = getIntent();
        String calorie = intent.getStringExtra("calorie");
        String totalFat = intent.getStringExtra("totalFat");
        String cholesterol = intent.getStringExtra("cholesterol");
        String sodium = intent.getStringExtra("sodium");
        String carbo = intent.getStringExtra("carbo");
        String totalSugar = intent.getStringExtra("totalSugar");
        String protein = intent.getStringExtra("protein");

        // Convert the values to integers
        int calorieValue = Integer.parseInt(calorie);
        int totalFatValue = Integer.parseInt(totalFat);
        int cholesterolValue = Integer.parseInt(cholesterol);
        int sodiumValue = Integer.parseInt(sodium);
        int carboValue = Integer.parseInt(carbo);
        int totalSugarValue = Integer.parseInt(totalSugar);
        int proteinValue = Integer.parseInt(protein);

        // Create PieEntries for the nutritional information
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(calorieValue));
        entries.add(new PieEntry(totalFatValue));
        entries.add(new PieEntry(cholesterolValue));
        entries.add(new PieEntry(sodiumValue));
        entries.add(new PieEntry(carboValue));
        entries.add(new PieEntry(totalSugarValue));
        entries.add(new PieEntry(proteinValue));

        PieDataSet dataSet = new PieDataSet(entries, "Nutritional Info");
        dataSet.setColors(Color.rgb(255, 0, 0), Color.rgb(0, 255, 0), Color.rgb(255, 165, 0),
                Color.rgb(0, 0, 255), Color.rgb(255, 192, 203), Color.rgb(255, 255, 0),
                Color.rgb(128, 0, 128));
        dataSet.setValueTextSize(12f);
        dataSet.setDrawValues(true);

        // Set the value formatter to format values as integers
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();
    }



    private void configurePieChart() {
        pieChart.setUsePercentValues(false); // Set to false to display actual values
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(2000);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        pieChart.setEntryLabelTypeface(Typeface.DEFAULT_BOLD);
        pieChart.setEntryLabelTextSize(15f);
        pieChart.setEntryLabelColor(Color.BLACK);

    }



    // Method to delete the food item from Firebase Realtime Database
    private void deleteFoodItem() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("foods");

        // Create a query to find the item with the specified foodName
        Query query = databaseReference.orderByChild("foodName").equalTo(foodName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Delete the item with the specified foodName
                    snapshot.getRef().removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Item deleted successfully
                                        Toast.makeText(getApplicationContext(), "Food item deleted", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getApplicationContext(),AdminActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        // Handle the error
                                        Toast.makeText(getApplicationContext(), "Failed to delete food item", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), UserActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();
    }
}
