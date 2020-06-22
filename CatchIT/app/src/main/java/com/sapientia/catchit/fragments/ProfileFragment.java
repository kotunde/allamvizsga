package com.sapientia.catchit.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sapientia.catchit.R;
import com.sapientia.catchit.ReactRoomDB;
import com.sapientia.catchit.dao.GroupDao;
import com.sapientia.catchit.dao.GroupPlayerDao;
import com.sapientia.catchit.dao.PlayerDao;
import com.sapientia.catchit.dao.TrainerDao;
import com.sapientia.catchit.helpers.GroupPlayerAdapter;
import com.sapientia.catchit.helpers.GroupAdapter;
import com.sapientia.catchit.models.Group;
import com.sapientia.catchit.models.GroupPlayer;
import com.sapientia.catchit.models.Player;
import com.sapientia.catchit.models.Trainer;
import com.sapientia.catchit.serverrelated.ReactDBServer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProfileFragment extends Fragment
{
    ReactRoomDB database;
    TrainerDao trainerDao;
    PlayerDao playerDao;
    GroupDao groupDao;
    GroupPlayerDao groupPlayerDao;
    ReactDBServer serverConnection;

    private static final String ARG_USERNAME = "username";
    private static final String ARG_ROLE = "role";
    private String mUsername;
    private String mRole;
    private int gender;
    private int birthYear, birthMonth, birthDay;
    //grouplist
    RecyclerView groupRecyclerView;
    GroupAdapter groupAdapter;
    private ArrayList<Group> mGroupList = new ArrayList<>();
    //groupPlayerList
    RecyclerView groupPlayerRecyclerView;
    GroupPlayerAdapter groupPlayerAdapter;
    EditText et_groupPlayerName;
    private ArrayList<GroupPlayer> mGroupPlayerList = new ArrayList<>();
    //playerList
    RecyclerView playerRecyclerView;
    private ArrayList<Player> mPlayerList = new ArrayList<>();
    boolean firstTime;
    long groupId;
    Group newGroup;
    int numberOfPlayersInGroup = 0;

    public ProfileFragment()
    {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String username, String role)
    {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_ROLE, role);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUsername = getArguments().getString(ARG_USERNAME);
            mRole = getArguments().getString(ARG_ROLE);
        }
        database = Room.databaseBuilder(getContext(), ReactRoomDB.class, "reactdb")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        groupDao = database.groupDao();
        groupPlayerDao = database.groupPlayerDao();
        //for toolbar icons - very important
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View retView = inflater.inflate(R.layout.fragment_profile, container, false);
        //customize toolbar
        Toolbar toolbar =retView.findViewById(R.id.tb_profile);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        //set title of action bar
        TextView t_tv_title = toolbar.findViewById(R.id.t_tv_title);
        t_tv_title.setText(getString(R.string.title_profile));
        //set username as title (if not empty)
        if(!mUsername.isEmpty())
        {
            if (mRole.equals("0"))
            {
                t_tv_title.setText(mUsername + " - Trainer");
            }
            else if (mRole.equals("1"))
            {
                t_tv_title.setText(mUsername + " - Player");
            }
        }
        initView(retView);
        return retView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_profile_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
        return ;
    }

    private void initView(View view)
    {
        // get age and gender from database and set text to textViews
        TextView tv_age = view.findViewById(R.id.tv_ageValue);
        TextView tv_gender = view.findViewById(R.id.tv_genderValue);

        if (mRole.equals("0"))
        {
            trainerDao = database.trainerDao();
            Trainer trainer = trainerDao.getTrainerByUserName(mUsername);
            birthYear = trainer.getBirthYear();
            birthMonth = trainer.getBirthMonth();
            birthDay = trainer.getBirthDay();
            gender = trainer.getGender();
        }
        else if (mRole.equals("1"))
        {
            playerDao = database.playerDao();
            Player player = playerDao.getPlayerByUserName(mUsername);
            birthYear = player.getBirthYear();
            birthMonth = player.getBirthMonth();
            birthDay = player.getBirthDay();
            gender = player.getGender();
        }
        int age = Calendar.getInstance().get(Calendar.YEAR) - birthYear;
        if (Calendar.getInstance().get(Calendar.MONTH) < birthMonth)
        {
            age --;
        }
        else if ((Calendar.getInstance().get(Calendar.MONTH) == birthMonth) &&
                (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < birthDay))
        {
            age--;
        }
        tv_age.setText(String.valueOf(age));
        if(gender == 1)
        {
            tv_gender.setText("male");
        }
        else if (gender == 2)
        {
            tv_gender.setText("female");
        }

        //populate and handle list of groups
        groupRecyclerView = view.findViewById(R.id.rv_groups);
        groupRecyclerView.setNestedScrollingEnabled(false); //https://stackoverflow.com/questions/27083091/recyclerview-inside-scrollview-is-not-working
        groupRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        groupRecyclerView.setLayoutManager(layoutManager);
        groupAdapter = new GroupAdapter(getActivity(),mUsername,mGroupList);
        groupRecyclerView.setAdapter(groupAdapter);

        groupAdapter.notifyDataSetChanged();
        //groupRecyclerView.setAdapter(groupAdapter); // TODO maybe remove later

        //get all groups the user created or belongs to
        new getUsersGroupsAsyncTask(getContext(),groupRecyclerView,mGroupList,groupDao,mUsername).execute(mUsername);

        Button btn_addGroup = view.findViewById(R.id.btn_addGroup);
        btn_addGroup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showAddGroupDialog();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.btn_synchronize)
        {
            //TODO synchronize with the server  regarding groups and traienrs only in case of players
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddGroupDialog()
    {
        final View dialogView = getLayoutInflater().inflate(R.layout.fragment_add_group, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        //set recyclerview
        groupPlayerRecyclerView = dialogView.findViewById(R.id.rv_players);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        groupPlayerRecyclerView.setLayoutManager(layoutManager);
        groupPlayerAdapter = new GroupPlayerAdapter(getActivity(),mGroupPlayerList);
        groupPlayerRecyclerView.setAdapter(groupPlayerAdapter);

        final EditText et_groupName = dialogView.findViewById(R.id.et_groupName);
        //get input player
        et_groupPlayerName = dialogView.findViewById(R.id.et_playerName);
        //handle addPlayer: add entered playername to list
        Button btn_addPlayer = dialogView.findViewById(R.id.btn_addPlayer);
        firstTime = true;

        btn_addPlayer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //get entered playername
                String str_groupPlayerName = et_groupPlayerName.getText().toString();
                //for the first time, create new group object, and store it in the DB
                if (firstTime)
                {
                    String str_groupName = et_groupName.getText().toString();
                    //create new group object initializing with groupName and owner name
                    newGroup = new Group(str_groupName,mUsername);
                    newGroup.setNumberOfPlayers(1);
                    firstTime = false;
                    //make editText not editable
                    et_groupName.setEnabled(false);
                    //if trainer
                    if (mRole.equals("0"))
                    {
                        //insert group in server DB and get id
                        //serverConnection.
                        //insert group in local db

                    }
                    //if player
                    else if (mRole.equals("1"))
                    {
                        //insert group in local db
                        groupId = groupDao.insertGroup(newGroup);
                    }
                }
                GroupPlayer groupPlayer = new GroupPlayer(groupId,str_groupPlayerName);
                mGroupPlayerList.add(groupPlayer);
                groupPlayerAdapter.notifyDataSetChanged();
                //groupPlayerAdapter.addItem(groupPlayer);
                et_groupPlayerName.setText("");

            }
        });
        builder.setCancelable(false);
        builder.setView(dialogView);
        builder.setTitle("Create new group");
        //when done, save all the data in the DB
        builder.setPositiveButton(getResources().getString(R.string.done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                if (mRole.equals("0"))
                {

                }
                else if (mRole.equals("1"))
                {
                    // insert all data from groupPlayerList into local  DB
                    List<GroupPlayer> groupPlayerList = new ArrayList<>();
                    groupPlayerList.addAll(mGroupPlayerList);
                    //update number of players in group table
                    groupDao.updateNumberOfPlayers(groupPlayerList.size(),groupId);
                    groupPlayerDao.insertAllGroupPlayers(groupPlayerList);
                }
                //mGroupList.add(newGroup);
                //groupAdapter.notifyDataSetChanged();
                //groupAdapter.addItem(newGroup);
                new getUsersGroupsAsyncTask(getContext(),groupRecyclerView,mGroupList,groupDao,mUsername).execute(mUsername);
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO undo insertions
                dialog.cancel();
            }
        });

        builder.show();
    }

    //TODO mute this part to Repository
    private static class getUsersGroupsAsyncTask extends AsyncTask<String, Void, List<Group>>
    {
        private GroupDao groupDao;
        @SuppressLint("StaticFieldLeak")
        private ArrayList<Group> groupList;
        String mUsername;
        GroupAdapter groupAdapter;
        @SuppressLint("StaticFieldLeak")
        private Context context;
        @SuppressLint("StaticFieldLeak")
        RecyclerView groupRecyclerView;

        private getUsersGroupsAsyncTask(Context context, RecyclerView groupRecyclerView, ArrayList<Group> groupList,GroupDao groupDao,String userName)
        {
            this.context = context;
            this.groupRecyclerView = groupRecyclerView;
            this.groupList = groupList;
            this.groupDao = groupDao;
            this.mUsername = userName;
        }

        @Override
        protected List<Group> doInBackground(String... params)
        {
            groupList.clear();
            groupList.addAll(groupDao.getUsersGroups(params[0]));
            Log.d("DEBUG-ProfFrag","DB-bol csooport-lista merete: "+ groupList.size());
            return groupList;
        }

        @Override
        protected void onPostExecute(List<Group> groups)
        {
            super.onPostExecute(groups);
            groupAdapter = new GroupAdapter(context,mUsername,groupList);
            groupRecyclerView.setAdapter(groupAdapter);
            groupAdapter.notifyDataSetChanged();
        }
    }
}
