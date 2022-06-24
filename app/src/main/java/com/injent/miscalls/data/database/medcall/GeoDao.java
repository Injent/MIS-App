package com.injent.miscalls.data.database.medcall;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface GeoDao {

    @Insert
    void insertGeo(Geo geo);

    @Query("SELECT * FROM geo_point WHERE call_id =:id LIMIT 1")
    Geo getGeoByCallId(int id);
}
