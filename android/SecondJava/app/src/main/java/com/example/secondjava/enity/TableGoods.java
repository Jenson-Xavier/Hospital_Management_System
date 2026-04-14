package com.example.secondjava.enity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.secondjava.R;

import java.util.ArrayList;

@Entity
public class TableGoods {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name;

    @NonNull
    private String description;

    private float price;

    private String pic_path;

    //大图的资源编号
    private int pic;

    public TableGoods() {
        name = "";
        description = "";
        pic_path = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @NonNull
    public String getPic_path() {
        return pic_path;
    }

    public void setPic_path(@NonNull String pic_path) {
        this.pic_path = pic_path;
    }

    @NonNull
    @Override
    public String toString() {
        return "TableGoods{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", pic_path='" + pic_path + '\'' +
                '}';
    }

    // 声明一个手机商品的名称数组
    private static final String[] mNameArray = {
            "iPhone11", "Mate30","小米10", "OPPO Reno3", "vivo x30", "荣耀30S"
    };

    // 声明一个手机商品的描述数组
    private static final String[] mDescArray= {
            "Apple iPhone11 256GB 绿色4G个网通手机",
            "华为 HUAWEI Mate30 8GB+256GB 丹霞橙 5G全网通 全面屏手机",
            "小米 MI10 8GB+128GB 钛银黑 5G手机 游戏拍照手机",
            "oPPo Reno3 8GB+128GB 蓝色星夜 双模5G 拍照游戏智能手机",
            "vivo x30 8GB+128GB 绯云 5G全网通 美颜拍照手机",
            "荣耀30s 8GB+128GB 蝶羽红 5G芯片 自拍全面屏手机"
    };

    // 声明一个手机商品的价格数组
    private static final float[] mPriceArray={6299,4999,3999,2999,2998,2399};

    // 声明一个手机商品的大图数组
    private static final int[] mPicArray= {
            R.drawable.iphone, R.drawable.huawei, R.drawable.xiaomi,
            R.drawable.oppo, R.drawable.vivo, R.drawable.rongyao
    };

    // 获取默认的手机信息列表
    public static ArrayList<TableGoods> getDefaultList() {
        ArrayList<TableGoods> goodsList = new ArrayList<TableGoods>();
        for (int i = 1; i <= mNameArray.length; i++) {
            TableGoods info = new TableGoods();
            info.setId(i);
            info.setName(mNameArray[i-1]);
            info.setDescription(mDescArray[i-1]);
            info.setPrice(mPriceArray[i-1]);
            info.setPic(mPicArray[i-1]);
            goodsList.add(info);
        }
        return goodsList;
    }
}
