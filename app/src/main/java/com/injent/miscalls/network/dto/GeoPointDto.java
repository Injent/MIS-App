package com.injent.miscalls.network.dto;

import com.google.gson.annotations.SerializedName;
import com.injent.miscalls.data.database.medcall.Geo;

public class GeoPointDto {

    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public static Geo toDomainObject(GeoPointDto geoPointDto) {
        return new Geo(geoPointDto.getLatitude(), geoPointDto.getLongitude());
    }
}
