package com.example.secondjava;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.secondjava.Util.ToastUtil;
import com.example.secondjava.adapter.PhoneBaseAdapter;
import com.example.secondjava.enity.TableGoods;

import java.util.List;

public class ListViewActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private List<TableGoods> mGoodsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_view);

        ListView lv_phone = findViewById(R.id.lv_phone);
        mGoodsList = TableGoods.getDefaultList();
        PhoneBaseAdapter adapter = new PhoneBaseAdapter(this, mGoodsList);
        lv_phone.setAdapter(adapter);

        lv_phone.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ToastUtil.show(this, "您选择的是:" + mGoodsList.get(i).getName());
    }
}