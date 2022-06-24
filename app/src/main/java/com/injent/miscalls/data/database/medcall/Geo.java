package com.injent.miscalls.data.database.medcall;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.osmdroid.util.GeoPoint;

@Entity(tableName = "geo_point")
public class Geo {

    public Geo() {
        // Empty body
    }

    @Ignore
    public Geo(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "call_id")
    private int callId;
    @ColumnInfo(name = "latitude")
    private double latitude;
    @ColumnInfo(name = "longitude")
    private double longitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCallId() {
        return callId;
    }

    public void setCallId(int callId) {
        this.callId = callId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public static GeoPoint toGeoPoint(Geo geo) {
        if (geo == null) return null;
        return new GeoPoint(geo.getLatitude(), geo.getLongitude());
    }
}
