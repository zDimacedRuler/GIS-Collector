package com.disarm.surakshit.collectgis.Util;

import com.mapbox.mapboxsdk.geometry.LatLng;
import org.osmdroid.util.GeoPoint;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AmanKumar on 6/24/2018.
 */

public class ConversionUtil {

    public static GeoPoint getGeoPoint(LatLng latLng) {
        return new GeoPoint(latLng.getLatitude(), latLng.getLongitude(), latLng.getAltitude());
    }

    public static LatLng getLatLng(GeoPoint geoPoint) {
        return new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude(), geoPoint.getAltitude());
    }

    public static List<GeoPoint> getGeoPointList(List<LatLng> polygonPoints) {
        ArrayList<GeoPoint> geoPoints = new ArrayList<>(polygonPoints.size() + 1);
        for (LatLng latLng : polygonPoints) {
            geoPoints.add(new GeoPoint(latLng.getLatitude(), latLng.getLongitude(), latLng.getAltitude()));
        }
        geoPoints.add(new GeoPoint(polygonPoints.get(0).getLatitude(), polygonPoints.get(0).getLongitude(), polygonPoints.get(0).getAltitude()));
        return geoPoints;
    }

    public static List<LatLng> getLatLngList(List<GeoPoint> polygonPoints) {
        ArrayList<LatLng> latLngPoints = new ArrayList<>(polygonPoints.size() + 1);
        for (GeoPoint geoPoint : polygonPoints) {
            latLngPoints.add(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude(), geoPoint.getAltitude()));
        }
        return latLngPoints;
    }
}
