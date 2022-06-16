package com.injent.miscalls.data.database.diagnoses;

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

    @Query("SELECT * FROM Diagnosis WHERE not_parent = 1 AND (name LIKE :s OR code LIKE :s)")
    List<Diagnosis> searchNotParentLike(String s);

    @Query("SELECT * FROM Diagnosis WHERE name LIKE :s OR code LIKE :s")
    List<Diagnosis> searchLike(String s);

    @Query("DELETE FROM Diagnosis WHERE id =:id")
    void delete(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Diagnosis... diagnoses);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Diagnosis> list);
}
