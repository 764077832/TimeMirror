package com.zp.quickaccess.utils;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class IconUtils {

	/**
	 *  Drawable ����תΪBitmap
	 *  
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitamp(Drawable drawable) {
		BitmapDrawable bd = (BitmapDrawable) drawable;
		return bd.getBitmap();
	}
	
	/**
	 *  Drawable����ת��Ϊbyte����
	 * 
	 * @param drawable
	 * @return
	 */
	public static byte[] getIconData(Drawable drawable) {
		// ��Drawable����ת��Ϊbitmap����
		Bitmap bmp = IconUtils.drawableToBitamp(drawable);
		int size = bmp.getWidth() * bmp.getHeight() * 4;
		// ��Bitmapת��Ϊ���沢����
		ByteArrayOutputStream out = new ByteArrayOutputStream(size);
		try {
			bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.close();
		} catch (Exception e) {
			return null;
		}
		return out.toByteArray();
	}
	
	/**
	 * bytes����ת��ΪBitmap
	 * 
	 * @param bytes
	 * @return
	 */
	public static Bitmap getBitmapFromBytes(byte bytes[]){
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}
	
	/**
	 * Bitmapת��ΪDrawable
	 * 
	 * @param bmp
	 * @return
	 */
	public static Drawable getDrawableFromBitmap(Bitmap bmp){
		return new BitmapDrawable(bmp);
	}
	
}
