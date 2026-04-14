package com.example.secondjava;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.secondjava.Util.ToastUtil;

public class SpinnerDropdownActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String[] officeArray = {"内科", "外科", "眼科", "耳鼻喉科", "口腔科"};
    private Spinner sp_dropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_spinner_dropdown);
        sp_dropdown = findViewById(R.id.sp_dropdown);
        // 声明一个下拉列表的数组适配器
        ArrayAdapter<String> startAdapter = new ArrayAdapter<>(this, R.layout.item_select, officeArray);
        sp_dropdown.setPrompt("请选择科室");
        sp_dropdown.setAdapter(startAdapter);
        // 设置下拉列表显示第一项
        sp_dropdown.setSelection(0);
        // 给下拉框设置选择监听器，一旦用户选择某一项，就触发监听器的onItemSelected方法
        sp_dropdown.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ToastUtil.show(this, "您选择的是" + officeArray[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}