package com.adorable.news.fragment;

import com.adorable.news.MainActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author liuqiang
 *
 */
public abstract class BaseFragment extends Fragment {

	public View view;
	public Context context;
	public SlidingMenu slidingMenu;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
		slidingMenu = ((MainActivity)context).getSlidingMenu();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = initView();
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		//给指定的view填充数据的操作
		initData();
		super.onActivityCreated(savedInstanceState);
	}
	public abstract void initData() ;
	public abstract View initView();
	
}
