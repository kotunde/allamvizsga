package com.sapientia.catchit.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import com.sapientia.catchit.models.Player;
import com.sapientia.catchit.models.Trainer;


public class LoginFragment extends Fragment
{
    ReactRoomDB database;
    TrainerDao trainerDao;
    PlayerDao playerDao;

    boolean inputError; //error not handled yet
    EditText et_username;
    EditText et_password;
    String str_username;
    String str_password;

    public LoginFragment()
    {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1, String param2)
    {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
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
        View retView = inflater.inflate(R.layout.fragment_login, container, false);

        //get toolbar from include layout
        View innerLayout =retView.findViewById(R.id.tb_login);
        Toolbar toolbar = innerLayout.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        //set title of action bar
        TextView t_tv_title = toolbar.findViewById(R.id.t_tv_title);
        t_tv_title.setText(getString(R.string.title_log_in));

        initView(retView);
        return retView;
    }

    //get views and add actions to them
    private void initView(View view)
    {
        et_username = view.findViewById(R.id.et_username);
        et_password = view.findViewById(R.id.et_password);
        et_username.setText("tunde");
        et_password.setText("1234");

        Button btn_login = view.findViewById(R.id.btn_login);
        Button btn_register = view.findViewById(R.id.btn_register);

        //handle login button: authenticate user by name and password
        btn_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //get entered data
                str_username = et_username.getText().toString();
                str_password = et_password.getText().toString();
                attemptLogin(str_username,str_password);
            }
        });

        //handle register button: switch to register screen
        btn_register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //load RegisterFragment
                RegisterFragment registerFragment = new RegisterFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fl_placeholder,registerFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

    }

    void attemptLogin(String pUsername, String pPassword)
    {
        //check database tables(trainer & players) if username exists
        trainerDao = database.trainerDao();
        playerDao = database.playerDao();
        Trainer alreadyExistingTrainer = trainerDao.getTrainerByUserName(str_username);
        Player alreadyExistingPlayer = playerDao.getPlayerByUserName(str_username);
        if(alreadyExistingTrainer == null && alreadyExistingPlayer == null)
        {
            et_username.setError(getString(R.string.error_username_doesnt_exist));
        }
        else if(alreadyExistingPlayer == null)
        {
            if (!alreadyExistingTrainer.getPassword().equals(str_password))
            {
                et_password.setError(getString(R.string.error_incorrect_password));
            }
            else
            {
                //everything is ok --> load ProfileFragment(NavigationFragment)
                letUserEnter("0");
            }
        }
        else if (alreadyExistingTrainer == null)
        {
            if (!alreadyExistingPlayer.getPassword().equals(str_password))
            {
                et_password.setError(getString(R.string.error_incorrect_password));
            }
            else
            {
                //everything is ok --> load ProfileFragment(NavigationFragment)
                letUserEnter("1");
            }
        }

    }

    private void letUserEnter(String role)
    {
        NavigationFragment navigationFragment = new NavigationFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_placeholder,navigationFragment.newInstance(str_username,role),"navigation");
        fragmentTransaction.addToBackStack("navigationStack");
        fragmentTransaction.commit();
    }
}
