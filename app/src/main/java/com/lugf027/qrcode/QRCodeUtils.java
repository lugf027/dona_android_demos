package com.lugf027.qrcode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Xfermode;
import androidx.annotation.Nullable;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;

public class QRCodeUtils {
    /**
     * 圆点二维码
     */
    @Nullable
    public static Bitmap generateColorfulBitmap1(String content, int size, Color startColor, Color endColor) {
        try {
            BitMatrix bitMatrix = createBitMatrix(content, 0);
            Bitmap qrCodeBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            drawRectBitMapImpl(qrCodeBitmap, bitMatrix, size, startColor, endColor);
            return qrCodeBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void drawRectBitMapImpl(Bitmap qrCodeBitmap, BitMatrix bitMatrix, int size, Color startColor, Color endColor) {
        Canvas canvas = new Canvas(qrCodeBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new LinearGradient(0f, 0f, (float) size, (float) size, startColor.toArgb(), endColor.toArgb(), Shader.TileMode.CLAMP));

        drawRectImpl(canvas, paint, bitMatrix, size);
        canvas.drawBitmap(qrCodeBitmap, null, new Rect(0, 0, size, size), null);
    }

    // 绘制三个定位角以外的小圆点们
    private static void drawRectImpl(Canvas canvas, Paint paint, BitMatrix bitMatrix, int size) {
        int matrixSize = bitMatrix.getWidth();
        float dotSize = size / (float) matrixSize;
        float rectLeft, rectTop;

        for (int row = 0; row < matrixSize; row++) {
            for (int column = 0; column < matrixSize; column++) {
                if (!bitMatrix.get(row, column)) {
                    continue;
                }
                rectLeft = row * dotSize;
                rectTop = column * dotSize;
                canvas.drawRect(rectLeft, rectTop, rectLeft + dotSize, rectTop + dotSize, paint);
            }
        }
    }

    /**
     * 渐变色二维码
     */
    @Nullable
    public static Bitmap generateColorfulBitmap(String content, int size, Color startColor, Color endColor) {
        try {
            BitMatrix bitMatrix = createBitMatrix(content, size);
            int[] arr = new int[size * size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (bitMatrix.get(i, j)) {
                        int r = (int) (255 * (startColor.red() - (startColor.red() - endColor.red()) / size * (j + 1)));
                        int g = (int) (255 * (startColor.green() - (startColor.green() - endColor.green()) / size * (j + 1)));
                        int b = (int) (255 * ((startColor.blue() - (startColor.blue() - endColor.blue()) / size * (j + 1))));
                        int colorInt = Color.argb(255, r, g, b);
                        arr[i * size + j] = bitMatrix.get(i, j) ? colorInt : 16777215;// 0x000000:0xffffff
                    } else {
                        arr[i * size + j] = Color.TRANSPARENT;
                    }
                }
            }
            return Bitmap.createBitmap(arr, size, size, Bitmap.Config.ARGB_8888);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static BitMatrix createBitMatrix(String content, int size) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        //定义属性
        HashMap<EncodeHintType, Object> hintTypeStringMap = new HashMap<>();
        hintTypeStringMap.put(EncodeHintType.MARGIN, 0);
        hintTypeStringMap.put(EncodeHintType.CHARACTER_SET, "utf8");
        hintTypeStringMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);//设置最高错误级别
        if (size > 0) {
            hintTypeStringMap.put(EncodeHintType.MAX_SIZE, size / 5); //设置最大值
            hintTypeStringMap.put(EncodeHintType.MIN_SIZE, size / 10); // 设置最小值
        }
        return qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, size, size, hintTypeStringMap);
    }
}
