package com.sapientia.catchit.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sapientia.catchit.MainActivity;
import com.sapientia.catchit.R;
import com.sapientia.catchit.ReactRoomDB;
import com.sapientia.catchit.dao.StatisticsDao;
import com.sapientia.catchit.dao.TrainerDao;
import com.sapientia.catchit.helpers.StatisticsAdapter;
import com.sapientia.catchit.models.Statistics;
import com.sapientia.catchit.models.Trainer;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment
{
    ReactRoomDB database;
    StatisticsDao statisticsDao;

    private ArrayList<Statistics> mStatisticsList = new ArrayList<>();
    private RecyclerView statisticsRecyclerView;
    private StatisticsAdapter statisticsAdapter;


    private static final String ARG_USERNAME = "username";
    private static final String ARG_PASSWORD = "password";

    private String mUsername;
    private String mPassword;

    public StatisticsFragment()
    {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static StatisticsFragment newInstance(String username, String password)
    {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_PASSWORD, password);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUsername = getArguments().getString(ARG_USERNAME);
            mPassword = getArguments().getString(ARG_PASSWORD);
        }
        database = Room.databaseBuilder(getContext(), ReactRoomDB.class, "reactdb")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        statisticsDao = database.statisticsDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View retView = inflater.inflate(R.layout.fragment_statistics, container, false);
        //set title of custom toolbar
        View innerLayout =retView.findViewById(R.id.tb_statistics);
        Toolbar toolbar = innerLayout.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        //set title of action bar
        TextView t_tv_title = toolbar.findViewById(R.id.t_tv_title);
        t_tv_title.setText(getString(R.string.title_statistics));

        initView(retView);
        return retView;
    }

    private void initView(View view)
    {
        statisticsRecyclerView = view.findViewById(R.id.rv_statistics);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        statisticsRecyclerView.setLayoutManager(layoutManager);
        statisticsAdapter = new StatisticsAdapter(getActivity(),mStatisticsList);
        statisticsRecyclerView.setAdapter(statisticsAdapter);


        new getAllStatisticsAsyncTask(getContext(),statisticsRecyclerView,statisticsDao).execute();

        //mStatisticsList =  statisticsDao.getAllStatistics(); //((MainActivity)getActivity()).getStats();
        //Log.d("DEBUG- STAT", "Size of stat list: "+ mStatisticsList.size());
//        for (int i=0; i< mStatisticsList.size(); ++i)
//        {
//            Log.d("DEBUG- STAT", "Stat list content "+ mStatisticsList.get(i) );
//        }
        //statisticsAdapter.notifyDataSetChanged();
        //statisticsRecyclerView.setAdapter(statisticsAdapter);

    }


    private static class getAllStatisticsAsyncTask extends AsyncTask<Void, Void,List<Statistics>>
    {
        private StatisticsDao statisticsDao;
        private ArrayList<Statistics> statisticsList = new ArrayList<Statistics>();
        StatisticsAdapter statisticsAdapter;
        @SuppressLint("StaticFieldLeak")
        Context context;
        @SuppressLint("StaticFieldLeak")
        RecyclerView statisticsRecyclerView;

        private getAllStatisticsAsyncTask(Context context,RecyclerView statisticsRecyclerView , StatisticsDao statisticsDao)
        {
            this.context = context;
            this.statisticsRecyclerView = statisticsRecyclerView;
            this.statisticsDao = statisticsDao;
        }

        @Override
        protected List<Statistics> doInBackground(Void... voids)
        {
            statisticsList.addAll(statisticsDao.getAllStatistics());
            return statisticsList;
        }

        @Override
        protected void onPostExecute(List<Statistics> statistics)
        {
            Log.d("DEBUG-StatFrag","Before notify; StatList size: "+ statisticsList.size());
            statisticsAdapter = new StatisticsAdapter(context,statisticsList);
            statisticsRecyclerView.setAdapter(statisticsAdapter);
            statisticsAdapter.notifyDataSetChanged();
            super.onPostExecute(statistics);

        }
    }
}
