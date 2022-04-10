package com.injent.miscalls.data.patientlist;

public class FailedDownloadDb extends Exception {

    public FailedDownloadDb(String dbName) {
        super(dbName);
    }
}
