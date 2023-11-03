package com.example.foodtoqu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class DiaryEntryAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SECTION_HEADER = 0;
    private static final int VIEW_TYPE_FOOD_ITEM = 1;

    private List<Object> items; // Use a list of Objects to handle both section headers and food items
    private Context context;

    public DiaryEntryAdapter2(List<Object> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_SECTION_HEADER) {
            View sectionHeaderView = inflater.inflate(R.layout.item_section_header, parent, false);
            return new SectionHeaderViewHolder(sectionHeaderView);
        } else {
            View foodItemView = inflater.inflate(R.layout.item_diary_entry, parent, false);
            return new FoodItemViewHolder(foodItemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SectionHeaderViewHolder) {
            SectionHeaderViewHolder sectionHeaderViewHolder = (SectionHeaderViewHolder) holder;
            String sectionTitle = (String) items.get(position);
            sectionHeaderViewHolder.sectionTitleTextView.setText(sectionTitle);
        } else if (holder instanceof FoodItemViewHolder) {
            FoodItemViewHolder foodItemViewHolder = (FoodItemViewHolder) holder;
            FoodItem2 foodItem = (FoodItem2) items.get(position);

            // Bind the food item data here
            foodItemViewHolder.foodNameTextView.setText(foodItem.getFoodName()); // Set the food name
            foodItemViewHolder.cardView.startAnimation(AnimationUtils.loadAnimation(foodItemViewHolder.itemView.getContext(), R.anim.recycler_animation2));


            // Load the image using Picasso
            Glide.with(context)  // Replace 'context' with your actual context
                    .load(foodItem.getImageUrl())
                    .placeholder(R.drawable.ic_baseline_local_pizza_24)
                    .error(R.drawable.ic_baseline_local_pizza_24)
                    .into(foodItemViewHolder.imageView);
            // Set an onClickListener for the food item view
            foodItemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Create an intent to start the FoodDetailActivity2
                    Intent intent = new Intent(context, FoddDetailActivity5.class);

                    // Pass food item details as extras
                    intent.putExtra("foodName", foodItem.getFoodName());
                    intent.putExtra("calorie", foodItem.getCalorie());
                    intent.putExtra("totalFat", foodItem.getTotalFat());
                    intent.putExtra("cholesterol", foodItem.getCholesterol());
                    intent.putExtra("sodium", foodItem.getSodium());
                    intent.putExtra("carbo", foodItem.getCarbo());
                    intent.putExtra("totalSugar", foodItem.getTotalSugar());
                    intent.putExtra("protein", foodItem.getProtein());
                    intent.putExtra("imageUrl", foodItem.getImageUrl());
                    intent.putExtra("rating", foodItem.getRating());
                    intent.putExtra("description", foodItem.getDescription());
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(0, 0); // Disable animation
                    ((Activity) context).finish();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof String) {
            return VIEW_TYPE_SECTION_HEADER;
        } else if (item instanceof FoodItem2) {
            return VIEW_TYPE_FOOD_ITEM;
        }
        return super.getItemViewType(position);
    }

    public void setData(List<Object> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    // ViewHolder for section header
    public class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView sectionTitleTextView;

        public SectionHeaderViewHolder(View itemView) {
            super(itemView);
            sectionTitleTextView = itemView.findViewById(R.id.text_section_title);
        }
    }

    // ViewHolder for food item
    public class FoodItemViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView;
        ImageView imageView;
        CardView cardView;
        public FoodItemViewHolder(View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.text_food_name);
            imageView = itemView.findViewById(R.id.image_view);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
