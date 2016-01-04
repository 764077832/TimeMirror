package com.zp.quickaccess.utils;

public class CommonUtils {
	/**
	 * ��������������ת��Ϊ *Сʱ*��*���ʱ����ʾ��ʽ
	 * 
	 * �������ʱ�����1Сʱ����ôֻ�� *Сʱ*�ֵ���ʽչʾ
	 * ���ʱ�����1Сʱ���Ǵ���1���ӣ���ô�� *��*�����ʽչʾ
	 * ���ʹ��ʱ�����1���� ��*�����ʽչʾ
	 * 
	 * @param time
	 * @return
	 */
	public static String getFormatTime(int time) {
		int hour = 0;
		int minute = 0;
		StringBuilder result = new StringBuilder();
		
		if (time > 3600) {
			hour = time / 3600;
			time = time % 3600;
			result.append(hour + "Сʱ");
			
			if (time > 60) {
				minute = time / 60;
				time = time % 60;
				result.append(minute + "��");
			}
		}else{
			if (time > 60) {
				minute = time / 60;
				time = time % 60;
				result.append(minute + "��");
				
				if(time > 0){
					result.append(time + "��");
				}
			}else{
				result.append(time + "��");
			}
		}
		return result.toString();
	}
	
	/**
	 * ������ת��Ϊ�����������ӵ�λ
	 * 
	 * @param time
	 * @return
	 */
	public static int getFormatMinute(int time) {
		int minute = 0;
		if (time > 60) {
			minute = minute / 60 ;
		}
		return minute + 1; // ����һ���Ӱ�һ���Ӽ���
	}
	
	/**
	 * 
	 * @comment ����ֵ����
	 * @param @param value
	 * @param @return   
	 * @return float  
	 * @throws
	 * @date 2015-12-28 ����12:57:59
	 */
	public static float abs(float f){
		if(f > 0){
			return f;
		}else{
			return -f;
		}
	}

}
