package com.example.smartparking;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ParkingMapActivity extends AppCompatActivity {


    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    int i = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkingmap);

        ArrayList<SensorData> sensorDataList = new ArrayList<>();
        SensorData sensorData = new SensorData();

//        ListView listView = (ListView) findViewById(R.id.listView);

// 기본 Text를 담을 수 있는 simple_list_item_1을 사용해서 ArrayAdapter를 만들고 listview에 설정
        ArrayAdapter<Double> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);

        databaseReference.child("parking3").addChildEventListener(new ChildEventListener() {  // message는 child의 이벤트를 수신합니다.
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                int resid = 0;
                double distance = (double) dataSnapshot.getValue(); // chatData를 가져오고
                if(distance<7){
                    resid = getResources().getIdentifier("car" + i, "id", getApplicationContext().getPackageName());
                    ImageView imageView = (ImageView)findViewById(resid);
                    imageView.setVisibility(View.VISIBLE);
                }
                i++;
                sensorData.setDistance(distance);
                sensorDataList.add(sensorData);
                adapter.add(distance);  // adapter에 추가합니다.
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
//        listView.setAdapter(adapter);
    }
}