package com.example.foodtoqu.Dialog_utils;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.foodtoqu.FoddDetailActivity5;
import com.example.foodtoqu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class Dialog2 {
    private FoddDetailActivity5 mActivity;

    public Dialog2(FoddDetailActivity5 activity) {
        this.mActivity = activity;
    }
    public void showMealSelectionDialog(Activity activity) {
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.checkbox_dialog_layout))
                .setContentWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setCancelable(false)
                .create();
        View dialogView = dialog.getHolderView();
        CheckBox checkboxBreakfast = dialogView.findViewById(R.id.checkbox_breakfast);
        CheckBox checkboxLunch = dialogView.findViewById(R.id.checkbox_lunch);
        CheckBox checkboxDinner = dialogView.findViewById(R.id.checkbox_dinner);
        Button btnSaveSelection = dialogView.findViewById(R.id.btnSaveSelection);
        Button cancel = dialogView.findViewById(R.id.cancel);
        btnSaveSelection.setOnClickListener(v -> {
            // Save the selection when the button is clicked
            String selectedMeal = getSelectedMeal(checkboxBreakfast, checkboxLunch, checkboxDinner);
            if (selectedMeal != null) {
                handleMealSelection(selectedMeal);
                dialog.dismiss();
            } else {
                // Handle case where no meal is selected
                Toast.makeText(activity, "Please select a meal", Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    private String getSelectedMeal(CheckBox checkboxBreakfast, CheckBox checkboxLunch, CheckBox checkboxDinner) {
        if (checkboxBreakfast.isChecked()) {
            return "Breakfast";
        } else if (checkboxLunch.isChecked()) {
            return "Lunch";
        } else if (checkboxDinner.isChecked()) {
            return "Dinner";
        }
        return null;
    }
    private void handleMealSelection(String selectedMeal) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userUid = user.getUid();
            mActivity.saveFoodToDatabase(selectedMeal, userUid);
        }
    }
}