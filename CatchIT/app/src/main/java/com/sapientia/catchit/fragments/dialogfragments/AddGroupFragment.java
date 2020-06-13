package com.sapientia.catchit.fragments.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.sapientia.catchit.R;
import com.sapientia.catchit.helpers.GroupPlayerAdapter;

import java.util.ArrayList;

// solution not used
/*
public class AddGroupFragment extends DialogFragment
{
    private static final String ARG_USERNAME = "username";
    private static final String ARG_PASSWORD = "password";
    private String mUsername;
    private String mPassword;
    GroupPlayerAdapter groupPlayerAdapter;
    RecyclerView mRecyclerView;
    //RecyclerView.Adapter groupPlayerAdapter;
    private EditText et_playerName;
    private ArrayList<String> mPlayerList = new ArrayList<>();

    public AddGroupFragment()
    {
        // Required empty public constructor
    }

    public static AddGroupFragment newInstance(String username, String password)
    {
        AddGroupFragment fragment = new AddGroupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_PASSWORD, password);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        /*mRecyclerView = new RecyclerView(getContext());
        // you can use LayoutInflater.from(getContext()).inflate(...) if you have xml layout
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setLayoutManager(LayoutInflater.from(getContext()).inflate(fragment_add_group));
        mRecyclerView.setAdapter(/* your adapter /);*/
        /*final View mView = getLayoutInflater().inflate(R.layout.fragment_add_group, null);
        mRecyclerView = mView.findViewById(R.id.rv_players);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        groupPlayerAdapter = new GroupPlayerAdapter(getActivity(),mPlayerList);
        mRecyclerView.setAdapter(groupPlayerAdapter);*//*


        return new AlertDialog.Builder(getActivity())
                .setTitle("Create new group")
                //.setView(mView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        //  handle DONE button
                    }
                }
                ).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // handle CANCEL button
                        dialog.cancel();
                    }
                })
                        .create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUsername = getArguments().getString(ARG_USERNAME);
            mPassword = getArguments().getString(ARG_PASSWORD);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_add_group, container, false);
        View retView = inflater.inflate(R.layout.fragment_add_group, container, false);
        initView(retView);
        this.getDialog().setTitle("Create group");
        return retView;
    }

    //@Override
    //public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    private void initView(View view)
    {
        Log.d("DEBUG - AddGroupFrgt","onViewCreated called");
        //does not work !!!
        //this.getDialog().setTitle("Create group");

        // make player object from String (later)

        //TODO fill mPlayerList with actual data from DB

        mRecyclerView = view.findViewById(R.id.rv_players);
        //mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        //layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        groupPlayerAdapter = new GroupPlayerAdapter(getActivity(),mPlayerList);
        mRecyclerView.setAdapter(groupPlayerAdapter);

        //JFD
        /*mPlayerList.add("user1");
        mPlayerList.add("user2");
        groupPlayerAdapter.notifyDataSetChanged();*//*

        et_playerName = view.findViewById(R.id.et_playerName);
        //handle addPlayer: add entered playername to list
        Button btn_addPlayer = view.findViewById(R.id.btn_addPlayer);
        btn_addPlayer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String newPlayer = et_playerName.getText().toString();
                //mPlayerList.add(newPlayer);
                //groupPlayerAdapter.notifyDataSetChanged();

                groupPlayerAdapter.addItem(newPlayer);
            }
        });
    }
}
*/