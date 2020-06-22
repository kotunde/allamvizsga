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
import com.sapientia.catchit.serverrelated.NodeClient;

import java.util.ArrayList;

// this VS PositionAdapter> set invisible unassigned nodes
public class PositionInvAdapter extends RecyclerView.Adapter<PositionInvAdapter.PositionInvViewHolder>
{
    private ArrayList<Position> mPositionList;
    Context context;

    public PositionInvAdapter(Context context, ArrayList<Position> mPositionList)
    {
        this.context = context;
        this.mPositionList = mPositionList;
    }
    public static class PositionInvViewHolder extends RecyclerView.ViewHolder
    {
        public Button btn_position;

        public PositionInvViewHolder(View view)
        {
            super(view);
            //view.setVisibility(View.VISIBLE);
            btn_position = view.findViewById(R.id.btn_position);
        }
    }
    // Create new views (invoked by the layout manager)
    @Override
    public PositionInvAdapter.PositionInvViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //create a new view/ inflate item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.position_view,parent,false);
        PositionInvViewHolder positionInvViewHolder = new PositionInvViewHolder(v);
        return positionInvViewHolder;
    }

    @Override
    public void onBindViewHolder(final PositionInvViewHolder holder, int position)
    {
        //set actual player name
        final Position output = mPositionList.get(position);
        NodeClient node = output.getAssignedNode();
        if (node != null)
        {
            holder.btn_position.setText(node.getId());
            //Log.d("DEBUG- PosInvAdpt","Position updated on pos: "+ position +" to nodeID:" + node.getId());
            //holder.btn_position.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.btn_position.setText(output.getSequenceNumber() + "");
            //Log.d("DEBUG- PosInvAdpt","Position updated on pos: "+ position +" to SeqNum:" + output.getSequenceNumber() + "");
            //if position does not belong to the sequence, set explicitly invisible
            if (output.getSequenceNumber() == 0)
            {
                holder.btn_position.setVisibility(View.INVISIBLE);
            }

        }

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

