package com.sapientia.catchit.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "group_players")
public class GroupPlayer
{
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long groupId;
    private String playerName;

    public GroupPlayer(long groupId, String playerName)
    {
        this.groupId = groupId;
        this.playerName = playerName;
    }


    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getGroupId()
    {
        return groupId;
    }

    public String getPlayerName()
    {
        return playerName;
    }
}
