package com.example.vlada.licenta.Domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class Cardio extends RealmObject {
    private int time_spent;
    private Exercise exercise;
    @Required
    private String exercise_name;
    private Date setDate;
    private long date_ms;
    private String notes;


    public Cardio() {
        time_spent = 0;
        exercise = null;
        notes = "";
        setSetDate(new Date());
        exercise_name = "";
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
        this.exercise_name = exercise.getName();
    }

    public String getExercise_name() {
        if (exercise != null) return exercise.getName();
        return exercise_name;
    }

    public void setExercise_name(String exercise_name) {
        this.exercise_name = exercise_name;
    }

    public String date2PrettyString() {
        if (setDate != null)
            return new SimpleDateFormat("dd MMM. yyyy ", Locale.US).format(setDate);
        return "";

    }

    public Date getSetDate() {
        return setDate;
    }

    public void setSetDate(Date setDate) {
        this.setDate = setDate;
        date_ms = setDate.getTime();
    }

    public long getDate_ms() {
        return date_ms;
    }

}
