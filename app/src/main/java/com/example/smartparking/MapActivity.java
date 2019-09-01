package com.example.smartparking;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.FusedLocationSource;

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


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private FusedLocationSource locationSource;
    private ArrayList<LatLng> location = new ArrayList<>();
    private JSONObject JSON_file;
    private ArrayList<LatLng> locationpath = new ArrayList<>();

    double my_longitude = 128.504183;
    double my_latitude = 36.574759;

    MapFragment mapFragment;
    LinearLayout popup;
    Button fullbt;

    private String url = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving?start=";
    private String API_KEY_ID = "X-NCP-APIGW-API-KEY-ID=jdgdtz7iav";
    private String API_KEY_SCREAT_KEY = "X-NCP-APIGW-API-KEY=FBBQc9XzbYciBcYXb5B5ilHFV1gdajDJqc5DmAfK";

    PathOverlay path = new PathOverlay();

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

        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        popup = (LinearLayout) findViewById(R.id.linear);
        fullbt = (Button) findViewById(R.id.fullbt);
        fullbt.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.setVisibility(View.INVISIBLE);
                fullbt.setVisibility(View.INVISIBLE);
            }
        });
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance(new NaverMapOptions().camera(new CameraPosition(
                    NaverMap.DEFAULT_CAMERA_POSITION.target, NaverMap.DEFAULT_CAMERA_POSITION.zoom, 30, 45)));
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
    }

    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            String provider = location.getProvider();
            double my_longitude = location.getLongitude();
            double my_latitude = location.getLatitude();
            double altitude = location.getAltitude();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

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
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    public Marker makeMarker(LatLng goal_location) {
        Marker marker = new Marker();
        marker.setPosition(goal_location);
        marker.setWidth(Marker.SIZE_AUTO);
        marker.setHeight(Marker.SIZE_AUTO);

        marker.setOnClickListener(o -> {
            databaseReference.child("parking3").addChildEventListener(new ChildEventListener() {  // message는 child의 이벤트를 수신합니다.
                int emptyArea = 0;

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    ArrayList array = (ArrayList) dataSnapshot.getValue(); // chatData를 가져오고
                    double distance = (double) array.get(1);
                    if (distance > 20) {
                        emptyArea++;
                    }
                    TextView emptyTv = (TextView) findViewById(R.id.available);
                    emptyTv.setText("남은자리 : " + emptyArea);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            Button btn_direction = (Button)findViewById(R.id.btn_direction);
            btn_direction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    double goal_latitude = goal_location.latitude;
                    double goal_longitude = goal_location.longitude;
                    ParkingInfoTask parkingInfoTask = new ParkingInfoTask(url+my_longitude + "," +my_latitude+ "&goal=" + goal_longitude + "," + goal_latitude + "&option=trafast&" + API_KEY_ID + "&" + API_KEY_SCREAT_KEY, null);
                    parkingInfoTask.execute();
                }
            });
            popup.setVisibility(View.VISIBLE);
            fullbt.setVisibility(View.VISIBLE);
            return true;
        });

        Button btn_parkingmap = (Button) findViewById(R.id.button_parkingMap);
        btn_parkingmap.setOnClickListener(o -> {
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
        JSONArray parkingInfoList = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            String jsonString = writer.toString();
            asset = new JSONObject(jsonString).getJSONObject("root");
            parkingInfoList = asset.getJSONArray("Row");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }finally {
            is.close();
        }
        return  parkingInfoList;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        naverMap.setLocationSource(locationSource);
        for(int i=0;i<location.size();i++) {
            Marker marker = makeMarker(location.get(i));
            marker.setMap(naverMap);
        }
        if(locationpath.size()>2) {

            path.setCoords(locationpath);
            path.setColor(Color.BLUE);
            path.setOutlineWidth(5);
            path.setPatternImage(OverlayImage.fromResource(R.drawable.path_pattern));
            path.setPatternInterval(10);
            path.setMap(naverMap);
        }
    }

    public class ParkingInfoTask extends AsyncTask<Void, Void, Void> {
        private String url;
        private ContentValues values;

        public ParkingInfoTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected Void doInBackground(Void... params) {
            locationpath = new ArrayList<>();
            String result; // 요청 결과를 저장할 변수.
            JSONArray path = null; //모델의 개수
            int size = 0;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            try {
                JSON_file = new JSONObject(result);
                path = new JSONObject(new JSONObject(JSON_file.getString("route")).getJSONArray("trafast").getString(0)).getJSONArray("path");
                size = path.length();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i = 0; size > i; i++) {
                try {
                    double latitude = 0, longitude = 0;
                    longitude = path.getJSONArray(i).getDouble(0);
                    latitude = path.getJSONArray(i).getDouble(1);
                    locationpath.add(new LatLng(latitude,longitude));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mapFragment.getMapAsync(MapActivity.this::onMapReady);
        }
    }
}