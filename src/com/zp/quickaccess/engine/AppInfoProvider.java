package com.zp.quickaccess.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.zp.quickaccess.domain.AppUseStatics;
import com.zp.quickaccess.utils.LogUtil;

/**
 * ��ȡϵͳ��Ӧ�õ�ʹ����Ϣ
 * 
 * ��ȡԭ����ʹ��packageManagerɨ�����а�װ��ϵͳ�е�Ӧ��Ȼ���ȡ��Ϣ
 * ��һ���ȽϺ�ʱ�Ĺ�����������Ҫ�������provider��ȡӦ����Ϣ��ʱ����Ҫ�����߳���ִ��
 */
public class AppInfoProvider {
	private static final String TAG = "AppInfoProvider";
	private Context context; // ���ڻ�ȡpm
	private PackageManager pm;

	public AppInfoProvider(Context c) {
		this.context = c;
		pm = this.context.getPackageManager();
	}

	/**
	 * ��ȡ�Ѿ���װ������Ӧ�õ���Ϣ����������Ϣ�б�
	 * 
	 * ����SharedPreference�ı�־λ������ǵ�һ�β�ѯ����ôֱ����ϵͳ�б���
	 * ������ǵ�һ�Σ���ôֱ�Ӵ����ݿ���в�ѯ����
	 * 
	 * @return List<AppInfo> Ӧ����Ϣ�б�,�Ұ�Ȩ���������
	 */
	public List<AppUseStatics> getAllApps() {
		List<PackageInfo> pkgInfo = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);
		List<AppUseStatics> result = new ArrayList<AppUseStatics>();

		for (PackageInfo info : pkgInfo) {
			result.add(getAppFromPkgName(info, pm));
			LogUtil.i(TAG, "getAllApps: " + info.packageName);
		}
		Collections.sort(result);
		return result;
	}

	/**
	 * 
	 * @comment ����PackageInfo��ȡ�ð���Ӧ��Ӧ�õ���Ϣ������Ӧ����Ϣ��AppUseStatics bean����ʽ����
	 * 			Ĭ��ͳ�����ݶ�Ϊ��
	 * 
	 * @param @param info
	 * @param @return   
	 * @return AppUseStatics  
	 * @throws
	 * @date 2015-12-30 ����10:35:33
	 */
	public static AppUseStatics getAppFromPkgName(PackageInfo info, PackageManager pm) {
		AppUseStatics myApp = new AppUseStatics();
		// ��ȡ����
		String pkgName = info.packageName;
		// ���ݰ�����ȡӦ����Ϣ
		ApplicationInfo appInfo = info.applicationInfo;
		Drawable icon = appInfo.loadIcon(pm);
		String appName = appInfo.loadLabel(pm).toString();
		
		myApp.setIcon(icon);
		myApp.setPkgName(pkgName);
		myApp.setName(appName);
		
		if (filterApp(appInfo)) { // ����ǵ�����Ӧ��
			myApp.setSysApp(0);
			myApp.setWeight(1);
		} else { // �����ϵͳӦ��
			myApp.setSysApp(1);
			myApp.setWeight(0);
		}
		myApp.setUseFreq(0);
		myApp.setUseTime(0);
		
		LogUtil.i(TAG, "getAllApps: " + myApp.getPkgName());
		return myApp;
	}

	/*
	 * �ж��Ƿ��ǵ�����Ӧ�á�����ǵ�����Ӧ�ã���ô����true���򷵻�false
	 */
	public static boolean filterApp(ApplicationInfo info) {
		// ���ϵͳӦ�ø���֮��Ҳ����Ϊ������Ӧ��
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			return true;
		} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
			return true;
		}
		return false;
	}
}
