package com.zp.quickaccess.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.zp.quickaccess.domain.DWAppInfo;

public class DayStatisticDBManager {
	
	private DBHelper helper;
	private SQLiteDatabase db;

	public DayStatisticDBManager(Context context) {
		helper = DBHelper.getInstance(context);
		db = helper.getWritableDatabase();
	}

	public void add(DWAppInfo appInfo) {
		db.beginTransaction();
		// appName pkgName useFreq useTime
		try {
			db.execSQL(
					"INSERT INTO " + DBHelper.DAY_APPINFO
							+ " VALUES(?, ?, ?, ?)",
					new Object[] { appInfo.getAppName(),
							appInfo.getPkgName(),
							appInfo.getUseFreq(),
							appInfo.getUseTime() });

			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}
	
	/**
	 * 
	 * @comment �����������Ӧ�õ����ݿ�
	 * 
	 * @param @param pkgName   
	 * @return void  
	 * @throws
	 * @date 2015-12-28 ����9:05:22
	 */
	public void addByName(String pkgName) {
		db.beginTransaction();
		// appName pkgName useFreq useTime
		DWAppInfo appInfo = new DWAppInfo();
		appInfo.setAppName(pkgName);
		appInfo.setPkgName(pkgName);
		appInfo.setUseFreq(0);
		appInfo.setUseTime(0);
		try {
			db.execSQL(
					"INSERT INTO " + DBHelper.DAY_APPINFO
							+ " VALUES(?, ?, ?, ?)",
					new Object[] { appInfo.getAppName(),
							appInfo.getPkgName(),
							appInfo.getUseFreq(),
							appInfo.getUseTime() });
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}


	/**
	 * ���������ݴ����ݿ���ɾ��
	 * 
	 * @return
	 */
	public int deleteAll() {
		return db.delete(DBHelper.DAY_APPINFO, null, null);
	}


	/**
	 * ��ѯ����Ӧ����Ϣ���˴�������Ҫ֪��Ӧ�ø�����ʹ�ô�����ʹ��ʱ��
	 * 
	 * �����޹���Ϣ�Ͳ��ṩ��
	 */
	public ArrayList<DWAppInfo> findAll() {
		ArrayList<DWAppInfo> infos = new ArrayList<DWAppInfo>();
		String sql = "SELECT * FROM " + DBHelper.DAY_APPINFO;
		Cursor c = db.rawQuery(sql, null);
		while (c.moveToNext()) {
			DWAppInfo info = new DWAppInfo();
			
			info.setAppName(c.getString(c.getColumnIndex("appName")));
			info.setUseFreq(c.getInt(c.getColumnIndex("useFreq")));
			info.setUseTime(c.getInt(c.getColumnIndex("useTime")));

			infos.add(info);
		}
		c.close();
		return infos;
	}
	
	/**
	 * 
	 * @comment ��ȡ���е�Ӧ������
	 * @param @return   
	 * @return ArrayList<String>  
	 * @throws
	 * @date 2015-12-28 ����9:02:00
	 */
	public ArrayList<String> findAllPkgNames() {
		ArrayList<String> infos = new ArrayList<String>();
		String sql = "SELECT pkgName FROM " + DBHelper.DAY_APPINFO;
		Cursor c = db.rawQuery(sql, null);
		while (c.moveToNext()) {
			String name = c.getString(0);
			infos.add(name);
		}
		c.close();
		return infos;
	}


	/**
	 * ����Ӧ��������Ӧ����Ϣ��
	 * ��������ʹ�ô�����ʹ��ʱ��
	 * 
	 */
	public void updateAppInfo(DWAppInfo info) {

		ContentValues cv = new ContentValues();
		cv.put("useFreq", info.getUseFreq());
		cv.put("useTime", info.getUseTime());
		String[] whereArgs = { String.valueOf(info.getAppName()) };
		db.update(DBHelper.DAY_APPINFO, cv, "appName=?", whereArgs);
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
