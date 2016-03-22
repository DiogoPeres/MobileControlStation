package com.diogoperes.mobilecontrolstation;

import android.util.Log;

import com.diogoperes.mobilecontrolstation.utils.Type;

import org.mavlink.MAVLinkReader;
import org.mavlink.messages.MAVLinkMessage;
import org.mavlink.messages.ardupilotmega.msg_gps_raw_int;
import org.mavlink.messages.ardupilotmega.msg_heartbeat;
import org.mavlink.messages.ardupilotmega.msg_vfr_hud;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;


public class MessageHandler {

    private DatagramPacket receivePacket;
    private byte[] receiveData;
    ByteArrayInputStream bais;
    DataInputStream dis;
    MAVLinkMessage msg;
    MainActivity mainActivity;
    UV uv;

    public MessageHandler(MainActivity mainActivity){

        this.mainActivity = mainActivity;

        //check_if_drone_exists();


        //readMessage();
    }

    public void dealWithMessage(DatagramPacket receivePacket, byte[] receiveData) {
        this.receivePacket = receivePacket;
        this.receiveData = receiveData;

        check_if_drone_exists();
        readMessage();
    }

    private void check_if_drone_exists() {
        if(mainActivity.getUv_list().size()==0){
            UV newUV = new UV(receivePacket.getAddress(), receivePacket.getPort());
            mainActivity.addUV(newUV);
            Log.d("NEW DRONE ADDED", "IP: " + receivePacket.getAddress() + ", Port: "+ receivePacket.getPort());
            uv = newUV;
            mainActivity.setUvSelected(newUV);

        }else{
            boolean uv_exists = false;
            for(UV u : mainActivity.getUv_list()){
                if(u.getSERVER_ADDRESS().equals(receivePacket.getAddress()) && u.getSERVER_PORT() == receivePacket.getPort()){
                    uv=u;
                    uv_exists = true;
                    //Log.d("EXISTS", "true");
                }
            }
            if (!uv_exists){
                UV newUV = new UV(receivePacket.getAddress(), receivePacket.getPort());
                mainActivity.addUV(newUV);
                Log.d("NEW DRONE ADDED", "IP: " + receivePacket.getAddress() + ", Port: " + receivePacket.getPort());
                uv = newUV;
            }
        }
        //Log.d("UV LIST SIZE", "" + mainActivity.getUv_list().size());

    }

    private void readMessage(){


        bais = new ByteArrayInputStream(receiveData);
        dis = new DataInputStream(bais);

        MAVLinkReader MAVReader = new MAVLinkReader(dis);

        try {
            msg = MAVReader.getNextMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (msg != null){
            //Log.d("MESSAGE RECEIVED", msg.toString());
            switch (msg.messageType){
                case 0:
                    msg_heartbeat heartbeat = (msg_heartbeat) msg;
                    Log.d("HEARTBEAT: ", "" + heartbeat);
                    if(heartbeat.base_mode < 150 && uv.getState()== UV.State.ARM ){ //81
                        if(is_selected())
                            mainActivity.getControllerFragment().setArmButtonText("ARM");
                        uv.setState(UV.State.DISARM);
                    }else if(heartbeat.base_mode > 150 && uv.getState()== UV.State.DISARM ){ //209
                        if(is_selected())
                            mainActivity.getControllerFragment().setArmButtonText("DISARM");
                        uv.setState(UV.State.ARM);
                    }
                    if(uv.getType().equals(Type.UNDEFINED)){
                        setUVType(heartbeat.type);
                    }
                    break;

                case 24:
                    msg_gps_raw_int gpsInfo = (msg_gps_raw_int) msg;
                    Log.d("GPS LOCATION: ", "" + gpsInfo);
                    uv.setLocation(gpsInfo.lat/ 1E7, gpsInfo.lon/ 1E7);
                    mainActivity.getMissionsFragment().updateMarker(uv);
                    break;
                case 74:
                    msg_vfr_hud hud = (msg_vfr_hud) msg;
                    Log.d("HUD: ", "" + hud);
                    uv.setAltitude(hud.alt);
                    uv.setGroundSpeed(hud.groundspeed);
                    uv.setVerticalSpeed(hud.airspeed);
                    uv.setHeading(hud.heading);
                    uv.setThrottle(hud.throttle);
                    if(is_selected())
                        mainActivity.getControllerFragment().setAltitudeTextView("" + hud.alt);
                    break;
            }

        }

    }

    private void setUVType(int type) {
        if(type>=0 && type<=27 && Type.getByIndex(type)!=null){
            uv.setType(Type.getByIndex(type));
        }else{
            uv.setType(Type.UNDEFINED);
        }

        /*switch (type) {
            case 0: //Generic micro air vehicle
                uv.setType(Type.MAV_TYPE_GENERIC);
                break;
            case 1: //Fixed wing aircraft.
                uv.setType(Type.MAV_TYPE_FIXED_WING);
                break;
            case 2: //Quadrotor
                uv.setType(Type.MAV_TYPE_QUADROTOR);
                break;
            case 3: //Coaxial helicopter
                uv.setType(Type.MAV_TYPE_COAXIAL);
                break;
            case 4: //Normal helicopter with tail rotor.
                uv.setType(Type.MAV_TYPE_HELICOPTER);
                break;
            case 5: //Ground installation
                uv.setType(Type.MAV_TYPE_ANTENNA_TRACKER);
                break;
            case 6: //Operator control unit / ground control station
                uv.setType(Type.MAV_TYPE_GCS);
                break;
            case 7: //Airship, controlled
                uv.setType(Type.MAV_TYPE_AIRSHIP);
                break;
            case 8: //Free balloon, uncontrolled
                uv.setType(Type.MAV_TYPE_FREE_BALLOON);
                break;
            case 9: //Rocket
                uv.setType(Type.MAV_TYPE_ROCKET);
                break;
            case 10: //Ground rover
                uv.setType(Type.MAV_TYPE_GROUND_ROVER);
                break;
            case 11: //Surface vessel, boat, ship
                uv.setType(Type.MAV_TYPE_SURFACE_BOAT);
                break;
            case 12: //Submarine
                uv.setType(Type.MAV_TYPE_SUBMARINE);
                break;
            case 13: //Hexarotor
                uv.setType(Type.MAV_TYPE_HEXAROTOR);
                break;
            case 14: //Octorotor
                uv.setType(Type.MAV_TYPE_OCTOROTOR);
                break;
            case 15: //Octorotor
                uv.setType(Type.MAV_TYPE_TRICOPTER);
                break;
            case 16: //Flapping wing
                uv.setType(Type.MAV_TYPE_FLAPPING_WING);
                break;
            case 17: //Flapping wing
                uv.setType(Type.MAV_TYPE_KITE);
                break;
            case 18: //Onboard companion controller
                uv.setType(Type.MAV_TYPE_ONBOARD_CONTROLLER);
                break;
            case 19: //Two-rotor VTOL using control surfaces in vertical operation in addition. Tailsitter.
                uv.setType(Type.MAV_TYPE_VTOL_DUOROTOR);
                break;
            case 20: //Quad-rotor VTOL using a V-shaped quad config in vertical operation. Tailsitter.
                uv.setType(Type.MAV_TYPE_VTOL_QUADROTOR);
                break;
            case 21: //Tiltrotor VTOL
                uv.setType(Type.MAV_TYPE_VTOL_TILTROTOR);
                break;
            case 22: //VTOL reserved 2
                uv.setType(Type.MAV_TYPE_VTOL_RESERVED2);
                break;
            case 23: //VTOL reserved 3
                uv.setType(Type.MAV_TYPE_VTOL_RESERVED3);
                break;
            case 24: //VTOL reserved 4
                uv.setType(Type.MAV_TYPE_VTOL_RESERVED4);
                break;
            case 25: //VTOL reserved 5
                uv.setType(Type.MAV_TYPE_VTOL_RESERVED5);
                break;
            case 26: //Onboard gimbal
                uv.setType(Type.MAV_TYPE_GIMBAL);
                break;
            case 27: //	Onboard ADSB peripheral
                uv.setType(Type.MAV_TYPE_ADSB);
                break;

        }*/
    }

    public boolean is_selected(){
        if(uv.equals(mainActivity.getUvSelected())){
            return true;
        }else{
            return false;
        }
    }

}
