package com.example.smartparking;


import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class XmlParserActivity extends AppCompatActivity {
    private static final String TAG_PARSE = "TAG_PARSE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InputStream inputStream = getResources().openRawResource(R.raw.parse);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferReader = new BufferedReader(inputStreamReader);

        try {
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
            xmlPullParser.setInput(bufferReader);

            int eventType = xmlPullParser.getEventType();

            while(eventType != xmlPullParser.END_DOCUMENT){
                switch(eventType){
                    case XmlPullParser.START_DOCUMENT: // 문서 시작
                        break;
                    case XmlPullParser.START_TAG: // 태그의 시작
                        Log.i(TAG_PARSE, "START_TAG : " + xmlPullParser.getName());

                        if(xmlPullParser.getName().equals("b")) {
                            Log.i(TAG_PARSE, "xmlPullParser.getName().equals(\"b\")");
                        }
                        int countParam = xmlPullParser.getAttributeCount();
                        Log.i(TAG_PARSE, "getAttributeCount() : " + countParam);

                        for(int i = 0; i < countParam; i++){
                            Log.i(TAG_PARSE, i + " param getAttributeName() : " + xmlPullParser.getAttributeName(i));
                            Log.i(TAG_PARSE, i + " param getAttributeValue() : " + xmlPullParser.getAttributeValue(i));
                        }
                        break;
                    case XmlPullParser.END_TAG: // 태그의 끝
                        Log.i(TAG_PARSE, "END_TAG : " + xmlPullParser.getName());
                        break;
                    case XmlPullParser.TEXT: // TEXT 인경우, "> 에서 < 사이"
                        Log.i(TAG_PARSE, "TEXT : " + xmlPullParser.getText());
                        break;
                }
                eventType = xmlPullParser.next(); // 다음 parse 가르키기
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}