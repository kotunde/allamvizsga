package com.sapientia.catchit.helpers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.sapientia.catchit.R;
import com.sapientia.catchit.models.Exercise;

import java.util.ArrayList;
import java.util.List;

public class SequenceAdapter extends RecyclerView.Adapter<SequenceAdapter.SequenceViewHolder>
{
    private ArrayList<Exercise> mExerciseList = new ArrayList<>();
    Context context;

    public SequenceAdapter(Context context, List<Exercise> exerciseList)
    {
        this.context = context;
        this.mExerciseList.addAll(exerciseList);
    }

    public static class SequenceViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tv_sequenceName;
        public TextView tv_numberOfNodes;
        public TextView tv_sequenceAuthor;


        public SequenceViewHolder(View view)
        {
            super(view);
            view.setVisibility(View.VISIBLE);
            tv_sequenceName = view.findViewById(R.id.tv_sequenceName);
            tv_numberOfNodes = view.findViewById(R.id.tv_numberOfNodes);
            tv_sequenceAuthor = view.findViewById(R.id.tv_sequenceAuthor);
        }
    }
    // Create new views (invoked by the layout manager)
    @Override
    public SequenceAdapter.SequenceViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //create a new view/ inflate item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sequence_view,parent,false);
        SequenceViewHolder playerViewHolder = new SequenceViewHolder(v);
        return playerViewHolder;
    }

    @Override
    public void onBindViewHolder(final SequenceViewHolder holder, int position)
    {
        //set actual player name
        final Exercise output = mExerciseList.get(position);
        holder.tv_sequenceName.setText(output.exerciseName);
        holder.tv_numberOfNodes.setText(Integer.toString(output.sequence.size()));
        holder.tv_sequenceAuthor.setText(output.createdBy);
        Log.d("DEBUG-Se-Adp","Item binded: " + output.exerciseName);
    }

    @Override
    public int getItemCount()
    {
        return mExerciseList == null ? 0 : mExerciseList.size();
    }

    //maybe works (setAdapter needs to be called again)
    public void addItem(Exercise newExercise)
    {
        mExerciseList.add(newExercise);
        notifyDataSetChanged();
    }

}
