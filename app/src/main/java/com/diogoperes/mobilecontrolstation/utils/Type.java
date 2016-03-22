package com.diogoperes.mobilecontrolstation.utils;

import com.diogoperes.mobilecontrolstation.R;

/**
 * Created by PC1 on 21-Mar-16.
 */
public enum Type {



    UNDEFINED(-1, R.mipmap.ic_marker_undefined),
    MAV_TYPE_GENERIC(0, R.mipmap.ic_marker_undefined),
    MAV_TYPE_FIXED_WING(1, R.mipmap.ic_marker_undefined),
    MAV_TYPE_QUADROTOR(2,R.mipmap.ic_marker_quad),
    MAV_TYPE_COAXIAL(3,R.mipmap.ic_marker_undefined),
    MAV_TYPE_HELICOPTER(4,R.mipmap.ic_marker_undefined),
    MAV_TYPE_ANTENNA_TRACKER(5,R.mipmap.ic_marker_undefined),
    MAV_TYPE_GCS(6,R.mipmap.ic_marker_undefined),
    MAV_TYPE_AIRSHIP(7,R.mipmap.ic_marker_undefined),
    MAV_TYPE_FREE_BALLOON(8,R.mipmap.ic_marker_undefined),
    MAV_TYPE_ROCKET(9,R.mipmap.ic_marker_undefined),
    MAV_TYPE_GROUND_ROVER(10, R.mipmap.ic_marker_undefined),
    MAV_TYPE_SURFACE_BOAT(11,R.mipmap.ic_marker_undefined),
    MAV_TYPE_SUBMARINE(12,R.mipmap.ic_marker_undefined),
    MAV_TYPE_HEXAROTOR(13,R.mipmap.ic_marker_undefined),
    MAV_TYPE_OCTOROTOR(14,R.mipmap.ic_marker_undefined),
    MAV_TYPE_TRICOPTER(15,R.mipmap.ic_marker_undefined),
    MAV_TYPE_FLAPPING_WING(16,R.mipmap.ic_marker_undefined),
    MAV_TYPE_KITE(17,R.mipmap.ic_marker_undefined),
    MAV_TYPE_ONBOARD_CONTROLLER(18,R.mipmap.ic_marker_undefined),
    MAV_TYPE_VTOL_DUOROTOR(19,R.mipmap.ic_marker_undefined),
    MAV_TYPE_VTOL_QUADROTOR(20,R.mipmap.ic_marker_undefined),
    MAV_TYPE_VTOL_TILTROTOR(21,R.mipmap.ic_marker_undefined),
    MAV_TYPE_VTOL_RESERVED2(22,R.mipmap.ic_marker_undefined),
    MAV_TYPE_VTOL_RESERVED3(23,R.mipmap.ic_marker_undefined),
    MAV_TYPE_VTOL_RESERVED4(24,R.mipmap.ic_marker_undefined),
    MAV_TYPE_VTOL_RESERVED5(25,R.mipmap.ic_marker_undefined),
    MAV_TYPE_GIMBAL(26,R.mipmap.ic_marker_undefined),
    MAV_TYPE_ADSB(27,R.mipmap.ic_marker_undefined);

    Type(int i, int image) {
        this.index = i;
        this.icon = image;
    }

    private int index;
    private int icon;

    public int getIndex() {
        return index;
    }

    public int getIcon() {
        return icon;
    }

    public static Type getByIndex(int value) {
        for(Type e: Type.values()) {
            if(e.getIndex() == value) {
                return e;
            }
        }
        return null;// not found
    }
}
