package edu.augustana.aces;

import java.util.*;

/**
 * Created by Kyle Workman, Kevin Barbian, Megan Janssen, Tan Nguyen, Tyler May
 *
 * This is our database with saved popular locations around Augustana and their lat/longs
 */

public class LocationDatabase {

    static private MyPlace abbey = new MyPlace("Abbey Art Studios", 41.505297, -90.551476);
    static private MyPlace andeberg = new MyPlace("Andeberg - 738 34th St", 41.503772, -90.553000);
    static private MyPlace anderson = new MyPlace("Anderson/Bartholomew", 41.502361, -90.551381);
    static private MyPlace andreen = new MyPlace("Andreen Hall", 41.501657, -90.548496);
    static private MyPlace ansvar = new MyPlace("Ansvar - 3054 9th Ave", 41.502387, -90.554907);
    static private MyPlace arbaugh = new MyPlace("Arbaugh TLA", 41.499354, -90.552103);
    static private MyPlace asgard = new MyPlace("Asgard - 742 34th St", 41.503647, -90.552875);
    static private MyPlace asianPagoda = new MyPlace("Asian Pagoda", 41.501930, -90.552834);
    static private MyPlace austin = new MyPlace("Austin - 610 39th St", 41.505788, -90.546521);
    static private MyPlace baldur = new MyPlace("Baldur - 3410 9 1/2 Ave", 41.501833, -90.552073);
    static private MyPlace bellman = new MyPlace("Bellman - 602 39th St", 41.506061, -90.546520);
    static private MyPlace bergendoff = new MyPlace("Bergendoff", 41.505680, -90.548908);
    static private MyPlace bergman = new MyPlace("Bergman - 929 32nd St", 41.502156, -90.554499);
    static private MyPlace blackCulture = new MyPlace("Black Culture House", 41.502689, -90.552829);
    static private MyPlace bostad = new MyPlace("Bostad - 727 34th St", 41.504003, -90.552286);
    static private MyPlace branting = new MyPlace("Branting - 3429 7th Ave", 41.505136, -90.552359);
    static private MyPlace bremer = new MyPlace("Bremer House - 3801 8th Ave", 41.504004, -90.547384);
    static private MyPlace brodahl = new MyPlace("Brodahl", 41.502800, -90.552291);
    static private MyPlace carver = new MyPlace("Carver Center", 41.506636, -90.550844);
    static private MyPlace casaLatina = new MyPlace("Casa Latina", 41.501896, -90.551838);
    static private MyPlace celsius = new MyPlace("Celsius - 808 34th St", 41.503181, -90.552841);
    static private MyPlace centennial = new MyPlace("Centennial Hall", 41.505123, -90.548681);
    static private MyPlace collegeCenter = new MyPlace("College Center", 41.504351, -90.548201);
    static private MyPlace delling = new MyPlace("Delling - 721 34th St", 41.504237, -90.552283);
    static private MyPlace denkmann = new MyPlace("Denkmann", 41.504425, -90.550528);
    static private MyPlace elflats = new MyPlace("11th Ave Flats", 41.499988, -90.548975);
    static private MyPlace erfara = new MyPlace("Erfara - 3052 9th Ave", 41.502349, -90.555128);
    static private MyPlace erickson = new MyPlace("Erickson Hall", 41.499363, -90.554705);
    static private MyPlace esbjorn = new MyPlace("Esbjorn - 3025 10th Ave", 41.501834, -90.556017);
    static private MyPlace evald = new MyPlace("Evald", 41.505108, -90.550090);
    static private MyPlace forseti = new MyPlace("Forseti - 1126 35th St", 41.499532, -90.551380);
    static private MyPlace freya = new MyPlace("Freya - 3235 8th Ave", 41.503724, -90.553357);
    static private MyPlace gerber = new MyPlace("Gerber Center", 41.502285, -90.550688);
    static private MyPlace gustav = new MyPlace("Gustav - 608 39th St", 41.505944, -90.546518);
    static private MyPlace hanson = new MyPlace("Hanson", 41.503561, -90.551447);
    static private MyPlace heimdall = new MyPlace("Heimdall - 731 34th St", 41.503885, -90.552284);
    static private MyPlace houseOnHill = new MyPlace("House on the Hill", 41.501186, -90.555065);
    static private MyPlace idun = new MyPlace("Idun - 3233 8th Ave", 41.503761, -90.553463);
    //static private MyPlace international = new MyPlace("International House", 41.500885, -90.555603); check coords
    static private MyPlace karsten = new MyPlace("Karsten - 1119 35th St", 41.499793, -90.550879);
    static private MyPlace larsson = new MyPlace("Larsson - 3250 9th Ave", 41.502319, -90.552838);
    static private MyPlace levander = new MyPlace("Levander - 750 35th St", 41.502794, -90.551581);
    static private MyPlace lindgren = new MyPlace("Lindgren - 1206 35th St", 41.499056, -90.551361);
    static private MyPlace localCulture = new MyPlace("Local Culture - 1118 35th St", 41.499868, -90.551358);
    static private MyPlace lundholm = new MyPlace("Lundholm - 753 34th St", 41.503281, -90.552262);
    static private MyPlace martinson = new MyPlace("Martinson - 800 34th St", 41.503374, -90.552839);
    static private MyPlace milles = new MyPlace("Milles - 1113 35th St", 41.500063, -90.550915);
    static private MyPlace moberg = new MyPlace("Moberg - 3336 7th Ave", 41.504683, -90.552785);
    static private MyPlace naeseth123 = new MyPlace("Naeseth TLA 1-3", 41.499284, -90.553739);
    static private MyPlace naeseth456 = new MyPlace("Naeseth TLA 4-6", 41.498787, -90.552714);
    static private MyPlace nobel = new MyPlace("Nobel - 812 34th St", 41.503060, -90.552838);
    static private MyPlace oden = new MyPlace("Oden - 921 34th St", 41.501640, -90.552342);
    static private MyPlace oldMain = new MyPlace("Old Main", 41.504344, -90.549497);
    static private MyPlace olin = new MyPlace("Olin", 41.503118, -90.550591);
    static private MyPlace ostara = new MyPlace("Ostara - 1202 30th St", 41.499452, -90.557377);
    static private MyPlace parkanderN = new MyPlace("Parkander North", 41.501175, -90.549681);
    static private MyPlace parkanderS = new MyPlace("Parkander South", 41.500545, -90.549934);
    static private MyPlace pepsico = new MyPlace("PepsiCo Recreation", 41.500332, -90.556294);
    static private MyPlace pottery = new MyPlace("Pottery Studio", 41.505721, -90.550474);
    static private MyPlace roslin = new MyPlace("Roslin - 618 39th St", 41.505650, -90.546498);
    static private MyPlace ryden = new MyPlace("Ryden - 3400 10th Ave", 41.501018, -90.552537);
    static private MyPlace sanning = new MyPlace("Sanning - 3048 9th Ave", 41.502385, -90.555274);
    static private MyPlace seminary = new MyPlace("Seminary Hall", 41.503043, -90.548144);
    static private MyPlace skadi = new MyPlace("Skadi - 3437 7th Ave", 41.505138, -90.551767);
    static private MyPlace sorensen = new MyPlace("Sorensen", 41.505139, -90.547201);
    static private MyPlace swanson = new MyPlace("Swanson Commons", 41.500638, -90.548042);
    static private MyPlace swedenborg = new MyPlace("Swedenborg - 3443 7th Ave", 41.505315, -90.551452);
    static private MyPlace swenson = new MyPlace("Swenson Geoscience", 41.503030, -90.549075);
    static private MyPlace thor = new MyPlace("Thor - 816 34th St", 41.502908, -90.552838);
    static private MyPlace tyr = new MyPlace("Tyr - 1111 37th St", 41.500012, -90.548547);
    static private MyPlace vidar = new MyPlace("Vidar - 1200 32nd St", 41.498606, -90.554914);
    static private MyPlace viking = new MyPlace("Viking - 730 34th St", 41.503960, -90.552839);
    static private MyPlace westerlin = new MyPlace("Westerlin Hall", 41.500495, -90.554667);
    static private MyPlace wicksell = new MyPlace("Wicksell - 1120 35th St", 41.499712, -90.551468);
    static private MyPlace zander = new MyPlace("Zander - 3203 10th Ave", 41.501782, -90.554557);
    static private MyPlace zorn = new MyPlace("Zorn - 3051 10th Ave", 41.501776, -90.555133);

    // Returns a dictionary of the Place's names mapped to an Array of the latitude and longitude.
    static public Map<String, double[]> getPlaces() {
        Map<String, double[]> map = new HashMap<>();
        map.put(abbey.name, new double[]{abbey.latitude, abbey.longitude});
        map.put(andeberg.name, new double[]{andeberg.latitude, andeberg.longitude});
        map.put(anderson.name, new double[]{anderson.latitude, anderson.longitude});
        map.put(andreen.name, new double[]{andreen.latitude, andreen.longitude});
        map.put(ansvar.name, new double[]{ansvar.latitude, ansvar.longitude});
        map.put(arbaugh.name, new double[]{arbaugh.latitude, arbaugh.longitude});
        map.put(asgard.name, new double[]{asgard.latitude, asgard.longitude});
        map.put(asianPagoda.name, new double[]{asianPagoda.latitude, asianPagoda.longitude});
        map.put(austin.name, new double[]{austin.latitude, austin.longitude});
        map.put(baldur.name, new double[]{baldur.latitude, baldur.longitude});
        map.put(bellman.name, new double[]{bellman.latitude, bellman.longitude});
        map.put(bergendoff.name, new double[]{bergendoff.latitude, bergendoff.longitude});
        map.put(bergman.name, new double[]{bergman.latitude, bergman.longitude});
        map.put(blackCulture.name, new double[]{blackCulture.latitude, blackCulture.longitude});
        map.put(bostad.name, new double[]{bostad.latitude, bostad.longitude});
        map.put(branting.name, new double[]{branting.latitude, branting.longitude});
        map.put(bremer.name, new double[]{bremer.latitude, bremer.longitude});
        map.put(brodahl.name, new double[]{brodahl.latitude, brodahl.longitude});
        map.put(carver.name, new double[]{carver.latitude, carver.longitude});
        map.put(casaLatina.name, new double[]{casaLatina.latitude, casaLatina.longitude});
        map.put(celsius.name, new double[]{celsius.latitude, celsius.longitude});
        map.put(centennial.name, new double[]{centennial.latitude, centennial.longitude});
        map.put(collegeCenter.name, new double[]{collegeCenter.latitude, collegeCenter.longitude});
        map.put(delling.name, new double[]{delling.latitude, delling.longitude});
        map.put(denkmann.name, new double[]{denkmann.latitude, denkmann.longitude});
        map.put(elflats.name, new double[]{elflats.latitude, elflats.longitude});
        map.put(erfara.name, new double[]{erfara.latitude, erfara.longitude});
        map.put(erickson.name, new double[]{erickson.latitude, erickson.longitude});
        map.put(esbjorn.name, new double[]{esbjorn.latitude, esbjorn.longitude});
        map.put(evald.name, new double[]{evald.latitude, evald.longitude});
        map.put(forseti.name, new double[]{forseti.latitude, forseti.longitude});
        map.put(freya.name, new double[]{freya.latitude, freya.longitude});
        map.put(gerber.name, new double[]{gerber.latitude, gerber.longitude});
        map.put(gustav.name, new double[]{gustav.latitude, gustav.longitude});
        map.put(hanson.name, new double[]{hanson.latitude, hanson.longitude});
        map.put(heimdall.name, new double[]{heimdall.latitude, heimdall.longitude});
        map.put(houseOnHill.name, new double[]{houseOnHill.latitude, houseOnHill.longitude});
        map.put(idun.name, new double[]{idun.latitude, idun.longitude});
        //map.put(international.name, new double[]{international.latitude, international.longitude});
        map.put(karsten.name, new double[]{karsten.latitude, karsten.longitude});
        map.put(larsson.name, new double[]{larsson.latitude, larsson.longitude});
        map.put(levander.name, new double[]{levander.latitude, levander.longitude});
        map.put(lindgren.name, new double[]{lindgren.latitude, lindgren.longitude});
        map.put(localCulture.name, new double[]{localCulture.latitude, localCulture.longitude});
        map.put(lundholm.name, new double[]{lundholm.latitude, lundholm.longitude});
        map.put(martinson.name, new double[]{martinson.latitude, martinson.longitude});
        map.put(milles.name, new double[]{milles.latitude, milles.longitude});
        map.put(moberg.name, new double[]{moberg.latitude, moberg.longitude});
        map.put(naeseth123.name, new double[]{naeseth123.latitude, naeseth123.longitude});
        map.put(naeseth456.name, new double[]{naeseth456.latitude, naeseth456.longitude});
        map.put(nobel.name, new double[]{nobel.latitude, nobel.longitude});
        map.put(oden.name, new double[]{oden.latitude, oden.longitude});
        map.put(oldMain.name, new double[]{oldMain.latitude, oldMain.longitude});
        map.put(olin.name, new double[]{olin.latitude, olin.longitude});
        map.put(ostara.name, new double[]{ostara.latitude, ostara.longitude});
        map.put(parkanderN.name, new double[]{parkanderN.latitude, parkanderN.longitude});
        map.put(parkanderS.name, new double[]{parkanderS.latitude, parkanderS.longitude});
        map.put(pepsico.name, new double[]{pepsico.latitude, pepsico.longitude});
        map.put(pottery.name, new double[]{pottery.latitude, pottery.longitude});
        map.put(roslin.name, new double[]{roslin.latitude, roslin.longitude});
        map.put(ryden.name, new double[]{ryden.latitude, ryden.longitude});
        map.put(sanning.name, new double[]{sanning.latitude, sanning.longitude});
        map.put(seminary.name, new double[]{seminary.latitude, seminary.longitude});
        map.put(skadi.name, new double[]{skadi.latitude, skadi.longitude});
        map.put(sorensen.name, new double[]{sorensen.latitude, sorensen.longitude});
        map.put(swanson.name, new double[]{swanson.latitude, swanson.longitude});
        map.put(swedenborg.name, new double[]{swedenborg.latitude, swedenborg.longitude});
        map.put(swenson.name, new double[]{swenson.latitude, swenson.longitude});
        map.put(thor.name, new double[]{thor.latitude, thor.longitude});
        map.put(tyr.name, new double[]{tyr.latitude, tyr.longitude});
        map.put(vidar.name, new double[]{vidar.latitude, vidar.longitude});
        map.put(viking.name, new double[]{viking.latitude, viking.longitude});
        map.put(westerlin.name, new double[]{westerlin.latitude, westerlin.longitude});
        map.put(wicksell.name, new double[]{wicksell.latitude, wicksell.longitude});
        map.put(zander.name, new double[]{zander.latitude, zander.longitude});
        map.put(zorn.name, new double[]{zorn.latitude, zorn.longitude});
        return map;
    }

    // Returns an Array of the Place's names
    static public String[] getNames() {
        // ADD international.name
        return new String[]{"Enter an Address", abbey.name, andeberg.name, anderson.name, andreen.name, ansvar.name,
                arbaugh.name, asgard.name, asianPagoda.name, austin.name, baldur.name, bellman.name, bergendoff.name, bergman.name, blackCulture.name, bostad.name,
                branting.name, bremer.name, brodahl.name, carver.name, casaLatina.name, celsius.name, centennial.name, collegeCenter.name, delling.name, denkmann.name,
                elflats.name, erfara.name, erickson.name, esbjorn.name, evald.name, forseti.name, freya.name, gerber.name, gustav.name, hanson.name, heimdall.name,
                houseOnHill.name, idun.name, karsten.name, larsson.name, levander.name, lindgren.name, localCulture.name, lundholm.name, martinson.name, milles.name,
                moberg.name, naeseth123.name, naeseth456.name, nobel.name, oldMain.name, olin.name, ostara.name, parkanderN.name, parkanderS.name, pepsico.name,
                pottery.name, roslin.name, ryden.name, sanning.name, seminary.name, skadi.name, sorensen.name, swanson.name, swedenborg.name, swenson.name, thor.name,
                tyr.name, vidar.name, viking.name, westerlin.name, wicksell.name, zander.name, zorn.name};
    }
}
