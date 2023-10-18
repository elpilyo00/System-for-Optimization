package com.example.foodtoqu;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class bodymass_index extends AppCompatActivity {

    private EditText weightInPoundsEditText;
    private EditText feetEditText;
    private EditText inchesEditText;
    private TextView bodyMassIndexTextView;
    private Button saveButton;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bodymass_index);
        startService(new Intent(this, MyService.class));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        weightInPoundsEditText = findViewById(R.id.weight_in_pounds);
        feetEditText = findViewById(R.id.feet);
        inchesEditText = findViewById(R.id.inches);
        bodyMassIndexTextView = findViewById(R.id.body_massIndex);
        saveButton = findViewById(R.id.saved);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Add TextChangedListeners to EditText fields
        weightInPoundsEditText.addTextChangedListener(textWatcher);
        feetEditText.addTextChangedListener(textWatcher);
        inchesEditText.addTextChangedListener(textWatcher);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateAndSaveBMI();
            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Not used in this case
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Calculate and update BMI whenever any EditText field changes
            calculateBMI();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // Not used in this case
        }
    };

    private void calculateBMI() {
        String weightStr = weightInPoundsEditText.getText().toString();
        String feetStr = feetEditText.getText().toString();
        String inchesStr = inchesEditText.getText().toString();

        if (!weightStr.isEmpty() && !feetStr.isEmpty() && !inchesStr.isEmpty()) {
            double weightInPounds = Double.parseDouble(weightStr);
            double feet = Double.parseDouble(feetStr);
            double inches = Double.parseDouble(inchesStr);

            // Calculate BMI
            double heightInInches = (feet * 12) + inches;
            double bmi = (weightInPounds / (heightInInches * heightInInches)) * 703;

            // Display BMI
            bodyMassIndexTextView.setText("Body mass index: " + String.format("%.2f", bmi));
        } else {
            // Clear BMI if any of the input fields are empty
            bodyMassIndexTextView.setText("");
        }
    }

    private void calculateAndSaveBMI() {
        String weightStr = weightInPoundsEditText.getText().toString();
        String feetStr = feetEditText.getText().toString();
        String inchesStr = inchesEditText.getText().toString();

        if (weightStr.isEmpty() || feetStr.isEmpty() || inchesStr.isEmpty()) {
            // Show a warning toast message if any of the input fields are empty
            Toast.makeText(this, "Please fill up all fields to proceed.", Toast.LENGTH_SHORT).show();
            return;
        }

        calculateBMI();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            String bmiStr = bodyMassIndexTextView.getText().toString();

            if (!bmiStr.isEmpty()) {
                double bmi = Double.parseDouble(bmiStr.substring(19)); // Remove "Body mass index: "
                String category = calculateBMICategory(bmi);
                String healthRisk = calculateHealthRisk(category);

                // Create an instance of BMIInfo
                BMIInfo bmiInfo = new BMIInfo(bmi, category, healthRisk);

                // Save BMIInfo to Firebase Realtime Database
                databaseReference.child("Body_massIndex").child(uid).setValue(bmiInfo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Data saved successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                    finish();
                                } else {
                                    // Handle the error
                                }
                            }
                        });
            }
        }
    }


    private String calculateBMICategory(double bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 24.9) {
            return "Normal Weight";
        } else if (bmi < 29.9) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }

    private String calculateHealthRisk(String category) {
        if (category.equals("Underweight")) {
            return "Malnutrition risk";
        } else if (category.equals("Normal Weight")) {
            return "Low risk";
        } else if (category.equals("Overweight")) {
            return "Enhanced risk";
        } else {
            return "High risk";
        }
    }

}