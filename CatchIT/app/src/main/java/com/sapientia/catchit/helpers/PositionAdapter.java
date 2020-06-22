package com.sapientia.catchit.helpers;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sapientia.catchit.R;
import com.sapientia.catchit.models.Position;

import java.util.ArrayList;

public class PositionAdapter extends RecyclerView.Adapter<PositionAdapter.PositionViewHolder>
{
    private ArrayList<Position> mPositionList;
    Context context;

    public PositionAdapter(Context context, ArrayList<Position> mPositionList)
    {
        this.context = context;
        this.mPositionList = mPositionList;
    }
    public static class PositionViewHolder extends RecyclerView.ViewHolder
    {
        public Button btn_position;

        public PositionViewHolder(View view)
        {
            super(view);
            view.setVisibility(View.VISIBLE);
            btn_position = view.findViewById(R.id.btn_position);
        }
    }
    // Create new views (invoked by the layout manager)
    @Override
    public PositionAdapter.PositionViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //create a new view/ inflate item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.position_view,parent,false);
        PositionViewHolder positionViewHolder = new PositionViewHolder(v);
        return positionViewHolder;
    }

    @Override
    public void onBindViewHolder(final PositionViewHolder holder, int position)
    {
        //set actual player name
        final Position output = mPositionList.get(position);
        holder.btn_position.setText(output.getSequenceNumber() + "");
        //setup position views (background, text)
        holder.btn_position.setVisibility(View.VISIBLE);
        holder.btn_position.setText("" + (position + 1) + "");
        holder.btn_position.setBackgroundResource(R.drawable.position_empty_background);
        holder.btn_position.setTextColor(context.getResources().getColor(R.color.black));

        //TODO if else branch

    }

    @Override
    public int getItemCount()
    {
        return mPositionList == null ? 0 : mPositionList.size();
        //return mGroupList.size();
    }

    public void addItem(Position newPosition)
    {
        mPositionList.add(newPosition);
        notifyDataSetChanged();
    }

}

