package com.adorable.news.pager.childpager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.adorable.news.bean.NewsCenterBean.NewsCenterItem;
import com.adorable.news.pager.BasePager;

public class NewsCenterPicPager extends BasePager {

	public NewsCenterPicPager(Context context,NewsCenterItem newsCenterItem) {
		super(context);
		
	}

	@Override
	public View initView() {
		TextView tv = new TextView(context);
		tv.setText("图片中心");
		return tv;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

}
