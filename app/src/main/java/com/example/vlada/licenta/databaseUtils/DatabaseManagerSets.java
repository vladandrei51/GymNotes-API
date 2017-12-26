package com.example.vlada.licenta.databaseUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.vlada.licenta.domain.Exercise;
import com.example.vlada.licenta.domain.Set;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManagerSets {
    private static DatabaseManagerSets _DBManager;
    private DatabaseHelper _DBHelper;
    private SQLiteDatabase _database;
    private Context _context;

    private DatabaseManagerSets(Context context){
        _context = context;
        _DBHelper = new DatabaseHelper(_context);
    }

    public static DatabaseManagerSets getInstance(Context context){
        if (_DBManager == null){
           _DBManager = new DatabaseManagerSets(context);
        }
        return _DBManager;
    }

    public void closeDatabase(){
        if (_database != null){
            _database.close();
        }
    }

    public void clearDatabase(){
        _database = _DBHelper.getWritableDatabase();
        _database.delete(DatabaseContent.TABLE_SETS, null, null);
    }

    public long add(Set set){
        _database = _DBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContent.COLUMN_SETS_REPS, set.getReps());
        contentValues.put(DatabaseContent.COLUMN_SETS_WEIGHT, set.getWeight());
        contentValues.put(DatabaseContent.COLUMN_SETS_EXERCISE, set.getExerciseName());
        return _database.insertWithOnConflict(DatabaseContent.TABLE_SETS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public boolean delete(Set set){
        return _database.delete(DatabaseContent.TABLE_SETS, DatabaseContent.COLUMN_SETS_ID+ "=" + set.getId(), null) > 0;
    }

    public List<Set> getStepsByExercise(Exercise exercise){
        ArrayList<Set> sets = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseContent.TABLE_SETS + " WHERE exercise_name=?";
        Cursor c = _database.rawQuery(selectQuery, new String[] { exercise.getName()});
        if (c.moveToFirst()) {
            Set set = new Set(c.getInt(c.getColumnIndex("id")), c.getInt(c.getColumnIndex("weight")),
                    c.getInt(c.getColumnIndex("reps")), c.getString(c.getColumnIndex("exercise_name")));
            sets.add(set);
        }
        c.close();
        return sets;
    }

    public List<Set> getAll(){
        Cursor c = _database.rawQuery("select * from " + DatabaseContent.TABLE_SETS,null);
        List<Set> sets = new ArrayList<>();
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                Set set = new Set(c.getInt(c.getColumnIndex("id")), c.getInt(c.getColumnIndex("weight")),
                        c.getInt(c.getColumnIndex("reps")), c.getString(c.getColumnIndex("exercise_name")));
                sets.add(set);
                c.moveToNext();
            }
        }
        c.close();
        return sets;
    }


}
