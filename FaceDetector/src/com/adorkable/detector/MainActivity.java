package com.adorkable.detector;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facepp.error.FaceppParseException;
import com.umeng.analytics.MobclickAgent;
import com.adorkable.detector.util.FacePPDetect;
import com.adorkable.detector.util.FacePPDetect.CallBack;
import com.adorkable.detector.view.DialogUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private static final int PICK_CODE = 0x110;
	private static final int TAKE_PHOTO = 0x111;

	private static final int RESET_IMGVIEW = 0;
	private static final int MSG_SUCESS = 1;
	private static final int MSG_ERROR = 2;
	protected static final String TAG = "MainActivity";

	private ImageView mPhoto;
	private Button mGetIamge;
	private Button mTakePhoto;
	private TextView mTip;
	private View mWaitting;

	private String mCurrentPhotoStr; // 当前图片路径
	private File outputImage; // 拍照时候图片保存位置

	private Bitmap mPhotoImg;
	private Paint mPaint; // 用于绘制修改完成的图片

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_SUCESS:
				mWaitting.setVisibility(View.GONE);
				JSONObject jsonObject = (JSONObject) msg.obj;
				// System.out.println(jsonObject);
				// 绘制带有人脸识别框的图像
				prepareRsBitmap(jsonObject);

				// 此时的mPhotoImg已经被修改
				mPhoto.setImageBitmap(mPhotoImg);
				break;

			case MSG_ERROR:

				break;
			case RESET_IMGVIEW:
				mPhoto.setImageResource(R.drawable.girl);
				break;
			default:
				break;
			}
		}

	};
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏

		setContentView(R.layout.activity_main);

		initViews();
		initEvents();

		mPaint = new Paint();
	}

	/**
	 * 把一张图片绘制上人脸识别框
	 * 
	 * @param jsonObject
	 */
	private void prepareRsBitmap(JSONObject jsonObject) {
		Bitmap bitmap = Bitmap.createBitmap(mPhotoImg.getWidth(),
				mPhotoImg.getHeight(), mPhotoImg.getConfig());

		Canvas canvas = new Canvas(bitmap);
		canvas.drawBitmap(mPhotoImg, 0, 0, null);

		//
		try {
			JSONArray faces = jsonObject.getJSONArray("face");
			int faceCount = faces.length();

			mTip.setText("找到" + faceCount + "张脸");
			for (int i = 0; i < faceCount; i++) {
				JSONObject face = faces.getJSONObject(i);

				JSONObject position = face.getJSONObject("position");

				// api 中获取的是百分比
				float center_x = (float) position.getJSONObject("center")
						.getDouble("x");
				float center_y = (float) position.getJSONObject("center")
						.getDouble("y");

				float width = (float) position.getDouble("width");
				float height = (float) position.getDouble("height");

				center_x = center_x / 100 * bitmap.getWidth();
				center_y = center_y / 100 * bitmap.getHeight();

				width = width / 100 * bitmap.getWidth();
				height = height / 100 * bitmap.getHeight();

				// 绘制脸部矩形框
				mPaint.setColor(0xffffffff);
				mPaint.setStrokeWidth(1);

				canvas.drawLine(center_x - width / 2, center_y - height / 2,
						center_x + width / 2, center_y - height / 2, mPaint);
				canvas.drawLine(center_x - width / 2, center_y - height / 2,
						center_x - width / 2, center_y + height / 2, mPaint);
				canvas.drawLine(center_x + width / 2, center_y - height / 2,
						center_x + width / 2, center_y + height / 2, mPaint);
				canvas.drawLine(center_x - width / 2, center_y + height / 2,
						center_x + width / 2, center_y + height / 2, mPaint);

				// 获取年龄性别信息
				int age = face.getJSONObject("attribute").getJSONObject("age")
						.getInt("value");
				String gender = face.getJSONObject("attribute")
						.getJSONObject("gender").getString("value");
				// 绘制个人信息气泡
				Bitmap ageBitmap = buildAgeBitmap(age, "Male".equals(gender));

				// 对气泡的大小进行缩放
				int ageWidth = ageBitmap.getWidth();
				int ageHeight = ageBitmap.getHeight();

				// Log.i("Width:",
				// bitmap.getWidth()+"---"+mPhoto.getWidth()+"--"+bitmap.getHeight()+"---"+mPhoto.getWidth());

				if (ageWidth > width || ageHeight > height) {
					float ratio = (float) Math.max(width * 1.0f / ageWidth,
							height * 1.0 / ageHeight);
					ageBitmap = Bitmap.createScaledBitmap(ageBitmap,
							(int) (ageBitmap.getWidth() * ratio),
							(int) (ageBitmap.getHeight() * ratio), false);
				}
				canvas.drawBitmap(ageBitmap, center_x - ageBitmap.getWidth()
						/ 2, center_y - height / 2 - ageBitmap.getHeight(),
						mPaint);
				//
				mPhotoImg = bitmap;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 把一个TextView 转换成Bitmap 显示性别年龄的那个小图片
	 * 
	 * @param age
	 * @param isMale
	 * @return
	 */
	private Bitmap buildAgeBitmap(int age, boolean isMale) {
		//
		TextView tv = (TextView) findViewById(R.id.tv_show_age);
		tv.setText(age + "");

		if (isMale) {
			tv.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.ic_male), null, null, null);
		} else {
			tv.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.ic_female), null, null, null);
		}

		tv.setDrawingCacheEnabled(true);
		tv.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());
		tv.buildDrawingCache();

		Bitmap b = tv.getDrawingCache();
		Bitmap bitmap = Bitmap.createBitmap(b);
		tv.destroyDrawingCache();

		return bitmap;
	}

	private void initEvents() {
		//
		mGetIamge.setOnClickListener(this);
		mTakePhoto.setOnClickListener(this);
	}

	private void initViews() {
		//
		mPhoto = (ImageView) findViewById(R.id.iv_pic);
		mGetIamge = (Button) findViewById(R.id.btn_get_photo);
		mTakePhoto = (Button) findViewById(R.id.btn_take_photo);
		mTip = (TextView) findViewById(R.id.tv_tip);
		mWaitting = findViewById(R.id.fl_waitting);
	}

	/**
	 * 因为图片不能超过 3M 索引对图片大小进行处理
	 */
	private void resizePhoto() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(mCurrentPhotoStr, options);
		double ratio = Math.max(options.outWidth * 1.0d / 1024f,
				options.outHeight * 1.0d / 1024f);

		// 对double取整,对图片压缩
		options.inSampleSize = (int) Math.ceil(ratio);
		options.inJustDecodeBounds = false;
		mPhotoImg = BitmapFactory.decodeFile(mCurrentPhotoStr, options);
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onClick(View v) {
		//
		switch (v.getId()) {
		case R.id.btn_get_photo:
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType("image/*");
			startActivityForResult(intent, PICK_CODE);
			break;
		case R.id.btn_take_photo:

			// 图片名称 时间命名
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			Date date = new Date(System.currentTimeMillis());
			String filename = format.format(date);
			// 创建File对象用于存储拍照的图片 SD卡根目录
			// File outputImage = new
			// File(Environment.getExternalStorageDirectory(),test.jpg);
			// 存储至DCIM文件夹
			File path = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
			outputImage = new File(path, filename + ".jpg");
			try {
				if (outputImage.exists()) {
					outputImage.delete();
				}
				outputImage.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 将File对象转换为Uri并启动照相程序
			Uri imageUri = Uri.fromFile(outputImage);
			// Toast.makeText(MainActivity.this,
			// ""+outputImage.getAbsolutePath(), Toast.LENGTH_LONG).show();
			Intent takePhotoIntent = new Intent(
					"android.media.action.IMAGE_CAPTURE"); // 照相
			takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 指定图片输出地址
			startActivityForResult(takePhotoIntent, TAKE_PHOTO); // 启动照相
			// 拍完照startActivityForResult() 结果返回onActivityResult()函数
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//
		if (requestCode == PICK_CODE) {
			if (data != null && data.getData() != null) {
				Uri uri = data.getData();
				Cursor cursor = getContentResolver().query(uri, null, null,
						null, null);
				cursor.moveToFirst();

				int index = cursor
						.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
				mCurrentPhotoStr = cursor.getString(index);

				cursor.close();
				// Toast.makeText(MainActivity.this, mCurrentPhotoStr,
				// Toast.LENGTH_LONG).show();
				// 将图片压缩
				resizePhoto();
				// 将压缩后的图片放置到ImageView中
				mPhoto.setImageBitmap(mPhotoImg);
				mTip.setText("");

				dialog = DialogUtil.createLoadingDialog(this, "网速较慢请稍等~~~");
				dialog.show();

				// 判断网络连接情况
				ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo nInfo = cm.getActiveNetworkInfo();

				if (nInfo != null && nInfo.isConnected()) {
					System.out.println("网络可用");
					FacePPDetect.dectect(mPhotoImg, new CallBack() {

						@Override
						public void success(JSONObject result) {
							//
							Message msg = new Message();
							msg.what = MSG_SUCESS;
							msg.obj = result;
							mHandler.sendMessage(msg);
							dialog.cancel();
						}

						@Override
						public void error(FaceppParseException exception) {
							//
							// System.out.println("error" + exception);
							dialog.cancel();
							Toast.makeText(MainActivity.this, "网络错误",
									Toast.LENGTH_LONG).show();
						}
					});
				} else { // 网络不可用
					dialog.cancel();
					mTip.setText("亲~需要联网哦。。。");
					Toast.makeText(MainActivity.this, "请首先连接网络",
							Toast.LENGTH_LONG).show();
				}
			} else {
				System.out.println("取消选择");
				Message msg = new Message();
				msg.what = RESET_IMGVIEW;
				mHandler.sendMessage(msg);
				return;
			}
		} else if (requestCode == TAKE_PHOTO) {

			if (outputImage != null && outputImage.exists()) {
				if (outputImage.length() > 0) {
					mCurrentPhotoStr = outputImage.getAbsolutePath();
				} else {
					outputImage.delete();
					Message msg = new Message();
					msg.what = RESET_IMGVIEW;
					mHandler.sendMessage(msg);
					return;
				}
			} else {
				Message msg = new Message();
				msg.what = RESET_IMGVIEW;
				mHandler.sendMessage(msg);
				return;
			}

			// 将图片压缩
			resizePhoto();
			// 将压缩后的图片放置到ImageView中
			mPhoto.setImageBitmap(mPhotoImg);
			mTip.setText("");

			dialog = DialogUtil.createLoadingDialog(this, "网速较慢请稍等~~~");
			dialog.show();

			// 判断网络连接情况
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo nInfo = cm.getActiveNetworkInfo();

			if (nInfo != null && nInfo.isConnected()) {
				System.out.println("网络可用");
				FacePPDetect.dectect(mPhotoImg, new CallBack() {

					public void success(JSONObject result) {
						//
						Message msg = new Message();
						msg.what = MSG_SUCESS;
						msg.obj = result;
						mHandler.sendMessage(msg);
						dialog.cancel();
					}

					public void error(FaceppParseException exception) {
						//
						// System.out.println("error" + exception);
						dialog.cancel();
						Toast.makeText(MainActivity.this, "网络错误",
								Toast.LENGTH_LONG).show();
					}
				});
			} else { // 网络不可用
				dialog.cancel();
				mTip.setText("亲~需要联网哦。。。");
				Toast.makeText(MainActivity.this, "请首先连接网络", Toast.LENGTH_LONG)
						.show();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private long exitTime;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (System.currentTimeMillis() - exitTime > 2000) {
				Toast.makeText(getApplicationContext(), R.string.exit,
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				MobclickAgent.onKillProcess(MainActivity.this);
				System.exit(0);
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}