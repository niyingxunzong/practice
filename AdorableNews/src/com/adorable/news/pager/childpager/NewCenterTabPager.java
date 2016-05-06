package com.adorable.news.pager.childpager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.adorable.news.pager.BasePager;

public class NewCenterTabPager extends BasePager{

	private String url;
	private TextView tv;
	public NewCenterTabPager(Context context,String url) {
		super(context);
		this.url = url;
	}

	@Override
	public View initView() {
		tv = new TextView(context);
		return tv;
	}

	@Override
	public void initData() {
		// 
		tv.setText(url);
	}

	
}
