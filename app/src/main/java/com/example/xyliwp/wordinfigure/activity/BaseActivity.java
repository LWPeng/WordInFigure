package com.example.xyliwp.wordinfigure.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.xyliwp.wordinfigure.customclass.ActivityCollector;

/**
 * Created by lwp940118 on 2016/7/11.
 * 自定义aseactivity，让其继承自activity。
 * 可以随时知道你运行的activity是哪一哥activity。并且可以随时随地的推出程序
 */
public class BaseActivity extends AppCompatActivity {

    /**
     * 重写  onCreate方法 继承它的活动都将在启动时  加入activity列表。
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BaseActivity------>",getClass().getSimpleName());
        ActivityCollector.addActivity(this);

    }

    /**
     * 重写onDestroy方法 在fish时将释放器activity
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //一个将要马上销毁的程序  被移除列表
        ActivityCollector.removeActivity(this);
    }
}
