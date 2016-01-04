package com.zp.quickaccess.ui;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.zp.quickaccess.domain.AppUseStatics;
import com.zp.quickaccess.service.WatchdogService;
import com.zp.quickaccess.utils.CommonUtils;
import com.zp.quickaccess.utils.LogUtil;
import com.zp.quickaccess.R;

/**
 * �鿴������װ��Ӧ�õ�ʹ������������������ۼƿ���ʱ��
 * 
 * ʹ����Ϣչʾ���棺����ʹ��Ƶ����ߵ�3��Ӧ�ã�����ʹ�ô�����ʹ��ʱ����ʹ�ø���
 * 
 * ��ô�������ˣ����ͳ��ÿ��������Լ������֯��Щ����
 */
public class ViewAppStaticsActivity extends Activity {
	private static final String TAG = "ViewAppStaticsActivity";

	private ImageView iv_usemost_icon_1;
	private ImageView iv_usemost_icon_2;
	private ImageView iv_usemost_icon_3;

	private TextView tv_usemost_freq_1;
	private TextView tv_usemost_freq_2;
	private TextView tv_usemost_freq_3;

	private TextView tv_usemost_time_1;
	private TextView tv_usemost_time_2;
	private TextView tv_usemost_time_3;

	private TextView tv_viewstatistics_day_count;
	private TextView tv_viewstatistics_day_time;
	private TextView tv_viewstatistics_day_num;
	private TextView tv_viewstatistics_week_count;
	private TextView tv_viewstatistics_week_time;
	private TextView tv_viewstatistics_week_num;

	private int day_count;
	private int day_time;
	private int day_num;
	private int week_count;
	private int week_time;
	private int week_num;

	private List<AppUseStatics> topThreeAppInfo;
	private List<AppUseStatics> infos;

	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_viewstatics);

		initViews();

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				pd.dismiss();
				// ���ݿ��ʼ�����֮��������
				Intent mWatchdogService = new Intent(ViewAppStaticsActivity.this, WatchdogService.class);
				startService(mWatchdogService);
				updateUI();
			}
		};
		
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
			pd = ProgressDialog.show(ViewAppStaticsActivity.this, "", "���ڼ����������Ժ򡭡�", true, false);
			new Thread() {
				public void run() {
					infos = AppContext.mAppInfoProvider.getAllApps();
					// �����ȼ������������
					Collections.sort(infos);
					AppContext.mDBManager.addAll(infos);
					Editor editor = AppContext.mSharedPreferences.edit();
					editor.putBoolean("isFirst", false);
					editor.commit();
					Message msg = new Message();
					handler.sendMessage(msg);
				};
			}.start();
		} else {
			// ����ֱ�Ӹ���UI
			updateUI();
		}

	}

	private void initViews() {
		tv_viewstatistics_day_count = (TextView) findViewById(R.id.tv_viewstatistics_day_count);
		tv_viewstatistics_day_time = (TextView) findViewById(R.id.tv_viewstatistics_day_time);
		tv_viewstatistics_day_num = (TextView) findViewById(R.id.tv_viewstatistics_day_num);
		tv_viewstatistics_week_count = (TextView) findViewById(R.id.tv_viewstatistics_week_count);
		tv_viewstatistics_week_time = (TextView) findViewById(R.id.tv_viewstatistics_week_time);
		tv_viewstatistics_week_num = (TextView) findViewById(R.id.tv_viewstatistics_week_num);

		iv_usemost_icon_1 = (ImageView) findViewById(R.id.iv_usemost_icon_1);
		iv_usemost_icon_2 = (ImageView) findViewById(R.id.iv_usemost_icon_2);
		iv_usemost_icon_3 = (ImageView) findViewById(R.id.iv_usemost_icon_3);

		tv_usemost_freq_1 = (TextView) findViewById(R.id.tv_usemost_freq_1);
		tv_usemost_freq_2 = (TextView) findViewById(R.id.tv_usemost_freq_2);
		tv_usemost_freq_3 = (TextView) findViewById(R.id.tv_usemost_freq_3);

		tv_usemost_time_1 = (TextView) findViewById(R.id.tv_usemost_time_1);
		tv_usemost_time_2 = (TextView) findViewById(R.id.tv_usemost_time_2);
		tv_usemost_time_3 = (TextView) findViewById(R.id.tv_usemost_time_3);
	}

	private void updateUI() {
		topThreeAppInfo = AppContext.mDBManager.findTopApp(3);

		day_count = AppContext.getSharedPreferences().getInt("day_count", 0);
		day_time = AppContext.getSharedPreferences().getInt("day_time", 0);
		day_num = AppContext.getSharedPreferences().getInt("day_num", 0);

		week_count = AppContext.getSharedPreferences().getInt("week_count", 0);
		week_time = AppContext.getSharedPreferences().getInt("week_time", 0);
		week_num = AppContext.getSharedPreferences().getInt("week_num", 0);

		// Ҫ��ȥ��Ӧ���������Ǻ�ͳ�Ʒ����ʵ�����
		tv_viewstatistics_day_count.setText(day_count + "��");
		tv_viewstatistics_day_time.setText(CommonUtils.getFormatTime(day_time));
		tv_viewstatistics_day_num.setText(day_num + "��");

		tv_viewstatistics_week_count.setText(week_count + "��");
		tv_viewstatistics_week_time.setText(CommonUtils
				.getFormatTime(week_time));
		tv_viewstatistics_week_num.setText(week_num + "��");

		if (topThreeAppInfo.size() == 3) {
			LogUtil.i(TAG, topThreeAppInfo.get(0).getName() + " -- " + topThreeAppInfo.get(0).getUseFreq() + "��");
			LogUtil.i(TAG, topThreeAppInfo.get(1).getName() + " -- " + topThreeAppInfo.get(1).getUseFreq() + "��");
			LogUtil.i(TAG, topThreeAppInfo.get(2).getName() + " -- " + topThreeAppInfo.get(1).getUseFreq() + "��");

			iv_usemost_icon_1.setImageDrawable(topThreeAppInfo.get(0).getIcon());
			tv_usemost_freq_1.setText(topThreeAppInfo.get(0).getUseFreq() + "��");
			tv_usemost_time_1.setText(CommonUtils.getFormatTime(topThreeAppInfo.get(0).getUseTime()));

			iv_usemost_icon_2.setImageDrawable(topThreeAppInfo.get(1).getIcon());
			tv_usemost_freq_2.setText(topThreeAppInfo.get(1).getUseFreq() + "��");
			tv_usemost_time_2.setText(CommonUtils.getFormatTime(topThreeAppInfo.get(1).getUseTime()));

			iv_usemost_icon_3.setImageDrawable(topThreeAppInfo.get(2).getIcon());
			tv_usemost_freq_3.setText(topThreeAppInfo.get(2).getUseFreq() + "��");
			tv_usemost_time_3.setText(CommonUtils.getFormatTime(topThreeAppInfo.get(2).getUseTime()));
		}
	}
}
