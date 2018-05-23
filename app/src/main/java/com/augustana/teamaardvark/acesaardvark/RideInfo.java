package com.augustana.teamaardvark.acesaardvark;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by kevinbarbian on 4/12/18.
 *
 * Acts as references to rides
 * Contains information about the users Email,Start and end location, number of riders, timestamp and wait time for the ride
 */

public class RideInfo implements Serializable {
    private String email;
    private String start;
    private String end;
    private int numRiders;
    private String endTime;
    private String time;
    private int waitTime;
    private String eta;

    public RideInfo() {

    }

    public RideInfo(String email, String start, String end, int numRiders, String time, int waitTime, String endtime, String eta) {
        this.email = email;
        this.start = start;
        this.end = end;
        this.endTime = endtime;
        this.numRiders = numRiders;
        this.time = time;
        this.waitTime = waitTime;
        this.eta = eta;
    }

    public String getEmail() {
        return email;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public int getNumRiders() {
        return numRiders;
    }

    public String getTime() {
        return time;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public String getETA() { return eta; }

    public void setETA(String newETA) { this.eta = newETA; }


}
