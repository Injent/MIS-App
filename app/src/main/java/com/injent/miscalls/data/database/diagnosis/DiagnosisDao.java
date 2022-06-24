package com.injent.miscalls.data.database.diagnosis;

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

    @Query("SELECT * FROM Diagnosis WHERE parent_id =:id")
    List<Diagnosis> getByParentId(int id);

    @Query("SELECT * FROM Diagnosis WHERE not_parent = 1 AND (name LIKE :s OR code LIKE :s) LIMIT :limit")
    List<Diagnosis> searchNotParentLike(String s, int limit);

    @Query("SELECT * FROM Diagnosis WHERE name LIKE :s OR code LIKE :s LIMIT :limit")
    List<Diagnosis> searchLike(String s, int limit);

    @Query("DELETE FROM Diagnosis WHERE id =:id")
    void delete(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Diagnosis... diagnoses);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertList(List<Diagnosis> list);
}
