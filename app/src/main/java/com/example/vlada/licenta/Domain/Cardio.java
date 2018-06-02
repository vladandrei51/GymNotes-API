package com.example.vlada.licenta.Domain;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class Cardio extends RealmObject {
    int time_spent;
    Exercise exercise;
    @Required
    String exercise_name;

    public Cardio() {
        time_spent = 0;
        exercise = null;
        exercise_name = "";
    }

    public int getTime_spent() {
        return time_spent;
    }

    public void setTime_spent(int time_spent) {
        this.time_spent = time_spent;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public String getExercise_name() {
        return exercise_name;
    }

    public void setExercise_name(String exercise_name) {
        this.exercise_name = exercise_name;
    }
}
