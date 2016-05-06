package com.adorable.news.pager.childpager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.adorable.news.bean.NewsCenterBean.NewsCenterItem;
import com.adorable.news.pager.BasePager;

public class NewsCenterTopicPager extends BasePager {

	public NewsCenterTopicPager(Context context,NewsCenterItem newsCenterItem) {
		super(context);
		
	}

	@Override
	public View initView() {
		TextView tv = new TextView(context);
		tv.setText("专题");
		return tv;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

}
