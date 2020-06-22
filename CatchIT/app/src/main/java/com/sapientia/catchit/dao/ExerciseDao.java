package com.sapientia.catchit.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sapientia.catchit.models.Exercise;

import java.util.List;

@Dao
public interface ExerciseDao
{
    @Insert
    void insertExercise(Exercise exercise);

    @Update
    void updateExercise(Exercise exercise);

    @Delete
    void deleteExercise(Exercise exercise);

    @Query("SELECT * FROM exercises")
    List<Exercise> getAllExercises();

    @Query("SELECT * FROM exercises WHERE createdBy = :creator ")
    List<Exercise> getExercisesByCreator(String creator);

    @Query("SELECT * FROM exercises WHERE exerciseName = :name ")
    Exercise getExercisesByName(String name);

}


