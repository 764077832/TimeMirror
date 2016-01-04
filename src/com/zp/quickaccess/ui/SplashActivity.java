package com.zp.quickaccess.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zp.quickaccess.domain.UpdateInfo;
import com.zp.quickaccess.engine.UpdateInfoService;
import com.zp.quickaccess.utils.LogUtil;
import com.zp.quickaccess.R;

public class SplashActivity extends Activity {

	protected static final String TAG = "SplashActivity";
	private Intent mainActivity;
	private LinearLayout ll_splash;
	private TextView tv_splashactivity_version;
	private Context context;

	private String curVersion = "1.0";
	public static final int NEED_UPDATE = 100;
	public static final int NOT_NEED_UPDATE = 101;
	public static final int DEFAULT_FINISH_SPLASH = 102;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case NEED_UPDATE:
				LogUtil.i(TAG, "��Ҫ����");
				startActivity(mainActivity);
				finish();
				showUpdateDialog(context);
				break;

			case NOT_NEED_UPDATE:
				LogUtil.i(TAG, "����Ҫ����");
				startActivity(mainActivity);
				finish();
				break;
				
			case DEFAULT_FINISH_SPLASH:
				LogUtil.i(TAG, "DEFAULT_FINISH_SPLASH");
				startActivity(mainActivity);
				finish();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		// Ϊsplash�������ö���
		ll_splash = (LinearLayout) findViewById(R.id.ll_splash);
		AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
		aa.setDuration(2000);
		ll_splash.setAnimation(aa);
		// ���ð汾��Ϣ
		tv_splashactivity_version = (TextView) findViewById(R.id.tv_splashactivity_version);
		tv_splashactivity_version.setText(" Time Mirror ");

		curVersion = getVersion();
		context = this;
		mainActivity = new Intent();
		mainActivity.setClass(this, MainActivity.class);

		/*
		new Thread() {
			@Override
			public void run() {
				
				boolean flag = isNeedUpdate(curVersion);
				if (flag) {
					// ��Ҫ���£���ô������ϢNEED_UPDATE����UI��֪ͨ�䵯�����¶Ի���
					handler.sendEmptyMessage(NEED_UPDATE);
				} else {
					// ����Ҫ���£���NOT_NEED_UPDATE��Ϣ��ֱ�ӽ���������
					handler.sendEmptyMessage(NOT_NEED_UPDATE);
				}
				
			}
		}.start(); // ��Ҫ���˿����߳�������
		*/
				
		//����һ��ʼ�������ӷ�������ȡ�°汾����������û�б�Ҫ������Ϊ����ʾsplash���˴�����4��
		new Thread(){
			public void run() {
				try {
					sleep(4000);
					handler.sendEmptyMessage(DEFAULT_FINISH_SPLASH);
					LogUtil.i(TAG, "DEFAULT_FINISH_SPLASH");
				} catch (InterruptedException e) {
					LogUtil.e(TAG, "InterruptedException : " + e.toString());
					e.printStackTrace();
				}
			};
		}.start();
	}

	/**
	 * ��ʾ�����Ի���
	 */
	private void showUpdateDialog(Context context) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("�°汾����");
		builder.setMessage("�ӷ�������ȡ�ĸ�����Ϣ����");
		builder.setCancelable(false); // �Ի��򲻿�ȡ��

		builder.setPositiveButton("ȷ��", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				LogUtil.i(TAG, "�û��������,����apk����");
			}
		});

		builder.setNegativeButton("ȡ��", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				LogUtil.i(TAG, "�û�ȡ������,�������������");
				// ����û����ȡ�����£���ô���Ͳ���Ҫ���²��������������Ϣ
				handler.sendEmptyMessage(NOT_NEED_UPDATE);
			}
		});

		builder.create().show();
	}

	/**
	 * �ж��Ƿ���Ҫ���°汾
	 * 
	 * ע���������4.0�Ժ��ǲ����������߳��н��к�ʱ����������˴����������
	 * ���isNeedUpdate()������Ҫ��һ�����߳���ִ�С���Ȼ�������̣߳���ôtoast��Ȼ�ǲ���������ʾ��
	 * ��Ҫôע�͵���Ҫôͨ��handler������Ϣ���и���
	 * 
	 * @param curVersion
	 *            ϵͳ��ǰ�汾��
	 */
	private boolean isNeedUpdate(String curVersion) {
		UpdateInfoService service = new UpdateInfoService(this);
		String newVersion = getVersion();
		try {
			UpdateInfo info = service.getUpdataInfo(R.string.updateurl);
			newVersion = info.getVersion();
			if (curVersion.equals(newVersion)) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * ��ȡ��ǰϵͳ�İ汾��Ϣ
	 * 
	 * @return �汾��
	 */
	public String getVersion() {
		try {
			PackageManager mPackageManager = getPackageManager();
			PackageInfo info = mPackageManager.getPackageInfo(getPackageName(),
					0);
			return info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return "�汾��δ֪";
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
