package com.injent.miscalls.data.diagnosis;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProtocolTempDao {

    @Query("SELECT * FROM RecommendationTemp")
    List<RecommendationTemp> getAll();

    @Query("SELECT * FROM RecommendationTemp")
    LiveData<List<RecommendationTemp>> getAllLiveData();

    @Query("SELECT * FROM RecommendationTemp WHERE id = :id LIMIT 1")
    RecommendationTemp getProtocolById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RecommendationTemp protocol);

    @Update
    void update(RecommendationTemp protocol);

    @Query("DELETE FROM RecommendationTemp WHERE id = :id")
    void delete(int id);
}
