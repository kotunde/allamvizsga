package com.sapientia.catchit.helpers;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

import com.sapientia.catchit.models.Player;


public class DBHelper extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "React.db";
    public static final String TRAINERS_TABLE_NAME = "trainers";
    public static final String PLAYERS_TABLE_NAME = "players";

    public DBHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
//        db.execSQL("CREATE TABLE " + TRAINERS_TABLE_NAME +
//                 "(id integer primary key autoincrement not null, username VARCHAR, password VARCHAR, birthYear INTEGER, birthMonth INTEGER," +
//                "birthDay INTEGER, gender INTEGER)");
//        db.execSQL("CREATE TABLE " + PLAYERS_TABLE_NAME +
//                "(id integer primary key autoincrement not null, username VARCHAR, password VARCHAR, birthYear INTEGER, birthMonth INTEGER," +
//                "birthDay INTEGER, gender INTEGER)");
        db.execSQL("CREATE TABLE " + TRAINERS_TABLE_NAME +
                "(id integer primary key autoincrement not null, trainer VARCHAR)");

        db.execSQL("CREATE TABLE " + PLAYERS_TABLE_NAME +
                "(id integer primary key autoincrement not null, player VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TRAINERS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PLAYERS_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertTrainer(String username, String passwords, int birthYear, int BirthMonth, int birthDay, int gender)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        return true;
    }
}
