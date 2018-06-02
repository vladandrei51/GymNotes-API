package com.example.vlada.licenta.Utils;

import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by andrei-valentin.vlad on 2/23/2018.
 */

public class RealmHelper {
    private Realm mRealm;

    public RealmHelper() {
        this.mRealm = Realm.getDefaultInstance();
    }

    public <E extends RealmObject> RealmObject getRealmObject(Class<E> realmObject, String field, String key) {
        return mRealm.where(realmObject).equalTo(field, key).findFirst();
    }

    public <E extends RealmObject> RealmResults<E> findAllFiltered(Class<E> realmObject, String field, String key) {
        return mRealm.where(realmObject)
                .contains(field, key, Case.INSENSITIVE)
                .findAll();
    }

    public <E extends RealmObject> RealmResults<E> findAllAsyncFiltered(Class<E> realmObject, String field, String key) {
        return mRealm.where(realmObject)
                .contains(field, key, Case.INSENSITIVE)
                .findAll();
    }

    public <E extends RealmObject> RealmResults<E> allStrengthExercisesByRating(Class<E> realmObject, String field, String key) {
        return mRealm.where(realmObject)
                .contains(field, key, Case.INSENSITIVE)
                .and().not().contains("type", "Cardio", Case.INSENSITIVE)
                .and().not().contains("type", "Plyometrics", Case.INSENSITIVE)
                .and().not().contains("type", "Stretching", Case.INSENSITIVE)
                .findAll()
                .sort("rating", Sort.DESCENDING);
    }

    public <E extends RealmObject> RealmResults<E> allCardioExercisesByRating(Class<E> realmObject) {
        return mRealm.where(realmObject)
                .contains("type", "Cardio", Case.INSENSITIVE)
                .or().contains("type", "Plyometrics", Case.INSENSITIVE)
                .or().contains("type", "Stretching", Case.INSENSITIVE)
                .findAll()
                .sort("rating", Sort.DESCENDING);
    }


    public <E extends RealmObject> RealmResults<E> findAllFilteredSorted(Class<E> realmObject, String field, String key, String sortField, Sort sortOrder) {
        return mRealm.where(realmObject)
                .contains(field, key, Case.INSENSITIVE)
                .findAll()
                .sort(sortField, sortOrder);
    }

    public <E extends RealmObject> void deleteAtPosition(RealmResults<E> realmResults, int position) {
        try (Realm r = Realm.getDefaultInstance()) {
            r.executeTransaction(realm -> realmResults.deleteFromRealm(position));
        }
    }

    public void insert(RealmModel object) {
        try (Realm r = Realm.getDefaultInstance()) {
            r.executeTransaction(realm -> realm.insertOrUpdate(object));
        }
    }

    public <E extends RealmObject> void deleteAllStrengthFiltered(Class<E> realmObject, String field, String key) {
        try (Realm r = Realm.getDefaultInstance()) {
            r.executeTransaction(realm -> realm.where(realmObject)
                    .contains(field, key, Case.INSENSITIVE)
                    .findAll()
                    .deleteAllFromRealm());
        }
    }

    public <E extends RealmObject> void deleteAllCardioFiltered(Class<E> realmObject) {
        try (Realm r = Realm.getDefaultInstance()) {
            r.executeTransaction(realm -> realm.where(realmObject)
                    .contains("type", "Cardio", Case.INSENSITIVE)
                    .or().contains("type", "Plyometrics", Case.INSENSITIVE)
                    .or().contains("type", "Stretching", Case.INSENSITIVE)
                    .findAll()
                    .deleteAllFromRealm());
        }
    }


    public <E extends RealmObject> void insertAllFromList(List<E> list) {
        try (Realm r = Realm.getDefaultInstance()) {
            r.executeTransaction(realm -> {
                for (E object : list) {
                    realm.insertOrUpdate(object);
                }
            });
        }
    }

}
