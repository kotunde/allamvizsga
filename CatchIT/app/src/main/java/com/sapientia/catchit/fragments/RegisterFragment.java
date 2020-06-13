package com.sapientia.catchit.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sapientia.catchit.R;
import com.sapientia.catchit.ReactRoomDB;
import com.sapientia.catchit.dao.PlayerDao;
import com.sapientia.catchit.dao.TrainerDao;
import com.sapientia.catchit.models.Player;
import com.sapientia.catchit.models.Trainer;

public class RegisterFragment extends Fragment
{
    ReactRoomDB database;
    TrainerDao trainerDao;
    PlayerDao playerDao;

    EditText et_username;
    EditText et_password;
    EditText et_retypePassword;
    String str_username;
    String str_password;
    String str_retypePassword;
    TextView tv_birthDate;
    private int myDay,myMonth,myYear;
    RadioGroup rg_gender;
    RadioButton rb_gender;
    RadioGroup rg_role;
    RadioButton rb_role;

    // TODO handle empty radio buttons
    String role = "";
    int gender = 0;

    public RegisterFragment()
    {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2)
    {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        //get instance of database
        database = Room.databaseBuilder(getContext(), ReactRoomDB.class, "reactdb")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        trainerDao = database.trainerDao();
        playerDao = database.playerDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View retView =inflater.inflate(R.layout.fragment_register, container, false);
        //set title of custom toolbar
        View innerLayout =retView.findViewById(R.id.tb_register);
        Toolbar toolbar = innerLayout.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        //set title of action bar
        TextView t_tv_title = toolbar.findViewById(R.id.t_tv_title);
        t_tv_title.setText(getString(R.string.title_register));

        initView(retView);
        return retView;
    }

    private void initView(final View view)
    {
        //TODO check fields before register


        Button btn_pickDate = view.findViewById(R.id.btn_pickDate);
        tv_birthDate = view.findViewById(R.id.tv_birthDate);
        et_username = view.findViewById(R.id.et_username);
        et_password = view.findViewById(R.id.et_password);
        et_retypePassword = view.findViewById(R.id.et_retypePassword);

        Button btn_register = view.findViewById(R.id.btn_register);

        rg_gender = view.findViewById(R.id.rg_gender);
        rg_role = view.findViewById(R.id.rg_role);


        //get birth date
        btn_pickDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDatePicker(v);
            }
        });

        //register
        btn_register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                attemptRegister(view);
            }
        });
    }

    //datePicker
    public void showDatePicker(View v)
    {
        DialogFragment newFragment = new DatePickerFragment(
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int month, int day)
                    {
                        myDay = view.getDayOfMonth();
                        myMonth =view.getMonth();
                        myYear = view.getYear();
                        tv_birthDate.setText(myYear + "." + (myMonth+1) + "." +myDay+ ".");
                    }
                });
        newFragment.show(getFragmentManager(), "date picker");

    }

    private void attemptRegister(View view)
    {
        //TODO hibakezelesek

        et_username.setError(null);
        et_retypePassword.setError(null);
        //get typed in values
        str_username = et_username.getText().toString();
        str_password = et_password.getText().toString();
        str_retypePassword = et_retypePassword.getText().toString();

        int selectedId = rg_gender.getCheckedRadioButtonId();
        rb_gender = view.findViewById(selectedId);
        //male = 1; female = 2;
        gender = rb_gender.getText().equals("Male")? 1 : 2;

        selectedId = rg_role.getCheckedRadioButtonId();
        rb_role = view.findViewById(selectedId);

        //check typed in data
        if (TextUtils.isEmpty(str_username))
        {
            et_username.setError(getString(R.string.error_field_required));
            return;
        }
        else
        {
            //check whether user exists with this username
            Trainer alreadyExistingTrainer = trainerDao.getTrainerByUserName(str_username);
            Player alreadyExistingPlayer = playerDao.getPlayerByUserName(str_username);
            if(alreadyExistingTrainer == null && alreadyExistingPlayer == null)
            {
                //the unique username is available
            }
            else
            {
                et_username.setError(getString(R.string.error_username_already_exists));
                return;
            }
        }
        if (!str_password.equals(str_retypePassword))
        {
            et_retypePassword.setError(getString(R.string.error_password_doesnt_match));
            return;

        }
        //if the user registers as a trainer
        if (rb_role.getText().equals("Trainer"))
        {
            Trainer newTrainer = new Trainer(str_username, str_password, myYear, myMonth, myDay, gender);
            trainerDao.insertTrainer(newTrainer);
            getFragmentManager().popBackStack();
        }
        //if the user registers as a Player
        else if (rb_role.getText().equals("Player"))
        {
            Player newPlayer = new Player(str_username, str_password, myYear, myMonth, myDay, gender);
            playerDao.insertPlayer(newPlayer);
            getFragmentManager().popBackStack();
        }




//        //load ProfileFragment(NavigationFragment)
//        NavigationFragment navigationFragment = new NavigationFragment();
//        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.fl_placeholder,navigationFragment.newInstance(str_username,str_password));
//        fragmentTransaction.addToBackStack("navigation");
//        fragmentTransaction.commit();
    }


}
