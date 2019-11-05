package com.overall.cleanup.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.overall.cleanup.R;

import java.util.Locale;

public class GressOkesView extends View {
    Paint mPaint;
    Paint mTextPaint;
    Paint mCirPaint;
    Paint mCirLightPaint;
    RectF mRectF;
    int mProgress;
    float mVisualProgress;
    int mMax;
    int mOffsetAngle;
    float mTextSize;
    String mUnitText;
    String mDescText;
    String mTypeText;
    int mProgressColor;
    ValueAnimator mProgressAnimator;

    public GressOkesView(Context context) {
        this(context, null);
    }

    public GressOkesView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GressOkesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mRectF = new RectF();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

        mCirPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirPaint.setStyle(Paint.Style.FILL);
        mCirPaint.setColor(Color.WHITE);

        mCirLightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirLightPaint.setStyle(Paint.Style.FILL);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GressOkesView, defStyleAttr, 0);
        mMax = a.getInt(R.styleable.GressOkesView_max, 100);
        mProgress = a.getInt(R.styleable.GressOkesView_progress, 0);
        mOffsetAngle = a.getInt(R.styleable.GressOkesView_offsetAngle, -45);
        mTextSize = a.getDimensionPixelSize(R.styleable.GressOkesView_textSize, 20);
        mProgressColor = a.getColor(R.styleable.GressOkesView_progressColor, Color.WHITE);
        mUnitText = a.getString(R.styleable.GressOkesView_unitText);
        mTypeText = a.getString(R.styleable.GressOkesView_typeText);
        mDescText = a.getString(R.styleable.GressOkesView_descText);
        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int progress = mProgress;
        mProgress = 0;
        updateProgress(progress);
    }

    public void setProgress(int progress) {
        if (mProgress != progress) {
            updateProgress(progress);
        }
    }

    public void setDescText(String descText) {
        mDescText = descText;
        postInvalidate();
    }

    private void updateProgress(int progress) {
        progress = Math.min(progress, mMax);
        progress = Math.max(progress, 0);
        if (mProgressAnimator != null) {
            mProgressAnimator.cancel();
        }
        mVisualProgress = 0;
        mProgressAnimator = ValueAnimator.ofFloat(mProgress, progress);
        mProgressAnimator.setDuration(1000L);
        mProgressAnimator.setInterpolator(new DecelerateInterpolator());
        mProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mVisualProgress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mProgressAnimator.start();
        mProgress = progress;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float radius = cx > cy ? cy : cx;
        float spacing = radius / 10f * 1.5f;

        //背景1 圆环
        mRectF.set(spacing / 2f, spacing / 2f, getWidth() - spacing / 2f, getHeight() - spacing / 2f);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(spacing);
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(50);
        canvas.drawArc(mRectF, 0, 360, false, mPaint);
        //背景2 内部圆
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(20);
        canvas.drawCircle(cx, cy, radius - spacing, mPaint);

        //进度背景
        mRectF.set(spacing, spacing, getWidth() - spacing, getHeight() - spacing);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(spacing / 1.7f);
        mPaint.setColor(Color.rgb(236, 245, 250));
        mPaint.setAlpha(255);
        canvas.drawArc(mRectF, 0, 360, false, mPaint);

        //进度
        float cirRadius = spacing / 2.5f;
        float arcRadius = radius - spacing;
        float angle = mVisualProgress / mMax * 360f;
        mRectF.set(spacing, spacing, getWidth() - spacing, getHeight() - spacing);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(spacing / 1.7f);
        mPaint.setColor(mProgressColor);
        canvas.drawArc(mRectF, mOffsetAngle, angle, false, mPaint);

        float radians = (float) Math.toRadians(mOffsetAngle + angle);
        float iCx = (float) (Math.cos(radians) * arcRadius);
        float iCy = (float) (Math.sin(radians) * arcRadius);

        //发光
        mCirLightPaint.setColor(mProgressColor);
        MaskFilter innerFilter = mCirLightPaint.getMaskFilter();
        if (innerFilter == null) {
            innerFilter = new BlurMaskFilter(cirRadius * 0.8f, BlurMaskFilter.Blur.NORMAL);
            mCirLightPaint.setMaskFilter(innerFilter);
        }
        canvas.drawCircle(iCx + cx, iCy + cy, cirRadius, mCirLightPaint);

        //发光圆
        MaskFilter cirFilter = mCirPaint.getMaskFilter();
        if (cirFilter == null) {
            cirFilter = new BlurMaskFilter(cirRadius * 0.8f, BlurMaskFilter.Blur.SOLID);
            mCirPaint.setMaskFilter(cirFilter);
        }
        canvas.drawCircle(iCx + cx, iCy + cy, cirRadius, mCirPaint);

        mTextPaint.setTextSize(mTextSize);
        String text = String.format(Locale.getDefault(), "%d%s", (int) mVisualProgress, mUnitText == null ? "" : mUnitText);
        float textW = mTextPaint.measureText(text);
        float textH;
        canvas.drawText(text, cx - textW / 2f, cy, mTextPaint);

        if (!TextUtils.isEmpty(mDescText)) {
            mTextPaint.setTextSize(mTextSize / 2.5f);
            text = mDescText;
            textW = mTextPaint.measureText(text);
            textH = mTextPaint.descent() - mTextPaint.ascent();
            canvas.drawText(text, cx - textW / 2f, cy + textH + 10, mTextPaint);
        }
        if (!TextUtils.isEmpty(mTypeText)) {
            mTextPaint.setTextSize(mTextSize / 2f);
            text = mTypeText;
            textW = mTextPaint.measureText(text);
            textH = mTextPaint.descent() - mTextPaint.ascent();
            canvas.drawText(text, cx - textW / 2f, cy + (radius - spacing * 1.294f) / 2f + textH / 2f - mTextPaint.descent(), mTextPaint);
        }
    }
}
