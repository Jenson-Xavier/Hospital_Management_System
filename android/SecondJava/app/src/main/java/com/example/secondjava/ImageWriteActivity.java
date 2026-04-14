package com.example.secondjava;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.secondjava.Util.FileUtil;
import com.example.secondjava.Util.ToastUtil;

import java.io.File;

public class ImageWriteActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_content;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_image_write);

        iv_content = findViewById(R.id.iv_content);

        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.btn_read).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_save) {
            String fileName = System.currentTimeMillis() + ".jpeg";
            // 获取外部存储的私有存储空间
            path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + File.separatorChar + fileName;
            // 从指定资源文件中获取位图对象
            Bitmap b1 = BitmapFactory.decodeResource(getResources(), R.drawable.rongyao);
            // 把位图对象保存为完整图片
            FileUtil.saveImage(path, b1);
            ToastUtil.show(this, "保存成功");
        }
        else if (v.getId() == R.id.btn_read) {
//            Bitmap b2 = FileUtil.openImage(path);
//            iv_content.setImageBitmap(b2);

//            Bitmap b2 = BitmapFactory.decodeFile(path);
//            iv_content.setImageBitmap(b2);

            // 直接调用setImageURI方法，设置图像视图的路径对象
            iv_content.setImageURI(Uri.parse(path));

            ToastUtil.show(this, "读取成功");
        }
    }
}