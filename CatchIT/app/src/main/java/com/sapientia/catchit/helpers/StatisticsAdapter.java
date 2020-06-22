package com.sapientia.catchit.helpers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sapientia.catchit.R;
import com.sapientia.catchit.models.Group;
import com.sapientia.catchit.models.Statistics;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.StatisticsViewHolder>
{
    private ArrayList<Statistics> mStatisticsList = new ArrayList<>();
    Context context;

    public StatisticsAdapter(Context context, List<Statistics> statisticsList)
    {
        this.context = context;
        this.mStatisticsList.addAll(statisticsList);
        Log.d("DEBUG-StatAdpt","StatList received; size: "+ mStatisticsList.size());
    }
    public static class StatisticsViewHolder extends RecyclerView.ViewHolder
    {
        public TextView playerName;
        public TextView exerciseName;
        public TextView nrOfMistakes;
        public TextView averageReactionTime;
        public TextView slowest;
        public TextView fastest;

        public StatisticsViewHolder(View view)
        {
            super(view);
            view.setVisibility(View.VISIBLE);
            playerName = view.findViewById(R.id.tv_playeName);
            exerciseName = view.findViewById(R.id.tv_exerciseName);
            nrOfMistakes = view.findViewById(R.id.tv_nrOfMistakes);
            averageReactionTime = view.findViewById(R.id.tv_avgReaction);
            slowest = view.findViewById(R.id.tv_slowest);
            fastest = view.findViewById(R.id.tv_fastest);
        }
    }
    // Create new views (invoked by the layout manager)
    @Override
    public StatisticsAdapter.StatisticsViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //create a new view/ inflate item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.statistics_view,parent,false);
        StatisticsViewHolder statisticsViewHolder = new StatisticsViewHolder(v);
        return statisticsViewHolder;
    }

    @Override
    public void onBindViewHolder(final StatisticsViewHolder holder, int position)
    {
        //set actual player name
        final Statistics output = mStatisticsList.get(position);
        holder.playerName.setText("Player: " + output.getPlayerName());
        holder.exerciseName.setText("Exercise: " + output.getExerciseName());
        holder.nrOfMistakes.setText("Mistakes: " + output.getMistakes());

        //round doubles to 3 decimals
        DecimalFormat decimalFormat = new DecimalFormat("0.000");
        holder.averageReactionTime.setText("Average: " + decimalFormat.format(output.getReaction_average()/(1000000*output.getNrOfReactions())) + " s");
        holder.slowest.setText("Slowest: " + decimalFormat.format(output.getReaction_max()/1000000)+" s");
        holder.fastest.setText("Fastest: " + decimalFormat.format(output.getReaction_min()/1000000)+" s");
        //holder.tv_groupName.setText(output.getGroupName());
        Log.d("DEBUG - STATADPT","Size of new statisctic: "+ mStatisticsList.size());

    }

    @Override
    public int getItemCount()
    {
        return mStatisticsList == null ? 0 : mStatisticsList.size();
    }

    public void addItem(Statistics newStat)
    {
        mStatisticsList.add(newStat);
        notifyDataSetChanged();
    }

    public static String doublePrecision(double amt, int precision)
    {
        return String.format("%." + precision + "f", amt);
    }
}

