package com.example.secondjava.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.secondjava.enity.TableCart;

import java.util.List;

@Dao
public interface TableCartDao {

    @Insert
    void insert(TableCart tableCart);

    // 根据good_id进行查询
    @Query("SELECT * FROM TableCart WHERE good_id = :goodId")
    TableCart queryCartInfoByGoodsId(int goodId);

    @Update
    void update(TableCart tableCart);

    // 查询商品总数
    @Query("SELECT SUM(count) FROM TableCart")
    int queryCountSum();

    // 查询所有购物车商品
    @Query("SELECT * FROM TableCart")
    List<TableCart> queryAllCartInfo();

    // 删除所有购物车信息
    @Query("DELETE FROM TableCart")
    void deleteAllCartinfo();

    @Delete
    void delete(TableCart tableCart);
}
