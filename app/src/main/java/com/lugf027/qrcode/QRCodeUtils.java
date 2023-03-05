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
    public static Bitmap generateDotBitmap(String content, int size, Color startColor, Color endColor) {
        try {
            BitMatrix bitMatrix = createBitMatrix(content, 0);
            Bitmap qrCodeBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            drawDotBitMap(qrCodeBitmap, bitMatrix, size, startColor, endColor);
            return qrCodeBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void drawDotBitMap(Bitmap qrCodeBitmap, BitMatrix bitMatrix, int size, Color startColor, Color endColor) {
        Canvas canvas = new Canvas(qrCodeBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new LinearGradient(0f, 0f, (float) size, (float) size, startColor.toArgb(), endColor.toArgb(), Shader.TileMode.CLAMP));

        drawInnerLittleDot(canvas, paint, bitMatrix, size);
        drawOuterBitDot(canvas, paint, bitMatrix, size);
        canvas.drawBitmap(qrCodeBitmap, null, new Rect(0, 0, size, size), null);
    }

    private static void drawInnerLittleDot(Canvas canvas, Paint paint, BitMatrix bitMatrix, int size) {
        int matrixSize = bitMatrix.getWidth();
        float dotSize = size / (float) matrixSize;
        float dotRadius = dotSize / 2;
        float curDotX;
        float curDotY;

        for (int row = 0; row < matrixSize; row++) {
            for (int column = 0; column < matrixSize; column++) {
                if (!bitMatrix.get(row, column)) {
                    continue;
                }
                if (row <= 6 && column <= 6
                        || row <= 6 && column >= matrixSize - 7
                        || row >= matrixSize - 7 && column <= 6) {
                    // 左上角、右上角、左下角
                    continue;
                }
                curDotX = row * dotSize + dotRadius;
                curDotY = column * dotSize + dotRadius;
                canvas.drawCircle(curDotX, curDotY, dotRadius, paint);
            }
        }
    }

    private static void drawOuterBitDot(Canvas canvas, Paint paint, BitMatrix bitMatrix, int size) {
        float perDotSize = size / (float) bitMatrix.getWidth();
        float totalRadius = perDotSize * 7 / 2;
        drawOneOuterBitDot(canvas, paint, totalRadius, totalRadius, perDotSize);
        drawOneOuterBitDot(canvas, paint, totalRadius, size - totalRadius, perDotSize);
        drawOneOuterBitDot(canvas, paint, size - totalRadius, totalRadius, perDotSize);
    }

    private static void drawOneOuterBitDot(Canvas canvas, Paint paint, float dotCenterX, float dotCentY, float perDotSize) {
        int oldAlpha = paint.getAlpha();
        Xfermode paintXfermode = paint.getXfermode();
        canvas.drawCircle(dotCenterX, dotCentY, perDotSize * 7 / 2, paint);

        paint.setAlpha(0);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawCircle(dotCenterX, dotCentY, perDotSize * 5 / 2, paint);

        paint.setAlpha(oldAlpha);
        paint.setXfermode(paintXfermode);
        canvas.drawCircle(dotCenterX, dotCentY, perDotSize * 3 / 2, paint);
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
