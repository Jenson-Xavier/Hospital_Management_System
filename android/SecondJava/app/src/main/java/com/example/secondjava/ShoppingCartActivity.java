package com.example.secondjava;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.secondjava.Util.ToastUtil;
import com.example.secondjava.database.TableDatabase;
import com.example.secondjava.enity.TableCart;
import com.example.secondjava.enity.TableGoods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCartActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_count;
    private TextView tv_title;
    private LinearLayout ll_cart;
    private TableDatabase tableDatabase;

    // 声明一个购物车的商品信息列表
    private List<TableCart> mCartList;
    // 声明一个根据商品编号查找商品信息的映射，把商品信息缓存起来，从而不需要每次查询数据库
    private Map<Integer, TableGoods> mGoodsMap = new HashMap<>();
    private TextView tv_total_price;
    private LinearLayout ll_empty;
    private LinearLayout ll_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shopping_cart);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("购物车");
        tv_total_price = findViewById(R.id.tv_total_price);
        tv_count = findViewById(R.id.tv_count);
        tv_count.setText(String.valueOf(MyApplication.getInstance().goodsCount));
        ll_cart = findViewById(R.id.ll_cart);
        ll_empty = findViewById(R.id.ll_empty);
        ll_content = findViewById(R.id.ll_content);

        findViewById(R.id.btn_clear).setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.btn_settle).setOnClickListener(this);
        findViewById(R.id.btn_shopping_channel).setOnClickListener(this);

        tableDatabase = MyApplication.getInstance().getTableDB();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showCart();
        showCount();
    }

    // 展示购物车中的商品列表
    private void showCart() {
        // 移除所有的子视图
        ll_cart.removeAllViews();
        // 查询购物车数据库中所有的商品记录
        mCartList = tableDatabase.tableCartDao().queryAllCartInfo();
        if (mCartList.size() == 0) {
            return;
        }

        for (TableCart info : mCartList) {
            // 根据商品编号去查询商品数据库中的商品记录
            TableGoods goods = tableDatabase.tableGoodsDao().queryGoodsInfoById(info.getGood_id());
            mGoodsMap.put(info.getGood_id(), goods);

            // 获取布局文件item_cart.xml的根视图
            View view = LayoutInflater.from(this).inflate(R.layout.item_cart, null);
            ImageView iv_thumb = view.findViewById(R.id.iv_thumb);
            TextView tv_name = view.findViewById(R.id.tv_name);
            TextView tv_price = view.findViewById(R.id.tv_price);
            TextView tv_desc = view.findViewById(R.id.tv_desc);
            TextView tv_count = view.findViewById(R.id.tv_count);
            TextView tv_sum = view.findViewById(R.id.tv_sum);
            Log.d("tjy", goods.getPic_path());
            iv_thumb.setImageURI(Uri.parse(goods.getPic_path()));
            tv_name.setText(goods.getName());
            tv_price.setText(String.valueOf((int) goods.getPrice()));
            tv_count.setText(String.valueOf(info.getCount()));
            tv_desc.setText(goods.getDescription());
            // 设置商品总价
            tv_sum.setText(String.valueOf((int) goods.getPrice() * info.getCount()));

            // 给商品行添加长按事件，长按商品行就删除该商品
            view.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingCartActivity.this);
                builder.setMessage("是否从购物车中删除" + goods.getName() + "?");
                builder.setPositiveButton("是", (dialog, which) -> {
                    // 移除当前视图
                    ll_cart.removeView(v);
                    // 删除该商品
                    deleteGoods(info);
                });
                builder.setNegativeButton("否", null);
                builder.create().show();

                return true;
            });

            // 往购物车中添加该商品行
            ll_cart.addView(view);
        }

        // 重新计算购物车中商品的结算总金额
        refreshTotalPrice();
    }

    private void deleteGoods(TableCart info) {
        MyApplication.getInstance().goodsCount -= info.getCount();
        // 从购物车的数据库中删除该商品
        tableDatabase.tableCartDao().delete(info);
        // 从购物车的列表中删除该商品
        TableCart removed = null;
        for (TableCart cartInfo : mCartList) {
            if (cartInfo.getGood_id() == info.getGood_id()) {
                removed = cartInfo;
                break;
            }
        }
        mCartList.remove(removed);
        // 显示最新的商品数量
        showCount();
        ToastUtil.show(this, "已从购物车中删除" + mGoodsMap.get(info.getGood_id()).getName());
        mGoodsMap.remove(info.getGood_id());
        // 刷新购物车中所有商品的总金额
        refreshTotalPrice();
    }

    // 显示购物车图标中的商品数量
    private void showCount() {
        tv_count.setText(String.valueOf(MyApplication.getInstance().goodsCount));
        // 购物车中没有商品，显示空空如也
        if (MyApplication.getInstance().goodsCount == 0) {
            ll_empty.setVisibility(View.VISIBLE);
            ll_content.setVisibility(View.GONE);
            ll_cart.removeAllViews();
        } else {
            ll_content.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        }
    }

    // 重新计算购物车中商品的结算总金额
    private void refreshTotalPrice() {
        int totalPrice = 0;
        for (TableCart info: mCartList) {
            TableGoods goods = mGoodsMap.get(info.getGood_id());
            totalPrice += goods.getPrice() * info.getCount();
        }
        tv_total_price.setText(String.valueOf(totalPrice));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            finish();
        }
        // 清空购物车
        else if (v.getId() == R.id.btn_clear) {
            // 清空购物车数据库
            tableDatabase.tableCartDao().deleteAllCartinfo();
            MyApplication.getInstance().goodsCount = 0;
            // 显示最新的商品数量
            showCount();
            ToastUtil.show(this, "购物车已清空");
        }
        else if (v.getId() == R.id.btn_settle) {
            // 点击结算按钮
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("结算商品");
            builder.setMessage("客观抱歉，支付功能尚未开通，请下次再来");
            builder.setPositiveButton("我知道了", null);
            builder.create().show();
        }
        else if (v.getId() == R.id.btn_shopping_channel) {
            Intent intent = new Intent(this, ShoppingChannelActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}