package com.example.foodtoqu;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserActivity extends AppCompatActivity {
    ImageView categoryImageView;
    AppCompatButton filterBtn, recommendBtn;
    RelativeLayout filterLayout;
    RecyclerView recyclerView2;
    DatabaseReference databaseReference;
    TextView heightText, weightText, bmiTextView, categoryTextView;
    ArrayList<DataClass> list;
    UserAdapter adapter;
    int processedReferences = 0;
    CheckBox diabetesCB,cardioCB,gastroC,osteoporosisCB,hypothyroidismCB,anemiaCB,hypoallergenicCB,
            hyperthyroidCb,PregnantCb,ArthritisCB;
    ValueEventListener eventListener;
    TextView fullName;
    boolean filterHidden = true;
    private List<Food3> foodList;
    private FoodAdapter2 foodAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user);
        startService(new Intent(getApplicationContext(), MyService.class));
        initWidgets();
        hideFilter();
        //CheckBoxes
        //Health Conditions
        diabetesCB = findViewById(R.id.diabetes);
        gastroC = findViewById(R.id.gastro);
        osteoporosisCB = findViewById(R.id.osteo);
        hypoallergenicCB = findViewById(R.id.hypoallergenic);
        hypothyroidismCB = findViewById(R.id.hypothyroidism);
        anemiaCB = findViewById(R.id.anemia);
        hyperthyroidCb = findViewById(R.id.hyper);
        PregnantCb = findViewById(R.id.Pregnant);
        categoryImageView = findViewById(R.id.categoryImage);
        heightText = findViewById(R.id.height);
        weightText = findViewById(R.id.weights);
        bmiTextView = findViewById(R.id.bmi);
        categoryTextView = findViewById(R.id.category);
        cardioCB = findViewById(R.id.cardio);
        ArthritisCB = findViewById(R.id.Arthritis);
        fullName = findViewById(R.id.name);
        recommendBtn = findViewById(R.id.recommendBtn);

        recyclerView2 = findViewById(R.id.recyclerView2);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Intent intent = new Intent();
//            String packageName = getPackageName();
//            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
//            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
//                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
//                intent.setData(Uri.parse("package:" + packageName));
//                startService(new Intent(getApplicationContext(), MyService.class));
//                startActivity(intent);
//            }
//        }
        foodList = new ArrayList<Food3>();
        foodAdapter = new FoodAdapter2(this, foodList);
        int spanCount = 2; // You can adjust the number of columns in the grid as needed
        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        recyclerView2.setLayoutManager(layoutManager);
        recyclerView2.setAdapter(foodAdapter);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("foods");

        recommendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilters();
            }
        });

        categoryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), profs.class);
                startActivity(i);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        // Load all foods initially
        loadFoods();
        retrieveUserDetails();
        retrieveUserDetails2();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();

// Check the third item (index 2)
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        // Handle Home item click
                        Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_LONG).show();
                        Intent homeIntent = new Intent(getApplicationContext(), UserActivity.class);
                        startActivity(homeIntent);
                        overridePendingTransition(0, 0);
                        finish();
                        break;

                    case R.id.heart:
                        Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_LONG).show();
                        Intent homeIntent2 = new Intent(getApplicationContext(), LikedFoodsActivity.class);
                        startActivity(homeIntent2);
                        finish();
                        overridePendingTransition(0, 0);
                        break;

                    case R.id.profile:
                        // Handle Quiz item click
                        Toast.makeText(getApplicationContext(), "profile", Toast.LENGTH_LONG).show();
                        Intent quizIntents = new Intent(getApplicationContext(), profs.class);
                        startActivity(quizIntents);
                        overridePendingTransition(0, 0);
                        finish();
                        break;


                    case R.id.diary:
                        // Handle Quiz item click
                        Toast.makeText(getApplicationContext(), "diary", Toast.LENGTH_LONG).show();
                        Intent diary = new Intent(getApplicationContext(), DiaryListActivity2.class);
                        startActivity(diary);
                        overridePendingTransition(0, 0);
                        finish();
                        break;


                    case R.id.logout:
                        // Handle Profile item click
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UserActivity.this);
                        builder.setTitle("Logout");
                        builder.setMessage("Are you sure you want to logout?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(UserActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                overridePendingTransition(0, 0);
                                stopService(new Intent(UserActivity.this, MyService.class));
                                startActivity(intent);
                                finish();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing
                            }
                        });
                        builder.show();
                        break;
                }
                return true;
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            // Construct the database reference using the UID
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Body_massIndex/" + uid);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Get the BMI category from the data
                        String bmiCategory = dataSnapshot.child("category").getValue(String.class);

                        // Select the corresponding radio button based on the BMI category
                        RadioGroup bmiRadioGroup = findViewById(R.id.bmi_radio_group);
                        if (bmiCategory != null) {
                            switch (bmiCategory) {
                                case "Underweight":
                                    bmiRadioGroup.check(R.id.underweight);
                                    break;
                                case "Normal weight":
                                    bmiRadioGroup.check(R.id.normal_weight);
                                    break;
                                case "Overweight":
                                    bmiRadioGroup.check(R.id.overweight);
                                    break;
                                case "Obesity":
                                    bmiRadioGroup.check(R.id.obese);
                                    break;
                                default:
                                    // Handle any other cases or set a default selection
                                    break;
                            }
                        }

                        // Continue with the rest of your filter logic here...
                        // (The code for filtering food items remains the same)
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }
    }

    private void applyFilters() {
             RadioGroup[] bmiRadioGroups = {findViewById(R.id.bmi_radio_group)
            };

            // Create an array for health condition CheckBoxes
            CheckBox[] healthConditionCheckboxes = { diabetesCB,cardioCB,gastroC,osteoporosisCB,hypothyroidismCB,anemiaCB,hypoallergenicCB,
                    hyperthyroidCb,PregnantCb,ArthritisCB};

            // Check if at least one BMI RadioButton or health condition checkbox is selected
            boolean anyFilterSelected = false;

            // Check BMI RadioButtons
            for (RadioGroup bmiRadioGroup : bmiRadioGroups) {
                int selectedRadioButtonId = bmiRadioGroup.getCheckedRadioButtonId();
                if (selectedRadioButtonId != -1) {
                    anyFilterSelected = true;
                    break;
                }
            }

            // Check health condition checkboxes
            if (!anyFilterSelected) {
                for (CheckBox healthConditionCheckbox : healthConditionCheckboxes) {
                    if (healthConditionCheckbox.isChecked()) {
                        anyFilterSelected = true;
                        break;
                    }
                }
            }

        // Rest of your filter logic here...
        // Create a list to store the filtered foods
        List<Food3> filteredFoods = new ArrayList<>();

        // Loop through the data and apply filters only if at least one filter is selected
        if (anyFilterSelected) {
            for (Food3 food : foodList) {
                boolean bmiMatched = false;
                boolean healthConditionMatched = false;

                // Check BMI RadioButtons
                for (RadioGroup bmiRadioGroup : bmiRadioGroups) {
                    int selectedRadioButtonId = bmiRadioGroup.getCheckedRadioButtonId();
                    if (selectedRadioButtonId != -1) {
                        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                        if (containsIgnoreCase(food.getBMI(), selectedRadioButton.getText().toString())) {
                            bmiMatched = true;
                            break;
                        }
                    }
                }

                // Check health condition checkboxes
                for (CheckBox healthConditionCheckbox : healthConditionCheckboxes) {
                    if (healthConditionCheckbox.isChecked() && containsIgnoreCase(food.getHealthConditions(), healthConditionCheckbox.getText().toString())) {
                        healthConditionMatched = true;
                        break;
                    }
                }

                // If BMI or health condition matched, add the food to the filtered list
                if (bmiMatched || healthConditionMatched) {
                    filteredFoods.add(food);
                }
            }
        } else {
            // If no filter is selected, show all the food items
            filteredFoods.addAll(foodList);
        }

        // Update the RecyclerView with the filtered data
        foodAdapter.setFoods(filteredFoods);
        foodAdapter.notifyDataSetChanged();
    }


    // Helper function to check if a map contains a key (case-insensitive)
    private boolean containsIgnoreCase(Map<String, Boolean> map, String key) {
        if (map != null) {
            for (Map.Entry<String, Boolean> entry : map.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(key) && entry.getValue()) {
                    return true;
                }
            }
        }
        return false;
    }


    private void retrieveUserDetails() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference().child("User").child(userId);
        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String imageUrl = dataSnapshot.child("image").getValue(String.class);
                    String name = dataSnapshot.child("name").getValue(String.class);
                    Double height = dataSnapshot.child("height").getValue(Double.class);
                    Double weight = dataSnapshot.child("weight").getValue(Double.class);

                    // Set the image URL to the ImageView's tag for later retrieval
                    categoryImageView.setTag(imageUrl);
                    fullName.setText(name);

                    // Format height as 5'11
                    if (height != null) {
                        int feet = (int) (height / 30.48);  // 1 foot = 30.48 cm
                        int inches = (int) ((height % 30.48) / 2.54);  // 1 inch = 2.54 cm
                        heightText.setText("Your height: " + feet + "'" + inches + "\"");
                    }

                    // Format weight as "Your weight: XX.XX kg"
                    if (weight != null) {
                        weightText.setText("Your weight: " + String.format("%.2f kg", weight));
                    }

                    // Load the image into the ImageView using a library like Picasso or Glide
                    // For example, using Picasso:
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Picasso.get().load(imageUrl).into(categoryImageView);
                    } else {
                        // Set a default drawable image if the image URL is empty
                        categoryImageView.setImageResource(R.drawable.ic_baseline_person_24);
                    }
                } else {
                    // Handle the case if student data does not exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error if necessary
            }
        });
    }


    private void retrieveUserDetails2() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference bmiRef = FirebaseDatabase.getInstance().getReference().child("Body_massIndex").child(userId);
        bmiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve BMI and category as per the data structure
                    Double bmi = dataSnapshot.child("bmi").getValue(Double.class);

                    if (bmi != null) {
                        // Now you can set the retrieved data to your UI elements
                        String bmiText = "Your BMI: " + String.valueOf(bmi);
                        bmiTextView.setText(bmiText);
                    } else {
                        // Handle the case if the BMI data is null
                        bmiTextView.setText("Your BMI: N/A");
                    }

                    String category = dataSnapshot.child("category").getValue(String.class);
                    categoryTextView.setText(category); // Assuming categoryTextView is a TextView
                } else {
                    // Handle the case if the data for the user doesn't exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error if necessary
            }
        });
    }



    private void loadFoods() {
        // Create a query to order the foods by their name
        Query query = databaseReference.orderByChild("foodName");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                try {
                    Food3 food = dataSnapshot.getValue(Food3.class);
                    if (food != null) {
                        foodList.add(food);
                        foodAdapter.notifyDataSetChanged();
                    } else {
                        // Handle the case where the data couldn't be converted to Food3
                        Log.e(TAG, "Failed to convert data to Food3: " + dataSnapshot.getKey());
                    }
                } catch (DatabaseException e) {
                    // Handle the exception (e.g., log the error)
                    Log.e(TAG, "Error converting data: " + e.getMessage());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Handle the case where a child's data has changed (e.g., rating updated)
                try {
                    Food3 updatedFood = dataSnapshot.getValue(Food3.class);
                    if (updatedFood != null) {
                        // Find the position of the updated item in foodList
                        int position = findPositionById(updatedFood.getFoodId());

                        if (position != -1) {
                            // Update the item in foodList
                            foodList.set(position, updatedFood);

                            // Notify the adapter that the item has changed
                            foodAdapter.notifyItemChanged(position);
                        }
                    }
                } catch (DatabaseException e) {
                    // Handle the exception (e.g., log the error)
                    Log.e(TAG, "Error converting data: " + e.getMessage());
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Handle the case where a child has been removed from the database
                // You should remove the corresponding item from foodList and call foodAdapter.notifyDataSetChanged() here
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Handle the case where a child has changed position within the data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserActivity.this, "Failed to load foods.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int findPositionById(String foodId) {
        for (int i = 0; i < foodList.size(); i++) {
            Food3 food = foodList.get(i);
            if (food != null && food.getFoodId().equals(foodId)) {
                return i; // Found a match, return the position
            }
        }
        return -1; // Item not found in the list
    }



    private void initWidgets() {

        filterBtn = findViewById(R.id.filterBtn);
        filterLayout = findViewById(R.id.filterTab);
    }

    public void showFilterTapped(View view) {
        if (filterHidden == true){
            filterHidden = false;
            showFilter();
        }
        else {
            filterHidden = true;
            hideFilter();
        }

    }
    private void hideFilter() {
        filterLayout.setVisibility(View.GONE);
    }
    private void showFilter() {
        filterLayout.setVisibility(View.VISIBLE);
    }
}