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
        mQRCodeImage.setBackground(generateDrawable(this, content));
    }

    private static Drawable generateDrawable(Context context, String content) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        //定义属性
        HashMap<EncodeHintType, Object> hintTypeStringMap = new HashMap<>();
        hintTypeStringMap.put(EncodeHintType.MARGIN, 0);
        hintTypeStringMap.put(EncodeHintType.CHARACTER_SET, "utf8");
        hintTypeStringMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);//设置最高错误级别
        hintTypeStringMap.put(EncodeHintType.MAX_SIZE, LOGO_WIDTH_MAX); //设置最大值
        hintTypeStringMap.put(EncodeHintType.MIN_SIZE, LOGO_WIDTH_MIN); // 设置最小值

        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE, hintTypeStringMap);
            int[] arr = new int[QR_CODE_SIZE * QR_CODE_SIZE];

            for (int i = 0; i < QR_CODE_SIZE; i++) {
                for (int j = 0; j < QR_CODE_SIZE; j++) {
                    if (bitMatrix.get(i, j)) {
                        arr[i * QR_CODE_SIZE + j] = Color.BLACK;
                    } else {
                        arr[i * QR_CODE_SIZE + j] = Color.WHITE;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(arr, QR_CODE_SIZE, QR_CODE_SIZE, Bitmap.Config.ARGB_8888);
            return new BitmapDrawable(context.getResources(), bitmap);
        } catch (WriterException e) {
            Toast.makeText(context, "generateDrawable e:" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return null;
    }
}