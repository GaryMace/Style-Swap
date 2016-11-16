package jameshassmallarms.com.styleswap.infrastructure;

/**
 * Created by gary on 15/11/16.
 */

public class LocationTuple {
    private double latitude;
    private double longitude;

    public LocationTuple(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
