//package com.example.foodtoqu;
//
//import android.os.Bundle;
//import android.view.View;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.Query;
//import com.google.firebase.database.ValueEventListener;
//import java.util.ArrayList;
//import java.util.List;
//
//public class DiaryActivity extends AppCompatActivity {
//
//    private RecyclerView recyclerView;
//    private DiaryEntryAdapter adapter;
//    private List<Object> diaryEntries;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_diary);
//
//        recyclerView = findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        // Initialize the list to store diary entries
//        diaryEntries = new ArrayList<>();
//
//        // Create an instance of the DiaryEntryAdapter and set it on the RecyclerView
//        adapter = new DiaryEntryAdapter(diaryEntries, this);
//        recyclerView.setAdapter(adapter);
//
//        // Retrieve diary entries from Firebase and populate the adapter
//        retrieveDiaryEntries();
//    }
//
//    private void retrieveDiaryEntries() {
//        // Get the current user's UID
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        FirebaseUser user = auth.getCurrentUser();
//        if (user != null) {
//            String userUid = user.getUid();
//
//            // Create a Firebase reference to the user's diary entries
//            DatabaseReference diaryRef = FirebaseDatabase.getInstance().getReference("diary");
//
//            // Add a ValueEventListener to fetch and populate diary entries
//            diaryRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    // Clear the previous entries
//                    diaryEntries.clear();
//
//                    // Iterate through the meal types (e.g., Breakfast, Lunch, Dinner)
//                    for (DataSnapshot mealSnapshot : dataSnapshot.getChildren()) {
//                        String mealType = mealSnapshot.getKey();
//
//                        // Add a section header for each meal type
//                        diaryEntries.add(mealType);
//
//                        // Iterate through the food items for the current meal type
//                        for (DataSnapshot foodSnapshot : mealSnapshot.getChildren()) {
//                            // Deserialize the FoodItem2 object
//                            FoodItem2 foodItem = foodSnapshot.getValue(FoodItem2.class);
//
//                            // Add the food item to the list
//                            diaryEntries.add(foodItem);
//                        }
//                    }
//
//                    // Notify the adapter that the data has changed
//                    adapter.setData(diaryEntries);
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    // Handle any errors
//                }
//            });
//        }
//    }
//
//    // Add any additional functionality or UI elements as needed
//
//}
