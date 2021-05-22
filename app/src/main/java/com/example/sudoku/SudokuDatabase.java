package com.example.sudoku;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SudokuDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "sudoku"; // the name of our database
    private static final int DB_VERSION = 2; // the version of the database

    SudokuDatabase (Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE GAME (ID INTEGER PRIMARY KEY AUTOINCREMENT, LIST TEXT, LISTC TEXT, SCORE INTEGER, LEVEL TEXT);");

        insertTestData(db, 12, "easy");
        insertTestData(db, 26, "medium");
        insertTestData(db, 3, "hard");

        db.execSQL("ALTER TABLE GAME ADD COLUMN FINISHED TEXT;");

        updateTestData(db, "FINISHED", "true", 1);
        updateTestData(db, "FINISHED", "true", 2);
        updateTestData(db, "FINISHED", "true", 3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion == 2){
            db.execSQL("ALTER TABLE GAME ADD COLUMN FINISHED TEXT;");
            updateTestData(db, "FINISHED", "true", 1);
            updateTestData(db, "FINISHED", "true", 2);
            updateTestData(db, "FINISHED", "true", 3);
        }
    }

    private static void insertTestData(SQLiteDatabase db, int score, String level){
        ContentValues values = new ContentValues();
        values.put("SCORE", score);
        values.put("LEVEL", level);
        db.insert("GAME", null, values);
    }

    private static void updateTestData(SQLiteDatabase db, String column, String value, int id){
        ContentValues values = new ContentValues();
        values.put(column, value);
        db.update("GAME", values, "ID = ?", new String[] {Integer.toString(id)});
    }
}
