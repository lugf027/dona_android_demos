package com.lugf027.qrcode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
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

public class QRCodeUtilsV2 {
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

        for (int column = 0; column < matrixSize; column++) {
            for (int row = 0; row < matrixSize; row++) {
                if (column <= 6 && row <= 6 || column <= 6 && row >= matrixSize - 7
                        || column >= matrixSize - 7 && row <= 6) {
                    // 左上角、右上角、左下角，不绘制小圆点
                    continue;
                }
                curDotCenterX = column * dotSize + dotRadius;
                curDotCenterY = row * dotSize + dotRadius;
                if (bitMatrix.get(column, row)) {
                    boolean needDrawCircle = drawRectWhenNeeded(canvas, paint, bitMatrix, curDotCenterX, curDotCenterY, dotRadius, column, row);
                    if (needDrawCircle) {
                        canvas.drawCircle(curDotCenterX, curDotCenterY, dotRadius, paint);
                    }

                } else {
                    drawRoundEdgeWhenNeeded(canvas, paint, bitMatrix, curDotCenterX, curDotCenterY, dotRadius, column, row);
                }
            }
        }
    }

    /**
     * 绘制矩形
     *
     * @return 是否还要绘制圆形
     */
    private static boolean drawRectWhenNeeded(Canvas canvas, Paint paint, BitMatrix bitMatrix, float centX, float centY,
                                              float dotRadius, int column, int row) {
        int matrixSize = bitMatrix.getWidth();
        boolean isLeftEmpty = column == 0 || !bitMatrix.get(column - 1, row);
        boolean isRightEmpty = column == matrixSize - 1 || !bitMatrix.get(column + 1, row);
        boolean isTopEmpty = row == 0 || !bitMatrix.get(column, row - 1);
        boolean isBottomEmpty = row == matrixSize - 1 || !bitMatrix.get(column, row + 1);
        if (!isLeftEmpty && !isRightEmpty || !isTopEmpty && !isBottomEmpty) {
            canvas.drawRect(centX - dotRadius, centY - dotRadius, centX + dotRadius, centY + dotRadius, paint);
            return false;
        }

        if (!isLeftEmpty) {
            canvas.drawRect(centX - dotRadius, centY - dotRadius, centX, centY + dotRadius, paint);
        }
        if (!isRightEmpty) {
            canvas.drawRect(centX + dotRadius, centY - dotRadius, centX, centY + dotRadius, paint);
        }

        if (!isTopEmpty) {
            canvas.drawRect(centX - dotRadius, centY - dotRadius, centX + dotRadius, centY, paint);
        }
        if (!isBottomEmpty) {
            canvas.drawRect(centX - dotRadius, centY + dotRadius, centX + dotRadius, centY, paint);
        }
        return true;
    }

    // 需要时，绘制矩形与圆重合，矩形有、圆没有的部分。适用于"L"这种拐角内部，拐角内部画个弧度
    private static void drawRoundEdgeWhenNeeded(Canvas canvas, Paint paint, BitMatrix bitMatrix, float centX, float centY,
                                                float dotRadius, int column, int row) {
        int matrixSize = bitMatrix.getWidth();
        float halfDotRadius = dotRadius / 2;
        boolean isLeftEmpty = column == 0 || !bitMatrix.get(column - 1, row);
        boolean isTopEmpty = row == 0 || !bitMatrix.get(column, row - 1);
        boolean isRightEmpty = column == matrixSize - 1 || !bitMatrix.get(column + 1, row);
        boolean isBottomEmpty = row == matrixSize - 1 || !bitMatrix.get(column, row + 1);

        boolean isLeftTopEmpty = row == 0 || column == 0 || !bitMatrix.get(column - 1, row - 1);
        boolean isLeftBottomEmpty = row == matrixSize - 1 || column == 0 || !bitMatrix.get(column - 1, row + 1);
        boolean isRightTopEmpty = row == 0 || column == matrixSize - 1 || !bitMatrix.get(column + 1, row - 1);
        boolean isRightBottomEmpty = row == matrixSize - 1 || column == matrixSize - 1 || !bitMatrix.get(column + 1, row + 1);

        if (!isLeftTopEmpty && !isLeftEmpty && !isTopEmpty) {
            RectF rectF = new RectF(centX - dotRadius, centY - dotRadius, centX - halfDotRadius, centY - halfDotRadius);
            drawRectWithCircleClear(canvas, paint, rectF, centX - halfDotRadius, centY - halfDotRadius, halfDotRadius);
        }
        if (!isLeftBottomEmpty && !isLeftEmpty && !isBottomEmpty) {
            RectF rectF = new RectF(centX - dotRadius, centY + halfDotRadius, centX - halfDotRadius, centY + dotRadius);
            drawRectWithCircleClear(canvas, paint, rectF, centX - halfDotRadius, centY + halfDotRadius, halfDotRadius);
        }
        if (!isRightTopEmpty && !isRightEmpty && !isTopEmpty) {
            RectF rectF = new RectF(centX + halfDotRadius, centY - dotRadius, centX + dotRadius, centY - halfDotRadius);
            drawRectWithCircleClear(canvas, paint, rectF, centX + halfDotRadius, centY - halfDotRadius, halfDotRadius);
        }
        if (!isRightBottomEmpty && !isRightEmpty && !isBottomEmpty) {
            RectF rectF = new RectF(centX + halfDotRadius, centY + halfDotRadius, centX + dotRadius, centY + dotRadius);
            drawRectWithCircleClear(canvas, paint, rectF, centX + halfDotRadius, centY + halfDotRadius, halfDotRadius);
        }
    }

    // 绘制矩形与圆重合，矩形有、圆没有的部分。适用于"L"这种拐角内部，拐角内部画个弧度
    private static void drawRectWithCircleClear(Canvas canvas, Paint paint, RectF rectF, float centX, float centY, float radius) {
        canvas.drawRect(rectF, paint);
        Xfermode paintXfermode = paint.getXfermode();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawCircle(centX, centY, radius, paint);
        paint.setXfermode(paintXfermode);
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
    private static void drawOneDetectPositionDotImpl(Canvas canvas, Paint paint, float dotCentX, float dotCentY, float perDotSize) {
        Xfermode paintXfermode = paint.getXfermode();
        float maxRadius = perDotSize * 7 / 2;
        canvas.drawRoundRect(dotCentX - maxRadius, dotCentY - maxRadius, dotCentX + maxRadius, dotCentY + maxRadius,
                (float)( perDotSize * 1.7), (float)( perDotSize * 1.7), paint);

        // 重合的地方混合方式，显示空白 https://www.jianshu.com/p/d11892bbe055
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        float midRadius = perDotSize * 5 / 2;
        canvas.drawRoundRect(dotCentX - midRadius, dotCentY - midRadius, dotCentX + midRadius, dotCentY + midRadius,
                perDotSize, perDotSize, paint);

        paint.setXfermode(paintXfermode);
        canvas.drawCircle(dotCentX, dotCentY, perDotSize * 3 / 2, paint);
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
