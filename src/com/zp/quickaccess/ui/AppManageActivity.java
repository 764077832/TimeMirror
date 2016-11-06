package com.zp.quickaccess.ui;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zp.quickaccess.domain.AppUseStatics;
import com.zp.quickaccess.service.WatchdogService;
import com.zp.quickaccess.utils.CommonUtils;
import com.zp.quickaccess.utils.LogUtil;
import com.zp.quickaccess.utils.StringUtils;
import com.zp.quickaccess.view.ListViewCompat;
import com.zp.quickaccess.view.SlideView;
import com.zp.quickaccess.view.SlideView.OnSlideListener;
import com.zp.quickaccess.R;

/**
 * ����Ӧ�ã���Ҫ�������г��Լ�������Ӧ�ò��ṩ�໬ж�أ��� ��������¼�
 * 
 * �໬ʵ�ֲο�����ʾ����Baidu Android�߼�����ʦ����յĲ���
 * http://blog.csdn.net/singwhatiwanna/article/details/17515543#comments 
 * 
 * �໬ListView���ڵ������Ǽ����ر𿨶١��ں�̨��ȡ��������֮����䵽��ʾ�Ĺ��̺ܳ�
 * 
 * ĿǰListViewCpmpact����һ��
 * setTag��getTagΪʲôû��Ч��������
 * 
 */
public class AppManageActivity extends Activity implements OnItemClickListener,
		OnClickListener, OnSlideListener {

	private static final String TAG = "AppManageActivity";

	private static int clicked_item_position = 0;
	private static String clicked_item_pkgname = "";

	private final static int GET_ALLAPP_FINISH = 1;
	private ListViewCompat lvc_appinfo;
	private LinearLayout ll_appinfo;

	private SlideView mLastSlideViewWithStatusOn; // �໬view

	private List<AppUseStatics> infos; // ����ʵ��
	private AppManagerAdapter adapter; // ��ʾ����ʵ���������

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GET_ALLAPP_FINISH:
				adapter = new AppManagerAdapter();
				lvc_appinfo.setAdapter(adapter);
				ll_appinfo.setVisibility(View.INVISIBLE);
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ȡ��������
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_appmanage);

		lvc_appinfo = (ListViewCompat) findViewById(R.id.lvc_appinfo);
		lvc_appinfo.setOnItemClickListener(this);

		// ���������ڵ�LinearLayout
		ll_appinfo = (LinearLayout) findViewById(R.id.ll_appinfo);
		ll_appinfo.setVisibility(View.VISIBLE);

		Intent watchdogService = new Intent(AppManageActivity.this,
				WatchdogService.class);
		startService(watchdogService);

		// �������̼߳���Ӧ����Ϣ
		new Thread() {
			public void run() {
				boolean isFirst = AppContext.getSharedPreferences().getBoolean(
						"isFirst", true);
				// ���ǵ�һ��ʹ�ã���ô����ϵͳ��װ��Ӧ����Ϣ
				/*
				 * �������һ���������⣺�ڱ�������������Ӧ����Ϣ�Ĺ����Ǻ������ģ�Ȼ����ȡ֮����䵽ListView��ʱ��ȴ��һ˲��
				 * ���˸о��ܲ��ã��ɲ�����������ȡ�������θ���ListView����
				 */
				if (isFirst) {
					infos = AppContext.mAppInfoProvider.getAllApps();
					// �����ȼ������������
					Collections.sort(infos);
					AppContext.mDBManager.addAll(infos);
					Editor editor = AppContext.mSharedPreferences.edit();
					editor.putBoolean("isFirst", false);

					editor.commit();
				} else {
					// �����ֱ�Ӵ����ݿ��в�ѯ��Ϣ
					infos = AppContext.mDBManager.findAll();
					Collections.sort(infos);
				}

				Message msg = new Message();
				msg.what = GET_ALLAPP_FINISH;
				handler.sendMessage(msg);
			};
		}.start();

	}

	@Override
	protected void onResume() {
		super.onResume();
		// �ָ��ɼ���ʱ�����̸�������
		if(infos != null){
			infos = AppContext.mDBManager.findAll();
			Collections.sort(infos);
			adapter.notifyDataSetChanged();
			LogUtil.i(TAG, "onResume �� ��������");
		}
	}
	public class AppManagerAdapter extends BaseAdapter {

		public AppManagerAdapter() {
			super();
		}

		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public Object getItem(int position) {
			return infos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			SlideView slideView = (SlideView) convertView;
			AppUseStatics info = infos.get(position);
			if (slideView == null) {
				View itemView = View.inflate(AppManageActivity.this,
						R.layout.activity_appmanage_appinfo_item, null);

				slideView = new SlideView(AppManageActivity.this);
				slideView.setContentView(itemView);

				holder = new ViewHolder(slideView);
				slideView.setOnSlideListener(AppManageActivity.this);
				slideView.setTag(holder);
			} else {
				holder = (ViewHolder) slideView.getTag();
			}

			// ���û���view����Ĭ����������ʾ
			AppUseStatics mAppUseStatics = infos.get(position);
			mAppUseStatics.setSlideView(slideView);
			mAppUseStatics.getSlideView().shrink();

			// ��֪���᲻�����û��ͼ������Σ����ڴ��������û�����һ���Ƿ�Ϊ�յ��ж�
			holder.icon.setImageDrawable(info.getIcon());
			holder.tv_appname.setText(info.getName());
			if (info.isSysApp() == 1) {
				holder.tv_issys.setText("ϵͳӦ��");
			} else {
				holder.tv_issys.setText("������Ӧ��");
			}
			holder.tv_app_freq.setText(info.getUseFreq() + "��");
			holder.tv_app_time.setText(CommonUtils.getFormatTime(info.getUseTime()));

			//holder.deleteHolder.setOnClickListener(AppManageActivity.this);

			holder.tv_start = (TextView) holder.deleteHolder.findViewById(R.id.tv_merge_start);
			holder.tv_delete = (TextView) holder.deleteHolder.findViewById(R.id.tv_merge_delete);
			holder.tv_start.setOnClickListener(AppManageActivity.this);
			holder.tv_delete.setOnClickListener(AppManageActivity.this);

			return slideView;
		}

		/**
		 * ʵ��ListView����Ŀ���ᳬ��һ�У������Ĳ��ֽص�����ʡ�Ժ�
		 * 
		 * ��������TextView�����ṩ�������Ĺ��� android:singleLine="true"
		 * ��Ȼ�Լ��˴��Ĵ����ǲ�����ϸ�ģ�������Ӣ�Ļ�ϵ��������ʾЧ���ܲ���
		 * 
		 * @param name
		 * @return
		 */
		private String getFormatAppName(String name) {
			if (StringUtils.isEnglish(name)) {
				if (name.length() > 18) {
					name = name.substring(0, 15) + "...";
				}
			} else {// �����Ӣ�ĵ�Ӧ���������ȴ���18
				if (name.length() > 18) {
					name = name.substring(0, 14);
					// ��ȡ�������ȫӢ�ģ���ôֱ�Ӽ�ʡ�Ժ�
					if (StringUtils.isEnglish(name)) {
						name += "...";
					} else { // �����Ȼ����Ӣ�Ļ�ϣ���ô��ȡǰ10������ַ�����ʡ�Ժ�
						name = name.substring(0, 10) + "...";
					}
				}
			}
			return name;
		}

	}

	/**
	 * 
	 * @file AppManageActivity.java
	 * @package com.zp.quickaccess.ui 
	 * @comment �ɸ��õ�view
	 * @author zp
	 */
	private static class ViewHolder {
		public ImageView icon;
		public TextView tv_appname;
		public TextView tv_app_freq;
		public TextView tv_app_time;
		public TextView tv_issys;
		public ViewGroup deleteHolder;
		public TextView tv_start;
		public TextView tv_delete;

		ViewHolder(View view) {
			icon = (ImageView) view.findViewById(R.id.iv_appicon);
			tv_appname = (TextView) view.findViewById(R.id.tv_appname);
			tv_app_freq = (TextView) view.findViewById(R.id.tv_app_freq);
			tv_app_time = (TextView) view.findViewById(R.id.tv_app_time);
			tv_issys = (TextView) view.findViewById(R.id.tv_issys);
			deleteHolder = (ViewGroup) view.findViewById(R.id.holder);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		AppUseStatics info = (AppUseStatics) lvc_appinfo.getItemAtPosition(position);
		/*
		 * �˴����õ�tag��onClick()�в�һ�����ǿ��Ի�ȡ
		 * ��Ϊ����tag����onItemClick()�ж�����onTouch()��
		 * ��˿��ܳ����û�ֱ�ӻ����б���û�д���onItemClick()�ĵ���¼�
		 */
		TextView tv_merge_start = (TextView)findViewById(R.id.holder).findViewById(R.id.tv_merge_start);
		TextView tv_merge_delete = (TextView)findViewById(R.id.holder).findViewById(R.id.tv_merge_delete);
		tv_merge_delete.setTag(info.getPkgName());
		tv_merge_start.setTag(info.getPkgName());
		LogUtil.i(TAG, "tv_merge_start.getTag() : " + tv_merge_start.getTag());
		LogUtil.i(TAG, "tv_merge_delete.getTag() : " + tv_merge_delete.getTag());
		 
		 
		clicked_item_position = position;
		clicked_item_pkgname = info.getPkgName();
		LogUtil.i(TAG, "onItemClick package name = " + clicked_item_pkgname);
	}

	@Override
	public void onSlide(View view, int status) {
		// ����һ�Σ��˷����ᱻִ������
		if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
			mLastSlideViewWithStatusOn.shrink();
		}
		if (status == SLIDE_STATUS_ON) {
			mLastSlideViewWithStatusOn = (SlideView) view;
		}
	}

	/**
	 * ����Ӧ�ú�ж��Ӧ�õĵ����Ӧ�¼�
	 */
	@Override
	public void onClick(View v) {
		LogUtil.i(TAG, "�����view��tag : " + v.getTag());
		AppUseStatics info = infos.get(clicked_item_position);
		String pkgName = info.getPkgName();

		if (v.getId() == R.id.tv_merge_delete) {
			if (info.isSysApp() == 1) {
				Toast.makeText(AppManageActivity.this, "ϵͳӦ�ò����Ա�ж��", 0).show();
				adapter.notifyDataSetChanged();
				LogUtil.i(TAG, "ϵͳӦ�ò����Ա�ж��" + pkgName);
			} else {
				// ж��Ӧ�õ��߼�;ж�سɹ�֮��ǵ�ȥ����ListView�Լ��������ݿ�
				String uriStr = "package:" + pkgName;
				Uri deleteUri = Uri.parse(uriStr);
				Intent deleteIntent = new Intent();
				deleteIntent.setData(deleteUri);
				deleteIntent.setAction(Intent.ACTION_DELETE);
				startActivityForResult(deleteIntent, 0);
				LogUtil.i(TAG, "delete " + pkgName);
			}
		} else if (v.getId() == R.id.tv_merge_start) {
			LogUtil.i(TAG, "start " + pkgName);
			// ����Ӧ�õ��߼������ݰ�����ȡ�����������Ե�activity��Ȼ����
			try {
				PackageInfo pkgInfo = getPackageManager().getPackageInfo(
						pkgName,
						PackageManager.GET_UNINSTALLED_PACKAGES
								| PackageManager.GET_ACTIVITIES);
				ActivityInfo[] activityInfos = pkgInfo.activities;
				// ������ЩϵͳӦ����û���������Եģ���������жϱ������
				if (activityInfos.length > 0) {
					adapter.notifyDataSetChanged();
					ActivityInfo startActivity = activityInfos[0];
					Intent intent = new Intent();
					intent.setClassName(pkgName, startActivity.name);
					startActivity(intent);
				} else {
					Toast.makeText(this, "Ӧ�ó����޷�����", 0).show();
				}

			} catch (Exception e) {
				Toast.makeText(this, "Ӧ�ó����޷��ҵ�����������", 0).show();
				e.printStackTrace();
			}

		} else if (v.getId() == R.id.holder) {
			LogUtil.i(TAG, "holder " + v.toString());
		}
	}

	/**
	 * ��ж���¼��ķ��ؽ�����жϣ��Ƿ�ɹ�ж��
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		/*
		 * �˴�ж��֮��ˢ���б���ж��Ӧ���Ƿ񻹴����б���ȡ���ڲ�ͬ��ϵͳ
		 * ģ�����Լ���Ϊ�ֻ���ж��֮���б��и����Ƴ�
		 * ����ж�سɹ����б�����Ȼ��������Ŀ
		 */
		adapter.notifyDataSetChanged(); // ˢ���б��Ƚϱ��յķ�ʽ�����ж�adapter�Ƿ�Ϊ��
		/*
		// ����ʼ����RESULT_CANCELED
		// ����ж����ʾ�����û���ʵ�ʵ�������ж��Ƿ����ִ��ж�ز�����
		// ����������жϣ���ô���û����ж��Ȼ��ȡ��������û�����жϣ�Ӧ��Ҳ�ᱻ���б����Ƴ�
		if (resultCode == RESULT_CANCELED) {
			Toast.makeText(AppManageActivity.this, "ȡ��ж��", 0).show();
			adapter.notifyDataSetChanged(); // ˢ���б���λ
			LogUtil.i(TAG, clicked_item_pkgname + "ȡ��ж��");
		} else if(resultCode == RESULT_OK){
			// ж��֮��ͽ����ݴ�������ɾ����ͬʱ��������ݿ���ɾ��
			AppUseStatics removeObject = AppContext.mDBManager
					.queryByPkgName(clicked_item_pkgname);
			AppContext.mDBManager.deleteByAppName(removeObject.getName());
			// �����ݿ��л�ȡ���º��Ӧ����Ϣ
			infos = AppContext.mDBManager.findAll();
			// ���б�����ݿ����Ƴ�֮��֪ͨListView����
			adapter.notifyDataSetChanged();
			Toast.makeText(AppManageActivity.this, "ж�سɹ�", 0).show();
			LogUtil.i(TAG, clicked_item_pkgname + "��ж��");
		}else{
			Toast.makeText(AppManageActivity.this, "�޷�ж��", 0).show();
			LogUtil.i(TAG, clicked_item_pkgname + "�޷�ж��");
		}
		*/
	}
	
	private void uninstallApplication(){
		
	}
}
