package com.sapientia.catchit.helpers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sapientia.catchit.R;
import com.sapientia.catchit.models.GroupPlayer;
import com.sapientia.catchit.models.Player;

import java.util.ArrayList;

public class GroupPlayerAdapter extends RecyclerView.Adapter<GroupPlayerAdapter.PlayerViewHolder>
{
    private ArrayList<GroupPlayer> mGroupPlayerList;
    Context context;

    public GroupPlayerAdapter(Context context, ArrayList groupPlayerList)
    {
        Log.d("DEBUG - GroupPlayerAdpt","new adapter created");
        this.context = context;
        this.mGroupPlayerList = groupPlayerList;
    }
    public static class PlayerViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tv_playerName;
        public Button btn_removePlayer;

        public PlayerViewHolder(View view)
        {
            super(view);
            view.setVisibility(View.VISIBLE);
            tv_playerName = view.findViewById(R.id.tv_playerName);
            btn_removePlayer = view.findViewById(R.id.btn_removePlayer);
        }
    }


    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public GroupPlayerAdapter.PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //create a new view/ inflate item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_player_view,parent,false);
        PlayerViewHolder playerViewHolder = new PlayerViewHolder(v);
        return playerViewHolder;
    }

    @Override
    public void onBindViewHolder(final PlayerViewHolder holder, final int position)
    {
        //set actual player name
        final GroupPlayer output = mGroupPlayerList.get(position);

        //Log.d("DEBUG - GroupPlayerAdpt","textView set to "+ output + "; List size = "+ getItemCount());
        holder.tv_playerName.setText(output.getPlayerName());
        //handle delete button
        holder.btn_removePlayer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("DEBUG --GroupPA","remove clicked");
                for(GroupPlayer i: mGroupPlayerList)
                {
                    Log.d("DEBUG --GroupPA",i.getPlayerName());
                }
                //removed clicked item from list
                //mPlayerList.remove(holder.tv_playerName.getText());
                if (mGroupPlayerList.size()>0)
                {
                    mGroupPlayerList.remove(position);
                }
                else
                {
                    mGroupPlayerList.clear();
                    notifyDataSetChanged();
                }
                Log.d("DEBUG --GroupPA","removed");
                for(GroupPlayer i: mGroupPlayerList)
                {
                    Log.d("DEBUG --GroupPA",i.getPlayerName());
                }
                //notifyItemRemoved(mPlayerList.indexOf(holder.tv_playerName.getText()));
                notifyItemRemoved(position);
            }
        });

    }

    @Override
    public int getItemCount()
    {
        //return mPlayerList == null ? 0 : mPlayerList.size();
        return mGroupPlayerList.size();
    }

    public void addItem(GroupPlayer newGroupPlayer)
    {
        mGroupPlayerList.add(newGroupPlayer);
        //Log.d("DEBUG - GroupPlayerAdpt","added Item: "+ newPlayer);
        notifyDataSetChanged();
    }

}
