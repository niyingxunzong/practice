package com.comcons.myinterface;

import java.util.List;

import com.comcons.bean.ItemInfo;

public interface IBinderMethodInterface {

	public List<ItemInfo> getAllPackageInfo();
	
	/**
	 * 在每次Activity初始化后，图形画面加载的时候，从数据库中加载一下，
	 * 以前就存在的流量使用情况
	 * @return
	 */
	public void loadOldDataMap();
}
