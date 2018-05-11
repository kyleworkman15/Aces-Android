package com.augustana.teamaardvark.acesaardvark;

/**
 * Created by meganjanssen14 on 4/9/2018.
 *
 * This was our database with saved popular locations around Augustana and their lat/longs
 * We decided to switch to GooglePlaces instead of using this. We also decided to keep this
 * database in case someone decided to improve the project by making an easy pick drop down of
 * these locations.
 */

public class LocationDatabase {


    public static String[] locations = new String[] {
            "Current Location", "Gerber Center", "Evald", "Westerlin", "Erikson", "Andreen", "Swanson", "Seminary", "Old Main", "Sorensen", "Carver Center", "PepsiCo Rec"
    };

    public static double[] latitude = new double[]{
            0.0, 41.502360, 41.505094, 41.500517, 41.499358, 41.501548, 41.500720, 41.503050, 41.504401, 41.505039, 41.506654, 41.500281
    };

    public static double[] longitude = new double[]{
            0.0, -90.550582, -90.550077, -90.554748, -90.554802, -90.548343, -90.548038, -90.548147, -90.549491, -90.547163, -90.550813, -90.556407
    };

    public static void setCurrentLatitude(double currentLatitude){
        latitude[0] = currentLatitude;
    }

    public static void setCurrentLongitude(double currentLongitude){
        longitude[0] = currentLongitude;
    }
}
