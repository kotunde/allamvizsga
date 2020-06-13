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

import java.util.ArrayList;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupViewHolder>
{
    private ArrayList<Group> mGroupList;
    Context context;

    public GroupsAdapter(Context context, ArrayList<Group> groupList)
    {
        this.context = context;
        this.mGroupList = groupList;
    }
    public static class GroupViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tv_groupName;

        public GroupViewHolder(View view)
        {
            super(view);
            view.setVisibility(View.VISIBLE);
            tv_groupName = view.findViewById(R.id.tv_groupName);
        }
    }
    // Create new views (invoked by the layout manager)
    @Override
    public GroupsAdapter.GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //create a new view/ inflate item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.string_list_item_view,parent,false);
        GroupViewHolder groupViewHolder = new GroupViewHolder(v);
        return groupViewHolder;
    }

    @Override
    public void onBindViewHolder(final GroupViewHolder holder, int position)
    {
        //set actual player name
        final Group output = mGroupList.get(position);
        holder.tv_groupName.setText(output.getGroupName());

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

