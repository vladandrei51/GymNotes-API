package com.example.vlada.licenta.Domain;

import java.sql.Timestamp;

public class Lift {
    private int id;

    private String notes;

    private int reps;

    private Timestamp setDate;

    private int weight;

    //bi-directional many-to-one association to Exercise
    private Exercise exercise;

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

    public Timestamp getSetDate() {
        return this.setDate;
    }

    public void setSetDate(Timestamp setDate) {
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
    }
}
