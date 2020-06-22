package com.sapientia.catchit.serverrelated;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NodeClient
{

    public enum SENDABLES
    {
        MEASURE, WAIT, LEDOFF, SEQEND
    }

    public enum RECIEVABLES
    {

        CONNECTED, MEASURED, WARNING, STARTED, OFF, ON
    }

    private String id;
    //not solved yet on client side
    private String battery;

    //client socket of node
    private Socket socket;

    //output stream
    private PrintWriter out;
    //input stream
    private BufferedReader in;

    NodeMessageHandler handler;
    Context context;

    public NodeClient(final Socket socket,Context context)
    {

        this.socket = socket;
        this.context = context;

        try
        {
            //the timeout that a read() call will block
            //we get messeage from a node in every 0,5 s, if we don't, wait for 2000ms(2s), then throw excpt
            socket.setSoTimeout(2000);
            //getting the object that we can use to send/receive data.
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e)
        {
            //TODO
            //T:  make a toast about deconnected node ?
            Log.d("MYDEBUG","Exception in NodeClient");
        }

        //each nodeClient receive messages from nodes, then handles it
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String inputLine;
                while (true)
                {
                    try
                    {
                        inputLine = in.readLine();
                    } catch (IOException e) {
                        Log.d("MYDEBUG","Read fail");
                        break;
                    }

                    //Log.d("MYDEBUG","HandleMessage called");
                    handleMessage(inputLine);
                }

                try
                {
                    socket.close();
                } catch (Exception e) {
                    Log.e("NODE", "Could not close socket: " + e.getMessage());
                }
                //??? Mire jo? hol van ez az uzenet lekezelve?
                //--> SequenceFragment
                handleMessage(RECIEVABLES.OFF.toString());
            }
        }).start();
    }

    public void setMessageHandler(NodeMessageHandler handler) {
        this.handler = handler;
    }

    private void handleMessage(String message)
    {
        //Log.d("MYDEBUG","Message: " + message);
        String splitted[] = message.split(",");
        //Log.d("MYDEBUG","Message: " + splitted[0]);

        switch (splitted[0])
        {
            case "CONNECTED":
                id = splitted[1];
                Log.d("MYDEBUG","Id " + id);
                break;
            case "ON":
                battery = splitted[1];
                //Log.d("MYDEBUG","battery " + battery);
                break;
            default:
                if (handler != null)
                {
                    //synch

                    handler.handleMessage(this, message);


                }
        }
    }

    public void send(final String message)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                out.println(message);
            }
        }).start();
    }

    public boolean hasHandler() {
        return (handler != null);
    }

    public String getId() {
        return id;
    }
}

