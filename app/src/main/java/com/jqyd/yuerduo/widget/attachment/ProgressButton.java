package com.jqyd.yuerduo.widget.attachment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jqyd.yuerduo.R;

/**
 * 显示进度的button
 */
@SuppressLint("DrawAllocation")
public class ProgressButton extends View {
    private Paint.FontMetrics fm;
    private long progress = 0;
    private int textColor = Color.WHITE;
    private Paint paint;
    private float textSize = 10;
    private int foreground;
    private int backgroundColor;
    private int pressedColor;
    private String text;
    private long max = 100;
    private int corner = 5;// 圆角的弧度
    private OnProgressButtonClickListener buttonClickListener;
    private boolean isActionDown;

    public ProgressButton(Context context) {
        super(context);
        init(context, null);
    }

    public ProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ProgressButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressButton);
        this.backgroundColor = typedArray.getInteger(R.styleable.ProgressButton_backgroundColor, Color.parseColor("#C6C6C6"));
        this.foreground = typedArray.getInteger(R.styleable.ProgressButton_foreground, Color.rgb(20, 131, 214));
        this.pressedColor = typedArray.getInteger(R.styleable.ProgressButton_pressedColor, Color.argb(120, 20, 131, 214));
        this.textColor = typedArray.getInteger(R.styleable.ProgressButton_textcolor, Color.WHITE);
        this.max = typedArray.getInteger(R.styleable.ProgressButton_max, 100);
        this.progress = typedArray.getInteger(R.styleable.ProgressButton_progress, 0);
        this.text = typedArray.getString(R.styleable.ProgressButton_text);
        this.textSize = typedArray.getDimension(R.styleable.ProgressButton_textSize, 20);
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        Paint paint1 = new Paint();
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setAntiAlias(true);
        paint1.setStrokeWidth(5);
        paint1.setColor(this.foreground);
        /**
         * 绘制背景
         */
        RectF oval = new RectF(0, 0, getWidth(), getHeight());

        if (isActionDown) {
            paint.setColor(this.pressedColor);
        } else {
            paint.setColor(this.backgroundColor);
        }
        canvas.drawRoundRect(oval, corner, corner, paint);
        canvas.drawRoundRect(oval, corner, corner, paint1);

        /***
         * 绘制进度值
         */

        paint.setColor(foreground);
        if (progress <= corner) {
            oval = new RectF(0, corner - progress, getWidth() * this.progress / this.max, getHeight()
                    - corner + progress);
            canvas.drawRoundRect(oval, progress, progress, paint);
        } else {
            oval = new RectF(0, 0, getWidth() * this.progress / this.max, getHeight());
            canvas.drawRoundRect(oval, corner, corner, paint);
        }

        /***
         * 绘制文本
         */
        if ("".equals(text) || text == null) {
            return;
        }
        paint.setTextSize(this.textSize);
        fm = paint.getFontMetrics();
        paint.setColor(this.textColor);

        float textCenterVerticalBaselineY = getHeight() / 2 - fm.descent + (fm.descent - fm.ascent) / 2;
        canvas.drawText(this.text, (getMeasuredWidth() - paint.measureText(this.text)) / 2, textCenterVerticalBaselineY,
                paint);
    }

    /**
     * 设置最大值
     *
     * @param max
     */
    public void setMax(long max) {
        this.max = max;
    }

    /**
     * 设置文本提示信息
     *
     * @param text
     */
    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    public String getText() {
        return text;
    }

    /**
     * 设置进度条的颜色值
     *
     * @param color
     */
    public void setForeground(int color) {
        this.foreground = color;
    }

    /**
     * 设置进度条的背景色
     */
    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * 点击时的颜色
     *
     * @return
     */
    public int getPressedColor() {
        return pressedColor;
    }

    /**
     * 设置点击时的颜色
     *
     * @param pressedColor
     */
    public void setPressedColor(int pressedColor) {
        this.pressedColor = pressedColor;
    }

    /***
     * 设置文本的大小
     */
    public void setTextSize(int size) {
        this.textSize = size;
    }

    /**
     * 设置文本的颜色值
     *
     * @param color
     */
    public void setTextColor(int color) {
        this.textColor = color;
    }

    /**
     * 设置进度值
     *
     * @param progress
     */
    public void setProgress(long progress) {
        if (progress > max) {
            return;
        }
        this.progress = progress;
        //设置进度之后，要求UI强制进行重绘
        postInvalidate();
    }

    public long getMax() {
        return max;
    }

    public long getProgress() {
        return progress;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (buttonClickListener != null) {
                    buttonClickListener.onClickListener();
                }
                isActionDown = false;
                invalidate();
                break;
            case MotionEvent.ACTION_DOWN:
                isActionDown = true;
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    public void setOnProgressButtonClickListener(OnProgressButtonClickListener clickListener) {
        buttonClickListener = clickListener;
    }

    public interface OnProgressButtonClickListener {
        void onClickListener();
    }
}
