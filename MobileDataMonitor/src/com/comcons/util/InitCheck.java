package com.comcons.util;

import java.util.List;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


/**
 * 执行初始化检查
 * @author LiuQiang
 *
 */
public class InitCheck {

	/** 
     * 用来判断服务是否运行. 
     * @param context 
     * @param className 判断的服务名字 
     * @return true 在运行 false 不在运行 
     */
    public static boolean isServiceRunning(Context mContext,String className) { 
        boolean isRunning = false; 
        ActivityManager activityManager = (ActivityManager) 
        mContext.getSystemService(Context.ACTIVITY_SERVICE);  
        List<ActivityManager.RunningServiceInfo> serviceList  = activityManager.getRunningServices(30); 
       if (!(serviceList.size()>0)) { 
            return false; 
        } 
        for (int i=0; i<serviceList.size(); i++) { 
            if (serviceList.get(i).service.getClassName().equals(className) == true) { 
                isRunning = true; 
                break; 
            } 
        } 
        return isRunning; 
    } 
    
    /**
     * 如果系统自己有自启动管理
     * 启动它
     * 例如小米
     */
    public static void startSysAutoStartManger(Context context){
    	/**
		 * 判断是不是小米，如果是小米的话执行下面的代码
		 * 是不是别的手机也要执行这个代码
		 */
		Intent intent = new Intent();  
		try {
			ComponentName comp = new ComponentName("com.android.settings",  
                "com.android.settings.BackgroundApplicationsManager"); 
	        intent.setComponent(comp);  
	        context.startActivity(intent);  
	        return;
		} catch (Exception e) {
			// TODO: handle exception
		}
        
		//后来的小米系统是这个
		try {
			ComponentName comp = new ComponentName("com.android.settings",  
                "com.miui.securitycenter.power.BackgroundApplicationsManager"); 
	        intent.setComponent(comp);  
	        context.startActivity(intent);  
	        return;
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		Toast.makeText(context, "如果有安全软件,请手动添加", Toast.LENGTH_LONG).show();
    }
}
