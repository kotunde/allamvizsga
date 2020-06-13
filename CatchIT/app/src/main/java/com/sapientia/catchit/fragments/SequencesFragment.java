package com.sapientia.catchit.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.sapientia.catchit.R;
import com.sapientia.catchit.ReactRoomDB;
import com.sapientia.catchit.dao.ExerciseDao;
import com.sapientia.catchit.helpers.PositionAdapter;
import com.sapientia.catchit.helpers.RecyclerItemClickListener;
import com.sapientia.catchit.helpers.SequenceAdapter;
import com.sapientia.catchit.models.Exercise;
import com.sapientia.catchit.models.Position;

import java.util.ArrayList;
import java.util.List;

public class SequencesFragment extends Fragment
{
    private static final String ARG_USERNAME = "username";
    private static final String ARG_ROLE = "role";

    ReactRoomDB database;
    ExerciseDao exerciseDao;

    private String mUsername;
    private String mRole;
    private ArrayList<Exercise> mExerciseList = new ArrayList<>();
    SequenceAdapter sequenceAdapter;
    RecyclerView sequenceRecyclerView;

    //for preview sequence dialog fragment
    private RecyclerView positionRecyclerView;
    private PositionAdapter positionAdapter;
    private ArrayList<Position> mPositionList = new ArrayList<>();

    public SequencesFragment()
    {
        // Required empty public constructor
    }

    public static SequencesFragment newInstance(String username, String role)
    {
        SequencesFragment fragment = new SequencesFragment();
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
        exerciseDao = database.exerciseDao();

        //for toolbar icons
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View retView = inflater.inflate(R.layout.fragment_sequences, container, false);
        setHasOptionsMenu(true);
        //setup custom toolbar
        Toolbar toolbar = retView.findViewById(R.id.tb_sequences);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        //set title of action bar
        TextView t_tv_title = toolbar.findViewById(R.id.t_tv_title);
        t_tv_title.setText(getString(R.string.title_sequences));

        initView(retView);
        return retView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_sequences_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
        return ;
    }

    private void initView(View view)
    {
        //create and populate recyclerView
        sequenceRecyclerView = view.findViewById(R.id.rv_sequences);
        sequenceAdapter = new SequenceAdapter(getActivity(),mExerciseList);
        sequenceRecyclerView.setAdapter(sequenceAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        sequenceRecyclerView.setLayoutManager(layoutManager);

        //refresh recyclerview list, getting data from database

        //mExerciseList = ((MainActivity)getActivity()).getExercises();
        new getAllExercisesAsyncTask(getContext(),sequenceRecyclerView,mExerciseList,exerciseDao).execute();
        Log.d("DEBUG-SeqF","Asynctask called.");


        //recycler View item click listener (on sequence item click)
        sequenceRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), sequenceRecyclerView, new RecyclerItemClickListener.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                //start sequence preview dialog fragment
                showPreviewSequenceDialog(position);
            }
            @Override
            public void onLongItemClick(View view, int position)
            {
                //TODO if trainer > options: upload sequence; delete sequence
            }
        }));
    }

    //handle toolbar buttons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.new_sequence)
        {
            CreateSequenceFragment createSequenceFragment = CreateSequenceFragment.newInstance(mUsername,mRole);
            //TODO remove bottom navigation from layout (find navigation fragment, and hide it)
            //TODO option appears on other fragments' toolbar
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment navigationFragment =  fragmentManager.findFragmentByTag("navigation");
            //if(fragmentManager.findFragmentByTag("navigation")!=null)
            /*if(navigationFragment!=null)
            {
                //fragmentTransaction.remove(fragmentManager.findFragmentByTag("navigation"));
                fragmentTransaction.remove(navigationFragment);
                //fragmentTransaction.commit();
            }*/
            fragmentTransaction.add(R.id.fl_navPlaceholder,createSequenceFragment.newInstance(mUsername,mRole),"createSequence");
            //fragmentTransaction.add(R.id.fl_placeholder,createSequenceFragment).hide(navigationFragment);
            fragmentTransaction.addToBackStack("createSeq");
            fragmentTransaction.commit();
            return true;
        }
        else if(id == R.id.import_sequence)
        {
            showImportSequenceDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }


    private void showPreviewSequenceDialog(int position)
    {
        final View dialogView = getLayoutInflater().inflate(R.layout.fragment_preview_sequence, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        //get actual exercise
        final Exercise actualExercise = mExerciseList.get(position);
        //initialize positions' layout
        initPositionsLayout(dialogView,actualExercise);
        //initialize textViews, block buttons
        initParameters(dialogView,actualExercise);

        builder.setView(dialogView);
        //set title of sequence(exercise)
        builder.setTitle(mExerciseList.get(position).exerciseName);
        //alertDialog buttons' onclick listener
        builder.setPositiveButton(getResources().getString(R.string.play), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //TODO start new fragment for playing

                // start new fragment for the game
                GameFragment gameFragment = new GameFragment(actualExercise);//GameFragment.newInstance(mUsername,mPassword);
                //TODO remove bottom navigation from layout (find navigation fragment, and hide it)
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //send data to Gamefragment
                Bundle bundle = new Bundle();
                bundle.putString("username", mUsername);
                bundle.putString("role", mRole);

                gameFragment.setArguments(bundle);
                //have to replace the old one to hide the previous fragment's toolbar
                fragmentTransaction.replace(R.id.fl_navPlaceholder,gameFragment,"game");//gameFragment.newInstance(mUsername,mPassword),"game");
                fragmentTransaction.addToBackStack("game");
                fragmentTransaction.commit();


            }
        });
        builder.setNeutralButton(getResources().getString(R.string.importSeq),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(getResources().getString(R.string.back), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    private void showImportSequenceDialog()
    {
        //TODO xml
        final View dialogView = getLayoutInflater().inflate(R.layout.fragment_import_sequence, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        //set title of sequence(exercise)
        builder.setTitle(getString(R.string.downloadSeq));
        builder.setPositiveButton(getResources().getString(R.string.download), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //TODO
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    private void initPositionsLayout(View dialogView,final Exercise exercise)
    {
        //set recyclerview
        positionRecyclerView = dialogView.findViewById(R.id.rv_positions);
        RecyclerView.LayoutManager layoutManager =  new GridLayoutManager(getContext(),exercise.columns);
        positionRecyclerView.setLayoutManager(layoutManager);
        positionAdapter = new PositionAdapter(getActivity(),mPositionList);
        positionRecyclerView.setAdapter(positionAdapter);

        mPositionList.clear();
        //initialize Position ArrayList
        for (int i = 0; i < exercise.rows*exercise.columns; i++)
        {
            mPositionList.add(new Position());
        }

        //Log.d("DEBUG - SeqFrag","Actual ezercise: ");
        //set sequence number of each position which was chosen as a nodePosition (important for UI)
        for (int i = 0; i < exercise.sequence.size(); i++)
        {
            //Log.d("DEBUG - SeqFrag","i: " + i+ " ex: "+ exercise.sequence.get(i));
            mPositionList.get(exercise.sequence.get(i)).setSequenceNumber(i + 1);
        }


        //LINKEK>
        //https://stackoverflow.com/questions/30397460/how-to-know-when-the-recyclerview-has-finished-laying-down-the-items
        //https://stackoverflow.com/questions/14119128/how-to-know-when-gridview-is-completely-drawn-and-ready/14119464#14119464
        //https://stackoverflow.com/questions/28300146/recyclerview-i-cant-get-item-view-with-getchildatposition-null-object-refer
        positionRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                final Handler handler = new Handler();

                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for(int i = 0; i < exercise.sequence.size(); ++i)
                        {
                            //get chosen positions and customize their view
                            //Position position = mPositionList.get(exercise.sequence.get(i));
                            //View child = positionRecyclerView.getChildAt(exercise.sequence.get(i));
                            View child = positionRecyclerView.getLayoutManager().findViewByPosition(exercise.sequence.get(i));
                            Log.d("DEBUG-SeFrag","getChild: " + child + "   index: "+ i + " position:"+ exercise.sequence.get(i));
                            Button btn_position = child.findViewById(R.id.btn_position);
                            //Log.d("DEBUG-SeFrag","button position: " + btn_position);
                            btn_position.setVisibility(View.VISIBLE);
                            btn_position.setText(String.valueOf(i+1));
                            btn_position.setBackgroundResource(R.drawable.position_filled_background);
                            btn_position.setTextColor(getResources().getColor(R.color.white));
                        }
                    }
                }, 800);



                positionRecyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

//        final Handler handler = new Handler();
//
//        handler.postDelayed(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                for(int i = 0; i < exercise.sequence.size(); ++i)
//                {
//                    //get chosen positions and customize their view
//                    //Position position = mPositionList.get(exercise.sequence.get(i));
//                    //View child = positionRecyclerView.getChildAt(exercise.sequence.get(i));
//                    View child = positionRecyclerView.getLayoutManager().findViewByPosition(exercise.sequence.get(i));
//                    Log.d("DEBUG-SeFrag","getChild: " + child + "   index: "+ i + " position:"+ exercise.sequence.get(i));
//                    Button btn_position = child.findViewById(R.id.btn_position);
//                    //Log.d("DEBUG-SeFrag","button position: " + btn_position);
//                    btn_position.setVisibility(View.VISIBLE);
//                    btn_position.setText(String.valueOf(i+1));
//                    btn_position.setBackgroundResource(R.drawable.position_filled_background);
//                    btn_position.setTextColor(getResources().getColor(R.color.white));
//                }
//            }
//        }, 800);

/*        positionRecyclerView.post(new Runnable()
        {
            @Override
            public void run()
            {

            }
        });*/



        positionRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), positionRecyclerView, new RecyclerItemClickListener.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {

            }

            @Override
            public void onLongItemClick(View view, int position)
            {

            }
        }));
        //update recyclerview
        positionAdapter.notifyDataSetChanged();
    }

    private void initParameters(View dialogView,Exercise exercise)
    {
        //Rows setText
        final TextView tv_rows_value = dialogView.findViewById(R.id.tv_rows_value);
        tv_rows_value.setText(Integer.toString(exercise.rows));
        Log.d("DEBUG-SeqFrag","rows_value set to "+ Integer.toString(exercise.rows));
        //Columns setText
        final TextView tv_columns_value = dialogView.findViewById(R.id.tv_columns_value);
        tv_columns_value.setText(Integer.toString(exercise.columns));
        //Repeat setText
        final TextView tv_repeat_value = dialogView.findViewById(R.id.tv_repeat_value);
        tv_repeat_value.setText(Integer.toString(exercise.repeat));
        //Delay setText
        final TextView tv_delay_value = dialogView.findViewById(R.id.tv_delay_value);
        tv_delay_value.setText(Double.toString(exercise.delay));
        //Probability setText
        final TextView tv_probability_value = dialogView.findViewById(R.id.tv_probability_value);
        tv_probability_value.setText(Double.toString(exercise.probability));

        //block buttons
        dialogView.findViewById(R.id.rows_minus).setClickable(false);
        dialogView.findViewById(R.id.rows_plus).setClickable(false);
        dialogView.findViewById(R.id.columns_minus).setClickable(false);
        dialogView.findViewById(R.id.columns_plus).setClickable(false);
        dialogView.findViewById(R.id.repeat_minus).setClickable(false);
        dialogView.findViewById(R.id.repeat_plus).setClickable(false);
        dialogView.findViewById(R.id.delay_minus).setClickable(false);
        dialogView.findViewById(R.id.delay_plus).setClickable(false);
        dialogView.findViewById(R.id.probability_minus).setClickable(false);
        dialogView.findViewById(R.id.probability_plus).setClickable(false);
    }

    //called from CreateSequenceFragment when AlertDialog Ok button pushed
    public void updateRecyclerView(Exercise exercise)
    {
        new getAllExercisesAsyncTask(getContext(),sequenceRecyclerView,mExerciseList,exerciseDao).execute();
    }

    //TODO mute this part to repository
    private static class getAllExercisesAsyncTask extends AsyncTask<Void, Void, List<Exercise>>
    {
        private ExerciseDao exerciseDao;
        private ArrayList<Exercise> exerciseList;
        SequenceAdapter sequenceAdapter;

        @SuppressLint("StaticFieldLeak")
        private Context context;
        @SuppressLint("StaticFieldLeak")
        RecyclerView sequenceRecyclerView;

        private getAllExercisesAsyncTask(Context context, RecyclerView sequenceRecyclerView,ArrayList<Exercise> exerciseList, ExerciseDao exerciseDao)
        {
            this.context = context;
            this.sequenceRecyclerView = sequenceRecyclerView;
            this.exerciseList = exerciseList;
            this.exerciseDao = exerciseDao;
        }

        @Override
        protected List<Exercise> doInBackground(Void... voids)
        {
            exerciseList.clear();
            exerciseList.addAll(exerciseDao.getAllExercises());
            return exerciseList;
        }

        @Override
        protected void onPostExecute(List<Exercise> exercises)
        {
            //Log.d("DEBUG-SeFrag","Size of exercise list: "+ exerciseList.size());
            sequenceAdapter = new SequenceAdapter(context,exerciseList);
            sequenceRecyclerView.setAdapter(sequenceAdapter);
            sequenceAdapter.notifyDataSetChanged();
            super.onPostExecute(exercises);

        }

    }
}
