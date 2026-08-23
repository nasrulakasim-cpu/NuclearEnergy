package com.nuclearenergy.model;

/**
 * A single reactor unit tracked by the monitoring system.
 */
public class Reactor {

    public static final String ONLINE = "ONLINE";
    public static final String MAINTENANCE = "MAINTENANCE";
    public static final String OFFLINE = "OFFLINE";

    /** Temperature (Celsius) above which the dashboard flags the reactor as too hot. */
    public static final double TEMP_ALERT_THRESHOLD = 400.0;

    private int id;
    private String name;
    private String location;
    private String status;
    private double temperatureC;
    private double outputMW;

    public Reactor() {
    }

    public Reactor(int id, String name, String location, String status,
                    double temperatureC, double outputMW) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.status = status;
        this.temperatureC = temperatureC;
        this.outputMW = outputMW;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTemperatureC() {
        return temperatureC;
    }

    public void setTemperatureC(double temperatureC) {
        this.temperatureC = temperatureC;
    }

    public double getOutputMW() {
        return outputMW;
    }

    public void setOutputMW(double outputMW) {
        this.outputMW = outputMW;
    }

    public boolean isOverheating() {
        return temperatureC >= TEMP_ALERT_THRESHOLD;
    }

    /** Cycles OFFLINE -> MAINTENANCE -> ONLINE -> OFFLINE ... */
    public String nextStatus() {
        if (OFFLINE.equals(status)) {
            return MAINTENANCE;
        } else if (MAINTENANCE.equals(status)) {
            return ONLINE;
        } else {
            return OFFLINE;
        }
    }
}
