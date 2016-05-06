package com.comcons.activity;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.comcons.activity.R;
import com.comcons.bean.ItemInfo;
import com.comcons.dao.NetworkDataInfoDAO;
import com.comcons.myinterface.IBinderMethodInterface;
import com.comcons.service.InternetStateMoniteService;
import com.comcons.util.InitCheck;
import com.comcons.util.TextFormater;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author LiuQiang
 *
 */
public class MainActivity extends Activity {

	private final int SHOW_VIEW = 1;
	private final int UPDATE_VIEW = 2;
	
	private List <ItemInfo> itemInfos ;
	private ListView listView ;
	private MyAdapter adapter;
	
	private MyServiceConnection myServiceConnection;
	private IBinderMethodInterface iMethod ;
	
	private ProgressDialog m_pDialog;
	
	/**
	 * 为了用于定期刷新
	 */
	private Timer timer ;
	private TimerTask timerTask;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_VIEW:
				
				try{
					iMethod.loadOldDataMap();
				}catch (Exception e) {
					try {
						Thread.sleep(300);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					iMethod.loadOldDataMap();
				}
				itemInfos = iMethod.getAllPackageInfo();
				if(itemInfos==null || itemInfos.isEmpty()){
					Toast.makeText(MainActivity.this, "请打开无线网络后再重新开启程序", Toast.LENGTH_LONG).show();
					adapter = new MyAdapter();
					listView.setAdapter(adapter);
					m_pDialog.hide();
				}else{
					
					Collections.sort(itemInfos);
					adapter = new MyAdapter();
					listView.setAdapter(adapter);
					m_pDialog.hide();
					
					//如果服务没有启动
					if(!InitCheck.isServiceRunning(MainActivity.this,"InternetStateMoniteService")){
						System.out.println("服务没有运行 0.0");
						AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
						builder.setMessage("为了能时刻监测流量的变化,请在你的安全软件的自启动管理中添加本应用!")
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								InitCheck.startSysAutoStartManger(MainActivity.this); 
							}
						})
						.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
						builder.create();
						builder.show();
					}
				}
				
				break;

			case UPDATE_VIEW:
				itemInfos = iMethod.getAllPackageInfo();
				Collections.sort(itemInfos);
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
			
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//创建ProgressDialog对象
        m_pDialog = new ProgressDialog(this);
        // 设置进度条风格，风格为圆形，旋转的
        m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // 设置ProgressDialog 提示信息
        m_pDialog.setMessage("程序正在加载，稍等！");
        // 设置ProgressDialog 的进度条是否不明确
        m_pDialog.setIndeterminate(false);
        // 设置ProgressDialog 是否可以按退回按键取消
        m_pDialog.setCancelable(false);
        // 让ProgressDialog显示
        m_pDialog.show();
		
		
		/**
		 * 暂时用这种方式开启
		 */
		startService(new Intent(this, InternetStateMoniteService.class));
		
		/**
		 * 初始化MyServiceConnection
		 * 以便于在绑定和解绑Service时使用
		 */
		myServiceConnection = new MyServiceConnection() ;
		Intent intent = new Intent(MainActivity.this, InternetStateMoniteService.class);
		bindService(intent, myServiceConnection, BIND_AUTO_CREATE);
		
		/**
		 * 获取ListView对象
		 */
		listView = (ListView) findViewById(R.id.list_view);
		
		/**
		 * 如果不暂停的话  绑定Service线程貌似是异步的
		 * 
		 */
		new Thread(){
			public void run() {
				try {
					while(iMethod==null){
						sleep(100);
					}
					/**
					 * 当获取到iMethod对象的时候
					 * 通知主线程可以显示数据了
					 */
					Message msg = new Message();
					msg.what = SHOW_VIEW;
					handler.sendMessage(msg);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();
		
	}

	@Override
	protected void onStart() {
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				//发送一消息通知主线程更新界面
				Message msg = new Message();
				msg.what = UPDATE_VIEW;
				handler.sendMessage(msg);
			}
		};
		timer.schedule(timerTask, 1000, 3000);
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		timer.cancel();
		timer = null;
		timerTask = null;
		super.onStop();
	}
	
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
		 // Inflate the menu; this adds items to the action bar if it is present.
	     getMenuInflater().inflate(R.menu.main, menu);
	     return true;
	 }

	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
		 // Handle action bar item clicks here. The action bar will
	     // automatically handle clicks on the Home/Up button, so long
	     // as you specify a parent activity in AndroidManifest.xml.
	     int id = item.getItemId();
	     if (id == R.id.action_settings) {
	    	 new NetworkDataInfoDAO(this).clearData();    //清零
	    	 
	    	 try{
	    		 iMethod.loadOldDataMap();    //重新加载数据库中的数据
	    	 }catch(Exception e){
	    		 try {
					Thread.sleep(500);
					iMethod.loadOldDataMap();    //重新加载数据库中的数据
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    	 }
	    	 
	    	 itemInfos = iMethod.getAllPackageInfo();    //重新获取数据
	    	 adapter.notifyDataSetChanged();    //更新显示
	    	 
	         return true;
	     }
	     return super.onOptionsItemSelected(item);
	 }
	
	private class MyServiceConnection implements ServiceConnection{

		/**
		 * 当绑定成功后会调用这个方法
		 */
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			//对接收到IBinder对象进行强制类型转换
			iMethod = (IBinderMethodInterface)service;  
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			System.out.println("dis connect");
		}
		
	}
	
	
	/**
	 * s实现数据适配器类
	 * @author LiuQiang
	 *
	 */
	private class MyAdapter extends BaseAdapter{

		private ImageView appIconView ;
		private TextView appNameView ;
		private TextView appRxView ;
		private TextView appTxView ;
		private TextView appTotalView ;
		private ItemInfo info;
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return itemInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			View view = null;
			if(convertView == null){
				view = View.inflate(
						getApplicationContext(), 
						R.layout.list_view_item,
						null);
			}else{
				view = convertView;
			}
			
			appIconView = (ImageView) view.findViewById(R.id.app_icon_iv);
			appNameView = (TextView) view.findViewById(R.id.app_name_tv);
			appRxView = (TextView) view.findViewById(R.id.app_rx_tv);
			appTxView = (TextView) view.findViewById(R.id.app_tx_tv);
			appTotalView = (TextView) view.findViewById(R.id.app_total_tv);
			
			info = itemInfos.get(position);
			appIconView.setImageDrawable(info.getAppIcon());
			appNameView.setText(info.getAppName());
			appRxView.setText(TextFormater.getDataSize(info.getMobileRx()));
			appTxView.setText(TextFormater.getDataSize(info.getMobileTx()));
			appTotalView.setText(TextFormater.getDataSize(info.getMobileRx()+info.getMobileTx()));
			
			return view;
		}
		
	}
	
}
