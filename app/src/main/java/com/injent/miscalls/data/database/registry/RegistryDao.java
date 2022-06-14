package com.injent.miscalls.data.database.registry;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.injent.miscalls.data.database.calls.CallInfo;
import com.injent.miscalls.data.database.diagnoses.Diagnosis;

import java.util.List;

@Dao
public interface RegistryDao {

    @Query("SELECT * FROM calls WHERE id =:id LIMIT 1")
    CallInfo getCallInfo(int id);

    @Query("SELECT * FROM registry")
    List<Registry> getAllRawRegistries();

    @Query("SELECT * FROM registry WHERE id =:id LIMIT 1")
    Registry getRegistry(int id);

    @Query("SELECT * FROM registry WHERE call_id =:id LIMIT 1")
    Registry getRegistryByCallId(int id);

    @Query("SELECT * FROM diagnosis WHERE id =:id LIMIT 1")
    Diagnosis getDiagnosis(int id);

    @Query("DELETE FROM registry WHERE id =:id")
    void deleteRegistry(int id);

    @Query("DELETE FROM objectively WHERE reg_id =:id")
    void deleteObjectivelyByRegId(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertRegistry(Registry registry);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertObjectively(Objectively objectively);

    @Query("SELECT * FROM objectively WHERE reg_id =:id")
    Objectively getObjectivelyByRegId(int id);

    @Query("DELETE FROM registry")
    void _deleteAllRegistries();

    @Query("DELETE FROM objectively")
    void _deleteAllObj();
}
