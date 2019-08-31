package com.example.smartparking;

public class SensorData {
    private String sensor;
    private double distance;

    public SensorData() { }

    public SensorData(String sensor, double distance) {
        this.sensor = this.sensor;
        this.distance = distance;
    }

    public String getSensor() {
        return sensor;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}