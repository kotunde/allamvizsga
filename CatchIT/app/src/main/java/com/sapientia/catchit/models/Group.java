package com.sapientia.catchit.models;

import java.util.ArrayList;

public class Group
{
    private ArrayList<Player> mPlayerList;
    private String mGroupName;
    public Group(String groupName, ArrayList<Player> players)
    {
        this.mPlayerList = players;
        this.mGroupName = groupName;
    }

    public int getNumberOfPlayers()
    {
        return mPlayerList.size();
    }

    public void addPlayer(Player newPlayer)
    {
        mPlayerList.add(newPlayer);
    }

    public ArrayList<Player> getPlayers()
    {
        return mPlayerList;
    }

    public String getGroupName()
    {
        return mGroupName;
    }

}
