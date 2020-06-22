package com.sapientia.catchit;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.sapientia.catchit.dao.ExerciseDao;
import com.sapientia.catchit.dao.GroupDao;
import com.sapientia.catchit.dao.GroupPlayerDao;
import com.sapientia.catchit.dao.PlayerDao;
import com.sapientia.catchit.dao.StatisticsDao;
import com.sapientia.catchit.dao.StringArrayListConverter;
import com.sapientia.catchit.dao.TrainerDao;
import com.sapientia.catchit.models.Exercise;
import com.sapientia.catchit.models.Group;
import com.sapientia.catchit.models.GroupPlayer;
import com.sapientia.catchit.models.Player;
import com.sapientia.catchit.models.Statistics;
import com.sapientia.catchit.models.Trainer;

@Database(entities = {
        Trainer.class,
        Player.class,
        Group.class,
        GroupPlayer.class,
        Statistics.class,
        Exercise.class
        },
        version = 4
)

@TypeConverters({StringArrayListConverter.class})
public abstract class ReactRoomDB extends RoomDatabase
{
    private static ReactRoomDB instance;
    private static final Object LOCK = new Object();

    public abstract PlayerDao playerDao();
    public abstract TrainerDao trainerDao();
    public abstract GroupDao groupDao();
    public abstract GroupPlayerDao groupPlayerDao();
    public abstract StatisticsDao statisticsDao();
    public abstract ExerciseDao exerciseDao();

    //only one thread at a time can access this method
    public static synchronized ReactRoomDB getInstance(Context context)
    {
        if (instance == null)
        {
            synchronized (LOCK)
            {
                instance = Room.databaseBuilder(context.getApplicationContext(),
                        ReactRoomDB.class, "react_database")
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return instance;
    }


    // for later> migration with simple schema changes
    //https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
    //https://stackoverflow.com/questions/49629656/please-provide-a-migration-in-the-builder-or-call-fallbacktodestructivemigration

    //room using asynctask
    //https://stackoverflow.com/questions/46460382/recyclerview-not-displaying-items

    public static void destroyInstance()
    {
        instance = null;
    }
}
