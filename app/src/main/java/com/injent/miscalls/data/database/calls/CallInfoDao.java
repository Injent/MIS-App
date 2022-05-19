package com.injent.miscalls.data.database.calls;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CallInfoDao {

    @Query("SELECT * FROM CallInfo")
    List<CallInfo> getAll();

    @Query("SELECT * FROM CallInfo WHERE id = :id LIMIT 1")
    CallInfo getById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CallInfo callInfo);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CallInfo> list);

    @Update
    void update(CallInfo callInfo);

    @Delete
    void delete(CallInfo callInfo);

    @Query("DELETE FROM CallInfo")
    void clearAll();
}
