package com.example.vlada.licenta.databaseUtils;

public interface DatabaseContent {

    String DATABASE_NAME = "gymnotes.db";

    String TABLE_EXERCISE = "Exercise";
    String COLUMN_EXERCISE_NAME = "name";
    String COLUMN_EXERCISE_MUSCLE_GROUP = "muscle_group";

    String TABLE_SETS = "Sets";
    String COLUMN_SETS_ID = "id";
    String COLUMN_SETS_WEIGHT = "weight";
    String COLUMN_SETS_REPS = "reps";
    String COLUMN_SETS_EXERCISE = "exercise_name";


    String CREATE_EXERCISE_TABLE = "CREATE TABLE " + TABLE_EXERCISE + " ("
            + COLUMN_EXERCISE_NAME + " TEXT PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_EXERCISE_MUSCLE_GROUP + " TEXT" + ")";

    String DROP_EXERCISE_TABLE = "DROP TABLE IF EXISTS " + TABLE_SETS;



    String CREATE_SETS_TABLE = "CREATE TABLE " + TABLE_SETS + " ("
            + COLUMN_SETS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_SETS_WEIGHT + " INTEGER, "
            + COLUMN_SETS_REPS + " INTEGER, " + COLUMN_SETS_EXERCISE + " TEXT, "
            + "FOREIGN KEY (" + COLUMN_SETS_EXERCISE + ") REFERENCES " + TABLE_SETS +
            "(" + COLUMN_EXERCISE_NAME + ")" + ")";


    String DROP_SETS_TABLE = "DROP TABLE IF EXISTS " + TABLE_SETS;


}
