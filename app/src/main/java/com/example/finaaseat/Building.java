package com.example.finaaseat;

import com.google.android.gms.maps.model.Polygon;

public class Building {
    private String name;
    private String img;
    private String location;
    private Integer capacity;
    private Integer timeClose;
    private Integer timeOpen;

    private Polygon polygon;

    public Building(String name, String img, String location, Integer capacity, Integer timeClose, Integer timeOpen, Polygon polygon) {
        this.name = name;
        this.img = img;
        this.location = location;
        this.capacity = capacity;
        this.timeClose = timeClose;
        this.timeOpen = timeOpen;
        this.polygon = polygon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getTimeClose() {
        return timeClose;
    }

    public void setTimeClose(Integer timeClose) {
        this.timeClose = timeClose;
    }

    public Integer getTimeOpen() {
        return timeOpen;
    }

    public void setTimeOpen(Integer timeOpen) {
        this.timeOpen = timeOpen;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    @Override
    public String toString() {
        return "Building{" +
                "name='" + name + '\'' +
                ", img='" + img + '\'' +
                ", location='" + location + '\'' +
                ", capacity=" + capacity +
                ", timeClose=" + timeClose +
                ", timeOpen=" + timeOpen +
                '}';
    }
}