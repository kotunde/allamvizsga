package com.sapientia.catchit.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User
{
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "usename")
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

    public User(int id,String userName, String password, int birthYear, int birthMonth, int birthDay, int gender)
    {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.birthYear = birthYear;
        this.birthMonth = birthMonth;
        this.birthDay = birthDay;
        this.gender = gender;
    }
}
