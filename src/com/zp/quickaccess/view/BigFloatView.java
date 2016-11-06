package com.zp.quickaccess.view;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zp.quickaccess.domain.AppUseStatics;
import com.zp.quickaccess.ui.AppContext;
import com.zp.quickaccess.utils.LogUtil;
import com.zp.quickaccess.utils.ScreenUtils;
import com.zp.quickaccess.R;

public class BigFloatView extends LinearLayout {
	
	private static final String TAG = "BigFloatView";
	private ImageView iv_appicon_1;
	private ImageView iv_appicon_2;
	private ImageView iv_appicon_3;
	private ImageView iv_appicon_4;
	private ImageView iv_appicon_5;
	private ImageView iv_appicon_6;
	
	private List<AppUseStatics> topSixInfo;

	// ��¼���������Ŀ��
	public int viewWidth;
	public int viewHeight;

	public WindowManager.LayoutParams bigWindowParams;
	public FloatViewManager mFloatViewManager;

	private Context context;

	public BigFloatView(Context context) {
		super(context);
		this.context = context;

		LayoutInflater.from(context).inflate(R.layout.floatview_big, this);
		
		View bigView = findViewById(R.id.big_window_layout);
		
		viewWidth = bigView.getLayoutParams().width;
		viewHeight = bigView.getLayoutParams().height;

		bigWindowParams = new WindowManager.LayoutParams();
		// ������ʾ��λ�ã�Ĭ�ϵ�����Ļ����
		bigWindowParams.x = ScreenUtils.getScreenWidth() / 2 - viewWidth / 2;
		bigWindowParams.y = ScreenUtils.getScreenHeight() / 2 - viewHeight / 2;
		bigWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		bigWindowParams.format = PixelFormat.RGBA_8888;

		// ���ý���ģʽ
		bigWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		bigWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
		bigWindowParams.width = viewWidth;
		bigWindowParams.height = viewHeight;
		
		mFloatViewManager = FloatViewManager.getInstance(context);

		initView();
	}

	private void initView() {
		iv_appicon_1 = (ImageView) findViewById(R.id.iv_appicon_1);
		iv_appicon_2 = (ImageView) findViewById(R.id.iv_appicon_2);
		iv_appicon_3 = (ImageView) findViewById(R.id.iv_appicon_3);
		iv_appicon_4 = (ImageView) findViewById(R.id.iv_appicon_4);
		iv_appicon_5 = (ImageView) findViewById(R.id.iv_appicon_5);
		iv_appicon_6 = (ImageView) findViewById(R.id.iv_appicon_6);
		
		topSixInfo = AppContext.mDBManager.findTopApp(6);
		// topSixInfo������ӷǿ��жϣ��������ʹ�û���Ϊ���ݿ�Ϊ�����´���
		if(topSixInfo != null && (topSixInfo.size() == 6)) {
			iv_appicon_1.setImageDrawable(topSixInfo.get(0).getIcon());
			iv_appicon_2.setImageDrawable(topSixInfo.get(1).getIcon());
			iv_appicon_3.setImageDrawable(topSixInfo.get(2).getIcon());
			iv_appicon_4.setImageDrawable(topSixInfo.get(3).getIcon());
			iv_appicon_5.setImageDrawable(topSixInfo.get(4).getIcon());
			iv_appicon_6.setImageDrawable(topSixInfo.get(5).getIcon());
		}
		
		iv_appicon_1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleClickAt(0);
			}
		});
		
		iv_appicon_2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleClickAt(1);
			}
		});
		
		iv_appicon_3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleClickAt(2);
			}
		});
		
		iv_appicon_4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleClickAt(3);
			}
		});
		
		iv_appicon_5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleClickAt(4);
			}
		});
		
		iv_appicon_6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleClickAt(5);
			}
		});
		
	}
	
	/**
	 * 
	 * @comment ��������������ImageView�ĵ���¼����߼�������Activity���ر�����
	 * 
	 * @param @param position   
	 * @return void  
	 * @throws
	 * @date 2015-12-27 ����7:54:44
	 */
	public void handleClickAt(int position){
		String pkgName = topSixInfo.get(position).getPkgName();
		if(topSixInfo.size() == 6 && pkgName != null){
			startActivityByPkgname(this.context, pkgName);
			FloatViewManager.getInstance(context).removeBigWindow();
			FloatViewManager.isBigWindowAdded = false;
		}else{
			Toast.makeText(context, "����ʧ��", 0).show();
			FloatViewManager.getInstance(context).removeBigWindow();
			FloatViewManager.isBigWindowAdded = false;
		}
	}
	
	/**
	 * 
	 * @comment ���ݰ�������һ��Ӧ�ã������Լ��߼���Ӧ�ù�������е�start��ť�ĵ���¼����߼���ȫ��ͬ
	 * 			���������ȥ�˴���activity������֮�⿪��actiivty���Ը�Intent������ FLAG_ACTIVITY_NEW_TASK
	 * 
	 * 			�����޷��ҵ����޷�������Ӧ�õ�����˾��ʾ
	 * 
	 * @param @param context ����Activity����Ҫ��������
	 * @param @param pkgName Ҫ������Ӧ�õİ���
	 * @return void  
	 * @throws
	 * @date 2015-12-27 ����7:52:46
	 */
	public void startActivityByPkgname(Context context, String pkgName){
		PackageInfo pkgInfo;
		try {
			pkgInfo = context.getPackageManager().getPackageInfo(pkgName,
					PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_ACTIVITIES);
			ActivityInfo[] activityInfos = pkgInfo.activities;
			if (activityInfos.length > 0) {
				ActivityInfo startActivity = activityInfos[0];
				Intent intent = new Intent();
				// ��activity������֮��Ļ����п���һ��Activity����Ҫ����FLAG_ACTIVITY_NEW_TASK
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setClassName(pkgName, startActivity.name);
				context.startActivity(intent);
			} else {
				Toast.makeText(context, "Ӧ�ó����޷�����", 0).show();
			}
		} catch (Exception e) {
			Toast.makeText(context, "Ӧ�ó����޷��ҵ����߲���������", 0).show();
			e.printStackTrace();
			LogUtil.i(TAG, e.toString());
		}
	}
}

