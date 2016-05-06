package com.comcons.util;

import java.util.Collections;
import java.util.List;

import com.comcons.bean.ItemInfo;
import com.comcons.comparator.NameComparator;

/**
 * 工具类
 * 对ArrayList<ItemInfo> 集合进行排序
 * @author LiuQiang
 *
 */
public class SortList {

	/**
	 * 根据应用程序的名字进行排序
	 * @param infos
	 */
	public static void sortByName(List<ItemInfo> infos) {
		Collections.sort(infos, new NameComparator());
	}
	
	
}
