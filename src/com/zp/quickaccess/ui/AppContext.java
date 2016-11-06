package com.zp.quickaccess.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.preference.PreferenceManager;

import com.zp.quickaccess.db.DBManager;
import com.zp.quickaccess.db.DayStatisticDBManager;
import com.zp.quickaccess.db.WeekStatisticDBManager;
import com.zp.quickaccess.engine.AppInfoProvider;
import com.zp.quickaccess.utils.LogUtil;

/**
 * 
 * @file AppContext.java
 * @package com.zp.quickaccess.ui 
 * @comment ���ڱ���һЩȫ�ֱ���
 * 
 * @author zp
 * @date 2015-12-29 ����10:28:09
 */
public class AppContext extends Application {

	private static final String TAG = "AppContext";
	public static AppContext mAppContext; // AppConetxtʵ��,Ϊû��Context�ķ�����߹㲥������ʹ��
	public static SharedPreferences mSharedPreferences;// SharedPreferencesʵ��

	// ��������������ʹ�ã�û��Ҫȫ�־�̬��ֱ����ʹ�õ�ʱ���ȡ����
	public static int appVersionCode; // app�汾��
	public static String appVersionName; // app�汾����
	
	// ���µ�ȫ�ֱ���Ҳ���Ա��滻
	public static DBManager mDBManager;
	public static DayStatisticDBManager mDayStatisticDBManager;
	public static WeekStatisticDBManager mWeekStatisticDBManager;
	
	public static AppInfoProvider mAppInfoProvider;
	public static ActivityManager mActivityManager;

	public static List<String> homeList;

	public static AppContext getAppContext() {
		return AppContext.mAppContext;
	}

	public static SharedPreferences getSharedPreferences() {
		return AppContext.mSharedPreferences;
	}

	/**
	 * ��ʼ�����ݿ�ʵ����SharedPreferencesʵ��
	 * 
	 * @Title init
	 */
	private void init() {
		AppContext.mAppContext = this;
		AppContext.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		mDBManager = new DBManager(mAppContext);
		mWeekStatisticDBManager = new WeekStatisticDBManager(mAppContext);
		mDayStatisticDBManager = new DayStatisticDBManager(mAppContext);
		mAppInfoProvider = new AppInfoProvider(mAppContext);
		mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		homeList = getHomes();
	}

	/**
	 * ��ȡ��ǰ����汾��
	 * 
	 * @Title initAppInfo
	 */
	private void initAppInfo() {
		final PackageManager pm = getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(getPackageName(), 0);
		} catch (final NameNotFoundException e) {
			pi = new PackageInfo();
			pi.versionName = "1.0";
			pi.versionCode = 20150101;
		}
		AppContext.appVersionCode = pi.versionCode;
		AppContext.appVersionName = pi.versionName;
	}

	/**
	 * ������������Ӧ�õİ�����
	 * 
	 * ������ͱ�Ӧ�ý����������������Ӧ�ý��治�򿪣������getHomes()�������ص�������Ӧ�õİ������ɵ��ַ�������
	 * 
	 * @return ���ذ������а������ַ����б�
	 */
	private List<String> getHomes() {
		// ��ȡ���������
		List<String> names = new ArrayList<String>();
		PackageManager packageManager = this.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(
				intent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo info : resolveInfo) {
			names.add(info.activityInfo.packageName);
		}
		// ��������Ҳ�ӽ�ȥ
		names.add(this.getPackageName());
		return names;
	}

	/**
	 * �жϵ�ǰ�����Ƿ�������
	 */
	public static boolean isHome() {
		List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
		return homeList.contains(rti.get(0).topActivity.getPackageName());
	}
	
	public static boolean isHome(String pkgName) {
		return homeList.contains(pkgName);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		init();
		initAppInfo();
		LogUtil.i(TAG, "App Context onCreate");
	}

}