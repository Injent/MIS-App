package com.injent.miscalls.data.templates;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.injent.miscalls.data.patientlist.PatientDao;

@Database(entities = {ProtocolTemp.class}, version = 1, exportSchema = false)

public abstract class ProtocolDatabase extends RoomDatabase {

    public static final String DB_NAME = "protocols";

    public abstract ProtocolDao protocolDao();
}
