package com.injent.miscalls.data.database.registry;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.injent.miscalls.data.database.medcall.MedCall;
import com.injent.miscalls.data.database.diagnosis.Diagnosis;

import java.util.List;

@Dao
public interface RegistryDao {

    @Query("SELECT * FROM calls WHERE id =:id LIMIT 1")
    MedCall getCallInfo(int id);

    @Query("SELECT * FROM registry WHERE user_id =:userId")
    List<Registry> getAllRawRegistries(int userId);

    @Query("SELECT * FROM registry WHERE id =:id AND user_id =:userId LIMIT 1")
    Registry getRegistry(int id, int userId);

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
    void deleteAllRegistries();

    @Query("DELETE FROM objectively")
    void deleteAllObj();

    @Update
    void updateRegistry(Registry registry);

    @Update
    void updateObjectively(Objectively objectively);
}
