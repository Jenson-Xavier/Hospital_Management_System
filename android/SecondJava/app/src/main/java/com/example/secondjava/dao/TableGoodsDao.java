package com.example.secondjava.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.secondjava.enity.TableGoods;

import java.util.List;

@Dao
public interface TableGoodsDao {

    // 如果遇到主键冲突，则忽略新插入的数据。
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertGoodsInfos(List<TableGoods> list);

    @Query("SELECT * FROM TableGoods")
    List<TableGoods> queryAllGoodsInfo();

    @Query("DELETE FROM TableGoods")
    void deleteAllGoodsInfo();

    @Query("SELECT * FROM TableGoods WHERE id =:id")
    TableGoods queryGoodsInfoById(int id);

}
