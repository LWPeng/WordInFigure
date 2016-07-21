package com.example.xyliwp.wordinfigure.customcontrols;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.xyliwp.wordinfigure.R;

/**
 * Created by lwp940118 on 2016/7/15.
 * 作用：书写保存图片的一个弹出窗口，继承自PopWindow，
 * 命名方式：以系统时间为命名
 */
public class PictureSavePopWindow extends PopupWindow{


    private View popView;
    private PictureSavePopWindow pictureSavePopWindow;

    /**
     * 用于  保存图片的弹出窗口
     * @param activity  活动
     * @param path  图片路径
     */
    public PictureSavePopWindow(final Activity activity,String path){

        LayoutInflater layoutInflater = (LayoutInflater)activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popView = layoutInflater.inflate(R.layout.picturesavepopwind,null);
        //获取宽高
        int h = activity.getWindowManager().getDefaultDisplay().getHeight();
        int w = activity.getWindowManager().getDefaultDisplay().getWidth();
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
        pictureSavePopWindow = this;

        LinearLayout linearLayout = (LinearLayout)popView.findViewById(R.id.linearlayout_picturesavepopwindow);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("图片保存------>","点击成功");
                Toast.makeText(activity,"图片保存成功",Toast.LENGTH_SHORT).show();
                pictureSavePopWindow.dismiss();
            }
        });

    }

    /**
     * 设置弹出窗口在主页面中出现的位置
     * @param anchor
     */
    @Override
    public void showAsDropDown(View anchor) {
        if (!this.isShowing()){
            this.showAtLocation(anchor,Gravity.CENTER,0,0);
        }else{
            this.dismiss();
        }

    }
}
