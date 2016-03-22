package com.diogoperes.mobilecontrolstation;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;

public class MainActivityOld extends AppCompatActivity {

    Button connectButton;

    private String clientName = "ThorControler";

    //private String SERVERIP = "192.168.2.106";
    //private int SERVERPORT_SEND = 4020;
    private String SERVERIP = "127.0.0.1";
    private int SERVERPORT_SEND = 14550;
    private Socket clientSocket;
    private DataOutputStream toServer;
    private DataInputStream fromServer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        connectButton = (Button) findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                connectToServer();
                /*Intent myIntent = new Intent(MainActivity.this, ControllerActivity.class);
                MainActivity.this.startActivity(myIntent);
                finish();*/

            }
        });



        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        int SDK_INT = Build.VERSION.SDK_INT;
        if(SDK_INT > 8 ){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }

    private void connectToServer()  {
        try {
            InetAddress host = InetAddress.getByName(SERVERIP);
            System.out.println("[ CLIENT ] Connecting to server on port " + SERVERPORT_SEND);

            clientSocket = new Socket(host,SERVERPORT_SEND);
            System.out.println("[ CLIENT ] Just connected to " + clientSocket.getLocalSocketAddress());

            toServer = new DataOutputStream(clientSocket.getOutputStream());
            fromServer = new DataInputStream(clientSocket.getInputStream());

            JSONObject json = new JSONObject();
            json.put("IP",clientSocket.getInetAddress().getHostAddress() );
            json.put("PORT",clientSocket.getLocalPort());
            json.put("ClientName",clientName );

            //String helloMessage = "ClientName= "+clientName+" IPAddress= " + clientSocket.getLocalSocketAddress();
            toServer.writeUTF(json.toString());

            String messageFromServer = fromServer.readUTF();
            System.out.println(messageFromServer);

        }
        catch(UnknownHostException ex) {
            System.out.println("[ CLIENT ] UnknownHostException: "+ex);
        }
        catch(IOException e){
            System.out.println("[ CLIENT ] IOException: "+e);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }








}
