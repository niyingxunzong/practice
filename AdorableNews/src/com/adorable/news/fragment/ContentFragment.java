package com.adorable.news.fragment;

import java.util.ArrayList;
import java.util.List;

import com.adorable.news.R;
import com.adorable.news.pager.BasePager;
import com.adorable.news.pager.FunctionPager;
import com.adorable.news.pager.GovAffarisPager;
import com.adorable.news.pager.NewsCenterPager;
import com.adorable.news.pager.SettingPager;
import com.adorable.news.pager.SmartServicePager;
import com.adorable.news.view.LazyViewPager.OnPageChangeListener;
import com.adorable.news.view.MyViewPager;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * 右侧内容页的内容
 * 
 * @author liuqiang
 *
 */
public class ContentFragment extends BaseFragment {

	protected static final String TAG = "ContentFragment";

	// 1.findViewById()2.给layout_content 赋值
	@ViewInject(R.id.layout_content)
	private MyViewPager layout_content;

	@ViewInject(R.id.main_radio)
	private RadioGroup main_radio;

	// 存放对应内容页下边的五个按钮的五个Pager页面
	private List<BasePager> pagerList;

	@Override
	public void initData() {
		pagerList = new ArrayList<BasePager>();

		pagerList.add(new FunctionPager(context));
		pagerList.add(new NewsCenterPager(context));
		pagerList.add(new GovAffarisPager(context));
		pagerList.add(new SmartServicePager(context));
		pagerList.add(new SettingPager(context));

		layout_content.setAdapter(new ContentViewPagerAdapter());
		//给Viewpager添加滑动事件
		layout_content.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				// 加载当前page的网络数据
				pagerList.get(position).initData();
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub
				
			}
		});
		// 给按钮组添加一个事件
		main_radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
//				Toast.makeText(context, String.valueOf(checkedId),
//						Toast.LENGTH_SHORT).show();
				//
				switch (checkedId) {
				case R.id.rb_function:
					layout_content.setCurrentItem(0);
					break;
				case R.id.rb_news_center:
					layout_content.setCurrentItem(1);
					break;
				case R.id.rb_smart_service:
					layout_content.setCurrentItem(2);
					break;
				case R.id.rb_gov_affairs:
					layout_content.setCurrentItem(3);
					break;
				case R.id.rb_setting:
					layout_content.setCurrentItem(4);
					break;
				default:
					break;
				}
			}
		});
		//按钮组默认选中第一个
		main_radio.check(R.id.rb_function);
	}

	@Override
	public View initView() {
		view = View.inflate(context, R.layout.fragment_content, null);
//		Button
		// xutil 注解的方式去找到指定控件对象
		// 先将父控件通过xutil管理起来，后续才可以获取指定控件对象
		ViewUtils.inject(this, view);
		return view;
	}

	/**
	 * 获取已经存在的NewsCenterPager对象
	 * @return NewsCenterPager
	 */
	public NewsCenterPager getNewCenterPager() {
		// TODO Auto-generated method stub
		return (NewsCenterPager) pagerList.get(1);
	}
	
	
	
	/**
	 * Viewpager的Adapter
	 * 
	 * @author liuqiang
	 *
	 */
	class ContentViewPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return pagerList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = pagerList.get(position).getRootView();
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(pagerList.get(position).getRootView());
		}
	}
	
}
