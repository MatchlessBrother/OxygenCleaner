package com.zxingbar.code.autil.nobase;

import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.Gravity;
import android.widget.Toast;
import com.zxingbar.code.R;
import android.view.KeyEvent;
import android.graphics.Color;
import android.content.Intent;
import android.widget.TextView;
import android.content.Context;
import android.util.TypedValue;
import android.provider.Settings;
import android.view.WindowManager;
import android.view.LayoutInflater;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ColorDrawable;

import com.zxingbar.code.autil.ScreenInfosUtils;
import com.zxingbar.code.awidgit.promptBox.BaseDialog;

/***弹出各样提示框的工具**/
public class PromptBoxUtils
{
    /**------------------------------------------------------------------------------------------**/
    /**------------------------------------------------------------------------------------------**/
    /**------------------------------------------------------------------------------------------**/
    /********************************快速弹出Toast提示框，参数是内容*******************************/
    public static void showToast(Context context,String str)
    {
        showToast(context,str,12f,TypedValue.COMPLEX_UNIT_SP,Toast.LENGTH_SHORT);
    }

    /*****************************快速弹出Toast提示框，参数是内容和显示时间************************/
    public static void showToast(Context context,String str,float strSize,int strTypeValue,int during)
    {
        showToast(context,str,strSize,strTypeValue,Gravity.CENTER,during);
    }

    /**********************快速弹出Toast提示框，参数是内容和显示位置以及显示时间*******************/
    public static void showToast(Context context, String str, float strSize, int strTypeValue, int gravity, int during)
    {
        Toast toast = new Toast(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.inflater_toastviewdefault,null,false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ScreenInfosUtils.getScreenWidth(context), ScreenInfosUtils.getScreenHeightWithoutNavigation(context));
        layout.setLayoutParams(params);
        toast.setView(layout);
        toast.setGravity(gravity,0,12);
        toast.setDuration(during);
        TextView toastTextView = (TextView) layout.findViewById(R.id.toastview);
        toastTextView.setTextSize(strTypeValue,strSize);
        if (str != null)
        {
            toastTextView.setText(str);
            toast.show();
        }
    }

    /**------------------------------------------------------------------------------------------**/
    /**------------------------------------------------------------------------------------------**/
    /**------------------------------------------------------------------------------------------**/
    /**************************************显示权限设置提示框**************************************/
    public static BaseDialog showPermissionDialog(Context context, View.OnClickListener onClickListener, BaseDialog.OnClickOutsideListener onClickOutsideListener)
    {
        return showPermissionDialog(context,"Current operation behavior lacks system permission! Please enter the system settings page to view and modify the relevant permissions of the current application before continuing to use, thank you!","Set up",onClickListener,onClickOutsideListener);
    }

    /********************显示权限设置提示框,contentStr为提示内容,btnStr为按钮内容******************/
    public static BaseDialog showPermissionDialog(Context context, String contentStr, String btnStr, View.OnClickListener onClickListener, BaseDialog.OnClickOutsideListener onClickOutsideListener)
    {
        return showPermissionDialog(context,contentStr, Color.argb(255,255,255,255),13, TypedValue.COMPLEX_UNIT_DIP,context.getResources().getDrawable(R.drawable.shape_dialogdefaultblackbg),btnStr, Color.argb(255,216,80,126),14, TypedValue.COMPLEX_UNIT_DIP, context.getResources().getDrawable(R.drawable.shape_dialogdefaultblackbg),onClickListener,onClickOutsideListener);
    }

    /**显示权限设置提示框,contentStr为提示内容,contentStrColor为提示字体的颜色,contentStrSize为提***
     *****示字体的大小(Dp),contentStrBackground为提示内容的背景,后面参数和前面的相似，自己变通*****/
    public static BaseDialog showPermissionDialog(final Context context, String contentStr, int contentStrColor, int contentStrSize, int contentStrSizeType, Drawable contentStrBackground, String btnStr, int btnStrColor, int btnStrSize, int btnStrSizeType, Drawable btnStrBackground, View.OnClickListener onClickListener, BaseDialog.OnClickOutsideListener onClickOutsideListener)
    {
        final BaseDialog permissionDialog = new BaseDialog(context);
        permissionDialog.setCanceledOnTouchOutside(true);
        permissionDialog.show();

        View view = LayoutInflater.from(context).inflate(R.layout.inflater_permissiondialogdefault, null);
        permissionDialog.setContentView(view);
        TextView content = (TextView)view.findViewById(R.id.permissiondialog_content);
        content.setText(contentStr.trim());
        content.setTextSize(contentStrSizeType,contentStrSize);
        content.setTextColor(contentStrColor);
        content.setBackgroundDrawable(contentStrBackground);
        TextView btn = (TextView)view.findViewById(R.id.permissiondialog_btn);
        btn.setText(btnStr.trim());
        btn.setTextSize(btnStrSizeType,btnStrSize);
        btn.setTextColor(btnStrColor);
        btn.setBackgroundDrawable(btnStrBackground);
        if(null != onClickListener)
            btn.setOnClickListener(onClickListener);
        else
        {
            btn.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    permissionDialog_Btn(context,permissionDialog);
                }
            });
        }
        if(null != onClickOutsideListener)
            permissionDialog.setOnClickOutsideListener(onClickOutsideListener);
        Window window = permissionDialog.getWindow();
        window.getDecorView().setPadding(0,0,0,0);
        window.setWindowAnimations(R.style.BottomOpenDialogAnim);
        window.getDecorView().setBackgroundResource(android.R.color.transparent);
        DisplayMetrics displayMetrics = new DisplayMetrics();/******************/
        ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = displayMetrics.widthPixels;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        return permissionDialog;
    }

    /***************************************隐藏权限设置提示框*************************************/
    public static void dismissPermissionDialog(BaseDialog dialog)
    {
        if(null != dialog && dialog.isShowing())
            dialog.dismiss();
    }

    /***********************************监听权限设置提示框的确定按钮*******************************/
    public static void permissionDialog_Btn(Context context, BaseDialog dialog)
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
        dismissPermissionDialog(dialog);
    }

    /**------------------------------------------------------------------------------------------**/
    /**------------------------------------------------------------------------------------------**/
    /**------------------------------------------------------------------------------------------**/
    /*************************************隐藏默认提示框*******************************************/
    public static void dismissPromptDialog(BaseDialog baseDialog)
    {
        if(null != baseDialog && baseDialog.isShowing())
            baseDialog.dismiss();
    }

    /*************************************显示默认提示框*******************************************/
    public static BaseDialog showPromptDialog(Context context, View.OnClickListener onClickTrueBtnListener, View.OnClickListener onClickFalseBtnListener, BaseDialog.OnClickOutsideListener onClickOutsideListener)
    {
        return showPromptDialog(context,"Warning:", Color.argb(255,255,255,255),15, TypedValue.COMPLEX_UNIT_DIP,new ColorDrawable(0xff61dafe), View.VISIBLE,"Are you sure you want to do this?", Color.argb(255,88,88,88),13, TypedValue.COMPLEX_UNIT_DIP,new ColorDrawable(0xffffffff),"No", Color.argb(255,139,139,139),14, TypedValue.COMPLEX_UNIT_DIP,new ColorDrawable(0xffeeeeee), View.VISIBLE,"Yes", Color.argb(255,255,255,255),14, TypedValue.COMPLEX_UNIT_DIP,new ColorDrawable(0xff61dafe), View.VISIBLE,true,onClickTrueBtnListener,onClickFalseBtnListener,onClickOutsideListener);
    }

    /**显示默认提示框，参数含义自己根据名字看，字体大小值默认以DPS为准，颜色属性值默认以动态Color生*
     ********成法为准,背景属性值默认以动态ColorDrawable生成法为准,其余则按照普通情况使用即可*******/
    public static BaseDialog showPromptDialog(Context context, String titleStr, int titleStrColor, Drawable titleStrBackground, int titleStrVisible, String contentStr, String falseStr, int falseStrVisible, String trueStr, int trueStrColor, Drawable trueStrBackground, int trueStrVisible, boolean isCanceledOnTouchOutside, View.OnClickListener onClickTrueBtnListener, View.OnClickListener onClickFalseBtnListener, BaseDialog.OnClickOutsideListener onClickOutsideListener)
    {
        return showPromptDialog(context,titleStr,titleStrColor,15, TypedValue.COMPLEX_UNIT_DIP,titleStrBackground,titleStrVisible,contentStr, Color.argb(255,88,88,88),13, TypedValue.COMPLEX_UNIT_DIP,new ColorDrawable(0xffffffff),falseStr, Color.argb(255,139,139,139),14, TypedValue.COMPLEX_UNIT_DIP,new ColorDrawable(0xffeeeeee),falseStrVisible,trueStr,trueStrColor,14, TypedValue.COMPLEX_UNIT_DIP,trueStrBackground,trueStrVisible,isCanceledOnTouchOutside,onClickTrueBtnListener,onClickFalseBtnListener,onClickOutsideListener);
    }

    /**显示默认提示框，参数含义自己根据名字看，字体大小值默认以DPS为准，颜色属性值默认以动态Color生*
     ********成法为准,背景属性值默认以动态ColorDrawable生成法为准,其余则按照普通情况使用即可*******/
    public static BaseDialog showPromptDialog(Context context, String titleStr, int titleStrColor, int titleStrSize, int titleStrSizeType, Drawable titleStrBackground, int titleStrVisible, String contentStr, int contentStrColor, int contentStrSize, int contentStrSizeType, Drawable contentStrBackground, String falseStr, int falseStrColor, int falseStrSize, int falseStrSizeType, Drawable falseStrBackground, int falseStrVisible, String trueStr, int trueStrColor, int trueStrSize, int trueStrSizeType, Drawable trueStrBackground, int trueStrVisible, final boolean isCanceledOnTouchOutside, final View.OnClickListener onClickTrueBtnListener, final View.OnClickListener onClickFalseBtnListener, BaseDialog.OnClickOutsideListener onClickOutsideListener)
    {
        final BaseDialog promptDialog = new BaseDialog(context);
        promptDialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
        promptDialog.show();

        View view = LayoutInflater.from(context).inflate(R.layout.inflater_promptdialogdefault, null);
        promptDialog.setContentView(view);
        TextView title = (TextView)view.findViewById(R.id.promptdialog_title);
        title.setText(titleStr.trim());
        title.setTextSize(titleStrSizeType,titleStrSize);
        title.setTextColor(titleStrColor);
        title.setVisibility(titleStrVisible);
        title.setBackgroundDrawable(titleStrBackground);
        TextView content = (TextView)view.findViewById(R.id.promptdialog_content);
        content.setText(contentStr.trim());
        content.setTextSize(contentStrSizeType,contentStrSize);
        content.setTextColor(contentStrColor);
        content.setBackgroundDrawable(contentStrBackground);
        TextView trueBtn = (TextView)view.findViewById(R.id.promptdialog_true);
        trueBtn.setText(trueStr.trim());
        trueBtn.setTextSize(trueStrSizeType,trueStrSize);
        trueBtn.setTextColor(trueStrColor);
        trueBtn.setVisibility(trueStrVisible);
        trueBtn.setBackgroundDrawable(trueStrBackground);
        TextView falseBtn = (TextView)view.findViewById(R.id.promptdialog_false);
        falseBtn.setText(falseStr.trim());
        falseBtn.setTextSize(falseStrSizeType,falseStrSize);
        falseBtn.setTextColor(falseStrColor);
        falseBtn.setVisibility(falseStrVisible);
        falseBtn.setBackgroundDrawable(falseStrBackground);
        if(falseBtn.getVisibility() != View.VISIBLE)
            view.findViewById(R.id.promptdialog_btnimg).setVisibility(View.GONE);

        trueBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                dismissPromptDialog(promptDialog);
                if(null != onClickTrueBtnListener)
                    onClickTrueBtnListener.onClick(view);
            }
        });

        falseBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                dismissPromptDialog(promptDialog);
                if(null != onClickFalseBtnListener)
                    onClickFalseBtnListener.onClick(view);
            }
        });
        if(null != onClickOutsideListener)
            promptDialog.setOnClickOutsideListener(onClickOutsideListener);
        promptDialog.setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                if(keyCode == KeyEvent.KEYCODE_BACK)
                {
                    if(isCanceledOnTouchOutside)
                        return false;
                    else
                        return true;
                }
                return false;
            }
        });

        Window window = promptDialog.getWindow();
        window.getDecorView().setPadding(0,0,0,0);
        window.getDecorView().setBackgroundResource(android.R.color.transparent);
        DisplayMetrics displayMetrics = new DisplayMetrics();/******************/
        ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams params = window.getAttributes();
        if(displayMetrics.widthPixels <= 1480)
            params.width = displayMetrics.widthPixels - 160;
        else
            params.width = displayMetrics.widthPixels / 2;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        return promptDialog;
    }
}