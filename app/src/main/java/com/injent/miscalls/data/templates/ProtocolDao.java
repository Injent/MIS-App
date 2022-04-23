package com.injent.miscalls.data.templates;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProtocolDao {

    @Query("SELECT * FROM ProtocolTemp")
    List<ProtocolTemp> getAll();

    @Query("SELECT * FROM ProtocolTemp")
    LiveData<List<ProtocolTemp>> getAllLiveData();

    @Query("SELECT * FROM ProtocolTemp WHERE id = :id LIMIT 1")
    ProtocolTemp getProtocolById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ProtocolTemp protocol);

    @Update
    void update(ProtocolTemp protocol);

    @Query("DELETE FROM ProtocolTemp WHERE id = :id")
    void delete(int id);
}
