package com.example.naver_map_api;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;


import java.util.HashMap;

// 메인 화면
public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback, Overlay.OnClickListener {
    // 위치 권한 확인 위한 변수
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    // 위치 객체 생성
    private FusedLocationSource mLocationSource;
    private NaverMap mNaverMap;

    // 해쉬맵 생성(key값 : 관측소명, value값 : 관측소명 위도 경도)
    public static HashMap<String, location> locationMap = new HashMap<>();

    // 해쉬맵 생성(key값 : 관측소명, value값 : 최근관측결과)
    public static HashMap<String, String> resultMap = new HashMap<>();

    // 해쉬맵 생성(key값 : 관측소명과 월 일, value값 : 관측소명 월 일 수위 결과)
    public static HashMap<String, String> dataMap = new HashMap<>();

    // 데이터베이스 연결 변수
    private DatabaseReference reff;
    private DatabaseReference reff2;

    // 관측소 위도, 경도
    public location location;

    // 최근 날짜
    public static int recent_month;
    public static int recent_day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reff2 = FirebaseDatabase.getInstance().getReference().child("db");
        reff2.addValueEventListener(new ValueEventListener() {
            // 일 단위 지하수 높이 데이터 저장
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot localSnapshot : snapshot.getChildren()) {
                    // 지역 단위 반복
                    String local = localSnapshot.getKey();
                    for(DataSnapshot monthSnapshot : localSnapshot.getChildren()) {
                        // 월 단위 반복
                        int month = Integer.parseInt(monthSnapshot.getKey());
                        for(DataSnapshot daySnapshot : monthSnapshot.getChildren()) {
                            // 일 단위 반복
                            int day = Integer.parseInt(daySnapshot.getKey());
                            Double height = Double.parseDouble(String.valueOf(daySnapshot.child("height").getValue()));
                            String result = String.valueOf(daySnapshot.child("result").getValue());

                            // key(지역, 월, 일), value(수위, 결과) 를 dataMap(해시맵)에 삽입
                            dataMap.put(local + "/" + month + "/" + day, height + "/" + result);

                            // 제일 마지막 날짜를 기억
                            recent_month = month;
                            recent_day = day;

                            // 최근 관측 결과 resultMap(해쉬맵)에 넣기
                            resultMap.put(local, result);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        // 지도 객체 생성
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        // 현재 위치 반환하는 객체 생성
        mLocationSource = new FusedLocationSource(this, PERMISSION_REQUEST_CODE);
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull final NaverMap naverMap) {
        // 객체 받아서 현재 위치 지정
        mNaverMap = naverMap;
        mNaverMap.setLocationSource(mLocationSource);   // 현재위치 설정
        mNaverMap.setMinZoom(6.0);                      // 최소 줌 레벨
        mNaverMap.setMaxZoom(11);                      // 최대 줌 레벨

        //권한 확인
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        // 파이어베이스 데이터 연결

        reff = FirebaseDatabase.getInstance().getReference().child("db2");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    // 각각의 레코드의 정보들 가져와 객체에 삽입
                    // 위치 데이터 저장
                    double lat = (double) postSnapshot.child("latitude").getValue();
                    double lng = (double) postSnapshot.child("longitude").getValue();
                    final String name = postSnapshot.getKey();
                    location = new location(lat, lng, name);

                    // 위치 정보 locationMap(해쉬맵)에 넣기
                    locationMap.put(name, location);

                    // 각각의 레코드에 대한 마커 생성
                    marker_making(location, mNaverMap);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    // 위치 권한 확인
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
            }
        }
    }

    // 마커 설정
    private void marker_making(location location, @NonNull NaverMap naverMap) {
        Marker marker = new Marker();
        OverlayImage image;
        if ("경계".equalsIgnoreCase(resultMap.get(location.name))) {           // "경계"일 때의 마커 이미지
            image = OverlayImage.fromResource(R.drawable.red_pin);
        } else if ("주의".equalsIgnoreCase(resultMap.get(location.name))) {    // "주의"일 때의 마커 이미지
            image = OverlayImage.fromResource(R.drawable.orrange_pin);
        } else if ("관심".equalsIgnoreCase(resultMap.get(location.name))) {    // "관심"일 때의 마커 이미지
            image = OverlayImage.fromResource(R.drawable.yellow_pin);
        } else if ("정상".equalsIgnoreCase(resultMap.get(location.name))) {    // "정상"일 때의 마커 이미지
            image = OverlayImage.fromResource(R.drawable.green_pin);
        } else if ("심각".equalsIgnoreCase(resultMap.get(location.name))) {    // "심각"일 때의 마커 이미지
            image = OverlayImage.fromResource(R.drawable.black_pin);
        } else {                                                               // "null" 값
            image = OverlayImage.fromResource(R.drawable.blue_pin);
        }
        marker.setIcon(image);
        marker.setPosition(new LatLng(location.lat, location.lng));         // 마커 좌표
        marker.setCaptionText(location.name);                             // 마커 밑에 관측소 표시
        marker.setMap(naverMap);
        marker.setWidth(70);
        marker.setHeight(70);
        marker.setOnClickListener(this);                                // 마커 클릭 이벤트 설정
    }

    // 마커 클릭 이벤트 리스너
    @Override
    public boolean onClick(@NonNull Overlay overlay) {
        if (overlay instanceof Marker) {
            // 마커 누를 시 서브 액티비티 띄움
            Marker marker = (Marker) overlay;
            Intent intent = new Intent(MainActivity.this, SubActivity.class);
            intent.putExtra("지하수", marker.getCaptionText());   // 액티비티간에 관측소명 넘겨주기
            startActivity(intent);                                      // 액티비티 이동
            return true;
        }
        return false;
    }
}