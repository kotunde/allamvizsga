package com.sapientia.catchit.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sapientia.catchit.models.Group;

import java.util.List;

@Dao
public interface GroupDao
{
    @Insert
    long insertGroup(Group group);

    @Update
    void updateGroup(Group group);

    @Delete
    void deleteGroup(Group group);

    @Query("SELECT * FROM groups")
    List<Group> getAllGroups();

    @Query("Select groups.* FROM groups " +
            "WHERE groups.owner = :userName OR groups.id IN " +
            "(SELECT groupId FROM group_players WHERE playerName = :userName)")
    List<Group> getUsersGroups(String userName);

    @Query("UPDATE groups SET numberOfPlayers = :numberOfPlayers WHERE id = :id")
    void updateNumberOfPlayers(int numberOfPlayers, long id);

    @Query("SELECT numberOfPlayers FROM groups WHERE id = :id")
    int selectNumberOfPlayersById(Integer id);

}
