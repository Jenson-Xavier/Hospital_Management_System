package com.example.secondjava;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.secondjava.Util.ToastUtil;
import com.example.secondjava.database.TableDatabase;
import com.example.secondjava.enity.TableCart;
import com.example.secondjava.enity.TableGoods;

import java.util.List;

public class ShoppingChannelActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_count;
    private GridLayout gl_channel;
    private TextView tv_title;
    private TableDatabase tableDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shopping_channel);

        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("手机商场");
        tv_count = findViewById(R.id.tv_count);
        gl_channel = findViewById(R.id.gl_channel);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_cart).setOnClickListener(this);

        tableDatabase = MyApplication.getInstance().getTableDB();
        showGoods();
    }

    // 回到该活动页面时
    @Override
    protected void onResume() {
        super.onResume();
        showCartInfoTotal();
    }

    private void showCartInfoTotal() {
        int count = tableDatabase.tableCartDao().queryCountSum();
        MyApplication.getInstance().goodsCount = count;
        tv_count.setText(String.valueOf(count));
    }

    // 从数据库查询出商品信息，并展示
    private void showGoods() {
        // 商品条目是一个线性布局，设置布局的宽度为屏幕的一半
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth / 2, LinearLayout.LayoutParams.WRAP_CONTENT);
        // 查询商品数据库中的所有商品记录
        List<TableGoods> list = tableDatabase.tableGoodsDao().queryAllGoodsInfo();

        // 移出所有的子视图
        gl_channel.removeAllViews();

        for (TableGoods info : list) {
            // 获取布局文件item_goods.xml的根视图
            View view = LayoutInflater.from(this).inflate(R.layout.item_goods, null);
            ImageView iv_thumb = view.findViewById(R.id.iv_thumb);
            TextView tv_name = view.findViewById(R.id.tv_name);
            TextView tv_price = view.findViewById(R.id.tv_price);

            android.widget.Button btn_add = view.findViewById(R.id.btn_add);
            // 为控件设置值
            iv_thumb.setImageURI(Uri.parse(info.getPic_path()));
            tv_name.setText(info.getName());
            tv_price.setText(String.valueOf((int) info.getPrice()));

            // 添加到购物车
            btn_add.setOnClickListener(v -> {
                addToCart(info.getId(), info.getName());
            });

            // 把商品视图添加到网格布局
            gl_channel.addView(view, params);
        }
    }

    private void addToCart(int goodsId, String name) {
        int count = ++MyApplication.getInstance().goodsCount;
        tv_count.setText(String.valueOf(count));

        // 如果购物车中不存在该商品，添加一条信息
        TableCart cartInfo = tableDatabase.tableCartDao().queryCartInfoByGoodsId(goodsId);
        if (cartInfo == null) {
            cartInfo = new TableCart();
            cartInfo.setGood_id(goodsId);
            cartInfo.setCount(1);
            tableDatabase.tableCartDao().insert(cartInfo);
        }
        // 如果购物车中已经存在该商品，更新商品数量
        else {
            cartInfo.setCount(cartInfo.getCount() + 1);
            tableDatabase.tableCartDao().update(cartInfo);
        }

        ToastUtil.show(this, "已添加一部" + name + "到购物车");
    }

    @Override
    public void onClick(View v) {
        // 点击返回图标
        if (v.getId() == R.id.iv_back) {
            finish();
        }
        // 点击购物车图标
        else if (v.getId() == R.id.iv_cart) {
            // 从现场页面跳转到购物车页面
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}