package com.example.secondjava;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;

import com.example.secondjava.Util.ToastUtil;
import com.example.secondjava.adapter.ImagePagerAdapter;
import com.example.secondjava.enity.TableGoods;

import java.util.ArrayList;

public class PageTabActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ArrayList<TableGoods> mGoodsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_page_tab);

        initPagerStrip();
        initViewPager();
    }

    // 初始化翻页视图
    private void initViewPager() {
        ViewPager vp_content = findViewById(R.id.vp_content);
        mGoodsList = TableGoods.getDefaultList();
        ImagePagerAdapter adapter = new ImagePagerAdapter(this, mGoodsList);
        vp_content.setAdapter(adapter);
        // 给翻页视图添加页面变更适配器
        vp_content.addOnPageChangeListener(this);
        vp_content.setCurrentItem(3);
    }

    // 初始化翻页标签栏
    private void initPagerStrip() {
        PagerTabStrip pts_tab = findViewById(R.id.pts_tab);
        // 设置标签栏的文本大小
        pts_tab.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        pts_tab.setTextColor(Color.BLACK);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        ToastUtil.show(this, "您翻到的手机品牌是：" + mGoodsList.get(position).getName());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}