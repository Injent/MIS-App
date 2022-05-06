package com.injent.miscalls.data.savedprotocols;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InspectionDao {

    @Query("SELECT * FROM Inspection WHERE id =:id LIMIT 1")
    Inspection getProtocolById(int id);

    @Query("SELECT * FROM Inspection WHERE id =:id LIMIT 1")
    Inspection getProtocolByPatientId(int id);

    @Query("SELECT * FROM Inspection")
    List<Inspection> getAll();

    @Query("DELETE FROM Inspection WHERE id =:id")
    void delete(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Inspection inspection);
}
