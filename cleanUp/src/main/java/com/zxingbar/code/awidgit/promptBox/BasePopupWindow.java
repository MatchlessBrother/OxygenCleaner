package com.zxingbar.code.awidgit.promptBox;

import android.view.View;
import android.view.Window;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.content.Context;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ColorDrawable;

/********代替系统PopupWindow的新型弹出框********/
public class BasePopupWindow extends PopupWindow
{
    private Context mContext;
    private float mAlpha = 0.66f;

    public Context getContext()
    {
        return mContext;

    }

    /**********************初始化BasePopupWindow*********************/
    private void initPopupWindow()
    {
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(null);
        setAnimationStyle(android.R.style.Animation_Dialog);
    }
    /**********************实例化BasePopupWindow*********************/
    public BasePopupWindow(Context context)
    {
        mContext = context;
        initPopupWindow();
    }

    /******************为BasePopupWindow设置显示内容*****************/
    public void setContentView(View contentView)
    {
        if(contentView != null)
        {
            contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            super.setContentView(contentView);
            addOnBackListener(contentView);
        }
    }

    /*****************为PopupWindow增加回退事件的监听****************/
    private void addOnBackListener(View contentView)
    {
        if(contentView != null)
        {
            contentView.setFocusable(true);
            contentView.setFocusableInTouchMode(true);
            contentView.setOnKeyListener(new View.OnKeyListener()
            {
                public boolean onKey(View view, int keyCode, KeyEvent event)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_BACK:dismiss();return true;
                        default:return false;
                    }
                }
            });
        }
    }

    /**************设置PopupWindow能否点击外围来取消显示*************/
    public void setOutsideTouchable(boolean touchable)
    {
        super.setTouchable(touchable);
        setFocusable(true);
    }

    /********************为BasePopupWindow设置背景*******************/
    public void setBackgroundDrawable(Drawable background)
    {
        if(null != background)
            super.setBackgroundDrawable(background);
        else
            super.setBackgroundDrawable(new ColorDrawable(0x00000000));
    }

    /**********************************************************************************************/
    /**********************************************************************************************/

    /*****************显示PopupWindow时伴随的黑色背景动画************/
    private ValueAnimator showAnimator()
    {
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, mAlpha);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float alpha = (float) animation.getAnimatedValue();
                setWindowBackgroundAlpha(alpha);
            }
        });
        animator.setDuration(360);
        return animator;
    }

    /****************隐藏PopupWindow时伴随的黑色背景动画*************/
    private ValueAnimator dismissAnimator()
    {
        ValueAnimator animator = ValueAnimator.ofFloat(mAlpha, 1.0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float alpha = (float) animation.getAnimatedValue();
                setWindowBackgroundAlpha(alpha);
            }
        });
        animator.setDuration(360);
        return animator;
    }

    /******************设置PopupWindow的黑色背景透明度***************/
    private void setWindowBackgroundAlpha(float alpha)
    {
        Window window = ((Activity)getContext()).getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.alpha = alpha;
        window.setAttributes(layoutParams);
    }

    /**********************************************************************************************/
    /**********************************************************************************************/

    public void dismiss()
    {
        super.dismiss();
        dismissAnimator().start();
    }

    public void showAsDropDown(View anchor)
    {
        super.showAsDropDown(anchor);
        showAnimator().start();
    }

    public void showAsDropDown(View anchor, int xoff, int yoff)
    {
        super.showAsDropDown(anchor, xoff, yoff);
        showAnimator().start();
    }

    public void showAtLocation(View parent, int gravity, int x, int y)
    {
        super.showAtLocation(parent, gravity, x, y);
        showAnimator().start();
    }

    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity)
    {
        super.showAsDropDown(anchor, xoff, yoff, gravity);
        showAnimator().start();
    }

    /**********************************************************************************************/
    /**********************************************************************************************/
}