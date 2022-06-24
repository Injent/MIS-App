package com.injent.miscalls.data.database.recommendation;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RecommendationDao {

    @Query("SELECT * FROM Medication")
    List<Medication> getAll();

    @Query("SELECT * FROM Medication WHERE id = :id LIMIT 1")
    Medication getById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Medication... protocol);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Medication> list);

    @Update
    void update(Medication protocol);

    @Query("DELETE FROM Medication WHERE id = :id")
    void delete(int id);

    @Query("DELETE FROM Medication")
    void clear();
}
