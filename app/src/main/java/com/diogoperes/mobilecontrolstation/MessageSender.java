package com.diogoperes.mobilecontrolstation;


import org.mavlink.messages.MAV_CMD;
import org.mavlink.messages.ardupilotmega.msg_command_long;

import java.net.SocketException;

public class MessageSender {

    private MainActivity mainActivity;

    public MessageSender(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    public void sendArm(UV uv){
        msg_command_long msgCommand = new msg_command_long(mainActivity.getUvSelected().getSysID(), mainActivity.getUvSelected().getComponentId());
        msgCommand.command = MAV_CMD.MAV_CMD_COMPONENT_ARM_DISARM; //Arms / Disarms a component
        msgCommand.target_system=1;
        msgCommand.target_component=0;
        msgCommand.param1 = 1; // arm command
        try {
            mainActivity.getClientConnection().MAVSend(msgCommand, uv);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void sendDisarm(UV uv){
        msg_command_long msgCommand = new msg_command_long(mainActivity.getUvSelected().getSysID(), mainActivity.getUvSelected().getComponentId());
        msgCommand.command = MAV_CMD.MAV_CMD_COMPONENT_ARM_DISARM; //Arms / Disarms a component
        msgCommand.target_system=1;
        msgCommand.target_component=0;
        msgCommand.param1 = 0; //Disarm command
        try {
            mainActivity.getClientConnection().MAVSend(msgCommand, uv);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


}
