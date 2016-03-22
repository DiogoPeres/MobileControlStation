package com.diogoperes.mobilecontrolstation.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.diogoperes.mobilecontrolstation.connectionHandler.ServerConnection;
import com.diogoperes.mobilecontrolstation.MainActivity;
import com.diogoperes.mobilecontrolstation.R;
import com.diogoperes.mobilecontrolstation.UV;

/**
 * Created by PC1 on 08-Mar-16.
 */
public class ControllerFragment extends Fragment{

    private View view;
    private Button disconnectButton, armButton;
    private MainActivity mainActivity;
    private ServerConnection serverConnection;

    private TextView altitudeInfoTextView;



    public ControllerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_controller, container, false);

        /* This will be my server connection test button */
        disconnectButton = (Button) view.findViewById(R.id.disconnectButton);
        disconnectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                serverConnection = new ServerConnection(mainActivity);
                serverConnection.execute();
            }
        });

        armButton = (Button) view.findViewById(R.id.armButton);
        armButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                armDisarm();
            }
        });

        altitudeInfoTextView = (TextView) view.findViewById(R.id.altitude_info_textView);



        return view;
    }


    public void armDisarm(){
        if (mainActivity.getUvSelected()!= null){
            if(mainActivity.getUvSelected().getState()==UV.State.DISARM)
                mainActivity.getMessageSender().sendArm(mainActivity.getUvSelected());
            if(mainActivity.getUvSelected().getState()==UV.State.ARM)
                mainActivity.getMessageSender().sendDisarm(mainActivity.getUvSelected());
        }
    }


    public Button getArmButton() {
        return armButton;
    }

    public void setArmButtonText(String text) {
        final String textToChange = text;
        if(getActivity() == null)
            return;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                armButton.setText(textToChange);

            }
        });
    }

    public void setAltitudeTextView(String altitude){
        final String textToChange = altitude;
        if(getActivity() == null)
            return;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                altitudeInfoTextView.setText(textToChange + "m");
            }
        });
    }

}
