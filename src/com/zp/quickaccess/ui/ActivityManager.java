package com.zp.quickaccess.ui;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

/**
 * activity����
 */
public class ActivityManager extends Application {

	private List<Activity> activityList = new LinkedList<Activity>();
	private static ActivityManager instance = null;

	private ActivityManager() {

	}

	/**
	 * �ӵ���ģʽ�л�ȡΨһ��ActivityManagerʵ��
	 */
	public static ActivityManager getInstance() {
		if (instance == null) {
			instance = new ActivityManager();
		}
		return instance;
	}

	/**
	 * ���activity��������
	 */
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	/**
	 * �˳�ϵͳ����������activity
	 */
	public void exit() {
		for (Activity activity : activityList) {
			if (activity != null){
				activity.finish();
			}
		}
		activityList.clear();
		// // ���ִ����System.exit(0)��ô���ϵͳ�ǿ�����FloatViewService����ô����ֿ�ָ���쳣
		// // ִ��System.exit(0)�ᵼ������ڷ����ϵĴ���Ҳ���رգ���ֱ��finish����Ӱ�쵽�ô���
		// // Ȼ����ֵ���WatchdogService������ʹ��System.exit(0)�˳�ȴ������ֿ�ָ���쳣
		//System.exit(0);
	}

}
