package com.adorkable.fragment;

import com.adorkable.light.R;
import com.umeng.analytics.MobclickAgent;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/**
 * 启动闪光灯
 * 
 * @author liuqiang
 *
 */
public class CameraLight extends Fragment implements OnClickListener {

	// 定义系统所用的照相机
	private Camera mCamera = null;
	private Parameters parameters = null;
	// 定义开关状态，默认、关闭状态为false，打开状态为true
	private static boolean isOn = false;
	// 代表fragment的View
	private View view;
	private ImageView imageView;

	private ImageView light_on;
	private ImageView light_off;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//
		view = inflater
				.inflate(R.layout.fragment_flash_light, container, false);
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		MobclickAgent.onPageStart("CameraLightFragmentPage"); //统计页面
		
		imageView = (ImageView) view.findViewById(R.id.iv_flashlight);
		
		light_off = (ImageView) view.findViewById(R.id.iv_flashlight);
		light_on = (ImageView) view.findViewById(R.id.iv_flashlight_on);

		Resources res = getResources();
		imageView.setOnClickListener(this);
		
		light_off.setOnClickListener(this);
		light_on.setOnClickListener(this);
	}
	
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("CameraLightFragmentPage"); 
	}

	@Override
	public void onClick(View v) {

		if (isOn) {
			//imageView.setImageResource(R.drawable.light_off);
			
			light_off.setVisibility(View.VISIBLE);
			light_on.setVisibility(View.GONE);
			
			parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
			mCamera.setParameters(parameters);
			// 释放手机摄像头
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			isOn = false;
		} else {
//			imageView.setImageResource(R.drawable.light_on);
			
			light_off.setVisibility(View.GONE);
			light_on.setVisibility(View.VISIBLE);
			
			// 获得Camera对象
			mCamera = Camera.open();
			parameters = mCamera.getParameters();
			parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
			mCamera.setParameters(parameters);
			mCamera.startPreview(); // 这句很重要
			isOn = true;
		}
	}

	@Override
	public void onStop() {
		//
		super.onStop();
		if (mCamera != null) {
			// 释放手机摄像头
			mCamera.release();
			isOn = false;
			mCamera = null;
		}
	}

}
