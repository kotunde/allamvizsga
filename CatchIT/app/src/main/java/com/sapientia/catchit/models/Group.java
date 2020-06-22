package com.sapientia.catchit.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "groups")
public class Group
{
    @PrimaryKey(autoGenerate = true)
    private long id = 0;
    private String groupName;
    private String owner;
    private int numberOfPlayers;

    public Group(String groupName, String owner)
    {
        this.groupName = groupName;
        this.owner = owner;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public void setNumberOfPlayers(int numberOfPlayers)
    {
        this.numberOfPlayers = numberOfPlayers;
    }

    public long getId()
    {
        return id;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public String getOwner()
    {
        return owner;
    }

    public int getNumberOfPlayers()
    {
        return numberOfPlayers;
    }
}
