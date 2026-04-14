package com.example.secondjava.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.secondjava.DynamicFregment;
import com.example.secondjava.enity.TableGoods;

import java.util.List;

public class MobilePagerAdapter extends FragmentPagerAdapter {

    private final List<TableGoods> mGoodList;

    public MobilePagerAdapter(@NonNull FragmentManager fm, List<TableGoods> goodsList) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mGoodList = goodsList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        TableGoods info = mGoodList.get(position);
        return DynamicFregment.newInstance(position, info.getPic(), info.getDescription());
    }

    @Override
    public int getCount() {
        return mGoodList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mGoodList.get(position).getName();
    }
}
