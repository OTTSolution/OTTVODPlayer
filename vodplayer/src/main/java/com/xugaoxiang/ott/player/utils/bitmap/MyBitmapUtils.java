package com.xugaoxiang.ott.player.utils.bitmap;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 三级缓存工具类
 * 
 * @author Kevin
 * @date 2015-8-15
 */
public class MyBitmapUtils {

	private NetCacheUtils mNetUtils;
	private LocalCacheUtils mLocalUtils;
	private MemoryCacheUtils mMemoryUtils;

	public MyBitmapUtils() {
		mMemoryUtils = new MemoryCacheUtils();
		mLocalUtils = new LocalCacheUtils();
		mNetUtils = new NetCacheUtils(mLocalUtils, mMemoryUtils);
	}

	public void display(ImageView imageView, String url) {
		Bitmap bitmap = mMemoryUtils.getBitmapFromMemory(url);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
			return;
		}

		bitmap = mLocalUtils.getBitmapFromLocal(url);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
			mMemoryUtils.setBitmapToMemory(url, bitmap);
			return;
		}

		mNetUtils.getBitmapFromNet(imageView, url);
	}

}
