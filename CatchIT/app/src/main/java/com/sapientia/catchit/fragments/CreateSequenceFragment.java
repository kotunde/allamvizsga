package com.sapientia.catchit.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sapientia.catchit.MainActivity;
import com.sapientia.catchit.R;
import com.sapientia.catchit.ReactRoomDB;
import com.sapientia.catchit.dao.ExerciseDao;
import com.sapientia.catchit.helpers.GroupsAdapter;
import com.sapientia.catchit.helpers.PositionAdapter;
import com.sapientia.catchit.helpers.RecyclerItemClickListener;
import com.sapientia.catchit.models.Exercise;
import com.sapientia.catchit.models.Position;

import java.util.ArrayList;

public class CreateSequenceFragment extends Fragment
{
    private static final String ARG_USERNAME = "username";
    private static final String ARG_PASSWORD = "password";
    private String mUsername;
    private String mPassword;

    ReactRoomDB database;
    ExerciseDao exerciseDao;

    private Exercise exercise = new Exercise();
    private ArrayList<Position> mPositionList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PositionAdapter positionAdapter;
    private FloatingActionButton btn_apply;

    private boolean positionsCleared = false;
    private int sequenceCounter = 0;

    private CreateSequenceFragment()
    {
        // Required empty public constructor
    }

    public static CreateSequenceFragment newInstance(String username, String password)
    {
        CreateSequenceFragment fragment = new CreateSequenceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_PASSWORD, password);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUsername = getArguments().getString(ARG_USERNAME);
            mPassword = getArguments().getString(ARG_PASSWORD);
        }
        //initPositions();
        database = Room.databaseBuilder(getContext(), ReactRoomDB.class, "reactdb")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        exerciseDao = database.exerciseDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        /*final LayoutInflater factory = getLayoutInflater();
        final View botNavView = factory.inflate(R.layout.fragment_navigation, null);
        BottomNavigationView bottomNavigationView = botNavView.findViewById(R.id.navigationView);
        bottomNavigationView.getMenu().findItem(R.id.navigation_profile).isEnabled() = false;*/

        // Inflate the layout for this fragment
        View retView = inflater.inflate(R.layout.fragment_create_sequence, container, false);
        //set title of custom toolbar
        View innerLayout =retView.findViewById(R.id.tb_create_sequence);
        Toolbar toolbar = innerLayout.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        //set title of action bar
        TextView t_tv_title = toolbar.findViewById(R.id.t_tv_title);
        t_tv_title.setText(getString(R.string.title_create_sequence));
        initView(retView);
        return retView;
    }

    private void initView(View view)
    {
        //position adapter
        recyclerView = view.findViewById(R.id.rv_positions);
        //recyclerView.setHasFixedSize(true);
        //fill recyclerView, set layout manager, first time we have to set the number of columns, so new layout manager will be created
        initPositionsLayout(true);

        //set onclick listener for recyclerView items
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                Button btn_postion = view.findViewById(R.id.btn_position);
                //positions will be cleared if one click happened on an itemview
                if (!positionsCleared)
                {
                    clearPositionViewTexts();
                }
                //set up new layout for a button after clicking
                btn_postion.setText(Integer.toString(++sequenceCounter));
                btn_postion.setTextColor(getResources().getColor(R.color.white));
                btn_postion.setBackgroundResource(R.drawable.position_filled_background);
                //set unclickable
                //TODO make button possible to unset
                //it is not so simple...
                btn_postion.setEnabled(false);

                exercise.sequence.add(position);
            }

            @Override
            public void onLongItemClick(View view, int position)
            {

            }
        }));

        //Rows setText
        final TextView tv_rows_value = view.findViewById(R.id.tv_rows_value);
        tv_rows_value.setText(Integer.toString(exercise.rows));
        //Columns setText
        final TextView tv_columns_value = view.findViewById(R.id.tv_columns_value);
        tv_columns_value.setText(Integer.toString(exercise.columns));
        //Repeat setText
        final TextView tv_repeat_value = view.findViewById(R.id.tv_repeat_value);
        tv_repeat_value.setText(Integer.toString(exercise.repeat));
        //Delay setText
        final TextView tv_delay_value = view.findViewById(R.id.tv_delay_value);
        tv_delay_value.setText(Double.toString(exercise.delay));
        //Probability setText
        final TextView tv_probability_value = view.findViewById(R.id.tv_probability_value);
        tv_probability_value.setText(Double.toString(exercise.probability));

        //set onClick to handle  + and - buttons for...
        //rows
        view.findViewById(R.id.rows_minus).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (exercise.rows > 1)
                {
                    tv_rows_value.setText(Integer.toString(--exercise.rows));
                    initPositionsLayout(false);
                }
            }
        });
        view.findViewById(R.id.rows_plus).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (exercise.rows < 8)
                {
                    tv_rows_value.setText(Integer.toString(++exercise.rows));
                    initPositionsLayout(false);
                }
            }
        });
        //columns
        view.findViewById(R.id.columns_minus).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (exercise.columns > 1)
                {
                    tv_columns_value.setText(Integer.toString(--exercise.columns));
                    initPositionsLayout(true);
                }
            }
        });
        view.findViewById(R.id.columns_plus).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (exercise.columns < 8)
                {
                    tv_columns_value.setText(Integer.toString(++exercise.columns));
                    initPositionsLayout(true);
                }
            }
        });

        view.findViewById(R.id.repeat_minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exercise.repeat > 1) {
                    tv_repeat_value.setText(Integer.toString(--exercise.repeat));
                }
            }
        });
        view.findViewById(R.id.repeat_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_repeat_value.setText(Integer.toString(++exercise.repeat));
            }
        });
        view.findViewById(R.id.delay_minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exercise.delay > 0) {
                    tv_delay_value.setText(doublePrecision(exercise.delay -= 0.1, 1));
                }
            }
        });
        view.findViewById(R.id.delay_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_delay_value.setText(doublePrecision(exercise.delay += 0.1, 1));
            }
        });
        view.findViewById(R.id.probability_minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exercise.probability > 0) {
                    tv_probability_value.setText(doublePrecision(exercise.probability -= 0.05, 2));
                }
            }
        });
        view.findViewById(R.id.probability_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exercise.probability < 1) {
                    tv_probability_value.setText(doublePrecision(exercise.probability += 0.05, 2));
                }
            }
        });

        Drawable drawable = getResources().getDrawable(R.drawable.checkmark48);
        drawable.mutate();
        //change color of resource
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        btn_apply = view.findViewById(R.id.btn_apply);
        btn_apply.setImageDrawable(drawable);
        btn_apply.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Log.d("DEBUG - CreateSeqFrag","apply button click");
                final View mView = getLayoutInflater().inflate(R.layout.fragment_enter_sequence_name, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(mView);
                builder.setTitle(R.string.tv_save_sequence);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        EditText et_sequence_name = mView.findViewById(R.id.et_sequence_name);
                        exercise.exerciseName = et_sequence_name.getText().toString();
                        exercise.createdBy = mUsername;

                        exerciseDao.insertExercise(exercise);
                        //((MainActivity) getActivity()).saveExercise(exercise);
                        SequencesFragment f = (SequencesFragment) getFragmentManager().findFragmentByTag("sequences");
                        f.updateRecyclerView(exercise);
                        //TODO make a toast about the saved sequence

                        //getActivity().getFragmentManager().popBackStack();
                        //TODO something is not ok with this line...
                        getActivity().onBackPressed();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    private void initPositionsLayout(boolean numberOfColumnsChanged)
    {
        //clear all selected positions, and start saving a new sequence
        sequenceCounter = 0;
        exercise.sequence.clear();
        mPositionList.clear();
        //if the number of columns has changed new layout manager needs to be created
        if(numberOfColumnsChanged)
        {
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),exercise.columns);
            recyclerView.setLayoutManager(layoutManager);
            positionAdapter = new PositionAdapter(getActivity(),mPositionList);
            recyclerView.setAdapter(positionAdapter);
        }

        //fill the list with the new number of elements
        for (int i = 0; i < exercise.rows*exercise.columns; i++)
        {
            mPositionList.add(new Position());
            //View child = recyclerView.getChildAt(i);
        }
        //update recyclerview
        positionAdapter.notifyDataSetChanged();
    }

    private static String doublePrecision(double amt, int precision)
    {
        return String.format("%." + precision + "f", amt);
    }

    private void clearPositionViewTexts()
    {
        for(int i=0; i < exercise.rows*exercise.columns; ++i)
        {
            View child = recyclerView.getChildAt(i);
            Button btn_position = child.findViewById(R.id.btn_position);
            btn_position.setText("");
        }

        View applyView = btn_apply;
        applyView.setVisibility(View.VISIBLE);

        positionsCleared = true;
    }
}
