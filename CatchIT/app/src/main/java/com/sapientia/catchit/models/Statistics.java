package com.sapientia.catchit.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "statistics")
public class Statistics
{
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String playerName;
    private String exerciseName;
    private double reaction_average;
    private double nrOfReactions;
    private double reaction_min;
    private double reaction_max;
    private int mistakes;

    public Statistics(String playerName, String exerciseName)
    {
        this.playerName = playerName;
        this.exerciseName = exerciseName;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public String getExerciseName()
    {
        return exerciseName;
    }


    public double getReaction_average() {
        return reaction_average ;
    }

    public double getNrOfReactions() { return nrOfReactions;}

    public double getReaction_max() {
        return reaction_max;
    }

    public double getReaction_min() {
        return reaction_min;
    }


    public int getMistakes() {
        return mistakes;
    }

    public void setMistakes(int mistakes)
    {
        this.mistakes = mistakes;
    }

    public void setReaction_average(double reaction_average)
    {
        this.reaction_average = reaction_average;
    }

    public void setNrOfReactions(double nrOfReactions)
    {
        this.nrOfReactions = nrOfReactions;
    }

    public void setReaction_min(double reaction_min)
    {
        this.reaction_min = reaction_min;
    }

    public void setReaction_max(double reaction_max)
    {
        this.reaction_max = reaction_max;
    }

//    public void addReaction(double reaction)
//    {
//        if (reaction == -1) {
//            mistakes++;
//        } else {
//            reaction_average += reaction;
//            nrOfReactions++;
//            if (reaction < reaction_min) {
//                reaction_min = reaction;
//            }
//            if (reaction > reaction_max) {
//                reaction_max = reaction;
//            }
//        }
//    }
}
