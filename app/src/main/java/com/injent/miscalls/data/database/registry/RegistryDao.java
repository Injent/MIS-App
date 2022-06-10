package com.injent.miscalls.data.database.registry;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface RegistryDao {

    @Query("SELECT * FROM Registry WHERE id =:id LIMIT 1")
    Registry getById(int id);

    @Query("SELECT * FROM Registry")
    List<Registry> getAll();

    @Query("DELETE FROM Registry WHERE id =:id")
    void delete(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRegistry(Registry registry);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertObjectively(Objectively objectively);

    @Transaction
    @Query("SELECT * FROM Registry WHERE id =:id LIMIT 1")
    List<RegistryAndObjectively> getRegAndObj(int id);

    @Transaction
    @Query("DELETE FROM Registry WHERE id =:id")
    void deleteRegAndObj(int id);
}
