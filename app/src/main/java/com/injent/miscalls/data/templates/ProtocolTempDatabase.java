package com.injent.miscalls.data.templates;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ProtocolTemp.class}, version = 1, exportSchema = false)

public abstract class ProtocolTempDatabase extends RoomDatabase {

    public static final String DB_NAME = "protocols";

    public abstract ProtocolTempDao protocolDao();
}
