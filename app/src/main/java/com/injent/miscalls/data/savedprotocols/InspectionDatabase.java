package com.injent.miscalls.data.savedprotocols;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Inspection.class}, version = 1, exportSchema = false)
public abstract class InspectionDatabase extends RoomDatabase {

    public static final String DB_NAME = "inspections";

    public abstract InspectionDao protocolDao();
}
