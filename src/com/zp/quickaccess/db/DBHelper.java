package com.zp.quickaccess.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @author zhangpeng
 * 
 */
public class DBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "shortshot.db"; // ���ݿ�����
	private static final int DATABASE_VERSION = 1; // ���ݿ�汾��
	public static String ALL_APP_INFO = "all_appinfo"; // ����Ӧ����Ϣ��
	public static String DAY_APPINFO = "day_appinfo"; // ÿ��Ӧ��ʵ����Ϣͳ�Ʊ�
	public static String WEEK_APPINFO = "week_appinfo"; // ÿ��Ӧ����Ϣͳ�Ʊ�

	// ����������Ӧ������ʹ��Ƶ�Σ�ʹ��ʱ�����Լ�����õ������ȼ�
	// ���ж����Ƿ���ϵͳӦ�ò��õ��ǲ������ͽ����жϣ������������ǲ����ڵģ�����ʹ��INTEGER��0����false��1����true
	private final static String ALL_APP_INFO_SQL = "CREATE TABLE IF NOT EXISTS "
			+ ALL_APP_INFO
			+ "(appName VARCHAR PRIMARY KEY,pkgName VARCHAR,"
			+ "isSysApp INTEGER, useFreq INTEGER, useTime INTEGER, appIcon BLOB,"
			+ "weight INTEGER)";
	// ÿ����Ϣֻ��Ҫ֪��ʱ�䳤�Ⱥ�ʹ�ô�����Щ������Ϣ�Ϳ�����
	private final static String DAY_APPINFO_SQL = "CREATE TABLE IF NOT EXISTS "
			+ DAY_APPINFO + "(appName VARCHAR PRIMARY KEY,pkgName VARCHAR,"
			+ "useFreq INTEGER, useTime INTEGER)";

	private final static String WEEK_APPINFO_SQL = "CREATE TABLE IF NOT EXISTS "
			+ WEEK_APPINFO
			+ "(appName VARCHAR PRIMARY KEY,pkgName VARCHAR,"
			+ "useFreq INTEGER, useTime INTEGER)";

	private static DBHelper mInstance = null;

	private DBHelper(Context context) {
		// CursorFactory����Ϊnull,ʹ��Ĭ��ֵ
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * ����ģʽ ��ȡ���ݿ�helperʵ��
	 */
	public static synchronized DBHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new DBHelper(context);
		}
		return mInstance;
	}

	/**
	 * ���ݿ��һ�α�����ʱonCreate�ᱻ���� ����ϵͳ����Ҫʹ�õ�3�ű�
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(ALL_APP_INFO_SQL);
		db.execSQL(DAY_APPINFO_SQL);
		db.execSQL(WEEK_APPINFO_SQL);
	}

	/**
	 * ���ݿ��ṹ�����仯ʱ��onUpgrade�ᱻ����
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
}
