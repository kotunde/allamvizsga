package com.sapientia.catchit.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sapientia.catchit.R;
import com.sapientia.catchit.ReactRoomDB;
import com.sapientia.catchit.dao.PlayerDao;
import com.sapientia.catchit.dao.TrainerDao;
import com.sapientia.catchit.helpers.GroupPlayerAdapter;
import com.sapientia.catchit.helpers.GroupsAdapter;
import com.sapientia.catchit.models.Group;
import com.sapientia.catchit.models.Player;
import com.sapientia.catchit.models.Trainer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ProfileFragment extends Fragment
{
    ReactRoomDB database;
    TrainerDao trainerDao;
    PlayerDao playerDao;

    private static final String ARG_USERNAME = "username";
    private static final String ARG_ROLE = "role";
    private String mUsername;
    private String mRole;
    private int gender;
    private int birthYear, birthMonth, birthDay;
    //grouplist
    RecyclerView groupRecyclerView;
    GroupsAdapter groupsAdapter;
    private ArrayList<Group> mGroupList = new ArrayList<>();
    //groupPlayerList
    RecyclerView groupPlayerRecyclerView;
    GroupPlayerAdapter groupPlayerAdapter;
    EditText et_groupPlayerName;
    private ArrayList<Player> mGroupPlayerList = new ArrayList<>();
    //playerList
    RecyclerView playerRecyclerView;
    private ArrayList<Player> mPlayerList = new ArrayList<>();

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View retView = inflater.inflate(R.layout.fragment_profile, container, false);
        //customize toolbar
        View innerLayout =retView.findViewById(R.id.tb_profile);
        Toolbar toolbar = innerLayout.findViewById(R.id.toolbar);
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

        //TODO get user's groups from database and fill recyclerview

        //populate and handle list of groups
        groupRecyclerView = view.findViewById(R.id.rv_groups);
        groupRecyclerView.setNestedScrollingEnabled(false); //https://stackoverflow.com/questions/27083091/recyclerview-inside-scrollview-is-not-working
        groupRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        groupRecyclerView.setLayoutManager(layoutManager);
        groupsAdapter = new GroupsAdapter(getActivity(),mGroupList);
        groupRecyclerView.setAdapter(groupsAdapter);


        groupsAdapter.notifyDataSetChanged();
        //groupRecyclerView.setAdapter(groupsAdapter); // TODO maybe remove later

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
        //get input player
        et_groupPlayerName = dialogView.findViewById(R.id.et_playerName);
        //handle addPlayer: add entered playername to list
        Button btn_addPlayer = dialogView.findViewById(R.id.btn_addPlayer);
        //TODO temporarly removed, since Player class changed
//        btn_addPlayer.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                String str_groupPlayerName = et_groupPlayerName.getText().toString();
//                Player newPlayer = new Player(str_groupPlayerName);
//                et_groupPlayerName.setText("");
//                groupPlayerAdapter.addItem(newPlayer);
//            }
//        });
        builder.setView(dialogView);
        builder.setTitle("Create new group");
        builder.setPositiveButton(getResources().getString(R.string.done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText et_groupName = dialogView.findViewById(R.id.et_groupName);
                String str_groupName = et_groupName.getText().toString();
                //create new group object initializing with name and player list
                Group newGroup = new Group(str_groupName,mGroupPlayerList);
                groupsAdapter.addItem(newGroup);
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
