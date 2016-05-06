package com.adorable.news.pager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * 政务信息
 */
public class GovAffarisPager extends BasePager {

	public GovAffarisPager(Context context) {
		super(context);
	}

	@Override
	public View initView() {
		
		TextView tv = new TextView(context);
		tv.setText("GovAffarisPager");
		return tv;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

}
