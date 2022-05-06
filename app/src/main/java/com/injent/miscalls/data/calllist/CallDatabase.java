package com.injent.miscalls.data.calllist;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {CallInfo.class}, version = 1, exportSchema = false)

public abstract class CallDatabase extends RoomDatabase {

    public static final String DB_NAME = "calls";

    public abstract CallInfoDao patientDao();
}
