package com.example.secondjava;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.secondjava.Util.ToastUtil;
import com.example.secondjava.adapter.PhoneBaseAdapter;
import com.example.secondjava.enity.TableGoods;

import java.util.List;

public class BaseAdapterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private List<TableGoods> goodsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_base_adapter);

        Spinner sp_phone = findViewById(R.id.sp_phone);
        goodsList = TableGoods.getDefaultList();
        // 构建适配器
        PhoneBaseAdapter adapter = new PhoneBaseAdapter(this, goodsList);
        sp_phone.setAdapter(adapter);
        sp_phone.setSelection(0);
        sp_phone.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        ToastUtil.show(this, "您选择的是:" + goodsList.get(i).getName());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}