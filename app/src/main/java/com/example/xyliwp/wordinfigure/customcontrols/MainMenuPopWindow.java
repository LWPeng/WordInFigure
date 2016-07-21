package com.example.xyliwp.wordinfigure.customcontrols;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.xyliwp.wordinfigure.MainActivity;
import com.example.xyliwp.wordinfigure.R;
import com.example.xyliwp.wordinfigure.popwindos.ContextMenuDialogFragment;
import com.example.xyliwp.wordinfigure.popwindos.MenuObject;
import com.example.xyliwp.wordinfigure.popwindos.MenuParams;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * pop  弹出  的设置
 * Created by lwp940118 on 2016/7/9.
 */
public class MainMenuPopWindow extends PopupWindow{

    private View popView;
    private Uri uriImageview;
    private MainMenuPopWindow mainMenuPopWindow;


    public MainMenuPopWindow(final Activity context){

        LayoutInflater layoutInflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popView = layoutInflater.inflate(R.layout.main_menu,null);
        //获取宽高
        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        //设置弹出的popview视图
        this.setContentView(popView);
        //设置弹出窗口的宽
        this.setWidth(w/2+20);
        //设置高度  为layout的自适应高度
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        //设置  pop可点击可见
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable colorDrawable = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener
        this.setBackgroundDrawable(colorDrawable);
        // 设置pop弹出窗体动画效果
        this.setAnimationStyle(R.style.PopWindowAnimation);
        // 获取SD卡路径
        mainMenuPopWindow = this;


        LinearLayout linearLayout_xiangce = (LinearLayout)popView.
                findViewById(R.id.linearlayout_mainmenu_xiangce);
        linearLayout_xiangce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainMenuPopWindow.dismiss();
                Log.e("xiangce---->","相冊");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                context.startActivityForResult(intent, 1);
            }
        });

        LinearLayout linearLayout_paizhao = (LinearLayout)popView.
                findViewById(R.id.linearlayout_mainmenu_paizhao);
        linearLayout_paizhao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //以系统时间作为该文件 民命
                mainMenuPopWindow.dismiss();
                SimpleDateFormat formatter  =   new    SimpleDateFormat("yyyy年MM月dd日HH-mm-ss");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String    str    =    formatter.format(curDate);
                Log.e("拍照---->",str);
                //建立file文件用于保存来拍照后的图片
                File outputFile = new File(Environment.getExternalStorageDirectory(),str+".jpg");
                /**
                 * 使用隐式intent进行跳转
                 */
                try {
                    if (outputFile.exists()){
                        outputFile.delete();
                    }
                    outputFile.createNewFile();

                }catch (Exception e){
                    e.printStackTrace();
                }
                uriImageview = Uri.fromFile(outputFile);
                //另mainactivity的uri等于本.java 文件中的  uri
                MainActivity.uriImageview = uriImageview;
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,uriImageview);
                //启动相机程序
                context.startActivityForResult(intent,2);

            }
        });


    }

    /**
     * 设置  pop 的弹出位置
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 5);
        } else {
            this.dismiss();
        }
    }

}
