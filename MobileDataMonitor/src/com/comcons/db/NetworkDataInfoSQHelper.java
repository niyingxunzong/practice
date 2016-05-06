package com.comcons.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class NetworkDataInfoSQHelper extends SQLiteOpenHelper {

	public NetworkDataInfoSQHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	/**
	 * 数据库第一次被创建的时候调用
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table data "
				+ "(_id integer primary key autoincrement, "
				+ "app_name varchar(20), "
				+ "pack_name varchar(50), "
				+ "mobile_rx integer, "
				+ "mobile_tx integer "
				+ ")");    //创建表
		
		db.execSQL("create table raw_data "
				+ "(_id integer primary key autoincrement, "
				+ "app_name varchar(20), "
				+ "mobile_rx integer,"
				+ "mobile_tx integer,"
				+ "pack_name varchar(50) "
				+ ")");    //
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}