package com.example.foodtoqu;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DiaryListActivity3 extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DiaryEntryAdapter2 adapter;
    private DatabaseReference databaseReference;
    private BarChart barChart;
    private ImageButton diaryButton;
    private ImageButton userButton;
    private ImageView img;
    TextView names;
    private Calendar selectedDate  = Calendar.getInstance();
    Button filterByDateButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list3);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        TextView dateTextView = findViewById(R.id.date);
        diaryButton = findViewById(R.id.diary);
        userButton = findViewById(R.id.user);
        img = findViewById(R.id.UserImage);
        names = findViewById(R.id.name);
        filterByDateButton = findViewById(R.id.filterByDateButton);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DiaryEntryAdapter2(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // Initialize BarChart
        barChart = findViewById(R.id.bar_chart);

        String uid = getIntent().getStringExtra("uid");


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User4 user = dataSnapshot.getValue(User4.class);
                    if (user != null) {
                        // Load user's image using Picasso
                        Picasso.get().load(user.getImage()).into(img);

                        // Set user's name to TextView
                        names.setText(user.getName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });


        filterByDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(selectedDate);
            }
        });

        // Build the Firebase Realtime Database reference using the UID
        databaseReference = FirebaseDatabase.getInstance().getReference("diary").child(uid);
        // Retrieve data from Firebase and update the RecyclerView and BarChart
        fetchDataFromFirebase(selectedDate);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy", Locale.getDefault());
        String localDateTime = dateFormat.format(calendar.getTime());
        dateTextView.setText(localDateTime);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();

        // Check the third item (index 2)
        MenuItem menuItem = menu.getItem(2);
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
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DiaryListActivity3.this);
                        builder.setTitle("Logout");
                        builder.setMessage("Are you sure you want to logout?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                overridePendingTransition(0, 0);
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


        diaryButton.setBackgroundResource(R.drawable.button_highlight);
        userButton.setBackgroundResource(R.drawable.button_highlight);

        diaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Diary", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), DiaryListActivity2.class);
                startActivity(i);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Food buddy", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), User_search2.class);
                startActivity(i);
                overridePendingTransition(0, 0);
                finish();
            }
        });

    }
    private void showDatePickerDialog(Calendar selectedDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate.set(year, month, dayOfMonth);

                        // Call the fetchDataFromFirebase method with the selected date
                        fetchDataFromFirebase(selectedDate);
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }


    private void fetchDataFromFirebase(Calendar selectedDate) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Object> diaryEntries = new ArrayList<>();
                List<BarEntry> barEntries = new ArrayList<>();
                String currentSection = null;

                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);

                for (DataSnapshot mealSnapshot : dataSnapshot.getChildren()) {
                    String mealType = mealSnapshot.getKey();
                    float totalCalorie = 0f;
                    float totalFat = 0f;
                    float totalCholesterol = 0f;
                    float totalSodium = 0f;
                    float totalCarbo = 0f;
                    float totalTotalSugar = 0f;
                    float totalProtein = 0f;

                    for (DataSnapshot entrySnapshot : mealSnapshot.getChildren()) {
                        FoodItem2 diaryEntry = entrySnapshot.getValue(FoodItem2.class);
                        String mealDate = diaryEntry.getDate();
                        Calendar entryCalendar = Calendar.getInstance();
                        try {
                            entryCalendar.setTime(sdf.parse(mealDate));

                            if (selectedDate.get(Calendar.YEAR) == entryCalendar.get(Calendar.YEAR) &&
                                    selectedDate.get(Calendar.MONTH) == entryCalendar.get(Calendar.MONTH) &&
                                    selectedDate.get(Calendar.DAY_OF_MONTH) == entryCalendar.get(Calendar.DAY_OF_MONTH)) {

                                String sectionHeader = mealType + " - " + mealDate;

                                if (!sectionHeader.equals(currentSection)) {
                                    currentSection = sectionHeader;
                                    diaryEntries.add(currentSection);
                                }

                                diaryEntries.add(diaryEntry);
                            }

                            // Always collect data for bar entries
                            totalCalorie += Float.parseFloat(diaryEntry.getCalorie());
                            totalFat += Float.parseFloat(diaryEntry.getTotalFat());
                            totalCholesterol += Float.parseFloat(diaryEntry.getCholesterol());
                            totalSodium += Float.parseFloat(diaryEntry.getSodium());
                            totalCarbo += Float.parseFloat(diaryEntry.getCarbo());
                            totalTotalSugar += Float.parseFloat(diaryEntry.getTotalSugar());
                            totalProtein += Float.parseFloat(diaryEntry.getProtein());
                        } catch (ParseException e) {
                            // Handle parsing exception if needed
                        }
                    }

                    // Add bar entries for all meal types without filtering
                    barEntries.add(new BarEntry(barEntries.size(), totalCalorie));
                    barEntries.add(new BarEntry(barEntries.size(), totalFat));
                    barEntries.add(new BarEntry(barEntries.size(), totalCholesterol));
                    barEntries.add(new BarEntry(barEntries.size(), totalSodium));
                    barEntries.add(new BarEntry(barEntries.size(), totalCarbo));
                    barEntries.add(new BarEntry(barEntries.size(), totalTotalSugar));
                    barEntries.add(new BarEntry(barEntries.size(), totalProtein));
                }

                adapter.setData(diaryEntries);


                BarDataSet dataSet = new BarDataSet(barEntries, "Nutritional Values");
                BarData barData = new BarData(dataSet);
                barChart.setData(barData);

                // Customize the chart colors
                int[] customColors = {
                        Color.rgb(255, 0, 0),        // Red
                        Color.rgb(0, 255, 0),        // Green
                        Color.rgb(255, 165, 0),      // Orange
                        Color.rgb(0, 0, 255),        // Blue
                        Color.rgb(255, 192, 203),    // Pink
                        Color.rgb(255, 255, 0),      // Yellow
                        Color.rgb(128, 0, 128)       // Purple
                };

                ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                ArrayList<BarEntry> entries = new ArrayList<>();

                for (int i = 0; i < barEntries.size(); i++) {
                    BarEntry entry = barEntries.get(i);
                    entries.add(entry);
                    if (i % 7 == 6 || i == barEntries.size() - 1) {
                        BarDataSet barDataSet = new BarDataSet(entries, "");
                        int colorIndex = i / 7;
                        int color = customColors[colorIndex % customColors.length];
                        barDataSet.setColor(color);
                        barDataSet.setValueTextSize(0f);
                        dataSets.add(barDataSet);
                        entries = new ArrayList<>();
                    }
                }

                BarData customizedBarData = new BarData(dataSets);
                barChart.setData(customizedBarData);

                // Customize the legend
                Legend legend = barChart.getLegend();
                legend.setForm(Legend.LegendForm.SQUARE);
                legend.setTextSize(8f);
                legend.setTextColor(Color.BLACK);
                legend.setXEntrySpace(3f);
                legend.setYEntrySpace(5f);

                // Create custom legend labels with colors and square markers
                String[] customLabels = new String[]{""};//"Calorie", "Fat", "Cholesterol", "Sodium", "Carbohydrates", "Sugar", "Protein" Original Content
                LegendEntry[] legendEntries = new LegendEntry[customLabels.length];
                for (int i = 0; i < customLabels.length; i++) {
                    LegendEntry entry = new LegendEntry();
                    entry.formColor = customColors[i % customColors.length]; // Use the custom color
                    entry.label = customLabels[i];
                    entry.form = Legend.LegendForm.SQUARE;
                    entry.formSize = 20f;
                    legendEntries[i] = entry;
                }

                // Set the custom legend labels
                legend.setCustom(legendEntries);

                // Configure the X-axis position and labels
                XAxis xAxis = barChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(customLabels));

                // Refresh the chart
                barChart.getDescription().setEnabled(false);
                barChart.getLegend().setEnabled(false);
                barChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), User_search2.class);
        startActivity(i);
        overridePendingTransition(0, 0);
        finish();
        super.onBackPressed();
    }
}
