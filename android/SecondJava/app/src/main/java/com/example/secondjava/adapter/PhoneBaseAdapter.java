package com.example.secondjava.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.secondjava.R;
import com.example.secondjava.Util.ToastUtil;
import com.example.secondjava.enity.TableGoods;

import org.w3c.dom.Text;

import java.util.List;

public class PhoneBaseAdapter extends BaseAdapter {

    private Context mContext;
    private List<TableGoods> mGoodsList;

    public PhoneBaseAdapter(Context mContext, List<TableGoods> mPhoneList) {
        this.mContext = mContext;
        this.mGoodsList = mPhoneList;
    }

    @Override
    public int getCount() {
        return mGoodsList.size();
    }

    @Override
    public Object getItem(int i) {
        return mGoodsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mGoodsList.get(i).getId();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null){
            // 根据布局文件item_list.xml生成转换视图对象
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list, null);
            holder = new ViewHolder();
            holder.iv_icon = convertView.findViewById(R.id.iv_icon);
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            holder.tv_desc = convertView.findViewById(R.id.tv_desc);
            holder.btn_operate = convertView.findViewById(R.id.btn_operate);
            // 将视图持有者保存到转换视图中
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_name.setText(mGoodsList.get(i).getName());
        holder.tv_desc.setText(mGoodsList.get(i).getDescription());
        holder.iv_icon.setImageResource(mGoodsList.get(i).getPic());
        holder.btn_operate.setOnClickListener(v -> {
            ToastUtil.show(mContext, "按钮被点击了," + mGoodsList.get(i).getName());
        });
        return convertView;
    }


    public final class ViewHolder {
        public ImageView iv_icon;
        public TextView tv_name;
        public TextView tv_desc;
        public android.widget.Button btn_operate;
    }
}
