package com.example.secondjava;

import android.app.Application;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.example.secondjava.Util.FileUtil;
import com.example.secondjava.Util.SharedUtil;
import com.example.secondjava.database.TableDatabase;
import com.example.secondjava.database.UserDatabase;
import com.example.secondjava.enity.TableGoods;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class MyApplication extends Application {

    private static MyApplication mApp;

    // 声明一个公共的信息映射对象。可当做全局变量使用
    public HashMap<String, String> infoMap = new HashMap<>();

    // 声明一个用户数据库对象
    private TableDatabase tableDatabase;
    private UserDatabase userDatabase;

    // 购物车商品数量
    public int goodsCount;

    public static MyApplication getInstance() {
        return mApp;
    }

    // APP启动时调用
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("tjy", "数据库未建立");
        mApp = this;
        // 构建数据库实例
        tableDatabase = Room.databaseBuilder(this, TableDatabase.class,  "table_Info")
                // 允许迁移数据库
                .addMigrations()
                // 允许在主线程中操作数据库
                .allowMainThreadQueries()
                .build();
//        clearGoodsInfo();
//        clearCartInfo();
        initGoodsInfo();

    }

    private void clearCartInfo() {
        tableDatabase.tableCartDao().deleteAllCartinfo();
    }

    // 在APP终止时调用
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    // 在配置改变时调用，如横屏变为竖屏
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // 清空数据库中的商品信息
    private void clearGoodsInfo() {
        SharedUtil.getInstance(this).writeBoolean("first", true);
        tableDatabase.tableGoodsDao().deleteAllGoodsInfo();
    }

    // 初始化商品信息 第一次初始化时，就借助SharedPreferences保存到本地
    private void initGoodsInfo() {
        boolean isFirst = SharedUtil.getInstance(this).readBoolean("first", true);
        // 获取当前APP的私有下载路径
        String directory = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + File.separatorChar;
        if (isFirst) {
            // 模拟网络图片的下载
            List<TableGoods> list = TableGoods.getDefaultList();
            for (TableGoods info : list) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), info.getPic());
                String path = directory + info.getId() + ".jpg";
                // 往存储卡保存商品图片
                FileUtil.saveImage(path, bitmap);
                // 回收位图对象
                bitmap.recycle();
                info.setPic_path(path);
            }
            // 打开数据库，把商品信息插入到表中
            tableDatabase.tableGoodsDao().insertGoodsInfos(list);
            // 把是否首次打开写入共享参数
            SharedUtil.getInstance(this).writeBoolean("first", false);

        }
    }

    // 获取数据库的实例
    public UserDatabase getUserDB(){
        return userDatabase;
    }

    // 获取数据库的实例
    public TableDatabase getTableDB(){
        return tableDatabase;
    }
}
