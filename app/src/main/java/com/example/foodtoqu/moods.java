package com.example.foodtoqu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class moods extends AppCompatActivity {
    TextView greetings;
    private Spinner moodSpinner;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, MyService.class));
        setContentView(R.layout.activity_moods);
        greetings = findViewById(R.id.greet);
        moodSpinner = findViewById(R.id.MoodSpinner);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        updateGreeting();
        databaseReference = FirebaseDatabase.getInstance().getReference("User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.moods_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moodSpinner.setAdapter(adapter);

        // Set an item selected listener for the spinner
        moodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected mood
                String selectedMood = parentView.getItemAtPosition(position).toString();

                // Check if the selected mood is not "Select Mood" before saving to Firebase
                if (!selectedMood.equals("Select Mood")) {
                    saveMoodToFirebase(selectedMood);
                    Toast.makeText(getApplicationContext(),"Mood Saved Successfully",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(moods.this, bodymass_index.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    finish();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }

    // Function to save the selected mood to Firebase Realtime Database
    private void saveMoodToFirebase(String selectedMood) {
        databaseReference.child("mood").setValue(selectedMood);
    }

    private void updateGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String greetingText;

        if (hour >= 4 && hour < 12) {
            greetingText = "Good Morning !";
        } else if (hour >= 12 && hour < 18) {
            greetingText = "Good Afternoon !";
        } else {
            greetingText = "Good Evening !";
        }

        greetings.setText(greetingText);

    }
}