package com.evan.parknbark.maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Park {
    private String name;
    private String street;
    private double lat;
    private double lon;

    public Park() {
    }

    public Park(String name, String street, double lat, double lon) {
        this.name = name;
        this.street = street;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}
