package com.adorable.news.pager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;


/**
 * 智慧服务
 * @author liuqiang
 *
 */
public class SmartServicePager extends BasePager {

	public SmartServicePager(Context context) {
		super(context);
	}

	@Override
	public View initView() {
		TextView tv = new TextView(context);
		tv.setText("SmartServicePager");
		return tv;
	}

	@Override
	public void initData() {
		
	}

}
