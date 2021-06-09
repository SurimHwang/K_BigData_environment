package com.example.naver_map_api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
// 위도 경도 클래스
public class location {
    public double lat;
    public double lng;
    public String name;

    public location(double lat, double lng, String name) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
    }
}