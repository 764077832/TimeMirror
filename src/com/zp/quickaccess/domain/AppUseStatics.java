package com.zp.quickaccess.domain;

import com.zp.quickaccess.view.SlideView;

/*
 * Ӧ�ó������Ϣ��������Ϣ����ʹ����Ϣ
 * ���ɻ�����Ϣ��APPInfo
 */
public class AppUseStatics extends AppInfo implements Comparable<AppUseStatics> {
	private int useFreq; // ʹ��Ƶ�ʣ��Դ���Ϊ��λ
	private int useTime; // ʹ��ʱ��������Ϊ��λ
	private int weight; // Ȩ�أ���������
	private SlideView slideView; // �Զ���Ļ����ؼ�

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getUseFreq() {
		return useFreq;
	}

	public void setUseFreq(int useFreq) {
		this.useFreq = useFreq;
	}

	public int getUseTime() {
		return useTime;
	}

	public void setUseTime(int useTime) {
		this.useTime = useTime;
	}

	public SlideView getSlideView() {
		return slideView;
	}

	public void setSlideView(SlideView slideView) {
		this.slideView = slideView;
	}

	/**
	 * ʵ���䰴Ȩ�غ��Ƿ���ϵͳӦ�ý�������
	 * 
	 * ���ΰ�Ȩ��ֵ��ʹ��Ƶ�ʣ�ʹ��ʱ�䣬�Ƿ��ǵ�����Ӧ�ý�������
	 * 
	 * a negative integer if this instance is less than another; a positive
	 * integer if this instance is greater than another; 0 if this instance has
	 * the same order as another.
	 * 
	 * Collection.sortʵ�ֵ��ǵ������򣬴˴���Ҫ���ǵݼ�����������compareToʵ��ʱʹ���෴���߼�
	 * 
	 * �����Ż���һ�£�ֱ�Ӽ���Ȩֵ����Ȩֵ���бȽ� Ȩֵ����ϵͳӦ�ó�ʼ��Ϊ0������Ӧ�ó�ʼ��Ϊ1����֤����Ӧ����������ϵͳӦ��ǰ�� Ȩֵ =
	 * ʹ��ʱ�䳤�� + ʹ�ô���
	 * 
	 * �ȽϺ����������ǻᵼ�����벻���Ĵ���˳��,��λ��᷸һЩ�Ƚ�����ͻ�Ĵ���
	 * ����û�ж�������ν����жϵ�ʱ��ͻ��׳������쳣��
	 * Comparison method violates its general contract
	 */
	@Override
	public int compareTo(AppUseStatics another) {

		if(this.weight > another.weight)
			return -1;
		else if(this.weight < another.weight)
			return 1;
		else
			return 0;
	}

}
