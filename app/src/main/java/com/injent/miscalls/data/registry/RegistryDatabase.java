package com.injent.miscalls.data.registry;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Registry.class}, version = 1, exportSchema = false)
public abstract class RegistryDatabase extends RoomDatabase {

    public static final String DB_NAME = "registry";

    public abstract RegistryDao registryDao();
}
