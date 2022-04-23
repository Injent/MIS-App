package com.injent.miscalls.data.savedprotocols;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ProtocolDao {

    @Query("SELECT * FROM Protocol WHERE id =:id LIMIT 1")
    Protocol getProtocol(int id);

    @Query("SELECT * FROM Protocol")
    List<Protocol> getAll();

    @Query("DELETE FROM Protocol WHERE id =:id")
    void delete(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Protocol protocol);
}
