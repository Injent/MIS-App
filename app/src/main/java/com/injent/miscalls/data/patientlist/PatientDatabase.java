package com.injent.miscalls.data.patientlist;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Patient.class}, version = 1, exportSchema = false)

public abstract class PatientDatabase extends RoomDatabase {

    public static final String DB_NAME = "patients";

    public abstract PatientDao patientDao();
}
