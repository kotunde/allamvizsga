package com.sapientia.catchit.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sapientia.catchit.models.GroupPlayer;

import java.util.List;

//Relation usage: https://stackoverflow.com/questions/45059942/return-type-for-android-room-joins

@Dao
public interface GroupPlayerDao
{
    @Insert
    void insertGroupPlayer(GroupPlayer groupPlayer);

    @Insert
    void insertAllGroupPlayers(List<GroupPlayer> groupPlayerList);

    @Update
    void updateGroupPlayer(GroupPlayer groupPlayer);

    @Delete
    void deleteGroupPlayer(GroupPlayer groupPlayer);

    @Query("DELETE FROM group_players WHERE playerName = :playerName")
    void deleteGroupPlayerByPlayer(String playerName);

    @Query("SELECT * FROM group_players WHERE groupId = :groupId")
    List<GroupPlayer> getGroupPlayersByGroupId(Integer groupId);
}
