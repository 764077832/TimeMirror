package com.zp.quickaccess.engine;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.zp.quickaccess.domain.UpdateInfo;

/**
 * �����������˻�ȡ�ĸ�����Ϣ��xml�ļ�
 * 
 */
public class UpdateInfoParser {

	/**
	 * 
	 * @param is
	 *            ������xml��inputstream
	 *            
	 * @return updateinfo
	 */
	public static UpdateInfo getUpdataInfo(InputStream is) throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		UpdateInfo info = new UpdateInfo();
		parser.setInput(is, "utf-8");
		int type = parser.getEventType();

		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				if("version".equals(parser.getName())){
					String version = parser.nextText();
					info.setVersion(version);
				}else if("description".equals(parser.getName())){
					String description = parser.nextText();
					info.setDescription(description);
				}else if("apkurl".equals(parser.getName())){
					String apkurl = parser.nextText();
					info.setApkurl(apkurl);
				}
				
				break;

			}

			type = parser.next();
		}
		return info;
	}
}
