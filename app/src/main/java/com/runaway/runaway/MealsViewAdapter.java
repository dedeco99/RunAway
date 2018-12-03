package com.runaway.runaway;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class MealsViewAdapter extends RecyclerView.Adapter<MealsViewAdapter.MealViewHolder> {
    private ArrayList<MealItem> mealList;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener{
        void onDeleteClick(int position);
    }

    void setOnItemClickListener(OnItemClickListener listener){ clickListener = listener; }

    static class MealViewHolder extends RecyclerView.ViewHolder{
        private TextView mealId;
        private TextView mealName;
        //private TextView mealCalories;
        private TextView mealTime;
        private ImageView deleteMeal;

        MealViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            mealId = itemView.findViewById(R.id.mealId);
            mealName = itemView.findViewById(R.id.mealName);
            mealTime = itemView.findViewById(R.id.mealTime);
            deleteMeal = itemView.findViewById(R.id.deleteMeal);

            deleteMeal.setOnClickListener(view -> {
                if(listener!=null){
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onDeleteClick(position);
                    }
                }
            });
        }
    }

    MealsViewAdapter(ArrayList<MealItem> dataSet){
        mealList = dataSet;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.meal_layout, viewGroup, false);

        return new MealViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder mealViewHolder, int i) {
        MealItem currentItem = mealList.get(i);

        mealViewHolder.mealId.setText(currentItem.getId());
        mealViewHolder.mealName.setText(currentItem.getName());
        mealViewHolder.mealTime.setText(currentItem.getTime());
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }
}
