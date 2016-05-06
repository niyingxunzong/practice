package com.comcons.receiver;


import com.comcons.service.InternetStateMoniteService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * 权限<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />  
 * @author LiuQiang
 *
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	 @Override  
	 public void onReceive(Context context, Intent intent) {  
		 Intent service = new Intent(context, InternetStateMoniteService.class);  
		 context.startService(service);  
		 System.out.println("成功收到开机启动信息。");  
	 }
	 
}
