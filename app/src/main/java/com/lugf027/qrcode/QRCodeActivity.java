package com.lugf027.qrcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.lugf027.R;

import java.util.HashMap;

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
        Toast.makeText(this, "not impl, pl wait", Toast.LENGTH_SHORT).show();
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
        mQRCodeImage.setBackground(
                new BitmapDrawable(
                        mQRCodeImage.getResources(),
                        generateBitmap(content, mQRCodeImage.getMeasuredWidth(), mQRCodeImage.getMeasuredHeight())
                )
        );
    }

    @Nullable
    private static Bitmap generateBitmap(String content, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        //定义属性
        HashMap<EncodeHintType, Object> hintTypeStringMap = new HashMap<>();
        hintTypeStringMap.put(EncodeHintType.MARGIN, 0);
        hintTypeStringMap.put(EncodeHintType.CHARACTER_SET, "utf8");
        hintTypeStringMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);//设置最高错误级别
        hintTypeStringMap.put(EncodeHintType.MAX_SIZE, Math.min(width, height) / 5); //设置最大值
        hintTypeStringMap.put(EncodeHintType.MIN_SIZE, Math.min(width, height) / 10); // 设置最小值

        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hintTypeStringMap);
            int[] arr = new int[width * height];

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (bitMatrix.get(i, j)) {
                        int r = (int) (187 - (187.0 - 5) / height * (j + 1));
                        int g = (int) (227 - (227.0 - 85) / height * (j + 1));
                        int b = (int) (29 - (29.0 - 245) / height * (j + 1));
                        int colorInt = Color.argb(255, r, g, b);
                        arr[i * height + j] = bitMatrix.get(i, j) ? colorInt: 16777215;// 0x000000:0xffffff
                    } else {
                        arr[i * height + j] = Color.TRANSPARENT;
                    }
                }
            }
            return Bitmap.createBitmap(arr, width, height, Bitmap.Config.ARGB_8888);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}