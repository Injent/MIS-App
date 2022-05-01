package com.injent.miscalls.data.patientlist;

public class ListEmptyException extends Exception {

    public ListEmptyException() {
        //Nothing to do
    }

    public ListEmptyException(String msg) {
        super(msg);
    }
}
