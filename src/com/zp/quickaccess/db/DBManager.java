package com.zp.quickaccess.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.zp.quickaccess.domain.AppUseStatics;
import com.zp.quickaccess.utils.IconUtils;

public class DBManager {
	private static final String TAG = "DBManager";
	private DBHelper helper;
	private SQLiteDatabase db;

	public DBManager(Context context) {
		helper = DBHelper.getInstance(context);
		// ִ����������仰�����ݿ���㴴��
		db = helper.getWritableDatabase();
	}

	public void add(AppUseStatics appUseStatics) {
		db.beginTransaction();

		try {
			// ���β��������Ӧ�������Ƿ���ϵͳӦ�ã�ʹ��Ƶ�Σ�ʹ��ʱ�䣬Ȩ��ֵ
			// ��������ʡ�����е�˳�������ڽ������ݲ����ʱ����Ҫע��ͽ���˳��һ��
			// + "(appName VARCHAR PRIMARY KEY,pkgName VARCHAR,"
			// +
			// "isSysApp INTEGER, useFreq INTEGER, useTime INTEGER, appIcon BLOB,"
			// + "weight INTEGER)";
			db.execSQL(
					"INSERT INTO " + DBHelper.ALL_APP_INFO
							+ " VALUES(?, ?, ?, ?, ?,?,?)",
					new Object[] { appUseStatics.getName(),
							appUseStatics.getPkgName(),
							appUseStatics.isSysApp(),
							appUseStatics.getUseFreq(),
							appUseStatics.getUseTime(),
							IconUtils.getIconData(appUseStatics.getIcon()),
							appUseStatics.getWeight() });

			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}

	}

	/**
	 * ���б�������������ӵ����ݿ���
	 * 
	 * ʵ����һЩ��Ч�������Ż�
	 * 
	 * @param allAppStatics
	 */
	public void addAll(List<AppUseStatics> allAppStatics) {
		for (AppUseStatics aps : allAppStatics) {
			add(aps);
		}
	}

	/**
	 * ����Ӧ������������ݿ���ɾ��
	 * 
	 * @param name
	 * @return
	 */
	public int deleteByAppName(String name) {
		return db.delete(DBHelper.ALL_APP_INFO, "appName=?",
				new String[] { name });
	}
	
	/**
	 * 
	 * @comment ����Ӧ�ð�����Ӧ��ɾ��
	 * @param @param pkgName
	 * @param @return   
	 * @return int  
	 * @throws
	 * @date 2015-12-30 ����11:12:53
	 */
	public int deleteByPkgName(String pkgName) {
		return db.delete(DBHelper.ALL_APP_INFO, "pkgName=?",
				new String[] { pkgName });
	}

	/**
	 * ���������ݴ����ݿ���ɾ��
	 * 
	 * @return
	 */
	public int deleteAll() {
		return db.delete(DBHelper.ALL_APP_INFO, null, null);
	}

	/**
	 * ����Ӧ�������в�ѯ
	 * 
	 * @param name
	 * @return info
	 */
	public AppUseStatics queryByAppName(String name) {
		String sql = "SELECT * FROM " + DBHelper.ALL_APP_INFO
				+ " where appName= '" + name + "'";

		AppUseStatics info = new AppUseStatics();
		Cursor c = db.rawQuery(sql, null);
		if (c.moveToNext()) {
			info.setPkgName(c.getString(c.getColumnIndex("pkgName")));
			info.setName(c.getString(c.getColumnIndex("appName")));
			info.setUseFreq(c.getInt(c.getColumnIndex("useFreq")));
			info.setUseTime(c.getInt(c.getColumnIndex("useTime")));
			info.setIcon(IconUtils.getDrawableFromBitmap(IconUtils
					.getBitmapFromBytes(c.getBlob(c.getColumnIndex("appIcon")))));
			info.setSysApp(c.getInt(c.getColumnIndex("isSysApp")));
			info.setWeight(c.getInt(c.getColumnIndex("weight")));
		}else{
			info.setName("empty");
			info.setPkgName("empty");
		}
		c.close();
		return info;
	}

	/**
	 * ���ݰ������в�ѯ
	 * 
	 * @param name
	 * @return info ����ֵ�ǿգ������ɵ������ж�
	 */
	public AppUseStatics queryByPkgName(String pkgName) {
		String sql = "SELECT * FROM " + DBHelper.ALL_APP_INFO
				+ " where pkgName= '" + pkgName + "'";

		AppUseStatics info = new AppUseStatics();
		Cursor c = db.rawQuery(sql, null);
		if (c.moveToNext()) {
			info.setPkgName(c.getString(c.getColumnIndex("pkgName")));
			info.setName(c.getString(c.getColumnIndex("appName")));
			info.setUseFreq(c.getInt(c.getColumnIndex("useFreq")));
			info.setUseTime(c.getInt(c.getColumnIndex("useTime")));
			info.setIcon(IconUtils.getDrawableFromBitmap(IconUtils
					.getBitmapFromBytes(c.getBlob(c.getColumnIndex("appIcon")))));
			info.setSysApp(c.getInt(c.getColumnIndex("isSysApp")));
			info.setWeight(c.getInt(c.getColumnIndex("weight")));
		}else{
			info.setName("empty");
			info.setPkgName("empty");
		}
		c.close();
		return info;
	}

	/**
	 * ��ѯ����Ӧ����Ϣ
	 */
	public ArrayList<AppUseStatics> findAll() {
		ArrayList<AppUseStatics> infos = new ArrayList<AppUseStatics>();
		String sql = "SELECT * FROM " + DBHelper.ALL_APP_INFO;
		Cursor c = db.rawQuery(sql, null);
		while (c.moveToNext()) {
			AppUseStatics info = new AppUseStatics();
			info.setName(c.getString(c.getColumnIndex("appName")));
			info.setPkgName(c.getString(c.getColumnIndex("pkgName")));
			info.setSysApp(c.getInt(c.getColumnIndex("isSysApp")));
			info.setUseFreq(c.getInt(c.getColumnIndex("useFreq")));
			info.setUseTime(c.getInt(c.getColumnIndex("useTime")));
			info.setIcon(IconUtils.getDrawableFromBitmap(IconUtils
					.getBitmapFromBytes(c.getBlob(c.getColumnIndex("appIcon")))));
			info.setWeight(c.getInt(c.getColumnIndex("weight")));

			infos.add(info);
		}
		c.close();
		Collections.sort(infos);
		return infos;
	}

	/**
	 * ��������ǰcount��Ӧ��
	 * 
	 * @param count
	 * @return
	 */
	public ArrayList<AppUseStatics> findTopApp(int count) {
		ArrayList<AppUseStatics> infos = new ArrayList<AppUseStatics>();

		// ��ѯ֮��Ȩ������
		String sql = "SELECT * FROM " + DBHelper.ALL_APP_INFO
				+ " order by weight desc";
		Cursor c = db.rawQuery(sql, null);

		if (c.getCount() <= 0) {
			return null;
		} else {
			// ȡ��ǰcount ��Ӧ�õ���Ϣ
			for (int i = 0; i < count; i++) {
				if (c.moveToNext()) {
					AppUseStatics info = new AppUseStatics();
					info.setPkgName(c.getString(c.getColumnIndex("pkgName")));
					info.setName(c.getString(c.getColumnIndex("appName")));
					info.setUseFreq(c.getInt(c.getColumnIndex("useFreq")));
					info.setUseTime(c.getInt(c.getColumnIndex("useTime")));
					info.setIcon(IconUtils.getDrawableFromBitmap(IconUtils
							.getBitmapFromBytes(c.getBlob(c.getColumnIndex("appIcon")))));
					info.setSysApp(c.getInt(c.getColumnIndex("isSysApp")));
					info.setWeight(c.getInt(c.getColumnIndex("weight")));

					infos.add(info);
				}
			}
		}
		c.close();
		Collections.sort(infos);
		return infos;
	}

	/**
	 * ����Ӧ��������Ӧ����Ϣ�������޸�Ȩ�غ�ʹ��ʱ����Ϣ��
	 * 
	 */
	public int updateAppInfo(AppUseStatics info) {

		ContentValues cv = new ContentValues();
		cv.put("useFreq", info.getUseFreq());
		cv.put("useTime", info.getUseTime());
		cv.put("weight", info.getWeight());
		String[] whereArgs = { String.valueOf(info.getName()) };
		return db.update(DBHelper.ALL_APP_INFO, cv, "appName=?", whereArgs);
	}

	/**
	 * �������ݿ�������Ӧ����Ϣ
	 * 
	 * @return ���µļ�¼��
	 */
	public int updateAll() {

		return 0;
	}

	/**
	 * @Description: �ر����ݿ�
	 */
	public void closeDB() {
		// db.close();
		helper.close();
	}
}
