package com.example.sudoku;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

public class GameFieldFragment extends Fragment {

    GridView gridView;
    TextView cell;
    private Boolean first = true;
    public String[] tiles = new String[9*9];
    public ArrayList<Integer> tilesComputer = new ArrayList<Integer>();
    public int score = 0;
    private int currentPosition = -1;
    private int oldPosition = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        updateTitle();

        View view = inflater.inflate(R.layout.fragment_game_field, container, false);

        gridView = (GridView) view.findViewById(R.id.gridView1);

        return view;
    }

    @Override
    public void onViewCreated (@NonNull View view, Bundle savedInstanceState) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.grid_cell, tiles);

        gridView.setAdapter(adapter);

        if (first) {
            gameStart();
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if(validSelection(position)){
                    currentPosition = position;
                    setTileWhite(oldPosition);
                    setTileGreen(currentPosition);
                    oldPosition = position;
                }else{
                    currentPosition = -1;
                }
            }
        });

    }

    private void gameStart(){
        Arrays.fill(tiles, " ");
        first = false;
        String level = ((GameActivity) Objects.requireNonNull(getActivity())).level;

        if(level.equals("easy")){
            initGame(40);
        }else if(level.equals("medium")){
            initGame(20);
        }else{
            initGame(10);
        }

       //setTilesColor();
    }

    public void selectNumber(String num){
        if(currentPosition != -1){
            tiles[currentPosition] = num;
            score++;
            updateTitle();
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
            setTileWhite(currentPosition);
            currentPosition = -1;
        }
    }

    public void deleteNumber(){
        if(currentPosition != -1) {
            tiles[currentPosition] = " ";
            score--;
            updateTitle();
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
        }
    }

    private Boolean validSelection(int p){
        Iterator<Integer> iter = tilesComputer.iterator();
        while(iter.hasNext()){
            if(iter.next() == p){
                return false;
            }
        }
        return true;
    }

    private void setTileWhite(int p) {
        cell = (TextView) gridView.getChildAt(oldPosition);
        cell.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }

    private void setTileGreen(int p) {
        cell = (TextView) gridView.getChildAt(p);
        cell.setBackgroundColor(Color.parseColor("#00FF00"));;
    }

    private  void updateTitle(){
        ((GameActivity) getActivity()).gameBar.setText("Score " + score + " | Level: " + ((GameActivity) getActivity()).level);
    }

    public Boolean gameCheck(){
        for(int r=0; r<=72; r+=9){
            for(int c=r+1; c<r+9; c++){
                if(tiles[r].equals(" ") || tiles[c].equals(" ")){
                    return false;
                }
            }
        }

        for(int c=0; c<9; c++){
            for(int r=c+9; r<=72; r+=9){
                if(tiles[c].equals(" ") || tiles[r].equals(" ")){
                    return false;
                }
            }
        }

        return true;
    }

    private void initGame(int lvl){
        int givenNumbers = lvl;
        String value;
        int row;
        int column;
        Random rand = new Random();

        outerloop:
        while (givenNumbers != 0){
            value = Integer.toString(rand.nextInt(9) + 1);
            row = rand.nextInt(9);
            column = rand.nextInt(9);

            if(!tiles[(row * 9) + column].equals(" ")){
                continue outerloop;
            }

            for(int i=0; i<9; i++){
                if(tiles[(row * 9) + i].equals(value) || tiles[column + (i * 9)].equals(value)){
                    continue outerloop;
                }
            }

            tiles[(row * 9) + column] = value;
            tilesComputer.add((row * 9) + column);
            givenNumbers--;

        }

    }

}