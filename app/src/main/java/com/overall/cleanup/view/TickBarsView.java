package com.overall.cleanup.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class TickBarsView extends View {
    Path mPath;
    Paint mPaint;
    RectF mRectF;
    Path mDrawPath;
    float mStopD;
    PathMeasure mPathMeasure;
    ValueAnimator mPathAnimator;

    public TickBarsView(Context context) {
        this(context, null);
    }

    public TickBarsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TickBarsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPath = new Path();
        mDrawPath = new Path();
        mRectF = new RectF();
        mPathMeasure = new PathMeasure();
    }

    private void getPath() {
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float radius = cx > cy ? cy : cx;
        float strokeWidthHalf = radius / 15f;
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2 * strokeWidthHalf);

        float lineEndX = (float) (cx + Math.cos(Math.toRadians(30)) * (radius - strokeWidthHalf));
        float lineEndY = (float) (cy - Math.sin(Math.toRadians(30)) * (radius - strokeWidthHalf));
        mPath.reset();
        mPath.moveTo(cx - radius / 2f, cy);
        mPath.lineTo(cx, cy + radius / 2f);
        mPath.lineTo(lineEndX, lineEndY);


        mRectF.set(cx - radius + strokeWidthHalf, cy - radius + strokeWidthHalf, cx + radius - strokeWidthHalf, cy + radius - strokeWidthHalf);
        mPath.addArc(mRectF, -30, -330);

        mPathMeasure.setPath(mPath, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPathMeasure.getSegment(0, mStopD, mDrawPath, true);
        canvas.drawPath(mDrawPath, mPaint);
    }

    public void start() {
        if (mPathAnimator != null) {
            mPathAnimator.cancel();
        }
        post(new Runnable() {
            @Override
            public void run() {
                getPath();
                innerStart(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (mPathMeasure.nextContour()) {
                            innerStart(null);
                        }
                    }
                });
            }
        });
    }

    private void innerStart(Animator.AnimatorListener listener) {
        mPathAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        mPathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mStopD = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        if (listener != null) {
            mPathAnimator.addListener(listener);
        }
        mPathAnimator.setInterpolator(new LinearInterpolator());
        mPathAnimator.setDuration(500L);
        mPathAnimator.start();
    }

    public void stop() {
        if (mPathAnimator != null) {
            mPathAnimator.cancel();
        }
    }
}
