package com.adorkable.detector.util;

import java.io.ByteArrayOutputStream;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Log;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

public class FacePPDetect {

	public interface CallBack {
		
		void success(JSONObject result);
		void error(FaceppParseException exception);
	}

	/**
	 * 比较耗时的操作交给单独的线程来完成
	 * 
	 * @param bitmap
	 * @param callBack
	 */
	public static void dectect(final Bitmap bitmap, final CallBack callBack) {
		new Thread(new Runnable() {
			@Override
			public void run() {
//				Log.i("Dected", "dected 函数调用");
				//
				HttpRequests requests = new HttpRequests(Constant.KEY,
						Constant.SECRET, true, true);
				Bitmap bitmapTemp = Bitmap.createBitmap(bitmap, 0, 0,
						bitmap.getWidth(), bitmap.getHeight());
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmapTemp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				
				byte[] array = stream.toByteArray();
				
				PostParameters parameters = new PostParameters();
				parameters.setImg(array);
				
				try {
					JSONObject jsonObject = requests.detectionDetect(parameters);
					Log.i("Dected", ""+jsonObject);
					if(callBack!=null){
						callBack.success(jsonObject);
					}
				} catch (FaceppParseException e) {
					Log.i("Dected", ""+e);
					if(callBack!=null){
						callBack.error(e);
					}
					e.printStackTrace();
				}
				
			}
		}).start();
	}
}
