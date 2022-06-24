package com.injent.miscalls.util;

public class FailedDownloadDb extends Exception {

    public FailedDownloadDb() {
    }

    public FailedDownloadDb(String dbName) {
        super(dbName);
    }
}
