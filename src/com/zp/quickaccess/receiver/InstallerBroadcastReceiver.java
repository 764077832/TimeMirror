package com.zp.quickaccess.receiver;

import com.zp.quickaccess.domain.AppUseStatics;
import com.zp.quickaccess.engine.AppInfoProvider;
import com.zp.quickaccess.ui.AppContext;
import com.zp.quickaccess.utils.LogUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * 
 * @file InstallerBroadcastReceiver.java
 * @package com.zp.quickaccess.receiver 
 * @comment ��������Ӧ�õİ�װ��ж���¼�����Ӧ�ð�װ��ɻ���ж����ɵ�ʱ�򴥷���ͬʱ�������ݿ�
 * 
 * @author zp
 * @date 2015-12-30 ����10:43:43
 */
public class InstallerBroadcastReceiver extends BroadcastReceiver{
	private static final String TAG = "InstallerBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// ��װ���
		if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {  
            String packageName = intent.getDataString();  
            String pkgName = getPkgNameWithoutPrifx(packageName);
			AppUseStatics temp = new AppUseStatics();
			PackageManager pm = AppContext.mAppContext.getPackageManager();
			AppInfoProvider provider = new AppInfoProvider(AppContext.mAppContext);
			
			try {
				PackageInfo pi = pm.getPackageInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);
				AppUseStatics aus = provider.getAppFromPkgName(pi, pm);
				AppContext.mDBManager.add(aus);
				LogUtil.i(TAG, "�°�װ�����Ϣ��ӵ����ݿ� : " + aus.getPkgName());
				
				LogUtil.i(TAG, "��װ�����Ϣ : Name --> " + aus.getName());
				LogUtil.i(TAG, "��װ�����Ϣ : PkgName --> " + aus.getPkgName());
				LogUtil.i(TAG, "��װ�����Ϣ : UseFreq --> " + aus.getUseFreq());
				LogUtil.i(TAG, "��װ�����Ϣ : UseTime --> " + aus.getUseTime());
				LogUtil.i(TAG, "��װ�����Ϣ : Weight --> " + aus.getWeight());
				
			} catch (NameNotFoundException e) {
				LogUtil.i(TAG, "NameNotFoundException : " + e.toString());
				e.printStackTrace();
			}
			
        }  
  
		// ж����� 
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {   
            String packageName = intent.getDataString();
            String pkgName = getPkgNameWithoutPrifx(packageName);
            int affectRow = AppContext.mDBManager.deleteByPkgName(pkgName);
            LogUtil.i(TAG, "ж��������� : " + pkgName);
            LogUtil.i(TAG, "ж�ظ��� : " + affectRow);
        }
        
        // ������װ����ж���¼�֮����Կ���һ��WatchdogService����
        // ��������
	}
	
	private String getPkgNameWithoutPrifx(String pkgNameWithPrifx){
		if(pkgNameWithPrifx.startsWith("package:", 0)){
			LogUtil.i(TAG, "with prefix: --> " + pkgNameWithPrifx);
			LogUtil.i(TAG, "erase prefix: --> " + pkgNameWithPrifx.substring(8));
			return pkgNameWithPrifx.substring(8);
		}
		return pkgNameWithPrifx;
	}

}
