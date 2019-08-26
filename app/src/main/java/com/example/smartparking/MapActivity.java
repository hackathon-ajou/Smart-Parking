package com.example.smartparking;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

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
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;

import static android.os.Build.USER;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("YOUR_CLIENT_ID"));

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


    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        naverMap.setLocationSource(locationSource);
        Marker marker = new Marker();
        marker.setPosition(new LatLng(37.5666102, 126.9783881));
        marker.setOnClickListener(o -> {
//            Intent intent = new Intent(getApplicationContext(),TestActivity.class);
//            startActivity(intent);
            return true;
        });
        marker.setMap(naverMap);

        Marker markerWithCustomIcon = new Marker();
        markerWithCustomIcon.setPosition(new LatLng(37.57000, 126.97618));
        markerWithCustomIcon.setIcon(MarkerIcons.BLACK);
        markerWithCustomIcon.setAngle(315);
        markerWithCustomIcon.setMap(naverMap);

        Marker flatMarker = new Marker();
        flatMarker.setPosition(new LatLng(37.57145, 126.98191));
//        flatMarker.setIcon(OverlayImage.fromResource(R.drawable.ic_info_black_24dp));
//        flatMarker.setWidth(getResources().getDimensionPixelSize(R.dimen.marker_size));
//        flatMarker.setHeight(getResources().getDimensionPixelSize(R.dimen.marker_size));
        flatMarker.setFlat(true);
        flatMarker.setAngle(90);
        flatMarker.setMap(naverMap);

        Marker markerWithAnchor = new Marker();
        markerWithAnchor.setPosition(new LatLng(37.56768, 126.98602));
//        markerWithAnchor.setIcon(OverlayImage.fromResource(R.drawable.marker_right_bottom));
        markerWithAnchor.setAnchor(new PointF(1, 1));
        markerWithAnchor.setAngle(90);
        markerWithAnchor.setMap(naverMap);

        Marker markerWithCaption = new Marker();
        markerWithCaption.setPosition(new LatLng(37.56436, 126.97499));
        markerWithCaption.setIcon(MarkerIcons.YELLOW);
        markerWithCaption.setCaptionAlign(Align.Left);
        markerWithCaption.setCaptionText(getString(R.string.marker_caption_1));
        markerWithCaption.setMap(naverMap);

        Marker markerWithSubCaption = new Marker();
        markerWithSubCaption.setPosition(new LatLng(37.56138, 126.97970));
        markerWithSubCaption.setIcon(MarkerIcons.PINK);
        markerWithSubCaption.setCaptionTextSize(14);
        markerWithSubCaption.setCaptionText(getString(R.string.marker_caption_2));
        markerWithSubCaption.setSubCaptionTextSize(10);
        markerWithSubCaption.setSubCaptionColor(Color.GRAY);
        markerWithSubCaption.setSubCaptionText(getString(R.string.marker_sub_caption_2));
        markerWithSubCaption.setMap(naverMap);

        Marker tintColorMarker = new Marker();
        tintColorMarker.setPosition(new LatLng(37.56500, 126.9783881));
        tintColorMarker.setIcon(MarkerIcons.BLACK);
        tintColorMarker.setIconTintColor(Color.RED);
        tintColorMarker.setAlpha(0.5f);
        tintColorMarker.setMap(naverMap);
    }
}