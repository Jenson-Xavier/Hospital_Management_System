package com.example.secondjava.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.secondjava.dao.TableCartDao;
import com.example.secondjava.dao.TableGoodsDao;
import com.example.secondjava.enity.TableCart;
import com.example.secondjava.enity.TableGoods;

@Database(entities = {TableGoods.class, TableCart.class}, version = 1, exportSchema = true)
public abstract class TableDatabase extends RoomDatabase {
    // 获取该数据库中某张表的持久化对象
    public abstract TableGoodsDao tableGoodsDao();
    public abstract TableCartDao tableCartDao();
}
