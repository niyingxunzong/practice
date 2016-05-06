package com.adorable.news;

import com.adorable.news.fragment.ContentFragment;
import com.adorable.news.fragment.MenuFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import android.os.Bundle;
import android.view.Window;


public class MainActivity extends SlidingFragmentActivity {

	private SlidingMenu slidingMenu ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.content);
		
		//侧拉栏目设置一块帧布局
		setBehindContentView(R.layout.menu_frame);
		
		slidingMenu = getSlidingMenu();
		
		slidingMenu.setMode(SlidingMenu.LEFT);
		
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		//分割线
		slidingMenu.setShadowDrawable(R.drawable.shadow);
		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		//触摸模式
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		
		//添加左侧侧拉栏目
		MenuFragment menuFragment = new MenuFragment();
		
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu,menuFragment, "MENU")
		.commit();
		
		//内容页
		ContentFragment contentFragment = new ContentFragment();
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, contentFragment, "CONTENT")
		.commit();
    }

    /**
     * @return
     * 返回MenuFragment对象
     */
    public MenuFragment getMenuFragment(){
    	return (MenuFragment) getSupportFragmentManager().findFragmentByTag("MENU");
    }
    
    /**
     * @return  获取ContentFragment对象
     */
    public ContentFragment getContentFragment() {
		// TODO Auto-generated method stub
    	return (ContentFragment) getSupportFragmentManager().findFragmentByTag("CONTENT");
	}
}
