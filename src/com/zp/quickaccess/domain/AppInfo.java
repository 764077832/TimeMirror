package com.zp.quickaccess.domain;

import android.graphics.drawable.Drawable;

/*
 * ��PackageManager��ȡ��Ӧ�ó������Ϣ
 */
public class AppInfo {

	private Drawable icon;
	private String name;
	private String pkgName;
	private int isSysApp; // �ж��Ƿ���ϵͳӦ��;1������ϵͳӦ�� 0 �����ǵ�����Ӧ��

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public int isSysApp() {
		return isSysApp;
	}

	public void setSysApp(int isSysApp) {
		this.isSysApp = isSysApp;
	}
}
