package com.adorkable.fragment;

import com.adorkable.light.R;
import com.adorkable.util.LightnessControl;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import com.adorkable.util.Log;
import com.umeng.analytics.MobclickAgent;

/**
 * 启动桌面照明
 * 
 * @author liuqiang
 *
 */
public class DesktopLight extends Fragment {

	private static String TAG = "DesktopLight";
	
	private View mSpacer; // 处理触摸事件

	private float mLastX;    //记录上次触摸的的位置
	private float mLastY;
	
	private boolean isAutoBrightness;
	
	private final int MOVE_FACOTR = 2;    //滑动因子滑动多大的距离才算是滑动了
	private final int CHANGE_RATE = 1;    //每次滑动后亮度的变化值
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//
		View view = inflater.inflate(R.layout.fragment_desktop_light,
				container, false);
		mSpacer = view.findViewById(R.id.ib_flashlight);
		return view;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		MobclickAgent.onPageEnd("DesktopLightFragmentPage"); 
		
		//恢复
		if(isAutoBrightness){
			LightnessControl.startAutoBrightness(getActivity());
		}
	}
	
	@Override
	public void onResume() {
		//
		super.onResume();
		
		MobclickAgent.onPageStart("DesktopLightFragmentPage"); //统计页面
		
		isAutoBrightness = LightnessControl.isAutoBrightness(getActivity());
		if(isAutoBrightness){
			LightnessControl.stopAutoBrightness(getActivity());
		}
		
		// 手势处理的那个View事件
		mSpacer.setOnTouchListener(mTouchListener);
	}

	/**
	 * call gesture detector 处理播放界面的手势处理的View的Touch事件
	 */
	private OnTouchListener mTouchListener = new OnTouchListener() {

		public boolean onTouch(View v, MotionEvent event) {
			// 单点触控的时候
			if (event.getPointerCount() == 1) {
				
				float y = event.getY();
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mLastY = y;
					break;
				case MotionEvent.ACTION_UP:
					
					break;
				case MotionEvent.ACTION_MOVE:
					int currentBrightness = LightnessControl.getLightness(getActivity());
					Log.i(TAG, "-->"+currentBrightness);
					if(y-mLastY > MOVE_FACOTR){    //向下滑，减小亮度
						LightnessControl.setLightness(getActivity(), currentBrightness-CHANGE_RATE);
					}else if(y-mLastY < MOVE_FACOTR){
						LightnessControl.setLightness(getActivity(), currentBrightness+CHANGE_RATE);
					}
					mLastY = y;
					break;
				default:
					break;
				}
			}
			// 一定要返回true，不然获取不到完整的事件
			return true;
		}
	};


}
