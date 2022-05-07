package com.injent.miscalls.data.recommendation;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RecommendationDao {

    @Query("SELECT * FROM Recommendation")
    List<Recommendation> getAll();

    @Query("SELECT * FROM Recommendation WHERE id = :id LIMIT 1")
    Recommendation getById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Recommendation... protocol);

    @Update
    void update(Recommendation protocol);

    @Query("DELETE FROM Recommendation WHERE id = :id")
    void delete(int id);

    @Query("DELETE FROM Recommendation")
    void clear();
}
