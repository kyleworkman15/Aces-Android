package com.augustana.teamaardvark.acesaardvark;

import java.sql.Timestamp;

/**
 * Created by kevinbarbian on 4/12/18.
 */

public class RideInfo {
    public String email;
    public String start;
    public String end;
    public int numRiders;
    public String time;

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

    public RideInfo(String email, String start, String end, int numRiders, String time){
        this.email = email;
        this.start = start;
        this.end = end;
        this.numRiders = numRiders;
        this.time = time;
    }

}
