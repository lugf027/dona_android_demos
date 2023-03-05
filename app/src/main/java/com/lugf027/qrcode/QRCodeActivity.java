package com.lugf027.qrcode;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.lugf027.R;

public class QRCodeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int QR_CODE_SIZE = 500;
    //logo的尺寸不能高于二维码的20%.大于可能会导致二维码失效
    public static final int LOGO_WIDTH_MAX = QR_CODE_SIZE / 5;
    //logo的尺寸不能小于二维码的10%，否则不搭
    public static final int LOGO_WIDTH_MIN = QR_CODE_SIZE / 10;

    private View mGenerateBtn;
    private View mChangeStyleBtn;
    private View mSave2GalleryBtn;
    private ImageView mQRCodeImage;
    private TextInputEditText mQRCodeContentInput;
    private final Color startColor = Color.valueOf(Color.parseColor("#ccff00"));
    private final Color endColor = Color.valueOf(Color.parseColor("#00ff99"));
    private int curStyleIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        initView();
    }

    private void initView() {
        mGenerateBtn = findViewById(R.id.qr_generate_image_btn);
        mChangeStyleBtn = findViewById(R.id.qr_change_style_btn);
        mSave2GalleryBtn = findViewById(R.id.qr_save_to_gallery_btn);
        mQRCodeImage = findViewById(R.id.qr_code_iv);
        mQRCodeContentInput = findViewById(R.id.qr_input_key);
        mGenerateBtn.setOnClickListener(this);
        mChangeStyleBtn.setOnClickListener(this);
        mSave2GalleryBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mGenerateBtn) {
            handleGenerateBtnClick();
        } else if (view == mChangeStyleBtn) {
            handleChangeStyleBtnClick();
        } else if (view == mSave2GalleryBtn) {
            handleSave2GalleryBtnClick();
        }
    }

    private void handleSave2GalleryBtnClick() {
        Toast.makeText(this, "not impl, pl wait", Toast.LENGTH_SHORT).show();
    }

    private void handleChangeStyleBtnClick() {
        curStyleIndex = (curStyleIndex + 1) % 3;
        handleGenerateBtnClick();
    }

    private void handleGenerateBtnClick() {
        if (mQRCodeContentInput.getText() == null) {
            Toast.makeText(this, "input invalid - no text", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = mQRCodeContentInput.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "input invalid - empty", Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap bitmap;
        if (curStyleIndex == 0) {
            bitmap = QRCodeUtils.generateColorfulBitmap(content, Math.min(mQRCodeImage.getMeasuredWidth(), mQRCodeImage.getMeasuredHeight()), startColor, endColor);
        } else if (curStyleIndex == 1) {
            bitmap = QRCodeUtils.generateDotBitmap(content, Math.min(mQRCodeImage.getMeasuredWidth(), mQRCodeImage.getMeasuredHeight()), startColor, endColor);
        } else {
            bitmap = QRCodeUtilsV2.generateDotBitmap(content, Math.min(mQRCodeImage.getMeasuredWidth(), mQRCodeImage.getMeasuredHeight()), startColor, endColor);
        }
        mQRCodeImage.setBackground(new BitmapDrawable(mQRCodeImage.getResources(), bitmap));
    }
}