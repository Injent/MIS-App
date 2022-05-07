package com.injent.miscalls.data.diagnosis;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DiagnosisDao {

    @Query("SELECT * FROM Diagnosis")
    List<Diagnosis> getAll();

    @Query("SELECT * FROM Diagnosis WHERE id =:id LIMIT 1")
    Diagnosis getById(int id);

    @Query("DELETE FROM Diagnosis WHERE id =:id")
    void delete(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Diagnosis... diagnosis);
}
