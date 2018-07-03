package com.disarm.surakshit.collectgis;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.disarm.surakshit.collectgis.Util.Constants;
import com.disarm.surakshit.collectgis.Util.ConversionUtil;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polygon;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.views.overlay.FolderOverlay;

import java.io.File;
import java.util.List;

public class TagGISActivity extends AppCompatActivity {
    private MapView mapView;
    private TextView descriptionText;
    private MaterialSpinner categorySpinner;
    private MaterialSpinner itemsSpinner;
    private Button nextButton;
    private TextView leftTextView;
    private TextView doneTextView;
    private String mapUrl;
    private String[] categoryArray;
    private String[] hallArray;
    private String[] departmentArray;
    private String[] placeToEatArray;
    private String[] landmarkArray;
    private String[] poiArray;
    private String[] selectedArray;
    private String description;
    private Toast toast;
    private Boolean started;
    private int fileCounter;
    private MapboxMap globalMapboxMap;
    private KmlDocument kmlDocument;
    private File mergedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_gis);
        init();
        mapView.onCreate(savedInstanceState);
        mapInit();
        categorySpinner.setItems(categoryArray);
        selectedArray = hallArray;
        itemsSpinner.setItems(selectedArray);
        categorySpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
//                showToastMessage(categoryArray[position]);
                switch (position) {
                    case 0:
                        selectedArray = hallArray;
                        break;
                    case 1:
                        selectedArray = departmentArray;
                        break;
                    case 2:
                        selectedArray = placeToEatArray;
                        break;
                    case 3:
                        selectedArray = landmarkArray;
                        break;
                    case 4:
                        selectedArray = poiArray;
                        break;
                }
                itemsSpinner.setItems(selectedArray);
                itemsSpinner.setSelectedIndex(0);
            }
        });
//        itemsSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
//                showToastMessage(selectedArray[position]);
//            }
//        });
        started = false;
        tagGISStatus();
    }

    private void tagGISStatus() {
        File downloadedFolder = Environment.getExternalStoragePublicDirectory(Constants.CMS_DOWNLOADED_KML);
        File taggedFolder = Environment.getExternalStoragePublicDirectory(Constants.CMS_TAGGED_KML);
        if (!taggedFolder.exists())
            taggedFolder.mkdir();
        if (taggedFolder.listFiles().length == downloadedFolder.listFiles().length) {
            showToastMessage("All Tagging done");
            setStatusText(0, downloadedFolder.listFiles().length);
        } else {
            File downloadedFiles[] = downloadedFolder.listFiles();
            File taggedFiles[] = taggedFolder.listFiles();
            setStatusText(downloadedFiles.length - taggedFiles.length, taggedFiles.length);
        }
    }


    private void setStatusText(int filesLeft, int filesDone) {
        String leftText = filesLeft + " Left";
        String doneText = filesDone + " Done";
        leftTextView.setText(leftText);
        doneTextView.setText(doneText);
    }

    private void mapInit() {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                globalMapboxMap = mapboxMap;
                mapboxMap.setStyleUrl(mapUrl);
                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(23.5477, 87.2931))
                        .zoom(16)
                        .build());
                mapboxMap.setMaxZoomPreference(18);
                mapboxMap.setMinZoomPreference(14);
            }
        });
    }

    private void showToastMessage(String s) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void init() {
        mapView = findViewById(R.id.tag_gis_map);
        descriptionText = findViewById(R.id.tag_gis_text);
        categorySpinner = findViewById(R.id.category_spinner);
        itemsSpinner = findViewById(R.id.items_spinner);
        doneTextView = findViewById(R.id.tag_gis_done_text);
        leftTextView = findViewById(R.id.tag_gis_left_text);
        nextButton = findViewById(R.id.tag_gis_next_button);
        mapUrl = getString(R.string.mapbox_style_mapbox_streets);
        categoryArray = getResources().getStringArray(R.array.categories);
        hallArray = getResources().getStringArray(R.array.hall_of_residence);
        departmentArray = getResources().getStringArray(R.array.department);
        placeToEatArray = getResources().getStringArray(R.array.place_to_eat);
        landmarkArray = getResources().getStringArray(R.array.landmarks);
        poiArray = getResources().getStringArray(R.array.main_poi);
        kmlDocument = null;
        mergedFile = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    public void onButtonPressed(View view) {
        File downloadedFolder = Environment.getExternalStoragePublicDirectory(Constants.CMS_DOWNLOADED_KML);
        File taggedFolder = Environment.getExternalStoragePublicDirectory(Constants.CMS_TAGGED_KML);
        File downloadedFiles[] = downloadedFolder.listFiles();
        File taggedFiles[] = taggedFolder.listFiles();
        if (!started) {
            started = true;
            nextButton.setText("NEXT");
            fileCounter = 0;
        }
        if (kmlDocument != null && mergedFile != null) {
            kmlDocument.mKmlRoot.setExtendedData(Constants.EXTENDED_DATA_TAG, selectedArray[itemsSpinner.getSelectedIndex()]);
            kmlDocument.saveAsKML(mergedFile);
        }
        if (fileCounter >= downloadedFiles.length) {
            setStatusText(0, downloadedFiles.length);
            showToastMessage("All files tagged");
            kmlDocument = null;
            mergedFile = null;
            return;
        }

        File file = downloadedFiles[fileCounter];
        mergedFile = Environment.getExternalStoragePublicDirectory(Constants.CMS_TAGGED_KML + file.getName());
        while (mergedFile.exists()) {
            fileCounter++;
            if (fileCounter >= downloadedFiles.length)
                return;
            file = downloadedFiles[fileCounter];
            mergedFile = Environment.getExternalStoragePublicDirectory(Constants.CMS_TAGGED_KML + file.getName());
        }
        setStatusText(downloadedFiles.length - fileCounter, fileCounter);
        Log.d("FileName:", "Counter:" + fileCounter + " " + file.getName());
        globalMapboxMap.removeAnnotations();
        org.osmdroid.views.MapView mMapView = new org.osmdroid.views.MapView(this);
        kmlDocument = new KmlDocument();
        kmlDocument.parseKMLFile(file);
        FolderOverlay kmlOverlay = (FolderOverlay) kmlDocument.mKmlRoot.buildOverlay(mMapView, null, null, kmlDocument);
        for (int i = 0; i < kmlOverlay.getItems().size(); i++) {
            if (kmlOverlay.getItems().get(i) instanceof org.osmdroid.views.overlay.Polygon) {
                List<LatLng> polyPoints = ConversionUtil.getLatLngList(((org.osmdroid.views.overlay.Polygon) kmlOverlay.getItems().get(i)).getPoints());
                String snippet = ((org.osmdroid.views.overlay.Polygon) kmlOverlay.getItems().get(i)).getSnippet();
                description = snippet;
                if (polyPoints.size() > 3) {
                    PolygonOptions polygonOptions = new PolygonOptions().addAll(polyPoints)
                            .alpha((float) 0.5)
                            .fillColor(R.color.transparent);
                    globalMapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(polyPoints.get(0).getLatitude(), polyPoints.get(0).getLongitude()), globalMapboxMap.getCameraPosition().zoom));
                    globalMapboxMap.addPolygon(polygonOptions);
                } else if (polyPoints.size() == 3) {
                    PolylineOptions polylineOptions = new PolylineOptions().add(polyPoints.get(0))
                            .add(polyPoints.get(1))
                            .color(R.color.black)
                            .width(3);
                    globalMapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(polyPoints.get(0).getLatitude(), polyPoints.get(0).getLongitude()), globalMapboxMap.getCameraPosition().zoom));
                    globalMapboxMap.addPolyline(polylineOptions);
                }
            } else if (kmlOverlay.getItems().get(i) instanceof org.osmdroid.views.overlay.Marker) {
                LatLng point = ConversionUtil.getLatLng(((org.osmdroid.views.overlay.Marker) kmlOverlay.getItems().get(i)).getPosition());
                String snippet = ((org.osmdroid.views.overlay.Marker) kmlOverlay.getItems().get(i)).getSnippet();
                description = snippet;
                MarkerOptions markerOptions = new MarkerOptions().position(point).setSnippet(snippet);
                globalMapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(point.getLatitude(), point.getLongitude()), globalMapboxMap.getCameraPosition().zoom));
                globalMapboxMap.addMarker(markerOptions);
            }
        }
        descriptionText.setText(description);
        fileCounter++;
    }
}
