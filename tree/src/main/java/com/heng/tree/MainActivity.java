package com.heng.tree;

import java.util.ArrayList;

import com.heng.tree.R;
import com.heng.tree.adapter.ParentAdapter;
import com.heng.tree.adapter.ParentAdapter.OnChildTreeViewClickListener;
import com.heng.tree.entity.ChildEntity;
import com.heng.tree.entity.ParentEntity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

/**
 * 
 * @author Apathy、恒
 * <a href='http://blog.csdn.net/shexiaoheng'>http://blog.csdn.net/shexiaoheng</a >
 * @Detail 本Demo为ExpandableListView嵌套ExpandableListView实现三级菜单的例子
 * #ParentAdapter.OnChildTreeViewClickListener
 * 
 * */
public class MainActivity extends Activity implements OnGroupExpandListener,
		OnChildTreeViewClickListener {

	private Context mContext;
	private ExpandableListView eList;
	private ArrayList<ParentEntity> parents;
	private ParentAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;
		setContentView(R.layout.activity_main);
		loadData();
		initEList();
	}

	/**
	 * @author Apathy、恒
	 * 
	 *         初始化菜单数据源
	 * */
	private void loadData() {

		parents = new ArrayList<ParentEntity>();

		for (int i = 0; i < 10; i++) {

			ParentEntity parent = new ParentEntity();

			parent.setGroupName("父类父分组第" + i + "项");

			parent.setGroupColor(getResources().getColor(
					android.R.color.holo_red_light));

			ArrayList<ChildEntity> childs = new ArrayList<ChildEntity>();

			for (int j = 0; j < 8; j++) {

				ChildEntity child = new ChildEntity();

				child.setGroupName("子类父分组第" + j + "项");

				child.setGroupColor(Color.parseColor("#ff00ff"));

				ArrayList<String> childNames = new ArrayList<String>();

				ArrayList<Integer> childColors = new ArrayList<Integer>();

				for (int k = 0; k < 5; k++) {

					childNames.add("子类第" + k + "项");

					childColors.add(Color.parseColor("#ff00ff"));

				}

				child.setChildNames(childNames);

				childs.add(child);

			}

			parent.setChilds(childs);

			parents.add(parent);

		}
	}

	/**
	 * @author Apathy、恒
	 * 
	 *         初始化ExpandableListView
	 * */
	private void initEList() {

		eList = (ExpandableListView) findViewById(R.id.eList);

		eList.setOnGroupExpandListener(this);

		adapter = new ParentAdapter(mContext, parents);

		eList.setAdapter(adapter);

		adapter.setOnChildTreeViewClickListener(this);

	}

	/**
	 * @author Apathy、恒
	 * 
	 *         点击子ExpandableListView的子项时，回调本方法，根据下标获取值来做相应的操作
	 * */
	@Override
	public void onClickPosition(int parentPosition, int groupPosition,
			int childPosition) {
		// do something
		String childName = parents.get(parentPosition).getChilds()
				.get(groupPosition).getChildNames().get(childPosition)
				.toString();
		Toast.makeText(
				mContext,
				"点击的下标为： parentPosition=" + parentPosition
						+ "   groupPosition=" + groupPosition
						+ "   childPosition=" + childPosition + "\n点击的是："
						+ childName, Toast.LENGTH_SHORT).show();
	}

	/**
	 * @author Apathy、恒
	 * 
	 *         展开一项，关闭其他项，保证每次只能展开一项
	 * */
	@Override
	public void onGroupExpand(int groupPosition) {
		for (int i = 0; i < parents.size(); i++) {
			if (i != groupPosition) {
				eList.collapseGroup(i);
			}
		}
	}

}
