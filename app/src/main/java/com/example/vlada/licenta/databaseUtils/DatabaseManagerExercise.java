package com.example.vlada.licenta.databaseUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.vlada.licenta.domain.Exercise;
import com.example.vlada.licenta.domain.MuscleGroup;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManagerExercise {
    private static DatabaseManagerExercise _DBManager;
    private DatabaseHelper _DBHelper;
    private SQLiteDatabase _database;
    private Context _context;

    private DatabaseManagerExercise(Context context){
        _context = context;
        _DBHelper = new DatabaseHelper(_context);
    }

    public static DatabaseManagerExercise getInstance(Context context){
        if (_DBManager == null){
           _DBManager = new DatabaseManagerExercise(context);
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
        _database.delete(DatabaseContent.TABLE_EXERCISE, null, null);
    }

    public long add(Exercise exercise){
        _database = _DBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContent.COLUMN_EXERCISE_NAME, exercise.getName());
        contentValues.put(DatabaseContent.COLUMN_EXERCISE_MUSCLE_GROUP, exercise.getMuscleGroup().toString());
        return _database.insertWithOnConflict(DatabaseContent.TABLE_EXERCISE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public boolean delete(Exercise exercise){
        return _database.delete(DatabaseContent.TABLE_EXERCISE, DatabaseContent.COLUMN_EXERCISE_NAME+ "=" + exercise.getName(), null) > 0;
    }

    public List<Exercise> getStepsByExercise(MuscleGroup muscleGroup){
        ArrayList<Exercise> exercises = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseContent.TABLE_EXERCISE + " WHERE muscle_group=?";
        Cursor c = _database.rawQuery(selectQuery, new String[] { muscleGroup.getName()});
        if (c.moveToFirst()) {
            Exercise exercise = new Exercise(c.getString(c.getColumnIndex("name")), muscleGroup);
            exercises.add(exercise);
        }
        c.close();
        return exercises;
    }

    public List<Exercise> getAll(){
        Cursor cursor = _database.rawQuery("select * from " + DatabaseContent.TABLE_EXERCISE,null);
        MuscleGroup mg = null;
        List<Exercise> exercises = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                for (MuscleGroup muscleGroup : MuscleGroup.values()){
                    if (muscleGroup.getName().equals(cursor.getString(cursor.getColumnIndex("muscle_group")))){
                       mg = muscleGroup;
                    }
                }
                if (mg != null) {
                    Exercise exercise = new Exercise(cursor.getString(cursor.getColumnIndex("name")), mg);
                    exercises.add(exercise);
                }

                cursor.moveToNext();
            }
        }
        cursor.close();
        return exercises;
    }

}
