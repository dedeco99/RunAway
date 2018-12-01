package com.runaway.runaway;

public class MealItem {
    private String id;
    private String name;
    private int calories;
    private String time;

    public MealItem(String mealId, String mealName, int mealCalories, String mealTime){
        id = mealId;
        name = mealName;
        calories = mealCalories;
        time = mealTime;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCalories() { return calories; }

    public String getTime() {
        return time;
    }
}
