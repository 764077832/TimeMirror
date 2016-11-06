package com.zp.quickaccess.ui;

import java.util.ArrayList;
import java.util.List;

import com.zp.quickaccess.R;
 
import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 
 * @file HelpPager.java
 * @package com.zp.quickaccess.ui 
 * @comment http://www.android100.org/html/201306/29/3353.html
 */
public class HelpPager extends Activity implements OnClickListener, OnPageChangeListener{
     private ViewPager vp;
        private ViewPagerAdapter vpAdapter;
        private List<View> views;
       
        //����ͼƬ��Դ
        private static final int[] pics = { R.drawable.help_00,
                R.drawable.help_01, R.drawable.help_02,
                R.drawable.help_03 };
       
        //�ײ�С��ͼƬ
        private ImageView[] dots ;
       
        //��¼��ǰѡ��λ��
        private int currentIndex;
       
       
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.main);
           
            views = new ArrayList<View>();
          
            LinearLayout.LayoutParams mParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
           
            //��ʼ������ͼƬ�б�
            for(int i=0; i<pics.length; i++) {
                ImageView iv = new ImageView(this);
                iv.setLayoutParams(mParams);
                iv.setImageResource(pics[i]);
                views.add(iv);
            }
            vp = (ViewPager) findViewById(R.id.viewpager);
            //��ʼ��Adapter
            vpAdapter = new ViewPagerAdapter(views);
            vp.setAdapter(vpAdapter);
            //�󶨻ص�
            vp.setOnPageChangeListener(this);
           
            //��ʼ���ײ�С��
            initDots();
           
        }
       
        private void initDots() {
            LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
          
 
            dots = new ImageView[pics.length];
 
            //ѭ��ȡ��С��ͼƬ
            for (int i = 0; i < pics.length; i++) {
            //�õ�һ��LinearLayout�����ÿһ����Ԫ��
                dots[i] = (ImageView) ll.getChildAt(i);
                dots[i].setEnabled(true);//����Ϊ��ɫ
                dots[i].setOnClickListener(this);
                dots[i].setTag(i);//����λ��tag������ȡ���뵱ǰλ�ö�Ӧ
            }
 
            currentIndex = 0;
            dots[currentIndex].setEnabled(false);//����Ϊ��ɫ����ѡ��״̬
        }
       
       
        private void setCurView(int position)
        {
            if (position < 0 || position >= pics.length) {
                return;
            }
 
            vp.setCurrentItem(position);
        }
 
       
        private void setCurDot(int positon)
        {
            if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
                return;
            }
 
            dots[positon].setEnabled(false);
            dots[currentIndex].setEnabled(true);
 
            currentIndex = positon;
        }
 
        //������״̬�ı�ʱ����
        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
           
        }
 
        //����ǰҳ�汻����ʱ����
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
           
        }
 
        //���µ�ҳ�汻ѡ��ʱ����
        @Override
        public void onPageSelected(int arg0) {
            //���õײ�С��ѡ��״̬
            setCurDot(arg0);
        }
 
        @Override
        public void onClick(View v) {
            int position = (Integer)v.getTag();
            setCurView(position);
            setCurDot(position);
        }
    public class ViewPagerAdapter extends PagerAdapter{
       
        //�����б�
        private List<View> views;
       
        public ViewPagerAdapter (List<View> views){
            this.views = views;
        }
 
        //����arg1λ�õĽ���
        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(views.get(arg1));       
        }
 
        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub
           
        }
 
        //��õ�ǰ������
        @Override
        public int getCount() {
            if (views != null)
            {
                return views.size();
            }
           
            return 0;
        }
       
 
        //��ʼ��arg1λ�õĽ���
        @Override
        public Object instantiateItem(View arg0, int arg1) {
           
            ((ViewPager) arg0).addView(views.get(arg1), 0);
           
            return views.get(arg1);
        }
 
        //�ж��Ƿ��ɶ������ɽ���
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return (arg0 == arg1);
        }
 
        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub
           
        }
 
        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }
 
        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub
           
        }
 
    }
}
