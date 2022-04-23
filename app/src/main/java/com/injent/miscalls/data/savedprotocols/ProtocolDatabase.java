package com.injent.miscalls.data.savedprotocols;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Protocol.class}, version = 1, exportSchema = false)
public abstract class ProtocolDatabase extends RoomDatabase {

    public static final String DB_NAME = "saved_protocols";

    public abstract ProtocolDao protocolDao();
}
