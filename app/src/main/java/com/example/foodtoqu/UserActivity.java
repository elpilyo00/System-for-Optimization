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
    ArrayList<DataClass> list;
    UserAdapter adapter;
    int processedReferences = 0;
    CheckBox diabetesCB, gastroCB, bowelCB, highBloodCB, weightCB, anemiaCB, cholesterolCB, heartCB, osteoporosisCB, celiacCB, renalCB, hypothyroidismCB;
    CheckBox obesity, Arthritis, Mental_health, gastroes, autoimmune;
    CheckBox happyCB, sadCB, angryCB, stressCB, excitedCB, nostalgiaCB, inLoveCB, calmCB;
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
        categoryImageView = findViewById(R.id.categoryImage);
        gastroCB = findViewById(R.id.gastrointestinal);
        bowelCB = findViewById(R.id.bowel);
        highBloodCB = findViewById(R.id.highBlood);
        weightCB = findViewById(R.id.weight);
        anemiaCB = findViewById(R.id.anemia);

        cholesterolCB = findViewById(R.id.highCholesterol);
        heartCB = findViewById(R.id.heartDisease);
        osteoporosisCB = findViewById(R.id.osteoporosis);
        celiacCB = findViewById(R.id.celiac);
        renalCB = findViewById(R.id.renal);
        fullName = findViewById(R.id.name);
        hypothyroidismCB = findViewById(R.id.hypothyroidism);
        obesity = findViewById(R.id.obesity);
        Arthritis = findViewById(R.id.Arthritis);
        Mental_health = findViewById(R.id.Mental_health);
        gastroes = findViewById(R.id.gastroes2);
        autoimmune = findViewById(R.id.Autoimmune);

        //Moods

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
                                stopService(new Intent(UserActivity.this,MyService.class));
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

    }


    private void applyFilters() {
        // Create arrays for mood RadioGroups
        RadioGroup[] moodRadioGroups = {
                findViewById(R.id.mood_radio_group),
                findViewById(R.id.mood_radio_group2),
                findViewById(R.id.mood_radio_group3)
        };

        // Create an array for health condition CheckBoxes
        CheckBox[] healthConditionCheckboxes = {diabetesCB, gastroCB, bowelCB, highBloodCB, weightCB, anemiaCB, cholesterolCB, heartCB, osteoporosisCB, celiacCB, renalCB, hypothyroidismCB, obesity, Arthritis, Mental_health, gastroes, autoimmune};

        // Check if at least one mood RadioButton or health condition checkbox is selected
        boolean anyFilterSelected = false;

        // Check mood RadioButtons
        for (RadioGroup moodRadioGroup : moodRadioGroups) {
            int selectedRadioButtonId = moodRadioGroup.getCheckedRadioButtonId();
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

        // If no filter is selected, re-enter the UserActivity
        if (!anyFilterSelected) {
            Intent intent = new Intent(UserActivity.this, UserActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
            return;
        }

        // Rest of your filter logic here...
        // Create a list to store the filtered foods
        List<Food3> filteredFoods = new ArrayList<>();

        // Loop through the data and apply filters only if at least one filter is selected
        if (anyFilterSelected) {
            for (Food3 food : foodList) {
                boolean moodMatched = false;
                boolean healthConditionMatched = false;

                // Check mood RadioButtons
                for (RadioGroup moodRadioGroup : moodRadioGroups) {
                    int selectedRadioButtonId = moodRadioGroup.getCheckedRadioButtonId();
                    if (selectedRadioButtonId != -1) {
                        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                        if (containsIgnoreCase(food.getMoods(), selectedRadioButton.getText().toString())) {
                            moodMatched = true;
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

                // If mood or health condition matched, add the food to the filtered list
                if (moodMatched || healthConditionMatched) {
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
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(key) && entry.getValue()) {
                return true;
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
                    String condition = dataSnapshot.child("health").getValue(String.class);
                    String mood = dataSnapshot.child("mood").getValue(String.class);

                    // Set the image URL to the ImageView's tag for later retrieval
                    categoryImageView.setTag(imageUrl);
                    fullName.setText(name);
                   // conditions.setText(condition);
                   // moods23.setText(mood);

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