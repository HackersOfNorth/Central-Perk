package com.hackinthenorth.centralperk.helper;

/**
 * Created by vish on 2/4/16.
 */
public class MeetInMiddle {
    public String compute(String locationsStr) {
        String centroid = "0,0";
        String error = "";
        String locations[] = locationsStr.split(" ");
        System.out.println("Size" + locations.length);
        if (locations.length > 1) {
            centroid = locations[0];
            double latd = Double.parseDouble(centroid.split(",")[0]);
            double longi = Double.parseDouble(centroid.split(",")[1]);
            double centroidLatd = 0;
            double centroidLongi = 0;
            int tot = 0, idx = 0;
            for (String location : locations) {
                double latdT = Double.parseDouble(location.split(",")[0]);
                double longiT = Double.parseDouble(location.split(",")[1]);
                if (getDistanceFromLatLonInKm(latd, longi, latdT, longiT) <= 100) {
                    centroidLatd += latdT;
                    centroidLongi += longiT;
                    tot++;
                }
                else {
                    error = error + ", " + idx;
                }
                idx++;
            }
            System.out.println (tot);
            centroidLatd /= tot;
            centroidLongi /= tot;
            centroid = centroidLatd + "," + centroidLongi;
        }
        return centroid +error;
    }

    double getDistanceFromLatLonInKm(double lat1, double lon1, double lat2, double lon2) {
        int R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2 - lat1);  // deg2rad below
        double dLon = deg2rad(lon2 - lon1);
        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c; // Distance in km
        System.out.println (d);
        return d;
    }

    double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }

    public static void main(String args[]) {
        System.out.println(new MeetInMiddle().compute("25.4918,81.8657 25.4299,81.772, 24,83"));
    }
}
