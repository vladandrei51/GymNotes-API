package com.example.vlada.licenta.domain;

public class Set {
    private int id;
    private int weight;
    private int reps;
    private String exerciseName;

    public Set(int id, int weight, int reps, String exerciseName) {
        this.id = id;
        this.weight = weight;
        this.reps = reps;
        this.exerciseName = exerciseName;
    }

    public int getId() {
        return id;
    }

    public int getWeight() {
        return weight;
    }

    public int getReps() {
        return reps;
    }

    public String getExerciseName() {
        return exerciseName;
    }
}
