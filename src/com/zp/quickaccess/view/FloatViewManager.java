package com.zp.quickaccess.view;

import com.zp.quickaccess.R;

import android.content.Context;
import android.view.WindowManager;

/**
 * 
 * @file FloatViewManager.java
 * @package com.zp.quickaccess.view 
 * @comment ����ģʽ��FloatViewManager�����ڹ����С����Ĵ�����ʾ�Ƴ���
 * @author zp
 * @date 2015-12-29 ����9:05:11
 */
public class FloatViewManager {

	// С����������
	public SmallFloatView mSmallWindow = null;
	public static boolean isSmallWindowAdded = false;
	// ������������
	public BigFloatView mBigWindow = null;
	public static boolean isBigWindowAdded = false;
	// ���ڿ�������Ļ����ӻ��Ƴ�������
	private WindowManager mWindowManager;
	// mFloatViewManager�ĵ���
	private static FloatViewManager mFloatViewManager;
	// �����Ķ���
	private Context context;

	private FloatViewManager(Context context) {
		this.context = context;
		mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	}

	public static FloatViewManager getInstance(Context context) {
		if (mFloatViewManager == null) {
			mFloatViewManager = new FloatViewManager(context);
		}
		return mFloatViewManager;
	}

	public void setOnClickListener(SmallFloatView.OnClickListener listener) {
		if (mSmallWindow != null) {
			mSmallWindow.setOnClickListener(listener);
		}
	}

	/*
	 * ����������
	 */
	private void createBigWindow(Context context) {
		if (mBigWindow == null) {
			mBigWindow = new BigFloatView(context);
		}
	}
	
	private void cteateSmallFloatWindow() {
		if (mSmallWindow == null) {
			mSmallWindow = new SmallFloatView(context, R.layout.floatview_small, R.id.floatwindow_layout);
		}
	}

	/*
	 * ������������ӵ�����
	 */
	public void addBigFloatWindow() {
		if (mBigWindow == null) {
			createBigWindow(this.context);
			mWindowManager.addView(mBigWindow, mBigWindow.bigWindowParams);
			isBigWindowAdded = true;
		}
	}
	
	public void addSmallFloatWindow() {
		if (mSmallWindow == null) {
			cteateSmallFloatWindow();
			mWindowManager.addView(mSmallWindow, mSmallWindow.mLayoutParams);
			isSmallWindowAdded = true;
		}
	}
	
	/*
	 * ������������Ļ���Ƴ�
	 */
	public void removeBigWindow() {
		if (mBigWindow != null) {
			mWindowManager.removeView(mBigWindow);
			mBigWindow = null;
			isBigWindowAdded = false;
		}
	}
	
	public void removeSmallWindow() {
		if (mSmallWindow != null) {
			mWindowManager.removeView(mSmallWindow);
			mSmallWindow = null;
			isSmallWindowAdded = false;
		}
	}

	public void removeAll() {
		removeSmallWindow();
		removeBigWindow();
	}

	/**
	 * 
	 * @comment  �Ƿ�����������ʾ(����С�������ʹ�����)
	 * @return boolean  ����������ʾ�������Ϸ���true��û�еĻ�����false
	 */
	public boolean isWindowShowing() {
		return mSmallWindow != null || mBigWindow != null;
	}
	
	public boolean isSmallWindowShowing() {
		return mSmallWindow != null ;
	}
	
	public boolean isBigWindowShowing() {
		return mSmallWindow != null ;
	}
}

