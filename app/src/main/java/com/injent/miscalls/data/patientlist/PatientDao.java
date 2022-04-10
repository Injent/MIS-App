package com.injent.miscalls.data.patientlist;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PatientDao {

    @Query("SELECT * FROM Patient")
    List<Patient> getAll();

    @Query("SELECT * FROM Patient")
    LiveData<List<Patient>> getAllLiveData();

    @Query("SELECT * FROM Patient WHERE id = :id LIMIT 1")
    Patient getPatientById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Patient patient);

    @Update
    void update(Patient patient);

    @Delete
    void delete(Patient patient);
}
