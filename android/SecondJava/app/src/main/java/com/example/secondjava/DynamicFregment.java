package com.example.secondjava;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class DynamicFregment extends Fragment {

    public static DynamicFregment newInstance(int position, int image_id, String desc) {
        DynamicFregment fragment = new DynamicFregment();
        // 把参数打包，传入Fragment中
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putInt("image_id", image_id);
        args.putString("desc", desc);
        fragment.setArguments(args);
        return fragment;
    }

    // 创建碎片视图
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 根据布局文件生成视图对象
        View view = inflater.inflate(R.layout.fragment_dynamic_fregment, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            ImageView iv_pic = view.findViewById(R.id.iv_pic);
            TextView tv_desc = view.findViewById(R.id.tv_desc);
            iv_pic.setImageResource(arguments.getInt("image_id", R.drawable.huawei));
            tv_desc.setText(arguments.getString("desc"));
        }
        return view;
    }
}