package com.zxingbar.code.awidgit.promptBox;

import android.view.View;
import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.app.ProgressDialog;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import android.view.ViewConfiguration;

public class BaseProgressDialog extends ProgressDialog
{
    private boolean mCloseable = true;
    private BaseProgressDialog.OnClickOutsideListener mOnClickOutsideListener;

    public BaseProgressDialog(@NonNull Context context) {
        super(context);
    }

    public BaseProgressDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, theme);
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        if (mCloseable && isOutOfBounds(getContext(), event) && null != mOnClickOutsideListener)
        {
            mOnClickOutsideListener.onClickOutside(this);
        }
        return super.onTouchEvent(event);
    }

    public void setCanceledOnTouchOutside(boolean cancel)
    {
        super.setCanceledOnTouchOutside(cancel);
        mCloseable = cancel;
    }

    public boolean getCanceledOnTouchOutside()
    {
        return mCloseable;
    }

    /*************这里判断用户的触摸点是否在弹出框的外围************/
    private boolean isOutOfBounds(Context context, MotionEvent event)
    {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
        final View decorView = getWindow().getDecorView();
        return (x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop)) || (y > (decorView.getHeight() + slop));
    }

    /***用户触摸弹出框外围时触发的类事件***/
    public interface OnClickOutsideListener
    {
        void onClickOutside(Dialog dialog);
    }

    public BaseProgressDialog.OnClickOutsideListener getOnClickOutsideListener()
    {
        return mOnClickOutsideListener;
    }

    public void setOnClickOutsideListener(BaseProgressDialog.OnClickOutsideListener onClickOutsideListener)
    {
        mOnClickOutsideListener = onClickOutsideListener;
    }
}