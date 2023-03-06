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

public class QRCodeUtils1 {
    /**
     * 圆点二维码
     */
    @Nullable
    public static Bitmap generateDotBitmap(String content, int size, Color startColor, Color endColor) {
        try {
            BitMatrix bitMatrix = createBitMatrix(content, 0);
            Bitmap qrCodeBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            drawDotBitMapImpl(qrCodeBitmap, bitMatrix, size, startColor, endColor);
            return qrCodeBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void drawDotBitMapImpl(Bitmap qrCodeBitmap, BitMatrix bitMatrix, int size, Color startColor, Color endColor) {
        Canvas canvas = new Canvas(qrCodeBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new LinearGradient(0f, 0f, (float) size, (float) size, startColor.toArgb(), endColor.toArgb(), Shader.TileMode.CLAMP));

        drawInnerLittleDot(canvas, paint, bitMatrix, size);
        drawOuterDetectPositionBigDot(canvas, paint, bitMatrix, size);
        canvas.drawBitmap(qrCodeBitmap, null, new Rect(0, 0, size, size), null);
    }

    // 绘制三个定位角以外的小圆点们
    private static void drawInnerLittleDot(Canvas canvas, Paint paint, BitMatrix bitMatrix, int size) {
        int matrixSize = bitMatrix.getWidth();
        float dotSize = size / (float) matrixSize;
        float dotRadius = dotSize / 2;
        float curDotCenterX, curDotCenterY;

        for (int row = 0; row < matrixSize; row++) {
            for (int column = 0; column < matrixSize; column++) {
                if (!bitMatrix.get(row, column)) {
                    continue;
                }
                if (row <= 6 && column <= 6 || row <= 6 && column >= matrixSize - 7 || row >= matrixSize - 7 && column <= 6) {
                    // 左上角、右上角、左下角，不绘制小圆点
                    continue;
                }
                curDotCenterX = row * dotSize + dotRadius;
                curDotCenterY = column * dotSize + dotRadius;
                canvas.drawCircle(curDotCenterX, curDotCenterY, dotRadius, paint);
            }
        }
    }

    // 绘制三个定位角
    private static void drawOuterDetectPositionBigDot(Canvas canvas, Paint paint, BitMatrix bitMatrix, int size) {
        float perDotSize = size / (float) bitMatrix.getWidth();
        float totalRadius = perDotSize * 7 / 2;
        drawOneDetectPositionDotImpl(canvas, paint, totalRadius, totalRadius, perDotSize);
        drawOneDetectPositionDotImpl(canvas, paint, totalRadius, size - totalRadius, perDotSize);
        drawOneDetectPositionDotImpl(canvas, paint, size - totalRadius, totalRadius, perDotSize);
    }

    // 绘制单个定位角。实现是依次绘制三个圆，且内部覆盖外部
    private static void drawOneDetectPositionDotImpl(Canvas canvas, Paint paint, float dotCenterX, float dotCentY, float perDotSize) {
        Xfermode paintXfermode = paint.getXfermode();
        canvas.drawCircle(dotCenterX, dotCentY, perDotSize * 7 / 2, paint);

        // 重合的地方混合方式，显示空白 https://www.jianshu.com/p/d11892bbe055
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawCircle(dotCenterX, dotCentY, perDotSize * 5 / 2, paint);

        paint.setXfermode(paintXfermode);
        canvas.drawCircle(dotCenterX, dotCentY, perDotSize * 3 / 2, paint);
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
