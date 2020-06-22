package com.sapientia.catchit.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.sapientia.catchit.MainActivity;
import com.sapientia.catchit.R;
import com.sapientia.catchit.ReactRoomDB;
import com.sapientia.catchit.dao.StatisticsDao;
import com.sapientia.catchit.helpers.PositionInvAdapter;
import com.sapientia.catchit.helpers.RecyclerItemClickListener;
import com.sapientia.catchit.models.Exercise;
import com.sapientia.catchit.models.Position;
import com.sapientia.catchit.models.Statistics;
import com.sapientia.catchit.serverrelated.NodeClient;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.sapientia.catchit.serverrelated.NodeMessageHandler;

public class GameFragment extends Fragment implements NodeMessageHandler
{
    ReactRoomDB database;
    StatisticsDao statisticsDao;

    private Exercise exercise;
    private Statistics statistics;
    Spinner sp_players;

    private ArrayList<Position> mPositionList = new ArrayList<>();
    private ArrayList<NodeClient> nodes;

    private PositionInvAdapter positionInvAdapter;

    private RecyclerView positionRecyclerView;
    private Button startButton;

    private int currentSequenceIndex = 0;
    private boolean isSequencePlaying = false;
    private int nextRepeat;
    private boolean isRandom;
    private Random randomGen;
    private int numberOfAssignedNodes = 0; //number of nodes the 'WAIT' msg was sent to
    private int numberOfIncomeMessage = 0;

    //try
    private static ReentrantLock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();

    private static final String ARG_USERNAME = "username";
    private static final String ARG_ROLE = "role";

    private String mUsername;
    private String mRole;

    public GameFragment()
    {
        // Required empty public constructor
    }

    public GameFragment(Exercise exercise)
    {
        this.exercise = exercise;
    }

    public static GameFragment newInstance(String username, String role)
    {
        GameFragment fragment = new GameFragment();
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
        if (getArguments() != null)
        {
            mUsername = getArguments().getString(ARG_USERNAME);
            mRole = getArguments().getString(ARG_ROLE);
        }
        if (exercise == null)
        {
            //Log.d("EXERCISE-NULL", exercise.exerciseName); ???
            Log.d("EXERCISE-NULL", "Exercise null!");
        }

        database = Room.databaseBuilder(getContext(), ReactRoomDB.class, "reactdb")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        statisticsDao = database.statisticsDao();

        //get joined nodes
        nodes = ((MainActivity) getActivity()).getNodes();
        nextRepeat = exercise.repeat * exercise.sequence.size();
        randomGen = new Random();

        //Log.d("EXERCISE", exercise.toString());
    }
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View retView =  inflater.inflate(R.layout.fragment_game, container, false);
        setHasOptionsMenu(true);
        //customize toolbar
        Toolbar toolbar = retView.findViewById(R.id.tb_game);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        //just for testing, set title of action bar
        sp_players = toolbar.findViewById(R.id.sp_players);
        List<String> sp_playersArray = new ArrayList<String>();
        sp_playersArray.add(mUsername); //tulajdonos - default jatekos
        //TODO itt kell majd feltolteni a lehetseges jatekosok listajat a toolbarnak az AB-bol

        ArrayAdapter<String> sp_adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,sp_playersArray);
        sp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_players.setAdapter(sp_adapter);
        //https://stackoverflow.com/questions/11920754/android-fill-spinner-from-java-code-programmatically
        
        initView(retView);
        
        return retView;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_game_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.close_window:

                //GameFragment currentFragment = this;
                //the below row causes duplicates in sequence fragment's list //TODO fix this
                //getActivity().getSupportFragmentManager().popBackStack();
                getActivity().onBackPressed();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initView(View view)
    {
        //recycler view of position grid
        positionRecyclerView = view.findViewById(R.id.rv_positions);
        //fill recyclerView, set layout manager
        initPositionsLayout();

        //TODO jatek utan tarsitas unset-elese
        //set onclick listener for recyclerView items
        positionRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), positionRecyclerView, new RecyclerItemClickListener.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                final Position p = mPositionList.get(position);
                //list with all node names
                String[] nodeNames = new String[nodes.size()];
                for (int i = 0; i < nodes.size(); i++)
                {
                    nodeNames[i] = nodes.get(i).getId();
                    //Log.d("MYDEBUG","Ciklus "+ i+": "+nodes.get(i)+" "+nodeNames[i]);

                }
                //Log.d("MYDEBUG","Builder elott; nodes merete: " + nodes.size());

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Pick a Node");
                //save picked node
                builder.setItems(nodeNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("MYDEBUG","node.click");
                        NodeClient node = nodes.get(which);
                        if (!node.hasHandler())
                        {
                            node.setMessageHandler(GameFragment.this);
                        }
                        //if one node is assigned again, the previous will be deleted
                        //TODO UI: legyen visszajelzes arrol hogy mely node-ok voltak mar tarsitva(mihez)
                        for (int i = 0; i < exercise.rows*exercise.columns; i++) {
                            if (mPositionList.get(i).getAssignedNode() == node) {
                                mPositionList.get(i).setAssignedNode(null);
                            }
                        }
                        Log.d("MYDEBUG","---------------"+ node.getId()+" ATTACHED---------------");
                        p.setAssignedNode(node);
                        //Log.d("MYDEBUG","positionAdapter elott.");
                        positionInvAdapter.notifyDataSetChanged();
                        setActivePositionsVisible();
                    }
                });
                builder.show();
            }

            @Override
            public void onLongItemClick(View view, int position)
            {

            }
        }));

        //handle random switch
        ((Switch) view.findViewById(R.id.switch_random)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isRandom = isChecked;
            }
        });

        startButton = view.findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                Log.d("MYDEBUG","---------------SEQUENCE STARTED---------------");
                startSequence();
            }
        });
        startButton.setVisibility(View.VISIBLE);


    }

    private void initPositionsLayout()
    {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),exercise.columns);
        positionRecyclerView.setLayoutManager(layoutManager);
        positionInvAdapter = new PositionInvAdapter(getActivity(),mPositionList);
        positionRecyclerView.setAdapter(positionInvAdapter);

        //clear position list to initialize
        mPositionList.clear();
        //fill the list with the new number of elements
        for (int i = 0; i < exercise.rows*exercise.columns; i++)
        {
            mPositionList.add(new Position());
        }
        //update recyclerview
        positionInvAdapter.notifyDataSetChanged(); // TODO maybe uncomment

        //set sequence number of each position which was chosen as a nodePosition (important for UI)
        for (int i = 0; i < exercise.sequence.size(); i++)
        {
            mPositionList.get(exercise.sequence.get(i)).setSequenceNumber(i + 1);
        }

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
//                for (int i = 0; i < exercise.rows * exercise.columns; ++i)
//                {
//                    View child = positionRecyclerView.getChildAt(i);
//                    Button btn_position = child.findViewById(R.id.btn_position);
//                    //btn_position.setVisibility(View.INVISIBLE);
//                    btn_position.setText("" + (i + 1) + "");
//                    btn_position.setBackgroundResource(R.drawable.position_empty_background);
//                    btn_position.setTextColor(getContext().getResources().getColor(R.color.black));
//                }

                for(int i = 0; i < exercise.sequence.size(); ++i)
                {
                    //get chosen positions and customize their view
                    View child = positionRecyclerView.getChildAt(exercise.sequence.get(i));
                    //Log.d("DEBUG-SeFrag","getChild: " + child);
                    Button btn_position = child.findViewById(R.id.btn_position);
                    //Log.d("DEBUG-SeFrag","button position: " + btn_position);
                    btn_position.setVisibility(View.VISIBLE);
                    btn_position.setText(String.valueOf(i+1));
                    btn_position.setBackgroundResource(R.drawable.position_filled_background);
                    btn_position.setTextColor(getResources().getColor(R.color.white));
                }
            }
        }, 300);
    }

    public void handleMessage(final NodeClient from, String message)
    {
        Log.d("SEQUENCE", from.getId() + ": " + message);
        String splitted[] = message.split(",");

        if (splitted[0].equals(NodeClient.RECIEVABLES.OFF.toString()))
        {
            //((MainActivity)getActivity()).myMakeToast("SequenceFragment","disconnect "+ from.getId());

            //cant make a toast on UI thread, because it's no more alive (screen shots down)
            /*getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(),"Node " + from.getId()+ " disconnected",Toast.LENGTH_SHORT).show();
                }
            });*/
            deleteNode(from);
        }
        else if (splitted[0].equals(NodeClient.RECIEVABLES.STARTED.toString()))
        {
            //Log.d("MYDEBUG","STARTED msg got.");
            // TODO ui
            //??? what to do on UI???


            //TODO make this part threadsafe
            lock.lock();
            try
            {
                //if the 'STARTED' msg has been got from all the clients, we send 'LEDOFF' and start the sequence
                ++numberOfIncomeMessage;
                if (numberOfIncomeMessage==numberOfAssignedNodes)
                {
                    numberOfIncomeMessage=0;
                    //sends ledOff --- not used on client side---
                    ledOFF();
                    if (isSequencePlaying)
                    {
                        //sends Measure => the actual exercise begins
                        next();
                    }
                }
                else
                {
                    Log.d("MYDEBUG","Number of income message 'STARTED' = " + numberOfIncomeMessage + " !=  "+ numberOfAssignedNodes);
                }

                condition.signal();
            }finally {
                lock.unlock();
            }

            //TODO end
        }
        else if (splitted[0].equals(NodeClient.RECIEVABLES.MEASURED.toString()))
        {
            //Log.d("MYDEBUG","MEASURED msg got.");
            // TODO ui
            //???? UI?

            //TODO specify the node the measured value came from
            //statistics.addReaction(Double.parseDouble(splitted[1]));
            updateReaction(Double.parseDouble(splitted[1]));
            if (isSequencePlaying)
            {
                next();
            }
            else
            {
                //if sequence is not playing it is the end of game
                resetExercise();

                //resetStartButton(); moved to showResultsDialog: if user continues playing
            }
        }
        else if (splitted[0].equals(NodeClient.RECIEVABLES.WARNING.toString())) {
            if (isSequencePlaying)
            {
                //statistics.addReaction(-1);
                updateReaction(-1);
                // TODO ui
            }
        }
        else
        {
            //((MainActivity)getActivity()).myMakeToast("SequenceFragment","disconnect "+ from.getId());

            deleteNode(from);
        }
    }

    //sends ledoff msg to every assigned nodeClient
    private void ledOFF(){
        for (int i = 0; i < exercise.sequence.size(); i++)
        {
            NodeClient node = mPositionList.get(exercise.sequence.get(i)).getAssignedNode();
            if (node != null)
            {
                node.send(NodeClient.SENDABLES.LEDOFF.toString());
                Log.d("MYDEBUG","LEDOFF sent");
            }
        }
    }

    private void startSequence()
    {
        numberOfAssignedNodes=0;
        String actualPlayer = sp_players.getSelectedItem().toString();
        statistics = new Statistics(actualPlayer,exercise.exerciseName);
        //initialize
        statistics.setReaction_max(Double.MIN_VALUE);
        statistics.setReaction_min(Double.MAX_VALUE);

        //numberOfAssignedNodes counts how many node the 'WAIT' msg was sent to
        //before playing, we send WAIT to each assigned node
        for (int i = 0; i < exercise.sequence.size(); i++)
        {
            NodeClient node = mPositionList.get(exercise.sequence.get(i)).getAssignedNode();
            //it is not necesarry to assign a node to every position
            if (node != null)
            {
                node.send(NodeClient.SENDABLES.WAIT.toString());
                Log.d("MYDEBUG","WAIT sent");
                numberOfAssignedNodes++;
            }
        }

        //if there is at least one attached node, start the game
        if (numberOfAssignedNodes != 0) {
            isSequencePlaying = true;
            startButton.setEnabled(false);
            Log.d("EXERCISE", "EXERCISE STARTED");
        }
    }

    //end of game; notify nodes, show result in a dialogFragment
    private void resetExercise()
    {
        //send sequence ended message to every connected node
        for (int i = 0; i < exercise.sequence.size(); i++)
        {
            NodeClient node = mPositionList.get(exercise.sequence.get(i)).getAssignedNode();
            if (node != null)
            {
                node.send(NodeClient.SENDABLES.SEQEND.toString());
                Log.d("MYDEBUG","SEQEND sent");

            }
        }

        //isSequencePlaying = false;
        //end of game
        nextRepeat = exercise.repeat * exercise.sequence.size();
        currentSequenceIndex = 0;

        //Log.d("MYDEBUG", "Size of statisctic before sent: "+ statistics.getNrOfReactions());
        //insert measured statistics into DB
        statisticsDao.insertStatistics(statistics);

        GameFragment currentFragment = this;
        showResultsDialog(statistics,currentFragment);

    }

    //when a sequence ends, the sever sends WAIT to all connected nodes again
    //modified: when a sequence ends, the start button is enabled again
    private void resetStartButton() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startButton.setEnabled(true);
            }
        });
        //isSequencePlaying = true;

        /*for (int i = 0; i < exercise.sequence.size(); i++) {
            NodeClient node = mPositionList.get(exercise.sequence.get(i)).getAssignedNode();
            if (node != null) {
                node.send(NodeClient.SENDABLES.WAIT.toString());
                Log.d("MYDEBUG","WAIT sent");
            }
        }*/
    }

    private void next()
    {
        NodeClient node = null;
        //the probability which the next measuring node will be generated with
        //nextInt() > returns a pseudorandom, uniformly distributed int value between 0 (inclusive)
        // and the specified value (exclusive), drawn from this random number generator's sequence.
        // exercise.probability is the probability of that the next chosen node is the next from the sequence
        //if exercise.probability =1 and isRandom = false there is no randomness at all

        if (exercise.actualSequence.isEmpty())
        {
            int p;
            do {
                node = null;
                while (node == null)
                {
                    //get assigned node by currentSequenceIndex, at first it will be the 0th of the sequence array
                    node = mPositionList.get(exercise.sequence.get(currentSequenceIndex)).getAssignedNode();
                    // fill the actualSequence with the random values of the very first time
                    exercise.actualSequence.add(exercise.sequence.get(currentSequenceIndex));

                    if (isRandom)
                    {
                        //if random is checked, the next will be generated randomly
                        currentSequenceIndex = randomGen.nextInt(exercise.sequence.size());
                    }
                    else
                    {
                        //if not random, it will be consecutive
                        currentSequenceIndex++;

                        if (currentSequenceIndex == exercise.sequence.size())
                        {
                            currentSequenceIndex = 0;
                        }
                    }
                }
                p = randomGen.nextInt(100);
            } while (p > exercise.probability * 100);
        }
        //if there is a saved sequence (every time, except the first time)
        else
        {
            node = mPositionList.get(exercise.actualSequence.get(currentSequenceIndex)).getAssignedNode();
            if(isRandom)
            {
                int p;
                do {
                    //if random is checked, the next will be generated randomly
                    currentSequenceIndex = randomGen.nextInt(exercise.sequence.size());
                    p = randomGen.nextInt(100);
                } while (p > exercise.probability * 100);
            }
            else
            {
                //get next position from the saved sequence
                currentSequenceIndex++;
                if (currentSequenceIndex == exercise.sequence.size())
                {
                    currentSequenceIndex = 0;
                }
            }
        }

        try {
            Thread.sleep((long) exercise.delay * 1000);
        } catch (Exception e) {
            //TODO handle this exception properly
        }

        //the node
        node.send(NodeClient.SENDABLES.MEASURE.toString());
        //print out which node is measuring now
        Log.d("MYDEBUG","MEASURE sent");
        //Log.d("SEQUENCE",  node.getId());
        Log.d("MYDEBUG","---------------"+ node.getId()+" MEASURES---------------");
        nextRepeat--;
        if (nextRepeat == 0)
        {
            isSequencePlaying = false;
        }
    }

    private void deleteNode(NodeClient node) {
        while(nodes.contains(node)) {
            nodes.remove(node);
        }

        for (int i = 0; i < exercise.rows*exercise.columns; i++) {
            if (node == mPositionList.get(i).getAssignedNode()) {
                mPositionList.get(i).setAssignedNode(null);
            }
        }
        //TODO problem when process terminates (screen shots down)
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                positionInvAdapter.notifyDataSetChanged();
            }
        });

        if (isSequencePlaying) {
            ledOFF();
            next();
        }
    }

    private void showResultsDialog(final Statistics statistics,final GameFragment currentFragemnt)
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                final View dialogView = getLayoutInflater().inflate(R.layout.statistics_view, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(dialogView);

                //get textview references
                TextView tv_exerciseName = dialogView.findViewById(R.id.tv_exerciseName);
                TextView tv_playerName = dialogView.findViewById(R.id.tv_playeName);
                TextView tv_nrOfMistakes = dialogView.findViewById(R.id.tv_nrOfMistakes);
                TextView tv_averageReactionTime = dialogView.findViewById(R.id.tv_avgReaction);
                TextView tv_slowest = dialogView.findViewById(R.id.tv_slowest);
                TextView tv_fastest = dialogView.findViewById(R.id.tv_fastest);

                //fill textview with the results
                tv_playerName.setText("Player: " + statistics.getPlayerName());
                tv_exerciseName.setText("Exercise: " + statistics.getExerciseName());
                tv_nrOfMistakes.setText("Mistakes: " + statistics.getMistakes());

                //round doubles to 3 decimals
                DecimalFormat decimalFormat = new DecimalFormat("0.000");
                //Log.d("DEBUG-GAMEF","Value of average: "+ statistics.get);
                tv_averageReactionTime.setText("Average: " + decimalFormat.format(statistics.getReaction_average()/(1000000*statistics.getNrOfReactions())) + " s");
                tv_slowest.setText("Slowest: " + decimalFormat.format(statistics.getReaction_max()/1000000)+" s");
                tv_fastest.setText("Fastest: " + decimalFormat.format(statistics.getReaction_min()/1000000)+" s");

                builder.setTitle(getResources().getString(R.string.tv_score));
                //alertDialog buttons' onclick listener
                builder.setPositiveButton(getResources().getString(R.string.tv_continue), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        resetStartButton();
                    }
                });

                builder.setNegativeButton(getResources().getString(R.string.tv_exit), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //getActivity().getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });
                builder.create().show();
            }
        });

    }

    //positions have to be set visible after dataset changed
    private void setActivePositionsVisible()
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
                    Log.d("DEBUG - GAME","Position set to visible on index: "+ exercise.sequence.get(i));
                    View child = positionRecyclerView.getChildAt(exercise.sequence.get(i));
                    Button btn_position = child.findViewById(R.id.btn_position);
                    btn_position.setVisibility(View.VISIBLE);
                    btn_position.setText(String.valueOf(i+1));
                    btn_position.setBackgroundResource(R.drawable.position_filled_background);
                    btn_position.setTextColor(getResources().getColor(R.color.white));
                }


//                int seqIndex = 0;
//                for (int i=0; i<exercise.rows * exercise.columns; ++i)
//                {
//                    if (mPositionList.get(i).getSequenceNumber() > 0)
//                    {
//                        //chosen position to assign node to it
//                        Log.d("DEBUG - GAME","Position set to visible on index: "+ exercise.sequence.get(seqIndex));
//                        //View child = positionRecyclerView.getChildAt(exercise.sequence.get(seqIndex));
//                        View child = positionRecyclerView.getChildAt(i);
//                        Button btn_position = child.findViewById(R.id.btn_position);
//                        btn_position.setVisibility(View.VISIBLE);
//                        btn_position.setText(String.valueOf(seqIndex+1));
//                        btn_position.setBackgroundResource(R.drawable.position_filled_background);
//                        btn_position.setTextColor(getResources().getColor(R.color.white));
//                        seqIndex++;
//                    }
//                    else
//                    {
//                        //set explicitly invisible positions that does not belong to the sequence
//                        View child = positionRecyclerView.getChildAt(i);
//                        Button btn_position = child.findViewById(R.id.btn_position);
//                        btn_position.setVisibility(View.INVISIBLE);
//                    }
//                }
            }
        }, 100);
    }
    public void updateReaction(double reaction)
    {
        if (reaction == -1)
        {
            statistics.setMistakes(statistics.getMistakes()+1);
        }
        else
        {
            //update average value
            statistics.setReaction_average(statistics.getReaction_average() + reaction);
            //update number of reactions
            statistics.setNrOfReactions(statistics.getNrOfReactions()+1);

            Log.d("DEBUG-Game","Reaction value tested:" + reaction + "VS min: "+statistics.getReaction_min());
            if (reaction < statistics.getReaction_min())
            {
                Log.d("DEBUG-Game","Is smaller");
                statistics.setReaction_min(reaction);
            }
            if (reaction > statistics.getReaction_max())
            {
                statistics.setReaction_max(reaction);
            }
        }
    }
}