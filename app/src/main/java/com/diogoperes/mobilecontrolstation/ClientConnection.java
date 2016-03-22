package com.diogoperes.mobilecontrolstation;

import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import org.mavlink.messages.ardupilotmega.msg_command_long;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by PC1 on 07-Mar-16.
 */



public class ClientConnection {

    private DatagramSocket clientSocket;
    private int CLIENT_PORT = 14550;
    private int SERVER_PORT = -1;
    //private String SERVERIP = "192.168.2.108";
    //private String SERVERIP = "10.0.2.2";
    private InetAddress SERVER_ADDRESS;
    private byte[] receiveData = new byte[263];
    private boolean connectedToDrone = false;
    MainActivity mainActivity;
    MessageHandler messageHandler;


    public ClientConnection(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        messageHandler = new MessageHandler(mainActivity);
    }

    public void startConnection() {

        int SDK_INT = Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (clientSocket == null) {

            try {


            //Open the UDP Port

                clientSocket = new DatagramSocket(CLIENT_PORT);

            } catch (SocketException e) {
                e.printStackTrace();
            }


        }
        connectedToDrone=true;
        ClientConnectionThread client = new ClientConnectionThread();
        new Thread(client).start();
    }





    public class ClientConnectionThread implements Runnable
    {
        @Override
        public void run() {
            //DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            ByteArrayInputStream bais;
            DataInputStream dis;
            while(connectedToDrone){
                //Socket receives byte Packets
                try {

                    byte[] receiveData = new byte[270];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData,
                                receiveData.length);
                    clientSocket.receive(receivePacket);
                    receiveData = receivePacket.getData();

                    //Log.d("MESSAGE", receivePacket.toString());

                    messageHandler.dealWithMessage(receivePacket, receiveData);
                    SERVER_PORT = receivePacket.getPort();
                    SERVER_ADDRESS = receivePacket.getAddress();



/*



                bais = new ByteArrayInputStream(receiveData);
                dis = new DataInputStream(bais);


                MAVLinkReader MAVReader = new MAVLinkReader(dis);

                MAVLinkMessage msg = MAVReader.getNextMessage();

                //ping();

                if(msg != null){
                    System.err.println("SysID:" + msg.sysId + "		CompID:" + msg.componentId + "		Seq:" + msg.sequence + "	" +msg.toString());
                    if(msg.toString().contains("MAVLINK_MSG_ID_HEARTBEAT")){
                        checkHeartbeat(msg);
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "No Connection",
                            Toast.LENGTH_LONG).show();
                    connectedToDrone=false;
                    serverSocket.close();


                }
                dis.close();*/

                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void MAVSend(msg_command_long msgCommand, UV uv) throws SocketException {

        byte[] sendData = new byte[263];
        DatagramSocket newSocket = new DatagramSocket();
        try {
            sendData = msgCommand.encode();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, uv.getSERVER_ADDRESS(), uv.getSERVER_PORT() );
            newSocket.send(sendPacket);
            newSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
