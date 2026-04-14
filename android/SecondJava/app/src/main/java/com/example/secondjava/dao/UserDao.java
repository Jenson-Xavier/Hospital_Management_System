package com.example.secondjava.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.secondjava.enity.User_room;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insert(User_room... user);

    @Delete
    void delete(User_room... user) ;

    @Query("DELETE FROM User_room")
    void deleteAll() ;

    @Update
    int update(User_room... user);

    @Query("SELECT * FROM User_room")
    List<User_room> queryAll() ;

    @Query("SELECT * FROM User_room WHERE name = :name ORDER BY id DESC LIMIT 1")
    User_room queryByName(String name) ;

}
