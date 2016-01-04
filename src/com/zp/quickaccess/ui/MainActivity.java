package com.zp.quickaccess.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.zp.quickaccess.adapter.MainUIAdapter;
import com.zp.quickaccess.service.FloatViewService;
import com.zp.quickaccess.utils.LogUtil;
import com.zp.quickaccess.R;

public class MainActivity extends Activity implements OnItemClickListener {

	private static final String TAG = "MainActivity";
	private GridView gv;
	private MainUIAdapter adapter;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ����û�б���
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ����ȫ����ȫ���Ƚϳ�һ�������
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main);
		ActivityManager.getInstance().addActivity(this);

		intent = new Intent();
		
		gv = (GridView) findViewById(R.id.gv_main);
		adapter = new MainUIAdapter(this);
		gv.setAdapter(adapter);

		gv.setOnItemClickListener(this);
	}

	/**
	 * gridview��Ŀ���������Ӧ�¼�
	 * 
	 * parent��gv view�����������Ŀ��Ӧ��view position���������Ŀ��λ�� id���������Ŀ��id
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 0: // ʹ�ð�������
			intent.setClass(this, HelpActivity.class);
			startActivity(intent);
			LogUtil.i(TAG, "����ʹ�ð�������");
			break;
		case 1: // Ӧ�����ý���
			intent.setClass(this, SettingActivity.class);
			startActivity(intent);
			LogUtil.i(TAG, "����Ӧ�����ý���");
			break;
		case 2: // Ӧ�ù������
			intent.setClass(this, AppManageActivity.class);
			startActivity(intent);
			LogUtil.i(TAG, "����Ӧ�ù������");
			break;
		case 3: // Ӧ����Ϣ�鿴����
			intent.setClass(this, ViewAppStaticsActivity.class);
			startActivity(intent);
			LogUtil.i(TAG, "����Ӧ����Ϣ�鿴����");
			break;
		}
	}
}
