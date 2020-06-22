package com.sapientia.catchit.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "trainers")
public class Trainer
{

    @PrimaryKey(autoGenerate = false)
    private Integer id = 0;
    @ColumnInfo(name = "userName")
    private String userName;
    @ColumnInfo(name = "password")
    private String password;
    @ColumnInfo(name = "birthYear")
    private int birthYear;
    @ColumnInfo(name = "birthMonth")
    private int birthMonth;
    @ColumnInfo(name = "birthDay")
    private int birthDay;
    // male = 1; female = 2;
    @ColumnInfo(name = "gender")
    private int gender;
    private int automaticLogin = 0;

    public Trainer(String userName, String password, int birthYear, int birthMonth, int birthDay, int gender)
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
