package com.adorable.news.base;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 为了简化后面步骤中书写Adapter的代码
 * 封装了这个
 * @author liuqiang
 *
 */
public abstract class AdorkableBaseAdapter<T> extends BaseAdapter {

	public List<T> list;
	public Context context;
	
	public AdorkableBaseAdapter(List<T> list ,Context context) {
		this.list = list;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return this.list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);
}
