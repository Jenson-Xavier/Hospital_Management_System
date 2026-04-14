package com.example.secondjava.enity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TableCart {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int good_id;

    private int count;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGood_id() {
        return good_id;
    }

    public void setGood_id(int good_id) {
        this.good_id = good_id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @NonNull
    @Override
    public String toString() {
        return "TableCart{" +
                "id=" + id +
                ", good_id=" + good_id +
                ", count=" + count +
                '}';
    }
}
