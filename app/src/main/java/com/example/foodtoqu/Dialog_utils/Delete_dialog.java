package com.example.foodtoqu.Dialog_utils;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.foodtoqu.FoodDetailActivity;
import com.example.foodtoqu.R;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class Delete_dialog {

    private FoodDetailActivity mActivity;

    public Delete_dialog(FoodDetailActivity activity) {
        this.mActivity = activity;
    }
    public void Delete(Activity activity) {
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setContentHolder(new ViewHolder(R.layout.delete))
                .setContentWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setCancelable(false)
                .create();
        View dialogView = dialog.getHolderView();
        Button yes = dialogView.findViewById(R.id.Yes);
        Button no = dialogView.findViewById(R.id.no);
        yes.setOnClickListener(v -> {
            mActivity.deleteFoodItem();
        });
        no.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();

    }
}

