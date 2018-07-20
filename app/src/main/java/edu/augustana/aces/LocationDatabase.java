package edu.augustana.aces;

import java.util.*;

/**
 * Created by meganjanssen14 on 4/9/2018.
 * <p>
 * This was our database with saved popular locations around Augustana and their lat/longs
 * We decided to switch to GooglePlaces instead of using this. We also decided to keep this
 * database in case someone decided to improve the project by making an easy pick drop down of
 * these locations.
 */

public class LocationDatabase {

    static private MyPlace abbey = new MyPlace("Abbey Art Studios", 41.505297, -90.551476);
    static private MyPlace aldi = new MyPlace("ALDI", 41.491941, -90.548270);
    static private MyPlace anderson = new MyPlace("Anderson/Bartholomew", 41.502361, -90.551381);
    static private MyPlace andreen = new MyPlace("Andreen Hall", 41.501657, -90.548496);
    static private MyPlace arbaugh = new MyPlace("Arbaugh TLA", 41.499354, -90.552103);
    static private MyPlace brodahl = new MyPlace("Brodahl", 41.502800, -90.552291);
    static private MyPlace carver = new MyPlace("Carver Center", 41.506636, -90.550844);
    static private MyPlace centennial = new MyPlace("Centennial Hall", 41.505123, -90.548681);
    static private MyPlace collegeCenter = new MyPlace("College Center", 41.504351, -90.548201);
    static private MyPlace denkmann = new MyPlace("Denkmann", 41.504425, -90.550528);
    static private MyPlace elflats = new MyPlace("11th Ave Flats", 41.499988, -90.548975);
    static private MyPlace erickson = new MyPlace("Erickson Hall", 41.499363, -90.554705);
    static private MyPlace evald = new MyPlace("Evald", 41.505108, -90.550090);
    static private MyPlace gerber = new MyPlace("Gerber Center", 41.502285, -90.550688);
    static private MyPlace hanson = new MyPlace("Hanson", 41.503561, -90.551447);
    static private MyPlace naeseth = new MyPlace("Naeseth TLA", 41.499284, -90.553739);
    static private MyPlace oldMain = new MyPlace("Old Main", 41.504344, -90.549497);
    static private MyPlace olin = new MyPlace("Olin", 41.503118, -90.550591);
    static private MyPlace parkanderN = new MyPlace("Parkander North", 41.501175, -90.549681);
    static private MyPlace parkanderS = new MyPlace("Parkander South", 41.500545, -90.549934);
    static private MyPlace pepsico = new MyPlace("PepsiCo Recreation", 41.500332, -90.556294);
    static private MyPlace pottery = new MyPlace("Pottery Studio", 41.505721, -90.550474);
    static private MyPlace seminary = new MyPlace("Seminary Hall", 41.503043, -90.548144);
    static private MyPlace swanson = new MyPlace("Swanson Commons", 41.500638, -90.548042);
    static private MyPlace sorensen = new MyPlace("Sorensen", 41.505139, -90.547201);
    static private MyPlace swenson = new MyPlace("Swenson Geoscience", 41.503030, -90.549075);
    static private MyPlace westerlin = new MyPlace("Westerlin Hall", 41.500495, -90.554667);


    // Returns a dictionary of the Place's names mapped to an Array of the latitude and longitude.

    static public Map<String, double[]> getPlaces() {
        Map<String, double[]> map = new HashMap<>();
        map.put(abbey.name, new double[]{abbey.latitude, abbey.longitude});
        map.put(aldi.name, new double[]{aldi.latitude, aldi.longitude});
        map.put(anderson.name, new double[]{anderson.latitude, anderson.longitude});
        map.put(andreen.name, new double[]{andreen.latitude, andreen.longitude});
        map.put(arbaugh.name, new double[]{arbaugh.latitude, arbaugh.longitude});
        map.put(brodahl.name, new double[]{brodahl.latitude, brodahl.longitude});
        map.put(carver.name, new double[]{carver.latitude, carver.longitude});
        map.put(centennial.name, new double[]{centennial.latitude, centennial.longitude});
        map.put(collegeCenter.name, new double[]{collegeCenter.latitude, collegeCenter.longitude});
        map.put(denkmann.name, new double[]{denkmann.latitude, denkmann.longitude});
        map.put(elflats.name, new double[]{elflats.latitude, elflats.longitude});
        map.put(erickson.name, new double[]{erickson.latitude, erickson.longitude});
        map.put(evald.name, new double[]{evald.latitude, evald.longitude});
        map.put(gerber.name, new double[]{gerber.latitude, gerber.longitude});
        map.put(hanson.name, new double[]{hanson.latitude, hanson.longitude});
        map.put(naeseth.name, new double[]{naeseth.latitude, naeseth.longitude});
        map.put(oldMain.name, new double[]{oldMain.latitude, oldMain.longitude});
        map.put(olin.name, new double[]{olin.latitude, olin.longitude});
        map.put(parkanderN.name, new double[]{parkanderN.latitude, parkanderN.longitude});
        map.put(parkanderS.name, new double[]{parkanderS.latitude, parkanderS.longitude});
        map.put(pepsico.name, new double[]{pepsico.latitude, pepsico.longitude});
        map.put(pottery.name, new double[]{pottery.latitude, pottery.longitude});
        map.put(seminary.name, new double[]{seminary.latitude, seminary.longitude});
        map.put(sorensen.name, new double[]{sorensen.latitude, sorensen.longitude});
        map.put(swanson.name, new double[]{swanson.latitude, swanson.longitude});
        map.put(swenson.name, new double[]{swenson.latitude, swenson.longitude});
        map.put(westerlin.name, new double[]{westerlin.latitude, westerlin.longitude});
        return map;
    }



    // Returns an Array of the Place's names

    static public String[] getNames() {
        return new String[]{"Enter an Address", abbey.name, aldi.name, anderson.name, andreen.name,
                arbaugh.name, brodahl.name, carver.name, centennial.name, collegeCenter.name, denkmann.name,
                elflats.name, erickson.name, evald.name, gerber.name, hanson.name, naeseth.name, oldMain.name,
                olin.name, parkanderN.name, parkanderS.name, pepsico.name, pottery.name, seminary.name,
                sorensen.name, swanson.name, swenson.name, westerlin.name};
    }


}
