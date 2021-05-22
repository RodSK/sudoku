package com.example.sudoku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.text.BoringLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class GameActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "Level";
    public static final String GAME_STATE = "game_state";
    public String level = "easy";
    GameFieldFragment gameFragment;
    public TextView gameBar;
    public String gameState = "keep";
    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getSupportActionBar().hide();

        Intent intent = getIntent();
        if(intent.getStringExtra(EXTRA_MESSAGE) != null){
            level = intent.getStringExtra(EXTRA_MESSAGE);
        }
        if(intent.getStringExtra(GAME_STATE) != null){
            gameState = intent.getStringExtra(GAME_STATE);
        }

        gameFragment = new GameFieldFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, gameFragment)
                .addToBackStack(null)
                .commit();

        setContentView(R.layout.activity_game);

        gameBar = this.findViewById(R.id.gameBar);

        SQLiteOpenHelper SudokuDatabase = new SudokuDatabase(this);
        db = SudokuDatabase.getWritableDatabase();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(gameState.equals("delete")){
            new Thread (runnableDeleteData).start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cursor != null){
            cursor.close();
        }
        db.close();
    }

//    public Handler threadHandler = new Handler(){
//        public void handleMessage (android.os.Message message){
//            int msg = message.what;
//        }
//    };

    /* ************************************************************ */
    /* ******************* Game Field Buttons ********************* */
    /* ************************************************************ */

    public void goMenu(View view){
        finish();
    }

    public void saveCheckpoint(View view){
        new Thread (runnableSaveData).start();
    }

    public void seeCheckpoints(View view){
        containerReplace(new CheckpointsFragment());
    }

    public void selectNumber (View view){
        Button b = (Button) view;
        String buttonText = b.getText().toString();
        gameFragment.selectNumber(buttonText);
    }

    public void deleteNumber (View view){
        gameFragment.deleteNumber();
    }

    public void useCheckpoint(View view){
        String t = view.getTag().toString();
        runGame(t);
    }

    public void runOldGame(){
        cursor = db.query ("GAME", new String[] {"ID"}, "FINISHED = ?", new String[] {"false"}, null, null, "ID DESC");
        if(cursor.moveToFirst()){
            runGame(cursor.getString(0));
        }else{
            finish();
        }
    }

    public void runGame(String id){
        cursor = db.query ("GAME", new String[] {"ID", "LIST", "LISTC", "SCORE", "LEVEL"}, "ID = ?", new String[] {id}, null, null, null);
        if(cursor.moveToFirst()) {
            containerReplace(gameFragment);
            String[] data = cursor.getString(2).split(",");
            ArrayList<Integer> intList = new ArrayList<Integer>();
            for (int i = 0; i < data.length; i++) {
                intList.add(Integer.parseInt(data[i]));
            }
            gameFragment.tilesComputer = intList;
            gameFragment.tiles = cursor.getString(1).split(",");
            gameFragment.score = cursor.getInt(3);
            level = cursor.getString(4);
            Log.v("test", intList + " | ");
            Log.v("test", cursor.getString(1).split(",")[0] + " | ");
            Log.v("test", cursor.getString(1).split(",")[1] + " | ");
        }
    }

    public void finishGame(){
        db.delete("GAME", "FINISHED = ?", new String[] {"false"});
        dbInsert(true);
        finish();
    }

    public void displayCheckpoints(){
        Button btn;
        String txt = "Saved with Score: ";
        cursor = db.query ("GAME", new String[] {"ID", "SCORE"}, "FINISHED = ?", new String[] {"false"}, null, null, null);

        if(cursor.moveToLast()) {
            btn =  this.findViewById(R.id.check1);
            btn.setTag(cursor.getInt(0));
            btn.setText(txt + cursor.getString(1));
        }
        if(cursor.moveToPrevious()){
            btn =  this.findViewById(R.id.check2);
            btn.setTag(cursor.getInt(0));
            btn.setText(txt + cursor.getString(1));
        }
        if(cursor.moveToPrevious()){
            btn =  this.findViewById(R.id.check3);
            btn.setTag(cursor.getInt(0));
            btn.setText(txt + cursor.getString(1));
        }
        if(cursor.moveToPrevious()){
            btn =  this.findViewById(R.id.check4);
            btn.setTag(cursor.getInt(0));
            btn.setText(txt + cursor.getString(1));
        }
        if(cursor.moveToPrevious()){
            btn =  this.findViewById(R.id.check5);
            btn.setTag(cursor.getInt(0));
            btn.setText(txt + cursor.getString(1));
        }

    }

    private void containerReplace(Fragment f){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, f)
                .addToBackStack(null)
                .commit();
    }

    private Runnable runnableDeleteData = new Runnable() {
        @Override
        public void run() {
            db.delete("GAME", "FINISHED = ?", new String[] {"false"});
        }
    };

    private Runnable runnableSaveData = new Runnable () {
        public void run() {
            dbInsert(false);
        }
    };

    private void dbInsert(Boolean finish){
        String dataTiles = "";
        String dataTilesComputer = "";

        for(int i=0; i<gameFragment.tiles.length; i++){
            dataTiles += gameFragment.tiles[i] + ",";
        }

        Iterator<Integer> iter = gameFragment.tilesComputer.iterator();
        while(iter.hasNext()){
            dataTilesComputer += iter.next() + ",";
        }

        ContentValues values = new ContentValues();
        values.put("LIST", dataTiles);
        values.put("LISTC", dataTilesComputer);
        values.put("SCORE", gameFragment.score);
        values.put("LEVEL", level);
        if(finish){
            values.put("FINISHED", "true");
        }else{
            values.put("FINISHED", "false");
        }
        db.insert("GAME", null, values);
    }

}