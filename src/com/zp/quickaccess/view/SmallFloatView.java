package com.zp.quickaccess.view;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.zp.quickaccess.utils.CommonUtils;
import com.zp.quickaccess.utils.LogUtil;
import com.zp.quickaccess.utils.ScreenUtils;

/**
 * ��������Ĺ����д�����õ���ȫ�ֵ������еĹ���С����Ĺ���ʹ�õ��Ǳ��ص�windowmanager���е���ʾ���Ƴ� ��һ�����ң������߼�������ȷ
 * 
 */
public class SmallFloatView extends LinearLayout {

	private static final String TAG = "SmallFloatView";
	private Context context;
	// �������Ŀ�͸�
	private int viewWidth;
	private int viewHeight;
	// ״̬���߶�
	private static int statusBarHeight;
	// ����������
	private WindowManager mWindowManager;
	private FloatViewManager mFloatViewManager;
	public WindowManager.LayoutParams mLayoutParams;
	// ��ǰ�ĺ�������
	private float xInScreen;
	private float yInScreen;
	// �����Ļ�ĺ�������
	private float xTouchInScreen;
	private float yTouchInScreen;
	// ���λ�����������ϵ�����
	private float xTouchInFloatwindow;
	private float yTouchInFloatwindow;
	// ��ȡ��Ļ��ߵĹ�����
	private ScreenUtils mScreenUtils;
	// �Զ�����¼�������
	private OnClickListener listener;

	public SmallFloatView(Context context, int layoutResId, int rootLayoutId) {
		super(context);
		this.context = context;
		mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		mFloatViewManager = FloatViewManager.getInstance(context);
		// ��䲼��
		LayoutInflater.from(context).inflate(layoutResId, this);
		// ��ȡ�����е�view
		View view = findViewById(rootLayoutId);
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
		statusBarHeight = getStatusBarHeight();

		mScreenUtils = new ScreenUtils(context);

		mLayoutParams = new WindowManager.LayoutParams();
		// ������ʾ����Ϊphone
		mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		// ��ʾͼƬ��ʽ
		mLayoutParams.format = PixelFormat.RGBA_8888;
		// ���ý���ģʽ
		mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		// ���ö��뷽ʽΪ����
		mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
		mLayoutParams.width = viewWidth;
		mLayoutParams.height = viewHeight;
		// ���Ҳ����λ����ʾview
		mLayoutParams.x = mScreenUtils.getScreenWidth();
		mLayoutParams.y = mScreenUtils.getScreenHeight() / 2;

	}

	/*
	 * �Լ�����ĵ���¼�����û��ʹ�ÿ���ṩ��setOnClickListener()���õ���¼�
	 * 
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		// ��ָ����ʱ��¼��Ҫ������,�������ֵ����ȥ״̬���ĸ߶�
		case MotionEvent.ACTION_DOWN:
			// ��ȡ�����С������������
			xTouchInFloatwindow = event.getX();
			yTouchInFloatwindow = event.getY();
			// ����ʱ������λ�ã�ֻ��¼һ��
			xTouchInScreen = event.getRawX();
			yTouchInScreen = event.getRawY() - statusBarHeight;
			break;
		case MotionEvent.ACTION_MOVE:
			// ʱʱ�ĸ��µ�ǰ��ָ����Ļ�ϵ�λ��
			xInScreen = event.getRawX();
			yInScreen = event.getRawY() - statusBarHeight;
			// ��ָ�ƶ���ʱ�����С��������λ�ã��϶��������5���ص�ʱ����Ϊ�����϶�
			if (CommonUtils.abs(xInScreen - xTouchInScreen) > 5
					&& CommonUtils.abs(yInScreen - yTouchInScreen) > 5) {
				updateViewPosition();
				LogUtil.i(TAG, "�������϶���");
			}
			return false;
		case MotionEvent.ACTION_UP:
			 // ���ƶ������� + - 5����֮�ڣ�����Ϊ�ǵ���¼��������϶��¼�
			if (CommonUtils.abs(xTouchInScreen - event.getRawX()) < 5
					&& CommonUtils.abs(yTouchInScreen
							- (event.getRawY() - statusBarHeight)) < 5) {
				OnClicked();
				if (listener != null) {

				}
				return false;
			}
		}
		return true;
	}

	/**
	 * ����ʹ��ȫ�ֵĵ����е�mFloatViewManager.isBigWindowAdded�������������ж�
	 * ����ñ��صĲ���ֵ�����жϵ�ʱ�����ڴ������³�����С�������������������������ʾ�����������������֮�����йرգ�
	 * ��ô��û����else���߼����ͻ������Ҫ������βŻ��ٴ���ʾ��������������
	 * 
	 * Ϊ���ڴ��߼����Ӧ����Ҫ��FloatWindowBigView��initView()����
	 * ��ͬ��ʹ��mFloatViewManager.isBigWindowAdded����״̬�ж�
	 */
	@SuppressWarnings("static-access")
	public void OnClicked() {
		if (!mFloatViewManager.isBigWindowAdded) {
			// mWindowManager.addView(FloatwindowService.mFloatwindowBigView,
			// FloatwindowService.mFloatwindowBigView.bigWindowParams);
			// isBigviewAdded = true;
			
//			mFloatViewManager.createBigWindow(context);
//			mFloatViewManager.isBigWindowAdded = true;
			mFloatViewManager.addBigFloatWindow();
		} else {
			// mWindowManager.removeView(FloatwindowService.mFloatwindowBigView);
			
//			mFloatViewManager.removeBigWindow();
//			mFloatViewManager.isBigWindowAdded = false;
			
			mFloatViewManager.removeBigWindow();
		}
		LogUtil.i(TAG, "�����򱻵����");
	}

	public void setOnClickListener(OnClickListener listener) {
		this.listener = listener;
	}

	public interface OnClickListener {
		public void click();
	}

	/**
	 * 
	 * @comment ���϶��������ָ��ֵ��ʱ����Ϊ�������϶��¼�������view��λ��
	 * 
	 * @param    
	 * @return void  
	 * @throws
	 * @date 2015-12-28 ����1:50:41
	 */
	private void updateViewPosition() {
		mLayoutParams.x = (int) (xInScreen - xTouchInFloatwindow);
		mLayoutParams.y = (int) (yInScreen - yTouchInFloatwindow);
		mWindowManager.updateViewLayout(this, mLayoutParams);
	}

	/**
	 * 
	 * @comment ��ȡ״̬���߶�
	 * @param @return   
	 * @return int  
	 * @throws
	 * @date 2015-12-28 ����1:51:32
	 */
	private int getStatusBarHeight() {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object o = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = (Integer) field.get(o);
			return getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
