package com.sapientia.catchit.serverrelated;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.sapientia.catchit.dao.MyObjectStringConverter;
import com.sapientia.catchit.fragments.ProfileFragment;
import com.sapientia.catchit.fragments.RegisterFragment;
import com.sapientia.catchit.models.Group;
import com.sapientia.catchit.models.Player;
import com.sapientia.catchit.models.Trainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;


//Android forbids placing any network operation in the main UI because it would block the interface with the user
//About AsynkTask https://stackoverflow.com/questions/18289623/how-to-use-asynctask
//make it singleton ?
public class ReactDBServer
{
    private static final String SERVER_ADDRESS = "192.168.0.100";
    private static final String PORT = "8080";
    private InetAddress serverAddress;
    private int port;
    Context context;
    MyObjectStringConverter objectStringConverter = new MyObjectStringConverter();;


    public ReactDBServer(Context context)
    {
        this.context = context;
    }

    public void attemptTrainerInsertion(Trainer trainer, RegisterFragment registerFragment)
    {
        String csvTrainer = objectStringConverter.trainerToCsv(trainer);
        InsertUserAsyncTask insertUserAsyncTask = new InsertUserAsyncTask(registerFragment);
        insertUserAsyncTask.execute("0",csvTrainer);
    }

    public void attemptPlayerInsertion(Player player, RegisterFragment registerFragment)
    {
        String csvPlayer = objectStringConverter.playerToCsv(player);
        InsertUserAsyncTask insertUserAsyncTask = new InsertUserAsyncTask(registerFragment);
        insertUserAsyncTask.execute("1",csvPlayer);
    }

    private void insertGroup(Group group, ProfileFragment profileFragment)
    {
        String csvGroup = objectStringConverter.groupToCsv(group);
        InsertGroupAsyncTask insertGroupAsyncTask = new InsertGroupAsyncTask(profileFragment);
        insertGroupAsyncTask.execute(csvGroup);
    }

    private static class InsertUserAsyncTask extends AsyncTask<String, Void, Integer>
    {
        private RegisterFragment registerFragment;
        String roleCase;
        public InsertUserAsyncTask(RegisterFragment registerFragment)
        {
            super();
            this.registerFragment = registerFragment;
        }

        //network operation on a separate thread
        @Override
        protected Integer doInBackground(String... params)
        {
            roleCase = params[0];
            String csvUser = params[1];
            String host = SERVER_ADDRESS;
            int port = Integer.parseInt(PORT);
            int timeout = 5000;
            Socket socket = new Socket();

            try
            {
                //connect to server
                //socket.setSoTimeout(timeout);
                socket.connect(new InetSocketAddress(host,port));
                PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //if the user is a trainer
                if (roleCase.equals("0"))
                {
                    String messageToSend = "00," + csvUser;
                    out.println(messageToSend);
                    out.flush();
                }
                else if (roleCase.equals("1"))
                {
                    String messageToSend = "01," + csvUser;
                    //Log.d("DEBUG-DB","Message to send: " + messageToSend);
                    out.println(messageToSend);
                    out.flush();
                }

                try
                {
                    String messageRecv = in.readLine();
                    //Log.d("DEBUG-DB","Background return value> "+ Integer.parseInt(messageRecv));
                    return Integer.parseInt(messageRecv);
                }
                catch (IOException e)
                {
                    Log.d("MYDEBUG","Read fail:" + e.getMessage());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //close socket
            try
            {
                //this line also closes the associated buffer
                socket.close();
            } catch (Exception e) {
                Log.e("ReactServerClient", "Could not close socket: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer id)
        {
            super.onPostExecute(id);
            if (roleCase.equals("0"))
            {
                registerFragment.registerTrainer(id);
            }
            else if (roleCase.equals("1"))
            {
                registerFragment.registerPlayer(id);
            }

        }
    }

    private static class InsertGroupAsyncTask extends AsyncTask<String, Void, Long>
    {
        private ProfileFragment profileFragment;

        public InsertGroupAsyncTask(ProfileFragment profileFragment)
        {
            super();
            this.profileFragment = profileFragment;
        }

        @Override
        protected Long doInBackground(String... params)
        {
            String csvGroup = params[0];
            String host = SERVER_ADDRESS;
            int port = Integer.parseInt(PORT);
            int timeout = 5000;
            Socket socket = new Socket();

            try
            {   //connect to server
                socket.connect(new InetSocketAddress(host,port));
                PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String messageToSend = "10," + csvGroup;
                out.println(messageToSend);
                out.flush();

                try
                {
                    String messageRecv = in.readLine();
                    //Log.d("DEBUG-DB","Background return value> "+ Integer.parseInt(messageRecv));
                    return Long.parseLong(messageRecv);
                }
                catch (IOException e)
                {
                    Log.d("MYDEBUG","Read fail:" + e.getMessage());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //close socket
            try
            {
                socket.close();
            } catch (Exception e) {
                Log.e("ReactServerClient", "Could not close socket: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Long id)
        {
            super.onPostExecute(id);


        }
    }

    //------------------------------TEST-----------------------------------------------------------
    public void test()
    {
        TestAsyncTask testasynctask = new TestAsyncTask();
        testasynctask.setContext(context);
        testasynctask.execute(SERVER_ADDRESS,PORT);
    }

    private static class TestAsyncTask extends AsyncTask<String, Void, String>
    {
        Context context;

        public TestAsyncTask()
        {
            super();
        }

        public void setContext(Context context)
        {
            this.context = context;
        }

        //network operation on a separate thread
        @Override
        protected String doInBackground(String... params)
        {
            String host = params[0];
            int port = Integer.parseInt(params[1]);
            int timeout = 5000;
            Socket socket = new Socket();

            try
            {
                //connect to server
                //socket.setSoTimeout(timeout);
                socket.connect(new InetSocketAddress(host,port));
                PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println("Hello");
                out.flush();
                //out.close();

                try
                {
                    String message = in.readLine();
                    return message;
                }
                catch (IOException e)
                {
                    Log.d("MYDEBUG","Read fail:" + e.getMessage());
                }


            } catch (IOException e)
            {
                e.printStackTrace();
            }
            //close socket
            try
            {
                //this line also closes the associated buffer
                socket.close();
            } catch (Exception e) {
                Log.e("ReactServerClient", "Could not close socket: " + e.getMessage());
            }


            return null;
        }
        @Override
        protected void onPostExecute(String message)
        {
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
            super.onPostExecute(message);
        }
    }

}

