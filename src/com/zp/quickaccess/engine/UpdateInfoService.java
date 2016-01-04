package com.zp.quickaccess.engine;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;

import com.zp.quickaccess.domain.UpdateInfo;

/**
 * ���ʷ�������ȡ������Ϣ
 * 
 */
public class UpdateInfoService {

	private Context context;

	public UpdateInfoService(Context context) {
		this.context = context;
	}

	/**
	 * 
	 * @param urlid
	 *            ������·��string��Ӧ��id
	 * @return ���µ���Ϣ
	 */
	public UpdateInfo getUpdataInfo(int urlid) throws Exception {
		String path = context.getResources().getString(urlid);
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000); // ���ӳ�ʱ5��
		conn.setRequestMethod("GET");
		InputStream is = conn.getInputStream();
		return UpdateInfoParser.getUpdataInfo(is);
	}
}
