package com.xugaoxiang.ott.player.utils.bitmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.xugaoxiang.ott.player.utils.MD5Encoder;

public class LocalCacheUtils {

	public static final String DIR_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/MyApp/Movie";

	public Bitmap getBitmapFromLocal(String url) {
		try {
			File file = new File(DIR_PATH, MD5Encoder.encode(url));

			if (file.exists()) {
				Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(
						file));
				return bitmap;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void setBitmapToLocal(Bitmap bitmap, String url) {
		File dirFile = new File(DIR_PATH);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			dirFile.mkdirs();
		}

		try {
			File file = new File(DIR_PATH, MD5Encoder.encode(url));
			bitmap.compress(CompressFormat.JPEG, 100,
					new FileOutputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
