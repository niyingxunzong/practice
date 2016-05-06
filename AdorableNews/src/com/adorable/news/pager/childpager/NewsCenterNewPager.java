package com.adorable.news.pager.childpager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;
import android.view.ViewGroup;

import com.adorable.news.R;
import com.adorable.news.bean.NewsCenterBean.NewsCenterItem;
import com.adorable.news.pager.BasePager;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.viewpagerindicator.TabPageIndicator;

public class NewsCenterNewPager extends BasePager {

	@ViewInject(R.id.tpi_new_center_indicator)
	private TabPageIndicator tpi_new_center_indicator;

	@ViewInject(R.id.vp_new_center)
	private ViewPager vp_new_center;

	//封装数据的entity类
	private NewsCenterItem mNesCenterItem;
	// tab页中的Pager
	private List<BasePager> pagerList = new ArrayList<BasePager>();
	//
	private TabPagerAdapter mPagerAdapter;

	/**
	 * 
	 * @param context
	 * @param newsCenterItem    数据
	 */
	public NewsCenterNewPager(Context context, NewsCenterItem newsCenterItem) {
		super(context);
		this.mNesCenterItem = newsCenterItem;
	}

	@Override
	public View initView() {
		view = View.inflate(context, R.layout.layout_new_center_tab, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void initData() {
		for (int i = 0; i < mNesCenterItem.children.size(); i++) {
			pagerList.add(new NewCenterTabPager(context,
					mNesCenterItem.children.get(i).url));
		}
		
		if(mPagerAdapter == null){
			mPagerAdapter = new TabPagerAdapter();
			vp_new_center.setAdapter(mPagerAdapter);
		}else{
			mPagerAdapter.notifyDataSetChanged();
		}
		
		//tab指示器
		tpi_new_center_indicator.setViewPager(vp_new_center);
		
	}
	
	/**
	 * 新闻中心的tab页面的Adapter
	 * @author LiuQiang
	 *
	 */
	class TabPagerAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return mNesCenterItem.children.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(pagerList.get(position).getRootView());
			pagerList.get(position).initData();
			return pagerList.get(position).getRootView();
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View)object);
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return mNesCenterItem.children.get(position).title;
		}
	}

}
