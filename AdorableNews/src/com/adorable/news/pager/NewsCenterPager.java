package com.adorable.news.pager;

import java.util.ArrayList;
import java.util.List;

import com.adorable.news.MainActivity;
import com.adorable.news.R;
import com.adorable.news.bean.NewsCenterBean;
import com.adorable.news.pager.childpager.NewsCenterIntPager;
import com.adorable.news.pager.childpager.NewsCenterNewPager;
import com.adorable.news.pager.childpager.NewsCenterPicPager;
import com.adorable.news.pager.childpager.NewsCenterTopicPager;
import com.adorkable.news.utils.AdorkableApi;
import com.adorkable.news.utils.SharedPreferencesUtil;
import com.adorkable.util.Log;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 新闻中心
 * 
 * @author liuqiang
 *
 */
public class NewsCenterPager extends BasePager {

	protected static final String TAG = "NewsCenterPager";

	//左侧边栏的标题
	private List<String> titleList;
	//子 view
	private List<BasePager> childPagers = new ArrayList<BasePager>();
	//主要内容ViewGroup
	@ViewInject(R.id.fl_news_center)
	private FrameLayout fl_news_center;
	
	public NewsCenterPager(Context context) {
		super(context);
		titleList = new ArrayList<String>();
	}

	//构造这个类的时候这个方法就会被调用
	@Override
	public View initView() {
		view = View.inflate(context, R.layout.new_center_frame, null);
		ViewUtils.inject(this, view);
		
		initTitleBar();
		
//		initData();
		return view;
	}

	@Override
	public void initData() {
		
		//获取本地缓存数据，有则展示，然后去请求网络数据，覆盖之前的数据
		String result = SharedPreferencesUtil.getStringData(context, AdorkableApi.NEWS_CENTER_CATEGORIES, "");
		//本地数据不为空
		if(!TextUtils.isEmpty(result)){
			//解析json  and  显示界面
			
		}
		getData();
	}

	/**
	 * 从网络获取数据
	 */
	private void getData() {
		Log.i(TAG, ""+AdorkableApi.NEWS_CENTER_CATEGORIES);
		requestData(HttpMethod.GET, AdorkableApi.NEWS_CENTER_CATEGORIES, null,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						// 请求成功后回调
						Log.i(TAG,responseInfo.result);
						//缓存网络数据
						SharedPreferencesUtil.saveStringData(context,
								AdorkableApi.NEWS_CENTER_CATEGORIES,
								responseInfo.result);
						
						//已经拿到最新数据，解析json，进行界面展示
						processData(responseInfo.result);
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						// 请求失败后回调
						Log.i(TAG,"请求失败");
					}
				});
	}
	
	/**
	 * 解析json数据
	 * @param result  json字符串
	 */
	private void processData(String result) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		NewsCenterBean newsCenterBean = gson.fromJson(result, NewsCenterBean.class);
		
		titleList.clear();
		//构造左侧侧拉栏需要的数据
		for(int i=0;i<newsCenterBean.data.size();i++){
			titleList.add(newsCenterBean.data.get(i).title);
		}
//		Log.i(TAG, newsCenterBean.data.get(0).children.get(0).url);
		//更新目录
		((MainActivity)context).getMenuFragment().initMenu(titleList);
		
		//设置子pager内容,
		childPagers.clear();
		childPagers.add(new NewsCenterNewPager(context,newsCenterBean.data.get(0)));
		childPagers.add(new NewsCenterTopicPager(context,newsCenterBean.data.get(0)));
		childPagers.add(new NewsCenterPicPager(context,newsCenterBean.data.get(0)));
		childPagers.add(new NewsCenterIntPager(context,newsCenterBean.data.get(0)));
		
		//设置默认展示的界面
		switchChildPager(0);
	}

	/**
	 * 切换 新闻中心中展示的内容页
	 * 对应左侧菜单栏的点击事件
	 * @param i
	 */
	public void switchChildPager(int i) {
		// 添加顶部标题
		txt_title.setText(titleList.get(i));
		//移除标题下面内容页的所有内容
		fl_news_center.removeAllViews();
		//添加某个View
		fl_news_center.addView(childPagers.get(i).getRootView());
		//填充数据
		childPagers.get(i).initData();
		
	}

}
