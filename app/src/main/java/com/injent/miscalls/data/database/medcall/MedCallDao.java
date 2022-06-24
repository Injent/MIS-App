package com.injent.miscalls.data.database.medcall;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MedCallDao {

    @Query("SELECT * FROM calls WHERE user_id =:id")
    List<MedCall> getCallByUserId(int id);

    @Query("SELECT * FROM calls WHERE id = :id LIMIT 1")
    MedCall getById(int id);

    @Query("SELECT * FROM calls WHERE snils =:snils LIMIT 1")
    MedCall getBySnils(long snils);

    @Insert
    long insertCall(MedCall medCall);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertList(List<MedCall> list);

    @Update
    void updateCall(MedCall medCall);

    @Delete
    void delete(MedCall medCall);

    @Query("DELETE FROM calls")
    void clearAll();
}
