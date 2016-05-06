package com.comcons.bean;

import android.graphics.drawable.Drawable;

public class ItemInfo implements Comparable<ItemInfo> {

	private String appName ;
	private Drawable appIcon;
	
	private long mobileRx ;
	private long mobileTx ;
	
	private String packName;
	
	
	public ItemInfo(){
		
	}
	
	public ItemInfo(String appName, Drawable appIcon,long mobileRx,long mobileTx,String packName) {
		super();
		this.appName = appName;
		this.appIcon = appIcon;
		this.mobileRx = mobileRx;
		this.mobileTx = mobileTx;
		this.packName = packName;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public Drawable getAppIcon() {
		return appIcon;
	}
	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}
	public long getMobileRx() {
		return mobileRx;
	}
	public void setMobileRx(long mobileRx) {
		this.mobileRx = mobileRx;
	}
	public long getMobileTx() {
		return mobileTx;
	}
	public void setMobileTx(long mobileTx) {
		this.mobileTx = mobileTx;
	}
	public String getPackName() {
		return packName;
	}
	public void setPackName(String packName) {
		this.packName = packName;
	}
	
	@Override
	public String toString() {
		return "ItemInfo [appName=" + appName + ", appIcon=" + appIcon
				+ ", mobileRx=" + mobileRx + ", mobileTx=" + mobileTx
				+ ", packName=" + packName + "]";
	}
	
	@Override
	public int compareTo(ItemInfo another) {
		// TODO Auto-generated method stub
		if((this.mobileRx+this.mobileTx)> (another.mobileRx + another.mobileTx)){
			return -1;
		}else if((this.mobileRx+this.mobileTx)< (another.mobileRx + another.mobileTx)){
			return 1;
		}else{
			return 0;
		}
	}
	
}
