package com.zp.quickaccess.ui;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zp.quickaccess.animation.TVAnimation;
import com.zp.quickaccess.domain.AppUseStatics;
import com.zp.quickaccess.service.FloatViewService;
import com.zp.quickaccess.service.WatchdogService;
import com.zp.quickaccess.utils.LogUtil;
import com.zp.quickaccess.R;

/**
 * Ӧ�����ý��棺��Ҫ�����������߹ر����������ֶ������Լ��ĳ���Ӧ�ã������£����ڣ������ʷ����(����)
 * 
 * ע���������Ƿ�����״̬��ʶ��Ҫд�뵽sp�У����ֻ���ڳ�����ʹ����ʱ�������棬�����ܼ�¼�û���ʵ������״̬
 * 
 */
public class SettingActivity extends Activity implements OnClickListener {
	private static final String TAG = "SettingActivity";
	private final static int INIT_DATA_FINISHED = 200;
	private final static int CLEAR_CACHE_FINISHED = 400;
	private final static int CLEAR_CACHE_CANCEL = 401;

	private LinearLayout ll_set_floatview_status;
	private LinearLayout ll_set_about_author;
	private LinearLayout ll_set_product_info;
	private LinearLayout ll_set_clear_cache;
	private LinearLayout ll_set_check_update;
	private LinearLayout ll_set_exit;
	private TextView tv_set_floatview_status;
	private TextView tv_common_title;
	// �Ƿ����������ı�־λ����ʼΪfalse����Ҫ��SharedPreference�ж�ȡ�û���ʵ������ֵ
	private SharedPreferences sp;
	private ProgressDialog pd;
	
	private boolean isOpenFloatview = false;
	private Intent mFloatViewService;
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CLEAR_CACHE_FINISHED:
				pd.dismiss();
				Toast.makeText(SettingActivity.this, "�����Ѿ����", 0).show();
				break;
			case CLEAR_CACHE_CANCEL:
				Toast.makeText(SettingActivity.this, "ȡ�����", 0).show();
				break;
				// ���ݳ�ʼ�����
			case INIT_DATA_FINISHED:
				pd.dismiss();
				Intent watchdogService = new Intent(SettingActivity.this, WatchdogService.class);
				startService(watchdogService);
				break;
			}
		
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);

		initViews();
		
		boolean isFirst = AppContext.getSharedPreferences().getBoolean("isFirst", true);
		// �������̼߳���Ӧ����Ϣ
		/**
		 * ����ǵ�һ�ο���ҳ�棬��ô˵�����ݿ�Ϊ�գ�WatchdogServiceҲû�п���
		 * 
		 * ���Կ����ڱ�Activity����APPManageActivity��������ݵ����ݿⲢ����WatchdogService��������ͳ��
		 * 
		 * Ĭ������²��������������ͳ��
		 */
		if (isFirst) {
			pd = ProgressDialog.show(SettingActivity.this, "", "���ڼ����������Ժ򡭡�", true, false);
			new Thread() {
				public void run() {
					List<AppUseStatics> infos = AppContext.mAppInfoProvider.getAllApps();
					// �����ȼ������������
					Collections.sort(infos);
					AppContext.mDBManager.addAll(infos);
					Editor editor = AppContext.mSharedPreferences.edit();
					editor.putBoolean("isFirst", false);
					editor.commit();
					Message msg = new Message();
					msg.what = INIT_DATA_FINISHED;
					handler.sendMessage(msg);
				};
			}.start();
		}
		
	}

	private void initViews() {
		ll_set_floatview_status = (LinearLayout) findViewById(R.id.ll_set_floatview_status);
		ll_set_about_author = (LinearLayout) findViewById(R.id.ll_set_about_author);
		ll_set_product_info = (LinearLayout) findViewById(R.id.ll_set_product_info);
		ll_set_clear_cache = (LinearLayout) findViewById(R.id.ll_set_clear_cache);
		ll_set_check_update = (LinearLayout) findViewById(R.id.ll_set_check_update);
		ll_set_exit = (LinearLayout) findViewById(R.id.ll_set_exit);

		tv_set_floatview_status = (TextView) findViewById(R.id.tv_set_floatview_status);
		// �������ı�
		tv_common_title = (TextView) findViewById(R.id.tv_common_title);
		tv_common_title.setText(R.string.app_set_tv_title);
		
		sp = AppContext.getSharedPreferences();
		pd = new ProgressDialog(this);
		
		ll_set_about_author.setOnClickListener(this);
		ll_set_product_info.setOnClickListener(this);
		ll_set_clear_cache.setOnClickListener(this);
		ll_set_check_update.setOnClickListener(this);
		ll_set_exit.setOnClickListener(this);
		ll_set_floatview_status.setOnClickListener(this);
		
		// ��Activity��finish��֮��service��ô�죿��������ڷ������׳���ָ���쳣
		mFloatViewService = new Intent(getApplicationContext(),FloatViewService.class);
		// ��Activity��ӵ�����ջ��
		ActivityManager.getInstance().addActivity(SettingActivity.this);
	}

	/**
	 * ������������״̬��Ҫ����ʵ�����ν������ã������ڴ���ɼ���ʱ������û����ö���״̬���и���
	 */
	@Override
	protected void onResume() {
		super.onResume();
		// ��sp�л�ȡ�û�����״̬
		isOpenFloatview = sp.getBoolean("isOpenFloatview", false);
		if(isOpenFloatview){
			tv_set_floatview_status.setText("�رո���");
		}else{
			tv_set_floatview_status.setText("��������");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// �������Ŀ����͹ر�
		case R.id.ll_set_floatview_status:
			/*
			 * ������ʵ�ֲο�
			 * http://blog.csdn.net/stevenhu_223/article/details/8504058
			 */
			if (isOpenFloatview) {
				tv_set_floatview_status.setText("��������");

				isOpenFloatview = false;
				Editor editor = sp.edit();
				editor.putBoolean("isOpenFloatview", isOpenFloatview);
				editor.commit();

				mFloatViewService.putExtra(FloatViewService.OPERATION,FloatViewService.HIDE_FLOATWINDOW);
				startService(mFloatViewService);
				Toast.makeText(this, "�������Ѿ��ر�", 0).show();
			} else {
				tv_set_floatview_status.setText("�رո���");
				
				isOpenFloatview = true;
				Editor editor = sp.edit();
				editor.putBoolean("isOpenFloatview", isOpenFloatview);
				editor.commit();

				mFloatViewService.putExtra(FloatViewService.OPERATION, FloatViewService.SHOW_FLOATWINDOW);
				startService(mFloatViewService);
				Toast.makeText(this, "�������Ѿ���", 0).show();
			}
			break;

		// ��������
		case R.id.ll_set_about_author:
			Intent intentAboutAuthor = new Intent(this,	AboutAuthorActivity.class);
			startActivity(intentAboutAuthor);
			LogUtil.i(TAG, "about_author : super sugar");
			break;

		// �鿴��Ʒ��Ϣ
		case R.id.ll_set_product_info:
			Intent intentAboutApp = new Intent(this, AboutAppActivity.class);
			startActivity(intentAboutApp);
			LogUtil.i(TAG, "product_info : a great products");
			break;

		// �����ʱ��������
			/**
			 * ����������Ҫ��ָӦ��ʹ�������ͳ�����ݣ���������Ӧ��ͳ�����ݣ������ݣ�����ǰ����Ӧ����Ϣ
			 * 
			 * ���������֮ǰ��Ҫ����һ���Ի�����û�����ȷ��
			 */
		case R.id.ll_set_clear_cache:
			final Message msg = new Message();
			AlertDialog.Builder builder1 = new Builder(this);
			builder1.setIcon(R.drawable.ic_launcher);
			builder1.setTitle("ȷ�����������");
			builder1.setMessage("�˲��������������Ӧ��ʹ�������ͳ����Ϣ");
			builder1.setPositiveButton("ȷ��",
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							/*
							 *  ������ݵ��߼�:��������ݰ��������ݿ��ͳ����Ϣ��sp�е�����ֵ
							 *  3�����ݿ⣬����Ӧ�����ݿ�ֻ���������Ȩ�أ�ʱ�䣬�����ݺ�ÿ������ֱ��deleteAll
							 *  ����sp����ֱ��ȫ������Ϊ��ʼ��0����
							 *  
							 *  ����ɾ�����ݱȽϺ�ʱ������ʹ�����߳�ɾ��
							 */
							pd.setMessage("�����������...");
							pd.setCancelable(false);
							pd.show();
							
							new Thread(){
								public void run() {
									// 6��Sp��������Ϊ0
									Editor editor = AppContext.getSharedPreferences().edit();
									
									editor.putInt("day_count", 0);
									editor.putInt("day_time", 0);
									editor.putInt("day_num", 0);
									
									editor.putInt("week_count", 0);
									editor.putInt("week_time", 0);
									editor.putInt("week_num", 0);
									
									editor.commit();
									
									// �������Ӧ�õ�ͳ�����ݣ������ǽ�������ͳ��ֵ��Ϊ0�������ǽ����ݿ��ÿ�
									List<AppUseStatics> infos = AppContext.mDBManager.findAll();
									for(int i=0;i<infos.size();i++){
										infos.get(i).setUseFreq(0);
										infos.get(i).setUseTime(0);
										infos.get(i).setWeight(0);
									}
									AppContext.mDBManager.deleteAll();
									AppContext.mDBManager.addAll(infos);
									
									// ���ÿ������ͳ�ƺ�ÿ������ͳ��
									AppContext.mWeekStatisticDBManager.deleteAll();
									AppContext.mDayStatisticDBManager.deleteAll();
									
									
									// ���������ɣ�����Ϣ����UI
									msg.what = CLEAR_CACHE_FINISHED;
									handler.sendMessage(msg);
								};
							}.start();
						}
					});
			builder1.setNegativeButton("ȡ��",
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							msg.what = CLEAR_CACHE_CANCEL;
							handler.sendMessage(msg);
						}
					});
			builder1.create().show();
			break;

		// �����µ���¼������ʷ���������°汾
		case R.id.ll_set_check_update:
			Toast.makeText(this, "���߾����������ˣ�û���°汾��", 0).show();
			break;

		// �˳�Ӧ��
			/**
			 * ������һ�����Ƶ��ӻ��رյ����Զ���
			 */
		case R.id.ll_set_exit:
			AlertDialog.Builder builder = new Builder(this);
			builder.setIcon(R.drawable.ic_launcher);
			builder.setTitle("ȷ���˳���");
			builder.setPositiveButton("ȷ��",
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							TVAnimation tva = new TVAnimation();
							
							ActivityManager.getInstance().exit();
						}
					});
			builder.setNegativeButton("ȡ��",
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// do nothing
						}
					});
			builder.create().show();
			break;
		}

	}
}
