package com.example.xyliwp.wordinfigure.customclass;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lwp940118 on 2016/7/11.
 * 该类实现。所有活动的集合管理。便于实现app的快速推出
 */
public class ActivityCollector {

    public static List<Activity> activities = new ArrayList<Activity>();

    /**
     * activity的活动入列表方法
     * @param activity
     */
    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    /**
     * activity的活动出列表方法
     * @param activity
     */
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    /**
     * 释放掉所有的activity，完成程序推出
     */
    public static void finishAll(){
        for (Activity activity:activities){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }

}
