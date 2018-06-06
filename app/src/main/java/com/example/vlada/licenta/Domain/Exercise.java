package com.example.vlada.licenta.Domain;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Exercise extends RealmObject {
    @PrimaryKey
    private int id;

    private String musclegroup;

    private String name;

    private String picsUrl;

    private String description;

    private float rating;

    private String videoUrl;

    private String type;

    @Ignore
    private List<Lift> lifts;


    public Exercise() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMusclegroup() {
        return this.musclegroup;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicsUrl() {
        return this.picsUrl;
    }

    public float getRating() {
        return this.rating;
    }

    public String getDescription() {
        return description;
    }

    public List<Lift> getLifts() {
        return this.lifts;
    }

    public String getType() {
        return type;
    }

    public Lift addLift(Lift lift) {
        getLifts().add(lift);
        lift.setExercise(this);

        return lift;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Exercise exercise = (Exercise) o;

        return (musclegroup != null ? musclegroup.equals(exercise.musclegroup) : exercise.musclegroup == null) && (name != null ? name.equals(exercise.name) : exercise.name == null);
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "id=" + id +
                ", musclegroup='" + musclegroup + '\'' +
                ", name='" + name + '\'' +
                ", picsUrl='" + picsUrl + '\'' +
                ", description='" + description + '\'' +
                ", rating='" + rating + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
