package com.xugaoxiang.ott.player.utils.bitmap;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class NetCacheUtils {

	private LocalCacheUtils mLocalUtils;
	private MemoryCacheUtils mMemoryUtils;

	public NetCacheUtils(LocalCacheUtils localUtils,
			MemoryCacheUtils memoryUtils) {
		mLocalUtils = localUtils;
		mMemoryUtils = memoryUtils;
	}

	public void getBitmapFromNet(ImageView imageView, String url) {
		BitmapTask task = new BitmapTask();
		task.execute(imageView, url);
	}

	class BitmapTask extends AsyncTask<Object, Integer, Bitmap> {

		private ImageView mImageView;
		private String url;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected Bitmap doInBackground(Object... params) {
			mImageView = (ImageView) params[0];
			url = (String) params[1];
			mImageView.setTag(url);
			return download(url);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				String bindUrl = (String) mImageView.getTag();
				if (bindUrl.equals(url)) {
					mImageView.setImageBitmap(result);
					System.out.println("网络下载图片成功!");
					mLocalUtils.setBitmapToLocal(result, url);
					mMemoryUtils.setBitmapToMemory(url, result);
				}
			}
		}

	}

	public Bitmap download(String url) {
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) (new URL(url).openConnection());

			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.setRequestMethod("GET");

			conn.connect();

			int responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				InputStream in = conn.getInputStream();
				Bitmap bitmap = BitmapFactory.decodeStream(in);
				return bitmap;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return null;
	}

}
