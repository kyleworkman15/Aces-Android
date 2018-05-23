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

public class RideInfo {
    private String email; //the users email
    private String end; //destination
    private String endTime; //timestamp of when the ride was fulfilled
    private String eta; //estimated time of arrival
    private int numRiders; //number of riders
    private String start; //start location
    private String time; //start time
    private int waitTime; //wait time

    public RideInfo(String email, String end, String endTime, String eta, int numRiders, String start, String time, int waitTime) {
        this.email = email;
        this.end = end;
        this.endTime = endTime;
        this.eta = eta;
        this.numRiders = numRiders;
        this.start = start;
        this.time = time;
        this.waitTime = waitTime;
    }

    public RideInfo() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getEndTime() { return endTime; }

    public void setEndTime(String time) {
        endTime = time;
    }

    public String getETA() { return eta; }

    public void setETA(String newETA) { this.eta = newETA; }

    public int getNumRiders() {
        return numRiders;
    }

    public void setNumRiders(int numRiders) {
        this.numRiders = numRiders;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

}
