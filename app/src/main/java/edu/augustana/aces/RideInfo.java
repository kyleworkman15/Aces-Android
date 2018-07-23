package edu.augustana.aces;

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
    private String email; //the users email
    private String end; //destination
    private String endTime; //timestamp of when the ride was fulfilled
    private String eta; //estimated time of arrival
    private String numRiders; //number of riders
    private String start; //start location
    private String time; //start time
    private String waitTime; //wait time
    private long ts; //timestamp
    private String token; //messaging token

    public RideInfo(String email, String end, String endTime, String eta, String numRiders, String start, String time, String waitTime, long ts, String token) {
        this.email = email;
        this.end = end;
        this.endTime = endTime;
        this.eta = eta;
        this.numRiders = numRiders;
        this.start = start;
        this.time = time;
        this.waitTime = waitTime;
        this.ts = ts;
        this.token = token;
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

    public String getNumRiders() {
        return numRiders;
    }

    public void setNumRiders(String numRiders) {
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

    public String getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(String waitTime) {
        this.waitTime = waitTime;
    }

    public long getTimestamp() { return ts; }

    public void setTimestamp(long ts) { this.ts = ts; }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }

}
