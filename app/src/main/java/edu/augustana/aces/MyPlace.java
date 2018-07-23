package edu.augustana.aces;

/**
 * Created by Kyle Workman
 *
 * Manages the name, latitude, and longitude for places used in our LocationDatabase
 */

public class MyPlace {

    public String name;
    public double latitude;
    public double longitude;

    public MyPlace(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
