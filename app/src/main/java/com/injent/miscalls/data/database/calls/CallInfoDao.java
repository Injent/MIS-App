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

    @Query("SELECT * FROM calls WHERE user_id =:id")
    List<CallInfo> getCallByUserId(int id);

    @Query("SELECT * FROM calls WHERE id = :id LIMIT 1")
    CallInfo getById(int id);

    @Insert
    long insertCall(CallInfo callInfo);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CallInfo> list);

    @Update
    void updateCall(CallInfo callInfo);

    @Delete
    void delete(CallInfo callInfo);

    @Query("DELETE FROM calls")
    void clearAll();
}
