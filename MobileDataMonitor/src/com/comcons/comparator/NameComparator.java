package com.comcons.comparator;

import java.util.Comparator;

import com.comcons.bean.ItemInfo;

public class NameComparator implements Comparator<ItemInfo>{

	@Override
	public int compare(ItemInfo lhs, ItemInfo rhs) {
		if(lhs.getAppName().compareTo(rhs.getAppName()) == 1){
			return 1;
		}else if(lhs.getAppName().compareTo(rhs.getAppName()) == -1){
			return -1;
		}else{
			return 0;
		}
	}
}