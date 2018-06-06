package com.example.vlada.licenta.Domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class Lift extends RealmObject {


    private String notes;

    private int reps;

    private Date setDate;

    private int weight;

    private long date_ms;


    private Exercise exercise;


    @Required
    private String exercise_name;

    public Lift() {
        this.notes = "";
        this.reps = 0;
        this.weight = 0;
        setSetDate(new Date());
    }


    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getReps() {
        return this.reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
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

    public int getWeight() {
        return this.weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Exercise getExercise() {
        return this.exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
        exercise_name = exercise.getName();
    }

    public String getExercise_name() {
        if (exercise != null) return exercise.getName();
        return exercise_name;
    }

    public String date2PrettyString() {
        if (setDate != null)
            return new SimpleDateFormat("dd MMM. yyyy ", Locale.US).format(setDate);
        return "";
    }

}
