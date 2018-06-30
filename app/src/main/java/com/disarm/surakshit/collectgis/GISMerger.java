package com.disarm.surakshit.collectgis;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.disarm.surakshit.collectgis.Model.KmlObject;
import com.disarm.surakshit.collectgis.SoftTfidf.Jaro;
import com.disarm.surakshit.collectgis.SoftTfidf.JaroWinklerTFIDF;
import com.disarm.surakshit.collectgis.Util.Constants;
import com.disarm.surakshit.collectgis.Util.ConversionUtil;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bishakh on 6/28/18.
 */

public class GISMerger {

    private static MapView mapView;
    private static List<KmlObject> kmlObjects;
    private static Map<String, List<KmlObject>> sameTileObjects;

    static void mergeGIS(Context context) {
        mapView = new MapView(context);
        kmlObjects = new ArrayList<>();
        sameTileObjects = new HashMap<>();
        File file = Environment.getExternalStoragePublicDirectory(Constants.CMS_DOWNLOADED_KML);

        /* Mark k and tk to each file */

        for (File kmlFile : file.listFiles()) {
            String sourceid = kmlFile.getName().split("_")[3];
            KmlDocument kml = new KmlDocument();
            if (kmlFile.getName().contains("kml")) {
                kml.parseKMLFile(kmlFile);
                final FolderOverlay kmlOverlay = (FolderOverlay) kml.mKmlRoot.buildOverlay(mapView, null, null, kml);
                for (int i = 0; i < kmlOverlay.getItems().size(); i++) {
                    if (kmlOverlay.getItems().get(i) instanceof org.osmdroid.views.overlay.Polygon) {
                        List<LatLng> polyPoints = ConversionUtil.getLatLngList(((org.osmdroid.views.overlay.Polygon) kmlOverlay.getItems().get(i)).getPoints());
                        String message = ((org.osmdroid.views.overlay.Polygon) kmlOverlay.getItems().get(i)).getSnippet();
                        KmlObject kmlObject = getKMLObject(sourceid, message, polyPoints, KmlObject.KMLOBJECT_TYPE_POLYGON,kmlFile);
                        kmlObjects.add(kmlObject);

                    } else if (kmlOverlay.getItems().get(i) instanceof org.osmdroid.views.overlay.Marker) {
                        LatLng point = ConversionUtil.getLatLng(((org.osmdroid.views.overlay.Marker) kmlOverlay.getItems().get(i)).getPosition());
                        String message = ((org.osmdroid.views.overlay.Marker) kmlOverlay.getItems().get(i)).getSnippet();
                    }
                }
            }
        }


        //dividing kmlObjects into buckets of same tile name
        for (KmlObject object : kmlObjects) {
            if (sameTileObjects.get(object.getTileName()) == null) {
                sameTileObjects.put(object.getTileName(), new ArrayList<KmlObject>());
            }
            sameTileObjects.get(object.getTileName()).add(object);
        }



        //comparing kmlObject of each bucket
        for (String tileName : sameTileObjects.keySet()) {
            List<KmlObject> bucket = sameTileObjects.get(tileName);
            Log.d("--*-----------", Arrays.toString(bucket.toArray()));
            for (int i = 0; i < bucket.size(); i++) {
                for (int j = i + 1; j < bucket.size(); j++) {
                    Log.d("Msg", bucket.get(i).getMessage() + " vs " + bucket.get(j).getMessage());
                    Log.d("Distance", "HouseD:" + housedorffDistance(bucket.get(i), bucket.get(j)));
                    Log.d("Tfidf", "Score:" + new JaroWinklerTFIDF().score(bucket.get(i).getMessage(), bucket.get(j).getMessage()));

                }
            }
        }

    }

    public static double housedorffDistance(KmlObject object1, KmlObject object2) {
        double hDistance1, hDistance2;
        hDistance1 = hDistance2 = Double.MIN_VALUE;
        for (LatLng latLng1 : object1.getPoints()) {
            double mind = Double.MAX_VALUE;
            for (LatLng latLng2 : object2.getPoints()) {
                double d = distance(latLng1.getLatitude(), latLng2.getLatitude(), latLng1.getLongitude(), latLng2.getLongitude(), latLng1.getAltitude(), latLng2.getAltitude());
                if (d < mind)
                    mind = d;
            }
            if (hDistance1 < mind)
                hDistance1 = mind;
        }
        for (LatLng latLng1 : object2.getPoints()) {
            double mind = Double.MAX_VALUE;
            for (LatLng latLng2 : object1.getPoints()) {
                double d = distance(latLng1.getLatitude(), latLng2.getLatitude(), latLng1.getLongitude(), latLng2.getLongitude(), latLng1.getAltitude(), latLng2.getAltitude());
                if (d < mind)
                    mind = d;
            }
            if (hDistance2 < mind)
                hDistance2 = mind;
        }
        if (hDistance1 > hDistance2)
            return hDistance1;
        return hDistance2;
    }

    public static KmlObject getKMLObject(String sourceId, String message, List<LatLng> polyPoints, int type, File kmlFile) {
        KmlObject object = new KmlObject();
        object.setMessage(message);
        object.setPoints(polyPoints);
        object.setSource(sourceId);
        object.setType(type);
        object.setFile(kmlFile);
        object = addTileNameAndLevel(object);
        return object;
    }

    public static KmlObject addTileNameAndLevel(KmlObject kmlobject) {

        List<LatLng> latLngs = kmlobject.getPoints();

        int minLevel = 10;
        int maxLevel = 20;
        int currentLevel = 15;

        int zoomLevel = binarySearchZoom(minLevel, maxLevel, currentLevel, latLngs);
        String tileName = getTileNumber(latLngs.get(0).getLatitude(), latLngs.get(0).getLongitude(), zoomLevel);
        kmlobject.setZoom(zoomLevel);
        kmlobject.setTileName(tileName);
        return kmlobject;

    }

    private static int binarySearchZoom(int minLevel, int maxLevel, int currentLevel, List<LatLng> latLngs) {
        if (currentLevel <= minLevel) {
            return currentLevel;
        }
        if (currentLevel >= maxLevel) {
            return currentLevel;
        }
        boolean allInSameZoom = true;
        String previousTileName = null;
        for (LatLng ll : latLngs) {
            double lat = ll.getLatitude();
            double lon = ll.getLongitude();
            String tileName = getTileNumber(lat, lon, currentLevel);
            if (previousTileName == null) {
                previousTileName = tileName;
            }
            if (!previousTileName.equals(tileName)) {
                allInSameZoom = false;
                break;
            }
        }

        if (allInSameZoom) {
            return binarySearchZoom(currentLevel, maxLevel, currentLevel + ((maxLevel - currentLevel) / 2), latLngs);
        } else {
            return binarySearchZoom(minLevel, currentLevel - 1, (currentLevel - (currentLevel - minLevel) / 2), latLngs);
        }
    }

    public static String getTileNumber(final double lat, final double lon, final int zoom) {
        int xtile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
        int ytile = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 << zoom));
        if (xtile < 0)
            xtile = 0;
        if (xtile >= (1 << zoom))
            xtile = ((1 << zoom) - 1);
        if (ytile < 0)
            ytile = 0;
        if (ytile >= (1 << zoom))
            ytile = ((1 << zoom) - 1);
        return ("" + zoom + "/" + xtile + "/" + ytile);
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * <p>
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     *
     * @returns Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}
