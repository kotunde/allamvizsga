package com.sapientia.catchit.models;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "exercises")
public class Exercise
{
    @PrimaryKey(autoGenerate = true)
    private int id;

    //all data is public since it's a struct-like class
    public int rows = 4;
    public int columns = 4;
    public String createdBy = "";
    public int repeat = 1;
    public double delay = 1;
    public double probability = 0.75;
    public String exerciseName = "";
    public List<Integer> sequence = new ArrayList<>();



    public Exercise(ArrayList<Integer> sequence, int repeat, double delay, double probability, String name)
    {
        this.sequence = sequence;
        this.repeat = repeat;
        this.delay = delay;
        this.probability = probability;
        this.exerciseName = name;
    }
    public Exercise()
    {}

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
}