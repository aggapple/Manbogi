package com.aggapple.manbogi.utils;

public class SocialUtils {
    public static double calcDistance(double lat1, double lon1, double lat2, double lon2) {
        double EARTH_R, Rad, radLat1, radLat2, radDist;
        double distance, ret;

        EARTH_R = 6371000.0;
        Rad = Math.PI / 180;
        radLat1 = Rad * lat1;
        radLat2 = Rad * lat2;
        radDist = Rad * (lon1 - lon2);

        distance = Math.sin(radLat1) * Math.sin(radLat2);
        distance = distance + Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radDist);
        ret = EARTH_R * Math.acos(distance);

        return ret;
    }

    public static String convertDistance(double distance){
        double rslt = Math.round(Math.round(distance) / 1000);
        String result = rslt + " km";
        if (rslt == 0) result = Math.round(distance) + " m";

        return result;
    }
}
