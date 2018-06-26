package com.augustana.teamaardvark.acesaardvark;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by meganjanssen14 on 5/11/2018.
 * <p>
 * Things that might change about ACES
 */


public class ACESConfiguration {

    static final double LAT1 = 41.497281;
    static final double LAT2 = 41.507930;
    static final double LONG1 = -90.565683;
    static final double LONG2 = -90.539093;

    private static final double ALDILAT = 41.491939699999996;
    private static final double ALDILONG = -90.5482703;


    /**
     * Making sure that the lat/lng of the selected place is in the bounds of ACES & ALDI
     *
     * @param coords - to check if in bounds
     * @return true if in bounds
     */
    public static boolean isInACESBoundary(LatLng coords) {

        if (((coords.latitude > LAT1 && coords.longitude > LONG1) && (coords.latitude < LAT2 && coords.longitude < LONG2)) ||
                (coords.latitude == ALDILAT && coords.longitude == ALDILONG)) { //This is a SPECIAL CASE allowing Aldi
            return true;
        } else {
            return false;
        }
    }


}
