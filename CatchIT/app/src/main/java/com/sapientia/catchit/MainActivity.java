package com.sapientia.catchit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import com.sapientia.catchit.fragments.LoginFragment;
import com.sapientia.catchit.models.Exercise;
import com.sapientia.catchit.models.Statistics;
import com.sapientia.catchit.serverrelated.NodeClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
{
    private ServerSocket serverSocket;
    private Thread thread;
    private ArrayList<NodeClient> nodes =  new ArrayList<>();
    private ArrayList<Statistics> statistics = new ArrayList<>();
    private ArrayList<Exercise> exercises = new ArrayList<>();
    private static final int SERVER_PORT = 13000;

    public MainActivity() {
        initServer();
    }

    private void initServer()
    {
        try
        {
            //create socket and assign address to it
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(SERVER_PORT));

        } catch (IOException e)
        {
            Log.d("Server address fail.", e.getMessage());
        }


        Log.d("SERVER", "Server Started!");

        thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (!serverSocket.isClosed())
                {
                    try
                    {
                        //extracts the first connection request on the queue of pending connections, creates a new connected socket
                        Socket clientSocket = serverSocket.accept();
                        Log.d("SERVER", "Node Connected");
                        nodes.add(new NodeClient(clientSocket,getApplicationContext()));

                    } catch (IOException e)
                    {
                        Log.d("SERVER", e.getMessage());
                    }
                }
            }
        });
        thread.start();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //load LoginFragment
        LoginFragment loginFragment = new LoginFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fl_placeholder,loginFragment);
        fragmentTransaction.commit();

    }

    public ArrayList<NodeClient> getNodes() {
        return nodes;
    }

    public ArrayList<Exercise> getExercises()
    {
        //TODO extend this function later
        //what?
        return exercises;
    }

    public ArrayList<Statistics> getStats() {
        //TODO extend this function later
        //what
        return statistics;
    }

    public void saveExercise(Exercise exercise)
    {
        //TODO rename this method, it does not saves exercise anymore, since it is solved by sqlite
        exercises.add(exercise);
    }

    public void saveStatistic(Statistics statistics) {
        this.statistics.add(statistics);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        nodes.clear();
    }
}
