package com.injent.miscalls.data.diagnosis;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {RecommendationTemp.class}, version = 1, exportSchema = false)

public abstract class RecommendationsDatabase extends RoomDatabase {

    public static final String DB_NAME = "recommendations";

    public abstract ProtocolTempDao protocolDao();
}
