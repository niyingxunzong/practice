package com.comcons.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.comcons.bean.ItemInfo;
import com.comcons.db.NetworkDataInfoSQHelper;

public class NetworkDataInfoDAO {

	/**
	 * 保存这个引用，后面能用到
	 */
	private NetworkDataInfoSQHelper helper =null;
	
	public NetworkDataInfoDAO(Context c) {
		helper = new NetworkDataInfoSQHelper(c, "network_data.db",null, 1);
	}
	
	/**
	 * 向数据库中添加
	 * 这个是在网络开启的时候记录的所有的网络流量数据
	 * @param name
	 */
	public void addRawData(ItemInfo info){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues  values = new ContentValues();
		
		values.put("app_name", info.getAppName());
		values.put("pack_name", info.getPackName());
		values.put("mobile_rx", info.getMobileRx());
		values.put("mobile_tx", info.getMobileTx());
		
		//sdb.execSQL("insert into person (name); values ('"+name+"')");
		db.insert("raw_data", null, values);
		/**
		 * 必须记得关闭数据库
		 */
		db.close();
	}
	
	/**
	 * 将一个链表写入数据库
	 * @param infos
	 */
	public void addRawDataList(List<ItemInfo> infos){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues  values = new ContentValues();
		for(ItemInfo info : infos){
			values.put("app_name", info.getAppName());
			values.put("pack_name", info.getPackName());
			values.put("mobile_rx", info.getMobileRx());
			values.put("mobile_tx", info.getMobileTx());
			db.insert("raw_data", null, values);
		}
		/**
		 * 必须记得关闭数据库
		 */
		db.close();
	}
	
	public void updateRawData(ItemInfo info){
		
	}
	
	/**
	 * 根据包名查找数据库中某个
	 * 应用程序的流量信息
	 * @param packName
	 * @return
	 */
	public ItemInfo getRawDataByPackName(String packName){
		SQLiteDatabase db= helper.getReadableDatabase();
        Cursor cursor =db.rawQuery("select * from raw_data where pack_name=?", new String[]{packName});  
        if(cursor.moveToFirst()){  
        	
            String app_name=cursor.getString(cursor.getColumnIndex("app_name"));  
            long mobile_rx=cursor.getLong(cursor.getColumnIndex("mobile_rx"));
            long mobile_tx=cursor.getLong(cursor.getColumnIndex("mobile_tx"));  
            ItemInfo info = new ItemInfo(); 
            
            info.setAppName(app_name);
            info.setPackName(packName);
            info.setMobileTx(mobile_tx);
            info.setMobileRx(mobile_rx);
            
            return info;  
        }  
        cursor.close(); 
        db.close();
        return null;  
	}
	
	/**
	 * 把raw_data 数据库中的内容以HashMap的方式返回
	 * 键为包名
	 * @return
	 */
	public Map<String,ItemInfo> getAllRawData(){
		Map<String, ItemInfo> map = new HashMap<>();
		
		SQLiteDatabase db= helper.getReadableDatabase();
        Cursor cursor =db.rawQuery("select * from raw_data ;", null);  
        while(cursor.moveToNext()){  
        	
            String app_name=cursor.getString(cursor.getColumnIndex("app_name")); 
            String packName=cursor.getString(cursor.getColumnIndex("pack_name")); 
            long mobile_rx=cursor.getLong(cursor.getColumnIndex("mobile_rx"));
            long mobile_tx=cursor.getLong(cursor.getColumnIndex("mobile_tx"));  
            ItemInfo info = new ItemInfo(); 
            
            info.setAppName(app_name);
            info.setPackName(packName);
            info.setMobileTx(mobile_tx);
            info.setMobileRx(mobile_rx);
            
            map.put(packName, info);
        }  
        cursor.close(); 
        db.close();
		
		return map;
	}
	
	
					//*   一下操作的是data 表     *//
	
	/**
	 * 向数据库中添加
	 * 这个是在网络断开时将具体的数据使用量添加到数据库中
	 * @param name
	 */
	public void addData(ItemInfo info){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues  values = new ContentValues();
		
		values.put("app_name", info.getAppName());
		values.put("pack_name", info.getPackName());
		values.put("mobile_rx", info.getMobileRx());
		values.put("mobile_tx", info.getMobileTx());
		
		db.insert("data", null, values);
		/**
		 * 必须记得关闭数据库
		 */
		db.close();
	}
	
	/**
	 * 将一个链表写入数据库
	 * @param infos
	 */
	public void addDataList(List<ItemInfo> infos){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues  values = new ContentValues();
		for(ItemInfo info : infos){
			values.put("app_name", info.getAppName());
			values.put("pack_name", info.getPackName());
			values.put("mobile_rx", info.getMobileRx());
			values.put("mobile_tx", info.getMobileTx());
			db.insert("data", null, values);
		}
		/**
		 * 必须记得关闭数据库
		 */
		db.close();
	}
	
	/**
	 * 更新数据
	 * @param info
	 */
	public void updateData(ItemInfo info){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put("app_name", info.getAppName());
		values.put("pack_name", info.getPackName());
		values.put("mobile_rx", info.getMobileRx());
		values.put("mobile_tx", info.getMobileTx());
		db.update("data", values, "pack_name=?", new String[]{info.getPackName()}); 
		db.close();
	}
	
	/**
	 * 根据包名查找数据库中某个
	 * 应用程序的流量信息
	 * @param packName
	 * @return
	 */
	public ItemInfo getDataByPackName(String packName){
		SQLiteDatabase db= helper.getReadableDatabase();
        Cursor cursor =db.rawQuery("select * from data where pack_name=?", new String[]{packName});  
        if(cursor.moveToFirst()){  
        	
            String app_name=cursor.getString(cursor.getColumnIndex("app_name"));  
            long mobile_rx=cursor.getLong(cursor.getColumnIndex("mobile_rx"));
            long mobile_tx=cursor.getLong(cursor.getColumnIndex("mobile_tx"));  
            ItemInfo info = new ItemInfo(); 
            
            info.setAppName(app_name);
            info.setPackName(packName);
            info.setMobileTx(mobile_tx);
            info.setMobileRx(mobile_rx);
            
            return info;  
        }  
        cursor.close(); 
        db.close();
        return null;  
	}
	
	/**
	 * 把data 数据库中的内容以HashMap的方式返回
	 * 键为包名
	 * @return
	 */
	public Map<String,ItemInfo> getAllDataMap(){
		Map<String, ItemInfo> map = new HashMap<>();
		
		SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor =db.rawQuery("select * from data ;", null);  
        while(cursor.moveToNext()){  
        	
            String app_name=cursor.getString(cursor.getColumnIndex("app_name")); 
            String packName=cursor.getString(cursor.getColumnIndex("pack_name")); 
            long mobile_rx=cursor.getLong(cursor.getColumnIndex("mobile_rx"));
            long mobile_tx=cursor.getLong(cursor.getColumnIndex("mobile_tx"));  
            ItemInfo info = new ItemInfo(); 
            
            info.setAppName(app_name);
            info.setPackName(packName);
            info.setMobileTx(mobile_tx);
            info.setMobileRx(mobile_rx);
            
            map.put(packName, info);
        }  
        cursor.close(); 
        db.close();
		
		return map;
	}
	
	/** 
	 * 把所有的流量使用情况清零
	 */
	public void clearData(){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mobile_rx", 0);
		values.put("mobile_tx", 0);
		db.update("data", values, null, null); 
		db.close();
	}
	
//	public List<ItemInfo> getAllDataList(){
//		List<ItemInfo> list = new ArrayList<>();
//		
//		SQLiteDatabase db= helper.getReadableDatabase();
//        Cursor cursor =db.rawQuery("select * from data ;", null);  
//        while(cursor.moveToNext()){  
//        	
//            String app_name=cursor.getString(cursor.getColumnIndex("app_name")); 
//            String packName=cursor.getString(cursor.getColumnIndex("pack_name")); 
//            long mobile_rx=cursor.getLong(cursor.getColumnIndex("mobile_rx"));
//            long mobile_tx=cursor.getLong(cursor.getColumnIndex("mobile_tx"));  
//            ItemInfo info = new ItemInfo(); 
//            
//            info.setAppName(app_name);
//            info.setPackName(packName);
//            info.setMobileTx(mobile_tx);
//            info.setMobileRx(mobile_rx);
//            
//            list.add(info);
//        }  
//        cursor.close(); 
//        db.close();
//		
//		return list;
//	}
	
	
	
	
	/**
	 * 根据表名删除表
	 * @param table
	 */
	public void deleteTable(String table){
		SQLiteDatabase db= helper.getReadableDatabase();
		db.delete(table, null, null);
	}
}
