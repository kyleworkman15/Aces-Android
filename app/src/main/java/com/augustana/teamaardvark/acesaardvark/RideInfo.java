package com.augustana.teamaardvark.acesaardvark;

import java.sql.Timestamp;

/**
 * Created by kevinbarbian on 4/12/18.
 *
 * Acts as references to rides
 * Contains information about the users Email,Start and end location, number of riders, timestamp and wait time for the ride
 */

public class RideInfo {
    public String email;
    public String start;
    public String end;
    public int numRiders;
    public String endTime;
    public String time;
    public int waitTime;

    public RideInfo() {

    }

    public RideInfo(String email, String start, String end, int numRiders, String time, int waitTime, String endtime) {
        this.email = email;
        this.start = start;
        this.end = end;
        this.endTime = endtime;
        this.numRiders = numRiders;
        this.time = time;
        this.waitTime = waitTime;
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

}
