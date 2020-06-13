package com.sapientia.catchit.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sapientia.catchit.models.Player;

import java.util.List;

@Dao
public interface PlayerDao
{
    @Insert
    void insertPlayer(Player player);

    @Update
    void updatePlayer(Player player);

    @Delete
    void deletePlayer(Player player);

    @Query("DELETE FROM players")
    void deleteAllPlayers();

    @Query("SELECT * FROM players ")
    List<Player> getAllPlayers();

    @Query("SELECT * FROM players WHERE userName = :name")
    Player getPlayerByUserName(String name);

    @Query("SELECT * FROM players WHERE userName = :id")
    Player getPlayerById(int id);

    @Query("SELECT * FROM players WHERE automaticLogin = 1")
    Player getRememberedUser();


    //TODO extend queries
}
