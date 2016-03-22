package com.diogoperes.mobilecontrolstation;


import com.diogoperes.mobilecontrolstation.utils.Type;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.net.InetAddress;
import java.util.ArrayList;

public class UV {


    private int SERVER_PORT = -1;
    private InetAddress SERVER_ADDRESS;

    public enum State {
        ARM, DISARM
    }



    private State state= State.DISARM;
    private Type type = Type.UNDEFINED;

    private int sysID = 1;
    private int componentId = 1;

    private double verticalSpeed = 0;
    private double groundSpeed = 0;
    private double altitude = 0;
    private int throttle = 0;
    private double heading = 0;
    private LatLng currentLocation;



    private ArrayList<Marker> markers_list = new ArrayList<Marker>();


    public UV(InetAddress ip, int port){
        SERVER_ADDRESS = ip;
        SERVER_PORT = port;


    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getSysID() {
        return sysID;
    }

    public int getComponentId() {
        return componentId;
    }

    public double getVerticalSpeed() {
        return verticalSpeed;
    }

    public void setVerticalSpeed(double verticalSpeed) {
        this.verticalSpeed = verticalSpeed;
    }

    public double getGroundSpeed() {
        return groundSpeed;
    }

    public void setGroundSpeed(double groundSpeed) {
        this.groundSpeed = groundSpeed;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public int getThrottle() {
        return throttle;
    }

    public void setThrottle(int throttle) {
        this.throttle = throttle;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public void setLocation(double lat, double lon) {
        currentLocation = new LatLng(lat, lon);
    }

    public LatLng getCurrentLocation() {
        return currentLocation;
    }

    public InetAddress getSERVER_ADDRESS() {
        return SERVER_ADDRESS;
    }

    public int getSERVER_PORT() {
        return SERVER_PORT;
    }

    public ArrayList<Marker> getMarkers_list() {
        return markers_list;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
