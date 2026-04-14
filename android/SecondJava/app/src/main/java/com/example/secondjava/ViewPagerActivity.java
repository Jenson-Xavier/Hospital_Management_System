package com.example.secondjava;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.secondjava.Util.ToastUtil;
import com.example.secondjava.adapter.ImagePagerAdapter;
import com.example.secondjava.enity.TableGoods;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ViewPagerActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ViewPager vp_content;
    private ArrayList<TableGoods> mGoodsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_pager);

        vp_content = findViewById(R.id.vp_content);
        mGoodsList = TableGoods.getDefaultList();
        ImagePagerAdapter adapter = new ImagePagerAdapter(this, mGoodsList);
        vp_content.setAdapter(adapter);
        vp_content.addOnPageChangeListener(this);
    }

    // 在翻页过程中触发
    // 1.当前页面的序号 2.页面偏移的百分比 3.页面的偏移距离
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    // 在翻页结束后触发, position表示当前滑到了哪一个页面
    @Override
    public void onPageSelected(int position) {
        ToastUtil.show(this, "您翻到的手机品牌是：" + mGoodsList.get(position).getName());
    }

    // 翻页状态改变时触发，state取值说明：0表示静止，1表示正在滑动，2表示滑动完毕
    // 在翻页过程中，状态值变化依次为：正在滑动->滑动完毕->静止
    @Override
    public void onPageScrollStateChanged(int state) {

    }
}