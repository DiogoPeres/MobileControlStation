package com.diogoperes.mobilecontrolstation.fragments;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.diogoperes.mobilecontrolstation.MainActivity;
import com.diogoperes.mobilecontrolstation.R;
import com.diogoperes.mobilecontrolstation.UV;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Created by PC1 on 08-Mar-16.
 */
public class MissionsFragment extends Fragment {

    private MapView mMapView;
    private GoogleMap mMap;
    private Bundle mBundle;
    private FloatingActionButton fab;


    private MainActivity mainActivity;
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private int markerIndex = 0;




    public MissionsFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();

        //setUpMapIfNeeded();
        super.onCreate(savedInstanceState);

    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //setUpMapIfNeeded();


        View v = inflater.inflate(R.layout.fragment_missions, container,
                false);


        MapsInitializer.initialize(getActivity());


        mMapView = (MapView) v.findViewById(R.id.map);
        mMapView.onCreate(mBundle);
        setUpMapIfNeeded(v);

        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if(markers.size()>0){
                    CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(markers.get(markers.size()-1).getPosition(), 16.0f);
                    mMap.animateCamera(yourLocation);
                }*/
                if(mainActivity.getUvSelected().getMarkers_list().size() > 0){
                    CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(mainActivity.getUvSelected().getMarkers_list().get(mainActivity.getUvSelected().getMarkers_list().size()-1).getPosition(), 16.0f);
                    mMap.animateCamera(yourLocation);
                }

            }
        });


        return v;


    }








    private void setUpMapIfNeeded(View inflatedView) {
        if (mMap == null) {
            mMap = ((MapView) inflatedView.findViewById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    public void updateMarker(UV u){
        if (mMap != null) {
            final double lat = u.getCurrentLocation().latitude;
            final double lon = u.getCurrentLocation().longitude;
            final float rotation =  (float) u.getHeading();
            final UV uv = u;
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //mMap.clear();
                    Marker newLocationMarker;
                    LatLng newLocationCoordinates = new LatLng(lat, lon);
                    newLocationMarker = mMap.addMarker(new MarkerOptions()
                            .position(newLocationCoordinates)
                            .title("Thor" + markerIndex++)
                            .anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_quad))
                            .rotation(rotation));

                    uv.getMarkers_list().add(newLocationMarker);
                    /*for(UV uv_correspondent : mainActivity.getUv_list()){
                        if (uv_correspondent.equals(u)){
                            uv_correspondent.getMarkers_list().add(newLocationMarker);
                        }

                    }*/



                    if(uv.equals(mainActivity.getUvSelected()) && mainActivity.getUvSelected().getMarkers_list().size() == 1){
                        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(newLocationCoordinates, 16.0f);
                        mMap.animateCamera(yourLocation);
                    }
                    if(uv.getMarkers_list().size()>1){
                        uv.getMarkers_list().get(uv.getMarkers_list().size()-2).remove();
                        Polyline polyline = mMap.addPolyline(new PolylineOptions()
                                .add(newLocationCoordinates, uv.getMarkers_list().get(uv.getMarkers_list().size() - 2).getPosition())
                                .width(10)
                                .color(Color.RED));

                    }

                    //markers.add(newLocationMarker);

                    /*if(markers.size()==1){
                        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(newLocationCoordinates, 16.0f);
                        mMap.animateCamera(yourLocation);
                    }
                    if(markers.size()>1){
                        //Log.d("MARKERS", "" + markers.size());
                        markers.get(markers.size()-2).remove();
                        Polyline polyline = mMap.addPolyline(new PolylineOptions()
                                .add(newLocationCoordinates, markers.get(markers.size() - 2).getPosition())
                                .width(10)
                                .color(Color.RED));
                    }*/



                }
            });
        }

    }

    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }


    }
