package com.injent.miscalls.util;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConverter {

    private DateConverter(){
        // Util class
    }

    public static final String DATE_PATTERN = "dd-MM-yyyy";

    @TypeConverter
    public static Date toDate(Long dateLong){
        return dateLong == null ? null: new Date(dateLong);
    }

    @TypeConverter
    public static long fromDate(Date date){
        if (date == null) return new Date().getTime();
        return date.getTime();
    }
}