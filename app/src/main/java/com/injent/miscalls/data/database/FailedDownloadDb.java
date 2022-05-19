package com.injent.miscalls.data.database;

public class FailedDownloadDb extends Exception {

    public FailedDownloadDb(String dbName) {
        super(dbName);
    }
}
