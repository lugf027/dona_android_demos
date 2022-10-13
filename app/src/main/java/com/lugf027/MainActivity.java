package com.lugf027;

import android.content.Intent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.lugf027.drawables.DrawablesActivity;
import com.lugf027.qrcode.QRCodeActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private View mDrawableContainer;
    private View mQRCodeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mDrawableContainer = findViewById(R.id.main_drawable_container);
        mDrawableContainer.setOnClickListener(this);
        mQRCodeContainer = findViewById(R.id.main_qrcode_container);
        mQRCodeContainer.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == mDrawableContainer) {
            handleDrawableViewClick();
        } else if (view == mQRCodeContainer) {
            handleQRCodeViewClick();
        }
    }

    private void handleQRCodeViewClick() {
        Intent intent = new Intent();
        intent.setClass(this, QRCodeActivity.class);
        startActivity(intent);
    }

    private void handleDrawableViewClick() {
        Intent intent = new Intent();
        intent.setClass(this, DrawablesActivity.class);
        startActivity(intent);
    }
}