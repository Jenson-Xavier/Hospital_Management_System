package com.example.secondjava.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.secondjava.dao.UserDao;
import com.example.secondjava.enity.User_room;

@Database(entities = {User_room.class}, version = 1, exportSchema = true)
public abstract class UserDatabase extends RoomDatabase {
    // 获取该数据库中某张表的持久化对象
    public abstract UserDao userDao();

}
