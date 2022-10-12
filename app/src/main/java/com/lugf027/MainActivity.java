package com.lugf027;

import android.content.Intent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.lugf027.drawables.DrawablesActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private View mDrawableContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mDrawableContainer = findViewById(R.id.main_drawable_container);
        mDrawableContainer.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == mDrawableContainer) {
            handleDrawableViewClick();
        }
    }

    private void handleDrawableViewClick() {
        Intent intent = new Intent();
        intent.setClass(this, DrawablesActivity.class);
        startActivity(intent);
    }
}