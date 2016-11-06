package com.zp.quickaccess.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.zp.quickaccess.ui.AppContext;
import com.zp.quickaccess.utils.LogUtil;
import com.zp.quickaccess.view.FloatViewManager;

/**
 * �������Ĺ������������Ƿ���ʾ�Ŀ���
 * 
 * ֮ǰ�г����Զ�������ʽ��ʾ�Զ��崰�壬���ڴ����С�Լ���ʾλ������ȷ�������û�в��� ����ʹ������Ķ����������ʽ��ʾ
 * 
 * ������������ǲ���Ҫһֱ�����ں�̨�ģ���˲��ð󶨿����ķ�ʽ�����Ա��û���ʽ�Ĺر�
 * 
 * ���������������ʱ����ʾ������ͬʱ����������񣻵��û�����رո�����ʱ���Ƴ�����ͬʱ�رո÷���
 * �˴����ڸ÷���Ŀ����͹ر�û�м������ֶ��ǽ�����һ��StartCommand����������ʾ�������ǹرո���
 * û�жԷ������رս��п��ƣ�����ɲ���Ҫ�ķ�������С�ͬʱ����StartCommand��
 *  intent.getIntExtra(OPERATION, SHOW_FLOATWINDOW);
 * ��Ӧ�ùرղ�����ʱ�����ǵ��¿�ָ���쳣
 * 
 * ����취����ʹ�ð󶨷���ķ�ʽ�����������Ŀ����رպͷ��������������ϵ��һ�𼴿ɣ�ȥ��StartCommand�е��߼�
 * 
 */
public class FloatViewService extends Service {

	private static final String TAG = "FloatViewService";
	public static final String OPERATION = "operation";

	public static final int SHOW_FLOATWINDOW = 100;
	public static final int HIDE_FLOATWINDOW = 101;
	private static final int HANDLE_HIDE_FLOATWINDOW = 102;
	private static final int HANDLE_SHOW_FLOATWINDOW = 103;
	private static final int HANDLE_CHECK_ACTIVITY = 200;

	// ��ʼʱ��������û����ӵģ�Ҳ�������ص�
	//public boolean isAdded = false; // �����ж�������ʵ���Ƿ������ʾ
	public boolean isHided = true; // �����ж��û��Ƿ�������������ʾ
	public boolean triger = true; // ���ڴ���HANDLE_CHECK_ACTIVITY��Ϣ��ʵ��ѭ��

	private Context context;
	private FloatViewManager mFloatViewManager;
	private static int operation;

	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		mFloatViewManager = FloatViewManager.getInstance(context);

		LogUtil.i(TAG, "FloatViewService onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.i(TAG, "onStartCommand");
		/*
		 *  ϵͳ�����������ʱ�򣬴���intentΪ�յ����Σ�����Ӧ�ñ���
		 *  ���Դ˴������˷ǿ��жϣ���Ȼ���Ա�֤Ӧ�ò������������ڱ������Ż��رպ��ܱ�ϵͳ����
		 *  ��������ǽ�WatchdogService������Ϊ��deamon����
		 */
		if (intent == null) {
			LogUtil.i(TAG, "intent == null");
		} else {
			LogUtil.i(TAG, "intent != null");

			operation = intent.getIntExtra(OPERATION, SHOW_FLOATWINDOW);
			switch (operation) {
			case SHOW_FLOATWINDOW:
				mHandler.sendEmptyMessage(HANDLE_SHOW_FLOATWINDOW);
				break;
			case HIDE_FLOATWINDOW:
				mHandler.sendEmptyMessage(HANDLE_HIDE_FLOATWINDOW);
				break;
			}
			LogUtil.i(TAG, "operation : " + operation);
		}

		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * �˴��Ĵ�����ʾ����������Ϣ������΢������һЩ�������߼�Ϊ��
	 * 
	 * 1.�����ǰ�û����������ʾ����������ô�������ж�isAdded�Ƿ���ӣ����û�б���ӣ���ô�ͽ������
	 * ͬʱ����FloatViewManager.isSmallWindowAddedΪtrue,isHidedΪfalse
	 * 
	 * 2.����û���ǰ���������������ť����ô����FloatViewManager.isSmallWindowAdded�Ƿ���ӣ��������ˣ���ô�����Ƴ�������Ϊ��
	 * ͬʱ����FloatViewManager.isSmallWindowAddedΪfalse,isHidedΪtrue
	 * 
	 * 3.������������ť�ض�����һ������HANDLE_CHECK_ACTIVITY�¼������ǾͿ���ÿ100ms���һ�ε�ǰ�Ƿ�������
	 * ��������棬����������������Ϊ��������(isHidedΪfalse)��ô�Ͱ�1���߼����ж��������Ƿ������ʾ����δ��ӣ����������ʾ
	 * ����������� �����������õ�Ϊ������(isHidedΪtrue)��ô����Ҫ�ж��������Ƿ�������ʾ(FloatViewManager.isSmallWindowAddedΪtrue)�����������ʾ����ô�����Ƴ�
	 * 
	 * ��Ҫע����ǣ������������Ӻ�ɾ������ͨ��FloatViewManager��ʵ�ֵģ�������Ӻ�ɾ����������ɶԳ���
	 * ������FloatViewManager���ڶ������ʹ�ã������亯���ķ�װһ��Ҫ��֤���ֳɶӵ��ص�
	 * ����ͻ�����Ѿ���Ӵ�����쳣�����ظ��Ƴ�����ĵ��쳣
	 * 
	 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_CHECK_ACTIVITY:
				// ֻ�е��û�������ʾ��ʱ����ж��Ƿ������棬�Ծ����Ƿ���ʾ
				boolean isOpenFloatview = AppContext.getSharedPreferences().getBoolean("isOpenFloatview", false);
				if (isOpenFloatview) {
					
					if (AppContext.isHome()) {
						mFloatViewManager.addSmallFloatWindow();
					} else {
						if (FloatViewManager.isSmallWindowAdded)
							mFloatViewManager.removeSmallWindow();
					}
				}else{
					/*
					 *  ����û����õ��ǲ���ʾ����ô�ͽ�����ر�
					 *  
					 *  ��ʱ�ر��Լ����Լ���ϵͳ��Դ���ģ�ȱ����ĳЩ����¿�����Ӧ���Ǻܼ�ʱ
					 *  �Ͼ��´ο�����Ҫ���¿���������ҪWatchdog�����俪��
					 */
					stopSelf();
				}
				// 100����ִ��һ�μ�飬�ж��Ƿ����л����������������Ӧ�ã����ж��Ƿ���ʾ������
				mHandler.sendEmptyMessageDelayed(HANDLE_CHECK_ACTIVITY, 100);
				break;

			case HANDLE_SHOW_FLOATWINDOW:
					mFloatViewManager.addSmallFloatWindow();
					LogUtil.i(TAG, "mFloatViewManager.addSmallFloatWindow()");
					isHided = false;
				// Ϊ��ʹHANDLE_CHECK_ACTIVITY��Ϣ�ܹ�������(����Ϣû�ж�Ӧ��ť����¼���������Ҫ���д���)
				// �˴���һ������������֤�䱻����һ�μ��ɣ�����������Ե�ִ��
				if (triger) {
					mHandler.sendEmptyMessage(HANDLE_CHECK_ACTIVITY);
					triger = false;
				}
				LogUtil.i(TAG, "HANDLE_SHOW_FLOATWINDOW");
				break;

			case HANDLE_HIDE_FLOATWINDOW:
				if(FloatViewManager.isSmallWindowAdded){
					mFloatViewManager.removeSmallWindow();
					isHided = true;
					LogUtil.i(TAG, "mFloatViewManager.addSmallFloatWindow()");
				}
				if (triger) {
					mHandler.sendEmptyMessage(HANDLE_CHECK_ACTIVITY);
					triger = false;
				}
				LogUtil.i(TAG, "HANDLE_HIDE_FLOATWINDOW");
				break;
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "FloatViewService onDestroy");
	}

}