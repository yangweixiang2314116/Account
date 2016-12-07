package com.ywxzhuangxiula.account;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


public class AddView extends View {

    protected Paint paint;
    protected int HstartX, HstartY, HendX, HendY;
    protected int SstartX, SstartY, SsendX, SsendY;
    protected int paintWidth = 2;
    protected int paintColor = Color.BLACK;
    protected int padding = 3;

    public int getPadding() {
        return padding;
    }

    public void setPadding(int value) {
        padding = value;
        SsendY = HendX = this.getWidth() - padding;
        SstartY = HstartX = padding;
    }

    public void setPaintColor(int paintColor) {
        paint.setColor(paintColor);
    }

    public void setPaintWidth(int paintWidth) {
        paint.setStrokeWidth(paintWidth);
    }

    public AddView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        paint = new Paint();
        paint.setColor(paintColor);
        paint.setStrokeWidth(paintWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = 60;
        }
        SstartX = SsendX = HstartY = HendY = width / 2;
        SsendY = HendX = width - getPadding();
        SstartY = HstartX = getPadding();
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(HstartX, HstartY, HendX, HendY, paint);
        canvas.drawLine(SstartX, SstartY, SsendX, SsendY, paint);
    }
}
