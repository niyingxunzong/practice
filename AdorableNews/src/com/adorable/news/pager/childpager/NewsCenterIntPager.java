package com.adorable.news.pager.childpager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.adorable.news.bean.NewsCenterBean.NewsCenterItem;
import com.adorable.news.pager.BasePager;

public class NewsCenterIntPager extends BasePager {

	public NewsCenterIntPager(Context context,NewsCenterItem newsCenterItem) {
		super(context);
		
	}

	@Override
	public View initView() {
		TextView tv = new TextView(context);
		tv.setText("互动");
		return tv;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}
