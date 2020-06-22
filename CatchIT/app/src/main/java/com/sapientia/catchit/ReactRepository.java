package com.sapientia.catchit;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.sapientia.catchit.dao.PlayerDao;
import com.sapientia.catchit.dao.TrainerDao;
import com.sapientia.catchit.models.Player;
import com.sapientia.catchit.models.Trainer;

import java.util.List;

public class ReactRepository
{
    private PlayerDao playerDao;
    private List<Player> allPlayers;

    private TrainerDao trainerDao;
    private List<Trainer> allTrainers;

    public ReactRepository(Application application)
    {
        ReactRoomDB database = ReactRoomDB.getInstance(application);
        trainerDao = database.trainerDao();
        allTrainers = trainerDao.getAllTrainers();
        playerDao = database.playerDao();
        allPlayers = playerDao.getAllPlayers();
    }

    public void insertTrainer(Trainer trainer)
    {
        new InsertTrainerAsyncTask(trainerDao).execute(trainer);
    }

    public void updateTrainer(Trainer trainer)
    {
        //TODO
    }

    public void deleteTrainer(Trainer trainer)
    {
        //TODO
    }

    public void deleteAllTrainers()
    {
        //TODO
    }

//    public List<Trainer> getAllTrainers()
//    {
//        AsyncTask as= new getAllTrainersAsyncTask(trainerDao);
//        as.execute();
//        List<Trainer> trainerList= as.//new getAllTrainersAsyncTask(trainerDao).execute();
//        //return getAllTrainersAsyncTask(trainerDao)
//    }

    private static class InsertTrainerAsyncTask extends AsyncTask<Trainer, Void,Void>
    {
        private TrainerDao trainerDao;

        private InsertTrainerAsyncTask(TrainerDao trainerDao)
        {
            this.trainerDao = trainerDao;
        }

        @Override
        protected Void doInBackground(Trainer... trainers)
        {
            trainerDao.insertTrainer(trainers[0]);
            return null;
        }
    }

    private static class getAllTrainersAsyncTask extends AsyncTask<Void, Void,List<Trainer>>
    {
        private TrainerDao trainerDao;
        private List<Trainer> trainerList;

        private getAllTrainersAsyncTask(TrainerDao trainerDao)
        {
            this.trainerDao = trainerDao;
        }

        @Override
        protected List<Trainer> doInBackground(Void... voids)
        {
            trainerList = trainerDao.getAllTrainers();
            return trainerList;
        }
    }
}
