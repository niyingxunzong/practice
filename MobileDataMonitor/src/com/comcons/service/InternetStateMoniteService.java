package com.comcons.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import com.comcons.bean.ItemInfo;
import com.comcons.dao.NetworkDataInfoDAO;
import com.comcons.myinterface.IBinderMethodInterface;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.net.NetworkInfo.State;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;


/**
 * 监视网络状态变化
 * @author LiuQiang
 *
 */
public class InternetStateMoniteService extends Service{

	/**
	 * 数据库操作
	 */
	private NetworkDataInfoDAO dao ;
	private PackageManager pm ;
	
	private NetworkStateReceiver netReceiver ;
	private final int MOBILE_CONNECT = 1;    //无线数据网连接
	private final int MOBILE_DISCONNECT = 2;    //无线数据网断开
//	private final int UPDATE_DATA_TABLE = 3;    //更新data数据表
	
	private boolean isMobileConnect = false;
	private boolean isWifiConnect = false;
	
	//牺牲空间换时间
	private Map<String,ItemInfo> allRawDataMap ;
	private Map<String,ItemInfo> oldDataMap ;
	
	
	/**
	 * 为了用于定期向数据库中写入数据
	 */
	private Timer timer ;
	private TimerTask timerTask;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MOBILE_CONNECT:
				dao.deleteTable("raw_data");
	        	List<ItemInfo> infos = getRawPackageInfo();
	        	dao.addRawDataList(infos);
	        	/**
	        	 * 用一个map存放在初次连接网络的时候的
	        	 * 手机里存储的流量使用情况
	        	 */
	        	allRawDataMap = dao.getAllRawData();
	        	
	        	//启动计时器，向数据库中保存数据
	        	timer = new Timer();
	    		timerTask = new TimerTask() {
	    			@Override
	    			public void run() {
	    				try {
	    					writeData2db();
						} catch (Exception e) {
							System.out.println("我也不知道为什么这里会发生异常");
						}
	    			}
	    		};
	    		timer.schedule(timerTask, 9000, 6000);
				break;
			case MOBILE_DISCONNECT:
				dao.deleteTable("raw_data");    //清空临时数据库表
				try{
					writeData2db();
					System.out.println("网络关闭时，数据写入成功");
					//更新data数据库对应的内存中的集合对象
					loadOldDataMap();
				}catch (Exception e){
					System.out.println("此异常出现是有原因的,因为不是网络开启后关闭,而是一开始就是关闭的 ");
				}
				//关闭计时器
				if(timer!=null && timerTask!=null){
					timer.cancel();
		        	timerTask.cancel();
					timer = null;
					timerTask = null;
				}
				allRawDataMap = null;
				break;
			default:
				break;
			}
		}
	};
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return new MyBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("服务开启");
		
		dao = new NetworkDataInfoDAO(this);
		pm = getPackageManager();
		
		/**
		 * 代码中注册网络监听的Receiver
		 */
		netReceiver = new NetworkStateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(netReceiver, filter);
		
	}
	
	
	/**
	 * 构造一个包含列表中每个条目信息的list
	 * 这个的流量信息是直接从手机文件中读取的
	 * 是wifi 和 手机流量的总和
	 * 在手机流量的网络打开时进行记录
	 * @return
	 */
	private List<ItemInfo> getRawPackageInfo(){
		List<ItemInfo> list = new ArrayList<>();
		/**
		 * 通过Intent过滤所有有图标的程序
		 */
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		List<ResolveInfo> resolveInfos = 
				pm.queryIntentActivities(intent, 
						PackageManager.GET_INTENT_FILTERS);
		/**
		 * 遍历所有的程序
		 * 获取每个程序的信息
		 * 以及流量使用情况
		 */
		for(ResolveInfo info : resolveInfos){
			String appName = info.loadLabel(pm).toString();
			Drawable appIcon = info.loadIcon(pm);
			//下面的操作为了获取这个应用的uid,然后获取流量信息
			String packageName = info.activityInfo.packageName;
			try {
				//根据包名获取应用程序的包信息  对应某个应用程序的manifest节点
				PackageInfo pkInfo = pm.getPackageInfo(packageName, 0);
				//从包信息中获取应用程序信息  对应Application节点
				//再从应用程序中获取uid
				int uid = pkInfo.applicationInfo.uid;
				//根据uid获取流量信息
				long mobileRx = TrafficStats.getUidRxBytes(uid);
				long mobileTx = TrafficStats.getUidTxBytes(uid);
				list.add(new ItemInfo(appName, appIcon, mobileRx, mobileTx,packageName));
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	
	/**
	 * 获取当前的流量使用量和在最近一次数据连接打开时的差值
	 * 也就是从打开流量到现在用的流量
	 * @return
	 */
	private Map<String,ItemInfo> getDifferenceValue(){
		Map<String, ItemInfo> map = new HashMap<>();
		/**
		 * 通过Intent过滤所有有图标的程序
		 */
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		List<ResolveInfo> resolveInfos = 
				pm.queryIntentActivities(intent, 
						PackageManager.GET_INTENT_FILTERS);
		/**
		 * 遍历所有的程序
		 * 获取每个程序的信息
		 * 以及流量使用情况
		 */
		for(ResolveInfo info : resolveInfos){
			Drawable appIcon = info.loadIcon(pm);
			//下面的操作为了获取这个应用的uid,然后获取流量信息
			String packageName = info.activityInfo.packageName;
			//获取数据库中存在的这个应用程序的信息
//			while(allRawDataMap==null){    //由于线程不同步可能导致这个对象为空
//				try {
//					Thread.sleep(300);
//					System.out.println("-----");
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
			ItemInfo raw_info = allRawDataMap.get(packageName);
			
			if(raw_info == null){
				System.out.println("raw_info == null");
				continue;
			}
			try {
				//根据包名获取应用程序的包信息  对应某个应用程序的manifest节点
				PackageInfo pkInfo = pm.getPackageInfo(packageName, 0);
				//从包信息中获取应用程序信息  对应Application节点
				//再从应用程序中获取uid
				int uid = pkInfo.applicationInfo.uid;
				//根据uid获取流量信息
				long mobileRx = TrafficStats.getUidRxBytes(uid) - raw_info.getMobileRx() ;
				long mobileTx = TrafficStats.getUidTxBytes(uid)- raw_info.getMobileTx() ;
				
				/**
				 * 低级错误，这个地方的的对象必须创建，
				 */
				ItemInfo newinfo = new ItemInfo();
				newinfo.setAppIcon(appIcon);
				newinfo.setMobileRx(mobileRx);
				newinfo.setMobileTx(mobileTx);
				newinfo.setAppName(raw_info.getAppName());
				newinfo.setPackName(packageName);
				map.put(packageName, newinfo);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return map;
	}
	
	
	/**
     * 判断移动数据连接是否可用
     * @param context
     * @return
     */
    public boolean isMobileAvailable(Context context){
    	ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    State mobileState = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();   //获取移动数据网络状态

	    if(mobileState == State.CONNECTED) {//判断移动数据是否已经连接
	    	return true;
	    }else{
	    	return false;
	    }
    }
	
	/**
	 * 这个是直接显示在ListView中的 条目列表
	 * 
	 * @return
	 */
	private List<ItemInfo> getActualPackageInfo(){
		
		List<ItemInfo> list = new ArrayList<>();
		
		/**
		 * 有时候网络打开但是初始化并没有完成
		 */
		while(isMobileAvailable(this)){
			if(allRawDataMap == null){
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				break;
			}
		}
		
		//如果手机数据连接打开，则要时时更新
		//数据库中读取的存放的以前的值  和   每次更新的值得总和
		if(isMobileAvailable(this)){
			
			Map<String, ItemInfo> map = getDifferenceValue();
			Iterator<Entry<String, ItemInfo>> iter = map.entrySet().iterator();
			
			ItemInfo itemInfo = null;
			ItemInfo itemInfoDataDB = null;
			ItemInfo newItemInfo = null;
			while (iter.hasNext()) {
				Entry<String, ItemInfo> entry = iter.next();
				itemInfo = entry.getValue();
				//如果这个数据库存在
				//则把数据库中的数据和当前获取的数据相加
				if(oldDataMap != null && !oldDataMap.isEmpty()){
					//创建一个新的ItemInfo对象
					newItemInfo = new ItemInfo();
					itemInfoDataDB = oldDataMap.get(itemInfo.getPackName());    //数据库中的数据
					//把流量信息添加到新的对象中
					newItemInfo.setMobileRx(itemInfo.getMobileRx() + itemInfoDataDB.getMobileRx());
					newItemInfo.setMobileTx(itemInfo.getMobileTx() + itemInfoDataDB.getMobileTx());
					//其他信息赋值
					newItemInfo.setAppIcon(itemInfo.getAppIcon());
					newItemInfo.setAppName(itemInfo.getAppName());
					newItemInfo.setPackName(itemInfo.getPackName());
					
					list.add(newItemInfo);
				}else{
					//continue;
					list.add(itemInfo);
				}
			}
		}else{     //如果没有打开，那么数据量是不会变的直接从数据库中读取就行了
			if(oldDataMap == null){
				return null;
			}
			Iterator<Entry<String, ItemInfo>> iter = oldDataMap.entrySet().iterator();
			ItemInfo itemInfo = null;
			while (iter.hasNext()) {
				Entry<String, ItemInfo> entry = iter.next();
				itemInfo = entry.getValue();
				//为了获取icon 应用程序图标
				try {
					Drawable icon = pm.getApplicationIcon(itemInfo.getPackName());
					itemInfo.setAppIcon(icon);
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				list.add(itemInfo);
			}
		}
		
//		System.out.println(list);
		
		return list;
	}
	
	/**
	 * 在每次Activity初始化后，图形画面加载的时候，从数据库中加载一下，
	 * 以前就存在的流量使用情况
	 * @return
	 */
	private void loadOldDataMap() {
		oldDataMap = dao.getAllDataMap();
	}
	
	/**
	 * 将应用程序流量使用情况数据
	 * 写入data表中
	 * 差值   加上   原始值
	 */
	private void writeData2db(){
		Map<String,ItemInfo> map = getDifferenceValue();
		Iterator<Entry<String, ItemInfo>> iter = map.entrySet().iterator();
		ItemInfo itemInfo = null;
		ItemInfo itemInfo_db = null;
		
		ItemInfo newItemInfo = new ItemInfo();
		while (iter.hasNext()) {
			Entry<String, ItemInfo> entry = iter.next();
			itemInfo = entry.getValue();
			itemInfo_db = dao.getDataByPackName(itemInfo.getPackName());
			
			//数据库中原来不存在这个程序的数据
			if(itemInfo_db==null){
				dao.addData(itemInfo);
			}else{
				newItemInfo.setMobileRx(itemInfo.getMobileRx() + itemInfo_db.getMobileRx());
				newItemInfo.setMobileTx(itemInfo.getMobileTx() + itemInfo_db.getMobileTx());
				newItemInfo.setAppIcon(itemInfo.getAppIcon());
				newItemInfo.setAppName(itemInfo.getAppName());
				newItemInfo.setPackName(itemInfo.getPackName());
				dao.updateData(newItemInfo);
			}
		}
	}
	
	
	/**
	 * android.permission.ACCESS_NETWORK_STATE
	 * 在小米上所有的广播只有在程序运行期间才能接收到
	 * 不知道别的系统如何
	 * @author LiuQiang
	 */
	public class NetworkStateReceiver extends BroadcastReceiver {  
	      
	    @Override  
	    public void onReceive(Context context, Intent intent) {  
	        
	    	Message msg = new Message();
	        System.out.println("网络变化。。收到广播");
	        if(isMobileAvailable(context)){
	        	System.out.println("移动数据可用");
	        	msg.what = MOBILE_CONNECT;
	        	isMobileConnect = true;
	        }else{
	        	System.out.println("移动数据不可用");
	        	msg.what = MOBILE_DISCONNECT;
	        	isMobileConnect = false;
	        }
	        handler.sendMessage(msg);
	    } 
	    
	    /**
	     * 判断移动数据连接是否可用
	     * @param context
	     * @return
	     */
	    public boolean isMobileAvailable(Context context){
	    	ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		    State mobileState = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();   //获取移动数据网络状态

		    if(mobileState == State.CONNECTED) {//判断移动数据是否已经连接
		    	return true;
		    }else{
		    	return false;
		    }
	    }
	}  
	

	/**
	 * 在这里我们就可以把这个内部类声明成私有的了
	 * 然后向外仅仅暴漏IBinderMethodInterface接口，就能调用方法了
	 * @author LiuQiang
	 *
	 */
	private class MyBinder extends Binder implements IBinderMethodInterface{

		@Override
		public List<ItemInfo> getAllPackageInfo() {
			// TODO Auto-generated method stub
			return InternetStateMoniteService.this.getActualPackageInfo();
		}

		@Override
		public void loadOldDataMap() {
			InternetStateMoniteService.this.loadOldDataMap();
		}
	}
	
}
