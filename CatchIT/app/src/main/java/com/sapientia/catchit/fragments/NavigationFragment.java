package com.sapientia.catchit.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sapientia.catchit.R;


public class NavigationFragment extends Fragment
{
    final Fragment profileFragment = new ProfileFragment();
    final Fragment sequencesFragment = new SequencesFragment();
    //final Fragment statisticsFragment = new StatisticsFragment();
    final Fragment settingsFragment = new SettingsFragment();
    Fragment active = profileFragment;
    FragmentManager fragmentManager;

    //final Fragment
    private static final String ARG_USERNAME = "username";
    private static final String ARG_ROLE = "role";

    private String mUsername;
    private String mRole;

    public NavigationFragment()
    {
        // Required empty public constructor
    }

    public static NavigationFragment newInstance(String username, String role)
    {
        NavigationFragment fragment = new NavigationFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View retView =  inflater.inflate(R.layout.fragment_navigation, container, false);

        //create fragments, pass username and password to all and hide them except the first
        fragmentManager = getActivity().getSupportFragmentManager();
        //TODO abstract extandable class ... ??
        Bundle bundle = new Bundle();
        bundle.putString("username", mUsername);
        bundle.putString("role", mRole);

        sequencesFragment.setArguments(bundle);
        fragmentManager.beginTransaction().add(R.id.fl_navPlaceholder,sequencesFragment,"sequences").hide(sequencesFragment).commit();

        //statisticsFragment.setArguments(bundle); //needs to be recreated
        //fragmentManager.beginTransaction().add(R.id.fl_navPlaceholder,statisticsFragment,"statistics").hide(statisticsFragment).commit();

        settingsFragment.setArguments(bundle);
        fragmentManager.beginTransaction().add(R.id.fl_navPlaceholder,settingsFragment,"settings").hide(settingsFragment).commit();

        profileFragment.setArguments(bundle);
        fragmentManager.beginTransaction().add(R.id.fl_navPlaceholder,profileFragment,"profile").commit();
        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Profile");

        initView(retView);
        return retView;
    }
    public void initView(View view)
    {
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.navigation_profile:
                        Toast.makeText(getActivity(),"Profile", Toast.LENGTH_SHORT).show();
                        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Profile");
                        fragmentManager.beginTransaction().hide(active).show(profileFragment).commit();
                        active = profileFragment;
                        break;
                    case R.id.navigation_sequences:
                        Toast.makeText(getActivity(),"Sequences",Toast.LENGTH_SHORT).show();
                        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Sequences");
                        fragmentManager.beginTransaction().hide(active).show(sequencesFragment).commit();
                        active = sequencesFragment;
                        break;
                    case R.id.navigation_statistics:
                        Toast.makeText(getActivity(),"Statistics",Toast.LENGTH_SHORT).show();
                        StatisticsFragment statisticsFragment = new StatisticsFragment();
                        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Statistics");
                        fragmentManager.beginTransaction().add(R.id.fl_navPlaceholder,statisticsFragment,"statistics").hide(statisticsFragment).commit();
                        fragmentManager.beginTransaction().hide(active).show(statisticsFragment).commit();
                        active = statisticsFragment;
                        break;
                    case R.id.navigation_settings:
                        Toast.makeText(getActivity(),"Settings",Toast.LENGTH_SHORT).show();
                        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Settings");
                        fragmentManager.beginTransaction().hide(active).show(settingsFragment).commit();
                        active = settingsFragment;
                }
                return true;
            }
        });
    }
}