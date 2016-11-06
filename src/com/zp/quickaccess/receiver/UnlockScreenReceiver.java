package com.zp.quickaccess.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zp.quickaccess.service.FloatViewService;
import com.zp.quickaccess.service.WatchdogService;
import com.zp.quickaccess.ui.AppContext;
import com.zp.quickaccess.utils.LogUtil;

/**
 * 
 * @file UnlockScreenReceiver.java
 * @package com.zp.quickaccess.receiver 
 * @comment ������Ļ�����¼������ڼ�������Ļ����֮���жϷ���͸����Ƿ��Ż�
 * 			������Ż�����
 * 
 * @author zp
 * @date 2015-12-24 ����4:08:01
 */
public class UnlockScreenReceiver extends BroadcastReceiver {

	private static final String TAG = "UnlockScreenReceiver";
	private Context context;

	/**
	 * ���յ��㲥��Ȼ��ֱ�ӿ���Ӧ��ͳ�Ʒ���
	 * �����������Ǹ����û�������ѡ������
	 * 
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		// ���������ʱ�������ݿ⻹û������isFirstΪtrue,��ô����ʱ����������
		if(AppContext.getSharedPreferences().getBoolean("isFirst", true)){
			LogUtil.i(TAG, "������Ļ�����ݿ�Ϊ�գ�δ����WatchdogService");
		}else{
			// ��������¼�������ʱ�����ݿ��Ѿ������ݣ���ô��������
			startWatchdogService();
			LogUtil.i(TAG, "������Ļ������WatchdogService");
		}
	}
	
	/**
	 * 
	 * @comment ����Ӧ�ý���ʹ�����ͳ�Ʒ���
	 * @param    
	 * @return void  
	 * @throws
	 * @date 2015-12-24 ����5:26:16
	 */
	public void startWatchdogService(){
		Intent mWatchdogService = new Intent(this.context, WatchdogService.class);
		this.context.startService(mWatchdogService);
		LogUtil.i(TAG, "startWatchdogService in UnlockScreenReceiver");
	}
	/**
	 * 
	 * @comment ����������������;���ں�����WatchdogService��ΪFloatViewService���ػ�����
	 * 			(�������û����ô�����ʾ������ʵ�ʴ���û����ʽ--���񱻹ر� ��������WatchdogService�Ż�ȥ����FloatViewService)
	 * 			���Ի�������Ҫ�ٽ�����ʱ����
	 * @param    
	 * @return void  
	 * @throws
	 * @date 2015-12-24 ����5:26:03
	 */
	public void startFloatViewService(){
		Intent intent = new Intent(this.context, FloatViewService.class);
		// �˴�������д��onStartCommand���������÷���ʹ�õ���Extra�е�����
		// �����ڿ���ʱ��Ҫָ�������Ķ�����ʲô
		// �����ָ�����ᵼ�¿�ָ���쳣
		// intent.putExtra(FloatViewService.OPERATION,	FloatViewService.SHOW_FLOATWINDOW);
		this.context.startService(intent);
		LogUtil.i(TAG, "startFloatViewService in UnlockScreenReceiver");
	}
}
