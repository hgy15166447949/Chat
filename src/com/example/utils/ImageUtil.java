package com.example.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

 
/**
 * Created on : June 18, 2016 Author : zetbaitsu Name : Zetra Email :
 * zetra@mail.ugm.ac.id GitHub : https://github.com/zetbaitsu LinkedIn :
 * https://id.linkedin.com/in/zetbaitsu
 */
public class ImageUtil {
	private static final String TAG = "ImageUtil";

	private ImageUtil() {

	}

	public static Bitmap getScaledBitmap(Context context, byte[] data,
			float maxWidth, float maxHeight) {
		// String filePath = FileUtil.getRealPathFromURI(context, imageUri);
		Bitmap scaledBitmap = null;

		BitmapFactory.Options options = new BitmapFactory.Options();

		// by setting this field as true, the actual bitmap pixels are not
		// loaded in the memory. Just the bounds are loaded. If
		// you try the use the bitmap here, you will get null.
		options.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length,
				options);

		int actualHeight = options.outHeight;
		int actualWidth = options.outWidth;

		float imgRatio = (float) actualWidth / actualHeight;
		float maxRatio = maxWidth / maxHeight;

		// width and height values are set maintaining the aspect ratio of the
		// image
		if (actualHeight > maxHeight || actualWidth > maxWidth) {
			if (imgRatio < maxRatio) {
				imgRatio = maxHeight / actualHeight;
				actualWidth = (int) (imgRatio * actualWidth);
				actualHeight = (int) maxHeight;
			} else if (imgRatio > maxRatio) {
				imgRatio = maxWidth / actualWidth;
				actualHeight = (int) (imgRatio * actualHeight);
				actualWidth = (int) maxWidth;
			} else {
				actualHeight = (int) maxHeight;
				actualWidth = (int) maxWidth;
			}
		}

		// setting inSampleSize value allows to load a scaled down version of
		// the original image
		options.inSampleSize = calculateInSampleSize(options, actualWidth,
				actualHeight);

		// inJustDecodeBounds set to false to load the actual bitmap
		options.inJustDecodeBounds = false;

		// this options allow android to claim the bitmap memory if it runs low
		// on memory
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inTempStorage = new byte[16 * 1024];

		try {
			// load the bitmap from its path
			bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
		} catch (OutOfMemoryError exception) {
			exception.printStackTrace();
		}
		try {
			scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,
					Bitmap.Config.RGB_565);
		} catch (OutOfMemoryError exception) {
			exception.printStackTrace();
		}

		float ratioX = actualWidth / (float) options.outWidth;
		float ratioY = actualHeight / (float) options.outHeight;
		float middleX = actualWidth / 2.0f;
		float middleY = actualHeight / 2.0f;

		Matrix scaleMatrix = new Matrix();
		scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

		Canvas canvas = new Canvas(scaledBitmap);
		canvas.setMatrix(scaleMatrix);
		canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2,
				middleY - bmp.getHeight() / 2, new Paint(
						Paint.FILTER_BITMAP_FLAG));

		// check the rotation of the image and display it properly
		/*
		 * ExifInterface exif; try { exif = new ExifInterface(filePath); int
		 * orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
		 * Matrix matrix = new Matrix(); if (orientation == 6) {
		 * matrix.postRotate(90); } else if (orientation == 3) {
		 * matrix.postRotate(180); } else if (orientation == 8) {
		 * matrix.postRotate(270); } scaledBitmap =
		 * Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(),
		 * scaledBitmap.getHeight(), matrix, true); } catch (IOException e) {
		 * e.printStackTrace(); }
		 */

		return scaledBitmap;
	}

	public static Bitmap getScaledBitmap(Context context, Uri imageUri,
			float maxWidth, float maxHeight) {
		// String filePath = FileUtil.getRealPathFromURI(context, imageUri);
		Bitmap scaledBitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
//		Bitmap bmp = BitmapFactory.decodeFile(imageUri.getPath(), options);
		String path = GetLocationPicturePath.getPath(context, imageUri);
		Bitmap bmp = BitmapFactory.decodeFile(path, options);
		int actualHeight = options.outHeight;
		int actualWidth = options.outWidth;
		float imgRatio = (float) actualWidth / (float) actualHeight;
		float maxRatio = maxWidth / maxHeight;
		if ((float) actualHeight > maxHeight || (float) actualWidth > maxWidth) {
			if (imgRatio < maxRatio) {
				imgRatio = maxHeight / (float) actualHeight;
				actualWidth = (int) (imgRatio * (float) actualWidth);
				actualHeight = (int) maxHeight;
			} else if (imgRatio > maxRatio) {
				imgRatio = maxWidth / (float) actualWidth;
				actualHeight = (int) (imgRatio * (float) actualHeight);
				actualWidth = (int) maxWidth;
			} else {
				actualHeight = (int) maxHeight;
				actualWidth = (int) maxWidth;
			}
		}

		options.inSampleSize = calculateInSampleSize(options, actualWidth,
				actualHeight);
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inTempStorage = new byte[16384];

		try {
//			bmp = BitmapFactory.decodeFile(imageUri.getPath(), options);
			path = GetLocationPicturePath.getPath(context, imageUri);
			bmp = BitmapFactory.decodeFile(path, options);
		} catch (OutOfMemoryError var23) {
			var23.printStackTrace();
		}
		if (actualWidth <= 0 || actualHeight <= 0) {
			actualWidth = (int) maxWidth;
			actualHeight = (int) maxHeight;
		}
		try {
			scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,
					Bitmap.Config.RGB_565);
		} catch (OutOfMemoryError var22) {
			var22.printStackTrace();
		}

		float ratioX = (float) actualWidth / (float) options.outWidth;
		float ratioY = (float) actualHeight / (float) options.outHeight;
		float middleX = (float) actualWidth / 2.0F;
		float middleY = (float) actualHeight / 2.0F;
		Matrix scaleMatrix = new Matrix();
		scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
		Canvas canvas = new Canvas(scaledBitmap);
		canvas.setMatrix(scaleMatrix);
		canvas.drawBitmap(bmp, middleX - (float) (bmp.getWidth() / 2), middleY
				- (float) (bmp.getHeight() / 2), new Paint(2));

		/*
		 * try { ExifInterface exif = new ExifInterface(filePath); int e =
		 * exif.getAttributeInt("Orientation", 0); Matrix matrix = new Matrix();
		 * if(e == 6) { matrix.postRotate(90.0F); } else if(e == 3) {
		 * matrix.postRotate(180.0F); } else if(e == 8) {
		 * matrix.postRotate(270.0F); }
		 * 
		 * scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
		 * scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true); }
		 * catch (IOException var21) { var21.printStackTrace(); }
		 */

		return scaledBitmap;
	}

	public static Bitmap qualityCompress(Bitmap bitmap, int quality) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Bitmap map = null;
		try {
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中 
			// int opt = quality;
			while ((baos.toByteArray().length / 1024) > 50) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩 
				baos.reset();// 閲嶇疆baos鍗虫竻绌篵aos
				bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);// 这里压缩options%，把压缩后的数据存放到baos中
				quality -= 10;// 姣忔閮藉噺灏�10
			}
			baos.flush();
			baos.close();
			String str = Base64.encodeToString(baos.toByteArray(),
					Base64.DEFAULT);
			Base64ImageEncode.getInstance().setIamgeString(str);
			ByteArrayInputStream isBm = new ByteArrayInputStream(
					baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
			map = BitmapFactory.decodeStream(isBm, null, null);//  把ByteArrayInputStream数据生成图片,然后再赋值给bitmap

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	public static void clearBitmap(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
		}
	}

	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		final float totalPixels = width * height;
		final float totalReqPixelsCap = reqWidth * reqHeight * 2;

		while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
			inSampleSize++;
		}

		return inSampleSize;
	}
}
