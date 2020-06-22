package com.sapientia.catchit.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "players")
public class Player
{
    @PrimaryKey(autoGenerate = false)
    private Integer id = 0;

    private String userName;

    private String password;

    private int birthYear;

    private int birthMonth;

    private int birthDay;
    // male = 1; female = 2;

    private int gender;
    private int automaticLogin = 0;

    public Player(String userName, String password, int birthYear, int birthMonth, int birthDay, int gender)
    {
        this.userName = userName;
        this.password = password;
        this.birthYear = birthYear;
        this.birthMonth = birthMonth;
        this.birthDay = birthDay;
        this.gender = gender;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public int getAutomaticLogin()
    {
        return automaticLogin;
    }

    public void setAutomaticLogin(int automaticLogin)
    {
        this.automaticLogin = automaticLogin;
    }

    public Integer getId()
    {
        return id;
    }

    public String getUserName()
    {
        return userName;
    }

    public String getPassword()
    {
        return password;
    }

    public int getBirthYear()
    {
        return birthYear;
    }

    public int getBirthMonth()
    {
        return birthMonth;
    }

    public int getBirthDay()
    {
        return birthDay;
    }

    public int getGender()
    {
        return gender;
    }
}
