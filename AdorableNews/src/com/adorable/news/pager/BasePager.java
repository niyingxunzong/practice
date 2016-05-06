package com.adorable.news.pager;

import com.adorable.news.MainActivity;
import com.adorable.news.R;
import com.adorkable.util.Log;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 用于构建ViewPager的一个View
 * 
 * @author liuqiang
 *
 */
public abstract class BasePager {

	private static final String TAG = "BasePager";
	public View view;
	public Context context;
	public SlidingMenu slidingMenu;
	
	private ImageButton ib_titlebar_left;
	public TextView txt_title;

	public BasePager(Context context) {
		this.context = context;
		view = initView();
		this.slidingMenu = ((MainActivity)context).getSlidingMenu();
	}

	/**
	 * 创建布局的方法，每一个界面都要去实现
	 * 
	 * @return
	 */
	public abstract View initView();

	/**
	 * //给界面填充数据的方法 在切换pager的时候调用这个方法，开始加载数据
	 */
	public abstract void initData();

	/**
	 * 返回当前Pager所构建出来的View对象
	 * 
	 * @return
	 */
	public View getRootView() {
		return view;
	}

	/**
	 * 请求网络数据
	 * @param httpMethod
	 * @param url
	 * @param params
	 * @param callback
	 */
	public void requestData(HttpMethod httpMethod, String url,
			RequestParams params, RequestCallBack<String> callback) {
		// xutils 初始化网络数据
		HttpUtils httpUtils = new HttpUtils();

		// RequestParams requestParams = new RequestParams();
		// requestParams.addBodyParameter("name", "zhangsan");
		Log.i(TAG, "BasePager-->initData");
		// 用get的方式请求一个url不需要参数，
		httpUtils.send(httpMethod, url, params, callback);
	}
	
	/**
	 * 初始化每个Pager上的TitleBar
	 */
	public void initTitleBar() {
		// 
		Button btn_titlebar_left = (Button) view.findViewById(R.id.btn_left);
		btn_titlebar_left.setVisibility(View.GONE);
		
		ib_titlebar_left = (ImageButton) view.findViewById(R.id.imgbtn_left);
		ib_titlebar_left.setVisibility(View.VISIBLE);
		ib_titlebar_left.setImageResource(R.drawable.img_menu);
		ib_titlebar_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				slidingMenu.toggle();
			}
		});
		
		txt_title = (TextView) view.findViewById(R.id.txt_title);
		
		ImageButton imgbtn_text = (ImageButton) view.findViewById(R.id.imgbtn_text);
		imgbtn_text.setVisibility(View.GONE);
		
		ImageButton imgbtn_right = (ImageButton) view.findViewById(R.id.imgbtn_right);
		imgbtn_right.setVisibility(View.GONE);
		
		ImageButton btn_right = (ImageButton) view.findViewById(R.id.btn_right);
		btn_right.setVisibility(View.GONE);
	}
}
