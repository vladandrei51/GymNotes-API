package com.example.vlada.licenta.Domain;

import java.util.ArrayList;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Required;

public class GeneratedExercises extends RealmObject {
    @Required
    private Date mPrettyDate;

    private RealmList<String> mWeakBodyParts;

    public GeneratedExercises() {
    }

    public GeneratedExercises(RealmList<String> weakBodyParts) {
        mPrettyDate = new Date();
        this.mWeakBodyParts = weakBodyParts;
    }

    public ArrayList<String> getArrWeakBodyParts() {
        ArrayList<String> returnMe = new ArrayList<>();
        returnMe.addAll(mWeakBodyParts);
        return returnMe;
    }

    public Date getmPrettyDate() {
        return mPrettyDate;
    }
}
