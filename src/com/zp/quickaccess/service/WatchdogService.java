package com.zp.quickaccess.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import android.os.PowerManager;

import com.zp.quickaccess.db.DayStatisticDBManager;
import com.zp.quickaccess.db.WeekStatisticDBManager;
import com.zp.quickaccess.domain.AppUseStatics;
import com.zp.quickaccess.engine.AppInfoProvider;
import com.zp.quickaccess.ui.AppContext;
import com.zp.quickaccess.utils.LogUtil;
import com.zp.quickaccess.view.FloatViewManager;

/**
 * ���ӵ�ǰ������Ӧ�õ�����ջ�����������activity
 * 
 * ����Ӧ��ʹ����Ϣ��ͳ�ƣ�ʵ���ּ�����Ļ�����Ҳ�������������µ�ջ��Ӧ��
 * Ȼ����ݻ�ȡ��Ӧ����Ϣ��ѯ���ݿ�õ�Ӧ��ʹ�õ�ͳ������ ����ÿ�ε�Ӧ��ʹ����Ϣ���µ����ݿ�
 * 
 * ֮ǰ�ļ��ӷ�����
 * ����activityջ���仯���Ի���Ϊ4�����Σ� ����-Ӧ�ã���ʱ��ʼ Ӧ��-���棺��ʱ�������������ݿ� Ӧ��-Ӧ�ã��ַ�Ϊ����Ӧ��-��Ӧ�� ����
 * ��Ӧ��-����Ӧ�� ����-���棺�����β���ʱ
 * 1���ӵ�ѭ�������ڽ���֮��Ѹ���˳��ĳ�����Կ��ܼ�ⲻ��������������ô��ȷ
 * 
 * �����ڽ����жϵ�ʱ�򣬽���Ӧ��Ҳ��Ϊ������룬��˴ӱ�Ӧ�õ����� == ����--���� ������--��Ӧ�� == ����--���� ������Ӧ��--��Ӧ�� ==
 * Ӧ�õ����� �����һ����ͳ�����
 * 
 * ���������ȥ���켫�˲�������Ļ������ǱȽ�׼ȷ�ģ�������ʱ�䣬����Ӧ����Ŀ �Ǽ��˵�����ʹ�ö�����ȷ�ġ�����һ����������л��޷�׼ȷ�ļ��
 * 
 * ��Ҫע��һ����ǣ���Ȼ�Լ������������װж�صĹ㲥�¼��������Դ�Ϊ���ݸ������ݿ�
 * �������ǻ������������������������ݲ������ݿ�֮ǰ�������ж�һ�¸�Ӧ���ǲ��������ݿ���
 * ������ڣ���ô������ӽ�ȥ��Ҳ�����޸�updateUseTime()���������һ���жϼ���
 */
public class WatchdogService extends Service {

	private static final String TAG = "WatchdogService";
	private ActivityManager mActivityManager;
	
	private List<RunningTaskInfo> infos;
	private RunningTaskInfo topTaskInfo;
	
	private String prePackageName;
	private String nextPackageName;
	
	private int sleepLength = 1000; // �߳�����ʱ��
	private int timeCount = 0; // ʱ�������
	private String week;
	private String curWeek;

	@Override
	public void onCreate() {
		LogUtil.i(TAG, "WatchdogService onCreate");
		
		mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		final WeekStatisticDBManager wsMansger = new WeekStatisticDBManager(this);
		final DayStatisticDBManager dsMansger = new DayStatisticDBManager(this);
		
		/*
		 *  Ŀǰ����һ���޷�ͳ�Ƶ����Σ���ͳ�ƵĹ����У�ͳ�����ݻ�û��д�뵽����У����Ƿ���ϵͳ��ֹ��Ȼ���ٴο���
		 *  ����ֹ֮ǰ��ͳ�Ƶ����ݾͶ�ʧ�ˡ���������������ٷ���������������ڴ治��ʱ���ܻᷢ��
		 *  
		 *  ����취������ϵͳ����ʱ�ߵ���������������·�ߣ���ϵͳ��ֹ�������Ƿ��洢���߳��л�Bundle����
		 */
		if(timeCount != 0){
			updateUseTime(prePackageName);
			updateSharedpreference(prePackageName);
			LogUtil.i(TAG, "onCreate : timeCount = " + timeCount);
		}
		
		new Thread() {
			public void run() {
				prePackageName = getPackageName();
				nextPackageName = getPackageName();
				PowerManager mPowerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
				boolean isScreenOn = true;
				LogUtil.i(TAG, "preAppInfo : package name : " + prePackageName);
				while (true) {
					try {
						sleep(sleepLength);
						isScreenOn = mPowerManager.isScreenOn();
						// ֻ�е���Ļ�����ʱ��Ž���ͳ��
						if(isScreenOn){
							LogUtil.i(TAG, "��Ļ���𣬿�ʼͳ��");
							
							// ��ȡ��ǰ��һ�ܵĵڼ���
							
							// �˴����ʹ�þ�̬����ȫ��mCalendar���󽫻�ʹ�����ӳ�
							//��ÿ�λ�ȡ�µĶ�����Ա�֤��ʱ��������
							Calendar mCalendar = Calendar.getInstance(); 
							mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
							curWeek = String.valueOf(mCalendar.get(Calendar.DAY_OF_WEEK));
							week = AppContext.getSharedPreferences().getString("week", "");
							LogUtil.w(TAG, "curWeek = " + curWeek + "  week = " + week);
							// ���day_of_week�仯�ˣ�˵�����µ�һ�졣����sp�е�����
							if (!week.equals(curWeek)) {
								Editor editor = AppContext.getSharedPreferences().edit();
								// �µ�һ�죬����ÿ�����ݣ���ʼ��Ϊ0
								editor.putInt("day_count", 0);
								editor.putInt("day_time", 0);
								editor.putInt("day_num", 0);
								// �������week��ֵ
								editor.putString("week", curWeek);

								// ���������ݵ�һ����־λ����֤�����if�����ִֻ��һ��
								// ������һ���춼������
								editor.putBoolean("week_flag", true); 
								
								editor.commit();
								// ���ÿ�����ݿ�
								dsMansger.deleteAll();
								LogUtil.w(TAG, "��������գ�curWeek =  " + curWeek);
							}
							// ��������ݡ�Ҳ��������Ϊһ�ܵ�������
							// ��Ҫ��֤����߼�����ִ��һ�Σ�������һ���춼��ִ��
							if ( "1".equals(curWeek)) { // ������Ϊ1,����һΪ2������
								if(AppContext.getSharedPreferences().getBoolean("week_flag", true)){
									Editor editor = AppContext.getSharedPreferences().edit();
									// ��������Ϣ���� ����ʼ��Ϊ0
									editor.putInt("week_count", 0);
									editor.putInt("week_time", 0);
									editor.putInt("week_num", 0);
									
									editor.putBoolean("week_flag", false);
									
									editor.commit();
									// ���ÿ�����ݿ�
									wsMansger.deleteAll();
									LogUtil.w(TAG, "��������գ� Calendar.SUNDAY = " + String.valueOf(mCalendar.get(Calendar.SUNDAY)));
								}
							}
	
							// ��ȡ��ǰ�������е�����
							infos = mActivityManager.getRunningTasks(1);
							// ��ȡ�������е�����Ȼ���ȡ�����������ջ����˵�Activity��������ȡ�������Ӧ�İ���
							// ���ݰ������Բ�ѯӦ����Ϣ���ݿ�ó��ð�����Ӧ��Ӧ�õ�������Ϣ
							topTaskInfo = infos.get(0);
							// ��ȡ��ǰ�������е�Ӧ�õ�����ջ����ȡ��ǰ�û��ɼ���Activity
							nextPackageName = topTaskInfo.topActivity.getPackageName();
							
							if (AppContext.isHome(nextPackageName)) {
								// �ػ�FloatViewService
								deamonOfFloatViewService();
								
								if (AppContext.isHome(prePackageName)) {
									// 1 1 ��nextPackageName������  prePackageName�����棬��ͳ��
									// com.sec.android.app.launcher--com.sec.android.app.launcher����
									// com.sec.android.app.launcher��com.zp.quickaccess ����Ϊ�����棬���������������Ҳ��ִ�����µ�log
									LogUtil.i(TAG, "1 1 : " + nextPackageName + "--" + prePackageName);
								} else {
									writeToStorage();
									// 1 0 : nextPackageName������  prePackageName�������棬�ӷ����浽���棬��ʱ����
									LogUtil.i(TAG, "1 0 : " + nextPackageName + "--" + prePackageName);
								}
							} else {
								if (AppContext.isHome(prePackageName)) {
									// 0 1 ��nextPackageName��������  prePackageName�����棬�����浽�����棬��ʱ��ʼ
									timeCount = timeCount + 1;
									prePackageName = nextPackageName;
									LogUtil.i(TAG, "0 1 : " + nextPackageName + "--" + prePackageName);
								} else {
									// 0 0��nextPackageName��������  prePackageName�������棬�ӷ����浽�����棬��ʱ����
									if (prePackageName.equals(nextPackageName)) {
										// 0 0 1 �����nextPackageName��prePackageName��ͬ����ô��ʱ����
										timeCount = timeCount + 1;
										LogUtil.i(TAG, "0 0 1");
									} else {
										// 0 0 0�� ���nextPackageName��prePackageName��ͬ����ô˵����prePackageName��ת����nextPackageName
										// 		  Ҳ����prePackageName�ļ�ʱ������nextPackageName�ļ�ʱ��ʼ
										writeToStorage();
										LogUtil.i(TAG, "0 0 0");
									}
								}
							}
						// end if(isScreenOn)
						} else { // ��ĻϨ��
							/*
							 * 	��ĻϨ���ʱ�����ͳ�Ʋ�û�н�������ô����ǰͳ�ƽ��д�룬Ȼ��ͳ�Ʊ�����ʼ��
							 * 
							 *  ����ͳ��ʱ�䶪ʧ�����統ǰ�����������������Ȼ��ֱ���������������else�н�֮ǰͳ������д��
							 *  ��ô���ܻ����֮ǰ���ݵĶ�ʧ
							 *  
							 *  д��ͳ�ƽ����ͬʱ������������رգ����ٲ���Ҫ�����С����û��´ν�����Ļ��ʱ���ٴο����˷���
							 */
							if(timeCount != 0){
								writeToStorage();
								LogUtil.i(TAG, "timeCount != 0");
							}else{
								LogUtil.i(TAG, "timeCount == 0");
							}
							stopSelf();
							LogUtil.i(TAG, "��ĻϨ�𣬽���ͳ�ƣ�����ֹͣ����");
							break; // ��������֮������ѭ��
						}
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} // end while
			} // end run

			/**
			 * ��ͳ������д�뵽SharedPreference�����ݿ�
			 */
			private void writeToStorage() {
				updateUseTime(prePackageName);
				updateSharedpreference(prePackageName);
				timeCount = 0;
				prePackageName = nextPackageName;
			};
		}.start();
	}
	
	/**
	 * 
	 * @comment FloatViewService���ػ����������û����õ���ʾ��������isOpenFloatviewΪtrue��
	 * 			��������û����ʾ��isSmallWindowShowingΪfalse��,��ô˵������ϵͳɱ������Ҫ���¿���
	 * @param    
	 * @return void  
	 * @throws
	 * @date 2015-12-29 ����10:46:01
	 */
	public void deamonOfFloatViewService(){
		boolean isOpenFloatview = AppContext.getSharedPreferences().getBoolean("isOpenFloatview", false);
		boolean isSmallWindowShowing = FloatViewManager.getInstance(this).isSmallWindowShowing();
		if(isOpenFloatview){
			if(!isSmallWindowShowing){
				Intent mFloatViewService = new Intent(getApplicationContext(), FloatViewService.class);
				mFloatViewService.putExtra(FloatViewService.OPERATION,FloatViewService.HIDE_FLOATWINDOW);
				startService(mFloatViewService);
				LogUtil.i(TAG, "deamonOfFloatViewService : WatchdogService start FloatViewService");
			}
		}else{
			// // ���ڹرշ���Ĳ�������FloatViewService�Լ�����
//			Intent mFloatViewService = new Intent(getApplicationContext(), FloatViewService.class);
//			stopService(mFloatViewService);
		}
	}
	
	/**
	 * 
	 * @comment mAppPkgNames �Ļ�ȡ����Ҫ�����ݿ��л�ȡ��,����mAppPkgNames�������Ϊ�ֲ�����
	 * 			�������׳��ֿ�ָ���쳣�����ߵ�Ӧ����ȫ�˳�����ȫ������ʱ��ͻ�����ظ�ͳ�Ƶ����
	 * 
	 * @param @param pkgName   ���ݰ�������Ӧ��
	 * @return void  
	 * @throws
	 * @date 2015-12-28 ����4:12:10
	 */
	private void updateSharedpreference(String pkgName) {
		List<String> mDayAppPkgNames = new ArrayList<String>();
		List<String> mWeekAppPkgNames = new ArrayList<String>();
		DayStatisticDBManager dsMansger = new DayStatisticDBManager(this);
		WeekStatisticDBManager wsMansger = new WeekStatisticDBManager(this);
		mDayAppPkgNames = dsMansger.findAllPkgNames();
		mWeekAppPkgNames = wsMansger.findAllPkgNames();
		
		// ʹ�õ�Ӧ�õĸ�����1
		Editor editor = AppContext.getSharedPreferences().edit(); 
		if (!mDayAppPkgNames.contains(pkgName)) {
			dsMansger.addByName(pkgName);
			// ʹ��app�ĸ�����1
			editor.putInt("day_num", AppContext.getSharedPreferences().getInt("day_num", 0) + 1);
			
		}
		if(!mWeekAppPkgNames.contains(pkgName)){
			wsMansger.addByName(pkgName);
			editor.putInt("week_num", AppContext.getSharedPreferences().getInt("week_num", 0) + 1);
		}
		
		// ʹ�ô�����1
		editor.putInt("day_count", AppContext.getSharedPreferences().getInt("day_count", 0) + 1);
		editor.putInt("week_count", AppContext.getSharedPreferences().getInt("week_count", 0) + 1);
		// ʹ��ʱ�䳤������
		editor.putInt("day_time", AppContext.getSharedPreferences().getInt("day_time", 0) + timeCount);
		editor.putInt("week_time", AppContext.getSharedPreferences().getInt("week_time", 0) + timeCount);

		editor.commit();
	}
	
	/**
	 * 
	 * @comment ���ݰ�������ǰͳ����Ϣд�뵽���ݿ�
	 * 			��д��֮ǰ���ж����ݿ����Ƿ��и�Ӧ�ã����û����ô��ϵͳ�л�ȡ(�ù��̿��ܵ���WatchdogService����)
	 * 			Ȼ�󽫻�ȡ����Ϣ��ֵ��tempAppInfo��
	 * 			����и�Ӧ��(tempAppInfo��name ��Ϊempty)��ôֱ��ʹ��tempAppInfoΪ������뵽���ݿ�
	 * 
	 * @param @param pkgName   
	 * @return void  
	 * @throws
	 * @date 2015-12-31 ����10:57:06
	 */
	public void updateUseTime(String pkgName){
		AppUseStatics tempAppInfo = AppContext.mDBManager.queryByPkgName(pkgName);
		if("empty".equals(tempAppInfo.getName())){
			LogUtil.i(TAG, "δ��ӵ����ݿ����Ӧ�� �� " + pkgName);
			try {
				AppInfoProvider provider = new AppInfoProvider(getApplicationContext());
				PackageManager pm = getPackageManager();
				PackageInfo info = pm.getPackageInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);
				AppUseStatics appStatics = provider.getAppFromPkgName(info, pm);
				appStatics.setUseFreq(tempAppInfo.getUseFreq() + 1); 
				appStatics.setUseTime(tempAppInfo.getUseTime() + timeCount); 
				appStatics.setWeight(tempAppInfo.getWeight() + timeCount + 1); 
				AppContext.mDBManager.add(appStatics);
				LogUtil.i(TAG, "δ��ӵ����ݿ����Ӧ�� �� " + pkgName + " ��Ϣ��ӳɹ�");
			} catch (NameNotFoundException e) {
				LogUtil.e(TAG, "updateUseTime " + pkgName+" NameNotFoundException : " + e.toString());
				e.printStackTrace();
			}
		}else{
			tempAppInfo.setUseFreq(tempAppInfo.getUseFreq() + 1); // ����ʹ�ô���
			tempAppInfo.setUseTime(tempAppInfo.getUseTime() + timeCount); // ����ʹ��ʱ��
			tempAppInfo.setWeight(tempAppInfo.getWeight() + timeCount + 1); // ����Ȩ��ֵ
			if(AppContext.mDBManager.updateAppInfo(tempAppInfo) > 0){
				LogUtil.i(TAG, "���� "+ pkgName +" Ӧ����Ϣ�����ݿ�ɹ�");
			}else{
				LogUtil.i(TAG, "���� "+ pkgName +" Ӧ����Ϣ�����ݿ�ʧ��");
			}
		}
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
//	/**
//	 * Service���ó�START_STICKY��kill ��ᱻ�������ȴ�5�����ң�
//	 * �ش�Intent
//	 * 
//	 * ���ں���ͨ�������㲥�����������ԾͲ���ͨ��START_STICKY����֤���񲻱�ϵͳ�ر���
//	 * 
//	 */
//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//		return START_STICKY;
//	}

	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(timeCount != 0){
			updateUseTime(prePackageName);
			updateSharedpreference(prePackageName);
			LogUtil.i(TAG, "WatchdogService onDestroy �� timeCount != 0");
		}
		LogUtil.i(TAG, "WatchdogService onDestroy");
	}

}
