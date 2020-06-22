package com.sapientia.catchit.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sapientia.catchit.R;
import com.sapientia.catchit.models.Group;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder>
{
    private ArrayList<Group> mGroupList;
    Context context;
    String mUsername;

    public GroupAdapter(Context context, String userName,ArrayList<Group> groupList)
    {
        this.context = context;
        this.mGroupList = groupList;
        this.mUsername = userName;
    }
    public static class GroupViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tv_groupName;
        public TextView tv_nrOfPlayers;

        public GroupViewHolder(View view)
        {
            super(view);
            view.setVisibility(View.VISIBLE);
            tv_groupName = view.findViewById(R.id.tv_groupName);
            tv_nrOfPlayers = view.findViewById(R.id.tv_nrOfPlayersValue);
        }
    }
    // Create new views (invoked by the layout manager)
    @Override
    public GroupAdapter.GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //create a new view/ inflate item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_view,parent,false);
        GroupViewHolder groupViewHolder = new GroupViewHolder(v);
        return groupViewHolder;
    }

    @Override
    public void onBindViewHolder(final GroupViewHolder holder, int position)
    {
        //set actual player name
        final Group output = mGroupList.get(position);
        holder.tv_groupName.setText(output.getGroupName());
        holder.tv_nrOfPlayers.setText(Integer.toString(output.getNumberOfPlayers()));
        //set itemView background green if it is not the player's group
        if (!output.getOwner().equals(mUsername))
        {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.LightGreen));
        }

    }

    @Override
    public int getItemCount()
    {
        return mGroupList == null ? 0 : mGroupList.size();
        //return mGroupList.size();
    }

    public void addItem(Group newGroup)
    {
        mGroupList.add(newGroup);
        notifyDataSetChanged();
    }

}

