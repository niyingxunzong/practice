package com.adorable.news.bean;

import java.util.List;

/**
 * 根据json接口构造Bean对象 存储json数据
 * 
 * @author liuqiang
 *
 */
public class NewsCenterBean {

	public List<NewsCenterItem> data;
	public List<String> extend;
	public String retcode;

	public class NewsCenterItem {
		public String id;
		public String title;
		public String type;
		public String url;
		public String url1;
		public String excurl;
		public String dayurl;
		public String weekurl;
		
		public List<Children> children;
		
		public class Children {
			public String id;
			public String title;
			public String type;
			public String url;
		}
	}
}
