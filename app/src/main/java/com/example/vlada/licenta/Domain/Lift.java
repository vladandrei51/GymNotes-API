package com.example.vlada.licenta.Domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class Lift extends RealmObject {

    private int id;

    private String notes;

    private int reps;

    private Date setDate;

    private int weight;

    //bi-directional many-to-one association to Exercise
    private Exercise exercise;

    @Required
    private String exercise_name;

    public Lift() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
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
        this.exercise_name = this.exercise.getName();
    }

    public String getExercise_name() {
        return exercise_name;
    }

    public String toPrettyString() {
        return String.format("Weight = " + weight + "\n\n" +
                "Reps = " + reps + "\n\n" +
                "Notes = " + notes + "\n\n" +
                "Date = " + new SimpleDateFormat("HH:mm  dd MMM. yyyy ", Locale.US).format(setDate));
    }

}
