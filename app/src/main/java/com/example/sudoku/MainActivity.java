package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "Level";
    public static final String GAME_STATE = "game_state";
    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, new ButtonFragment())
                .addToBackStack(null)
                .commit();

        setContentView(R.layout.activity_main);

        SQLiteOpenHelper SudokuDatabase = new SudokuDatabase(this);
        db = SudokuDatabase.getReadableDatabase();

    }

    @Override
    protected void onResume() {
        super.onResume();
        containerReplace(new ButtonFragment());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }

    public void new_game(View view){
        containerReplace(new LevelFragment());
    }

    public void selectLevel(View view){
        String t = view.getTag().toString();

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Level", t);
        startActivity(intent);
    }

    public void continue_game(View view){
        cursor = db.query ("GAME", new String[] {"ID"}, "FINISHED = ?", new String[] {"false"}, null, null, null);
        if(cursor.moveToFirst()){
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("game_state", Integer.toString(cursor.getInt(0)));
            startActivity(intent);
        }
    }

    public void scores_game(View view){
        containerReplace(new ScoreFragment());
    }

    public void rules_game(View view){
        containerReplace(new RulesFragment());
    }

    public void exit_game(View view){
        MainActivity.this.finish();
        System.exit(0);
    }

    private void containerReplace(Fragment f){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, f)
                .addToBackStack(null)
                .commit();
    }

    public void buildPedestal(){
        TextView tx;
        cursor = db.query ("GAME", new String[] {"SCORE", "LEVEL"}, "FINISHED = ?", new String[] {"true"}, null, null, "SCORE DESC");

        if(cursor.moveToFirst()){
            tx = (TextView) this.findViewById(R.id.textView1);
            tx.setText("Gold Medal | Score: " + cursor.getString(0) + " Level: " + cursor.getString(1));
        }
        if(cursor.moveToNext()){
            tx = (TextView) this.findViewById(R.id.textView2);
            tx.setText("Bronze Medal | Score: " + cursor.getString(0) + " Level: " + cursor.getString(1));
        }
        if(cursor.moveToNext()){
            tx = (TextView) this.findViewById(R.id.textView3);
            tx.setText("Silver Medal | Score: " + cursor.getString(0) + " Level: " + cursor.getString(1));
        }
    }

}