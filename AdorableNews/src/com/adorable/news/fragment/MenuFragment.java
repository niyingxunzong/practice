package com.adorable.news.fragment;

import java.util.List;

import com.adorable.news.MainActivity;
import com.adorable.news.R;
import com.adorable.news.base.AdorkableBaseAdapter;
import com.adorkable.util.Log;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MenuFragment extends BaseFragment {

	private static final String TAG = "MenuFragment";

	@ViewInject(R.id.lv_menu)
	private ListView lv_menu;

	// 记录当前的选中的条目
	private int currendPosition = 0;

	private MenuListAdapter menuListAdapter;

	@Override
	public View initView() {
		// xml ---> view

		view = View.inflate(context, R.layout.layout_left_menu, null);
		ViewUtils.inject(this, view);

		return view;
	}

	@Override
	public void initData() {
		//

	}

	/**
	 * 更新左侧侧边栏
	 * 
	 * @param titleList
	 */
	public void initMenu(List<String> titleList) {
		Log.i(TAG, titleList.toString());
		menuListAdapter = new MenuListAdapter(titleList, context);
		lv_menu.setAdapter(menuListAdapter);
		lv_menu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// listView条目点击事件

				// 记录当前点击的条目
				currendPosition = position;
				menuListAdapter.notifyDataSetChanged();

				// 切换内容页的内容
				((MainActivity) context).getContentFragment()
						.getNewCenterPager().switchChildPager(position);
				
				//收回侧边栏
				slidingMenu.toggle();
			}

		});
	}

	/**
	 * 
	 * @author liuqiang
	 *
	 */
	class MenuListAdapter extends AdorkableBaseAdapter<String> {

		public MenuListAdapter(List<String> list, Context context) {
			super(list, context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = View.inflate(context,
						R.layout.layout_left_menu_item, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_menu_item = (ImageView) convertView
						.findViewById(R.id.iv_menu_item);
				viewHolder.tv_menu_item = (TextView) convertView
						.findViewById(R.id.tv_menu_item);

				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.tv_menu_item.setText(list.get(position));

			// 高亮显示
			if (position == currendPosition) {
				viewHolder.iv_menu_item
						.setBackgroundResource(R.drawable.menu_arr_select);
				viewHolder.tv_menu_item.setTextColor(context.getResources()
						.getColor(R.color.red));
				convertView
						.setBackgroundResource(R.drawable.menu_item_bg_select);
			} else {
				// 去掉高亮
				viewHolder.iv_menu_item
						.setBackgroundResource(R.drawable.menu_arr_normal);
				viewHolder.tv_menu_item.setTextColor(context.getResources()
						.getColor(R.color.white));
				convertView.setBackgroundResource(R.drawable.transparent);
			}

			return convertView;
		}
	}

	class ViewHolder {
		ImageView iv_menu_item;
		TextView tv_menu_item;
	}
}
