package com.injent.miscalls.data.calllist;

public class FailedDownloadDb extends Exception {

    public FailedDownloadDb(String dbName) {
        super(dbName);
    }
}
