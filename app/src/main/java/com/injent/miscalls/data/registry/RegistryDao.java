package com.injent.miscalls.data.registry;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RegistryDao {

    @Query("SELECT * FROM Registry WHERE id =:id LIMIT 1")
    Registry getProtocolById(int id);

    @Query("SELECT * FROM Registry WHERE id =:id LIMIT 1")
    Registry getProtocolByPatientId(int id);

    @Query("SELECT * FROM Registry")
    List<Registry> getAll();

    @Query("DELETE FROM Registry WHERE id =:id")
    void delete(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Registry registry);
}
