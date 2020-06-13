package com.sapientia.catchit.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sapientia.catchit.models.Statistics;

import java.util.List;

@Dao
public interface StatisticsDao
{
    @Insert
    void insertStatistics(Statistics statistics);

    @Update
    void updateStatistics(Statistics statistics);

    @Delete
    void deleteStatistics(Statistics statistics);

    @Query("SELECT * FROM statistics")
    List<Statistics> getAllStatistics();

    //TODO extend queries for complex statistical results
}
