package com.example.smartparking;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Align;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private ArrayList<LatLng> location = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("jdgdtz7iav"));
        try {
            getParkingInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance(new NaverMapOptions().camera(new CameraPosition(
                    NaverMap.DEFAULT_CAMERA_POSITION.target, NaverMap.DEFAULT_CAMERA_POSITION.zoom, 30, 45)));
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    public Marker makeMarker(LatLng location){
        Marker marker = new Marker();
        marker.setPosition(location);

        LinearLayout popup = (LinearLayout)findViewById(R.id.linear);

        marker.setOnClickListener(o -> {
            popup.setVisibility(View.VISIBLE);
            return true;
        });

        Button btn_parkingmap = (Button)findViewById(R.id.button_parkingMap);
        btn_parkingmap.setOnClickListener(o->{
            Intent intent = new Intent(getApplicationContext(), ParkingMapActivity.class);
            startActivity(intent);
        });

        return marker;
    }

    private void getParkingInfo() throws IOException {
        JSONArray parkingInfoList = loadJSONFromResource();
        double latitude = 0, longitude = 0;
        for (int position = 0; position < parkingInfoList.length(); position++) {
            try {
                JSONObject parkingInfo = new JSONObject(parkingInfoList.get(position).toString());
                latitude = parkingInfo.getDouble("위도");
                longitude = parkingInfo.getDouble("경도");
                location.add(new LatLng(latitude,longitude));
                Log.i("@@@@@@@@@@@@@@@@@@@",latitude+" "+longitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public JSONArray loadJSONFromResource() throws IOException {
        InputStream is = getResources().openRawResource(R.raw.parking);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        Reader reader = null;
        JSONObject asset = null;
        JSONArray eyewearInfoList = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            String jsonString = writer.toString();
            asset = new JSONObject(jsonString).getJSONObject("root");
            eyewearInfoList = asset.getJSONArray("Row");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }finally {
            is.close();
        }
        return  eyewearInfoList;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        naverMap.setLocationSource(locationSource);
        for(int i=0;i<location.size();i++) {
            Marker marker = makeMarker(location.get(i));
            marker.setMap(naverMap);
        }

    }
}