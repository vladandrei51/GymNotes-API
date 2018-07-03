package com.example.vlada.licenta.Domain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Required;

import static com.example.vlada.licenta.Views.ExerciseGenerator.ExerciseGeneratorView.DATE_FORMAT;

public class GeneratedExercises extends RealmObject {
    @Required
    private String mPrettyDate;

    private RealmList<String> mWeakBodyParts;

    public GeneratedExercises() {
    }

    public GeneratedExercises(RealmList<String> weakBodyParts) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        mPrettyDate = simpleDateFormat.format(new Date());
        this.mWeakBodyParts = weakBodyParts;
    }

    public ArrayList<String> getArrWeakBodyParts() {
        ArrayList<String> returnMe = new ArrayList<>();
        returnMe.addAll(mWeakBodyParts);
        return returnMe;
    }

    public String getmPrettyDate() {
        return mPrettyDate;
    }
}
