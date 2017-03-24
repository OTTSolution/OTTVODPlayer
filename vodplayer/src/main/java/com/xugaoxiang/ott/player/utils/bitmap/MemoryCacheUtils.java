package com.xugaoxiang.ott.player.utils.bitmap;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class MemoryCacheUtils {

	private LruCache<String, Bitmap> mCache;

	public MemoryCacheUtils() {
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		mCache = new LruCache<String, Bitmap>(maxMemory / 8) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				int size = value.getRowBytes() * value.getHeight();
				return size;
			}
		};
	}

	public Bitmap getBitmapFromMemory(String url) {

		return mCache.get(url);
	}

	public void setBitmapToMemory(String url, Bitmap bitmap) {
		mCache.put(url, bitmap);
	}

}
