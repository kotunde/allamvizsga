package com.sapientia.catchit.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sapientia.catchit.models.Trainer;
import java.util.List;

@Dao
public interface TrainerDao
{
    @Insert
    void insertTrainer(Trainer trainer);

    @Update
    void updateTrainer(Trainer trainer);

    @Delete
    void deleteTrainer(Trainer trainer);

    @Query("DELETE FROM trainers")
    void deleteAllTrainers();

    @Query("SELECT * FROM trainers")
    List<Trainer> getAllTrainers();


    @Query("SELECT * FROM trainers WHERE userName = :name")
    Trainer getTrainerByUserName(String name);

    @Query("SELECT * FROM trainers WHERE automaticLogin = 1")
    Trainer getRememberedUser();

    //void getPasswordByUserName(String name)
    //TODO extend queries
}

