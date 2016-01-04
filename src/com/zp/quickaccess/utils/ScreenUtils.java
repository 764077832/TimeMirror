package com.zp.quickaccess.utils;

import android.content.Context;
import android.view.WindowManager;

public class ScreenUtils {
	private Context context;
	private static WindowManager mWindowManager;
	
	public ScreenUtils(Context c){
		this.context = c;
		mWindowManager = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
	}
	/**
	 * ��ȡ��Ļ���
	 * @param c
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static int getScreenWidth() {
		return mWindowManager.getDefaultDisplay().getWidth();
	}

	/**
	 * ��ȡ��Ļ�߶�
	 * @param c
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static int getScreenHeight() {
		return mWindowManager.getDefaultDisplay().getHeight();
	}

}
