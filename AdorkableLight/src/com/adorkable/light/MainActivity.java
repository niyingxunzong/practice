package com.adorkable.light;

import com.adorkable.fragment.CameraLight;
import com.adorkable.fragment.DesktopLight;
import com.adorkable.util.FeatureCheck;
import com.adorkable.util.ShortcutUtil;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	private FrameLayout fragment_container;
	private LightType lightTpye;

	private CameraLight cameraLight;
	private DesktopLight desktopLight;
	private long exitTime;

	private Button toggleBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 保持屏幕唤醒
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_main);

		fragment_container = (FrameLayout) findViewById(R.id.fragment_container);
		toggleBtn = (Button) findViewById(R.id.btn_toggle);
		toggleBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//
				toggleFragment();
			}
		});

		// 添加桌面快捷方式
		if (!ShortcutUtil.hasShortcut(this, R.string.app_name))
			ShortcutUtil.addShortcut(this, R.string.app_name,
					R.drawable.ic_launcher);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		//
		super.onResume();

		MobclickAgent.onResume(this);

		cameraLight = new CameraLight();
		desktopLight = new DesktopLight();
		// 有闪光灯
		if (FeatureCheck.isCameraFlashAvailable(this)) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.fragment_container, cameraLight).commit();
			lightTpye = LightType.CameraLight;
		} else {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.fragment_container, desktopLight).commit();
			lightTpye = LightType.DesktopLight;
		}
	}

	/**
	 * 切换照明的方式
	 */
	private void toggleFragment() {
		// 当前是有闪光灯来照明
		if (lightTpye == LightType.CameraLight) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.fragment_container, desktopLight).commit();
			lightTpye = LightType.DesktopLight;
		} else {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.fragment_container, cameraLight).commit();
			lightTpye = LightType.CameraLight;
		}
	}

	/**
	 * 照明工具选择
	 * 
	 * @author liuqiang
	 *
	 */
	public enum LightType {
		CameraLight, DesktopLight
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (System.currentTimeMillis() - exitTime > 2000) {
				Toast.makeText(getApplicationContext(), R.string.exit,
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
