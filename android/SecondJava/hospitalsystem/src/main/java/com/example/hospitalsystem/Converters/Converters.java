package com.example.hospitalsystem.Converters;

import androidx.room.TypeConverter;

import java.util.Date;

//定义类型转换器
public class Converters {
    @TypeConverter public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}