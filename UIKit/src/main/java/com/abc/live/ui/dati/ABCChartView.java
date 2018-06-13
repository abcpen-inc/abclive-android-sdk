package com.abc.live.ui.dati;

/**
 * Created by shaoxiaoze on 2017/10/24.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.abc.live.R;
import com.liveaa.livemeeting.sdk.util.BitmapUtil;

import java.util.ArrayList;

public class ABCChartView extends View {

    Paint mChartPaint = new Paint();
    Paint mChartBarPaint = new Paint();
    Paint mHumanNumPaint = new Paint();
    Paint mPercentPaint = new Paint();
    Paint mCharacterPaint = new Paint();

    public final Point bottomPoint = new Point();
    public final Point optionNamePoint = new Point();
    public float margin = 40.0f;
    public float marginy = 60.0f;

    public float rectWidth = 45.0f;
    public float gapWidth = 0.0f;

    public static final int triangleSize = 20;

    private float dipScale = 1.0f;

    private int totalCount = 0;

    public ABCChartView(Context context) {
        super(context, null);
        init();
    }

    public ABCChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        dipScale = BitmapUtil.dip2px(this.getContext(), .6f);
        /**
         * 图表线绘制
         */
        mChartPaint.setColor(getResources().getColor(R.color.abc_g3_70));
        mChartPaint.setStrokeWidth(2.0f);
        mChartPaint.setTextSize(24 * dipScale);
        mChartPaint.setStyle(Paint.Style.FILL);
        mChartPaint.setAntiAlias(true);

        rectWidth = 45 * dipScale;
        margin = 80 * dipScale;

        /**
         * 柱状图
         */
        mChartBarPaint.setColor(getResources().getColor(R.color.abc_grey));
        mChartBarPaint.setAntiAlias(true);
        mChartBarPaint.setStyle(Paint.Style.FILL);

//        mHumanNumCorrentPaint.setColor(getResources().getColor(R.color.abc_w1));
//        mHumanNumCorrentPaint.setTextSize(18);
//        mHumanNumCorrentPaint.setAntiAlias(true);

//        mHumanNumPaint.setColor(getResources().getColor(R.color.abc_g3));
        /**
         * 人数绘制
         */
        mHumanNumPaint.setTextSize(18 * dipScale);
//        mHumanNumPaint.setColor(getResources().getColor(R.color.abc_g3));
        mHumanNumPaint.setAntiAlias(true);
        mHumanNumPaint.setTextAlign(Paint.Align.CENTER);

        mPercentPaint.setColor(getResources().getColor(R.color.abc_b2));
        mPercentPaint.setAntiAlias(true);
        mPercentPaint.setTextSize(26 * dipScale);
        mPercentPaint.setColor(getResources().getColor(R.color.abc_b2));
        mPercentPaint.setTextAlign(Paint.Align.CENTER);
        /**
         * 底部选项
         */
        mCharacterPaint.setAntiAlias(true);
        mCharacterPaint.setTextSize(28 * dipScale);
        mCharacterPaint.setTextAlign(Paint.Align.CENTER);

    }

    private ArrayList<String> optionsName = new ArrayList<>();
    private ArrayList<Integer> optionsPeople = new ArrayList<>();
    private ArrayList<String> correctName = new ArrayList<>();

    /*
     * 圆心（坐标值是相对与控件的左上角的）
     */
    Point po = new Point();
    /*
     * 控件的中心点
     */
    int centerX, centerY;

    /*
     */
    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        centerX = w / 2;
        centerY = h / 2;
        po.set(centerX, centerY);
        bottomPoint.set(w, h - (int) (36 * dipScale));
        optionNamePoint.set(0, h - (int) (56 * dipScale) + (int) (marginy));
        gapWidth = (w - optionsName.size() * rectWidth - 2 * margin - 20) / 6.0f;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /*
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画坐标轴
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            // 画直线
            final float rightEndX = 2 * centerX - triangleSize * 1.73f * 0.5f - margin;
            final float leftStartX = triangleSize * 0.5f + margin;
            canvas.drawLine(leftStartX, bottomPoint.y, rightEndX, bottomPoint.y, mChartPaint);//绘制x轴
            drawTriangle(canvas, new Point((int) rightEndX, (int) (bottomPoint.y + triangleSize * 0.5f)),
                    new Point((int) (rightEndX), (int) (bottomPoint.y - triangleSize * 0.5f)),
                    new Point((int) (2 * centerX - margin), bottomPoint.y));

            canvas.drawLine(leftStartX, bottomPoint.y, leftStartX, triangleSize * 1.73f * 0.5f + marginy, mChartPaint);
            drawTriangle(canvas, new Point((int) (leftStartX), (int) marginy), new Point((int) margin, triangleSize + (int) marginy),
                    new Point((int) (triangleSize + margin), (int) (triangleSize + marginy)));

            for (int i = 0; i < optionsName.size(); i++) {
                final float centerix = (i + 1) * (rectWidth + gapWidth) - 0.5f * rectWidth;
                //画底部的选项文字
                String option = optionsName.get(i);
                mCharacterPaint.setColor(isCorrect(option) ? (getResources().getColor(R.color.abc_g4))
                        : (getResources().getColor(R.color.abc_b1)));
                drawText(optionsName.get(i), canvas, centerix, bottomPoint.y + 3 * dipScale, 28 * dipScale, mCharacterPaint);
            }

            for (int i = 0; i < optionsName.size(); i++) {
                final float centerix = (i + 1) * (rectWidth + gapWidth) - 0.5f * rectWidth;
                String option = optionsName.get(i);
                int percent = 0;
                if (totalCount != 0) {
                    percent = (int) (optionsPeople.get(i) / (totalCount * 1.0f) * 100);
                }
                float topY = (bottomPoint.y - marginy) * (100 - percent) * 0.01f;
                RectF targetRect = new RectF(centerix - rectWidth * 0.5f + margin, topY + marginy,
                        centerix + rectWidth * 0.5f + margin, bottomPoint.y);
                mChartBarPaint.setColor(isCorrect(option) ? getResources().getColor(R.color.abc_g4) : getResources().getColor(R.color.abc_grey));
                drawRect(canvas, targetRect, mChartBarPaint);
                drawText(percent + "%", canvas, centerix, topY - 30 * dipScale + marginy, 26 * dipScale, mPercentPaint);
            }

            for (int i = 0; i < optionsPeople.size(); i++) {
                final float centerix = (i + 1) * (rectWidth + gapWidth) - 0.5f * rectWidth;
                //画人数
                int num = optionsPeople.get(i);
                String option = optionsName.get(i);
                mHumanNumPaint.setColor(isCorrect(option) ? (getResources().getColor(R.color.abc_g4))
                        : (getResources().getColor(R.color.abc_g3)));
                drawText(String.format("(%d人)", num), canvas, centerix + 36 * dipScale, bottomPoint.y + 12 * dipScale, 18 * dipScale, mHumanNumPaint);
            }

            drawText(getResources().getString(R.string.abc_percent), canvas, -margin, marginy, 24 * dipScale, mChartPaint);
            drawText(getResources().getString(R.string.abc_options), canvas, rightEndX - margin * 0.8f, bottomPoint.y - 8 * dipScale, 24 * dipScale, mChartPaint);

        }
    }

    private void drawRect(final Canvas canvas, final RectF targetRect, Paint paint) {
        canvas.drawRect(targetRect, paint);
    }

    private boolean isCorrect(String option) {
        for (int i = 0; i < correctName.size(); i++) {
            final String name = correctName.get(i);
            if (name.equals(option))
                return true;
        }
        return false;
    }

    private void drawTriangle(Canvas canvas, Point p1, Point p2, Point p3) {
        Path path = new Path();
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);
        path.close();
        canvas.drawPath(path, mChartPaint);
    }


    private void drawText(final String text, Canvas canvas, final float centerX, final float y, final float boxHeight, final Paint paint) {
        RectF targetRect = new RectF(centerX - rectWidth * 0.5f, y, centerX + rectWidth * 0.5f, y + boxHeight);
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        float baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        canvas.drawText(text, margin + targetRect.centerX(), baseline, paint);
    }

    public ArrayList<String> getOptionsName() {
        return optionsName;
    }

    public void setOptionsName(ArrayList<String> optionsName) {
        this.optionsName = optionsName;
    }

    public ArrayList<Integer> getOptionPeople() {
        return optionsPeople;
    }

    public void setOptionPeople(ArrayList<Integer> optionPeople) {
        this.optionsPeople = optionPeople;
    }

    public ArrayList<String> getCorrectName() {
        return correctName;
    }

    public void setCorrectName(ArrayList<String> correctName) {
        this.correctName = correctName;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}



