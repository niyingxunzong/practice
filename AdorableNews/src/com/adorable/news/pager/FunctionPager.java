package com.adorable.news.pager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * 
 * @author liuqiang
 *
 */
public class FunctionPager extends BasePager {

	public FunctionPager(Context context) {
		super(context);
	}

	@Override
	public View initView() {
		TextView tv = new TextView(context);
		tv.setText("FunctionPager");
		return tv;
	}

	@Override
	public void initData() {
		
	}

}
