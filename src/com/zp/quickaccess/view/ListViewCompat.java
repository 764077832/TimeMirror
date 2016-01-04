package com.zp.quickaccess.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.zp.quickaccess.domain.AppUseStatics;
import com.zp.quickaccess.utils.LogUtil;

public class ListViewCompat extends ListView {

	private static final String TAG = "ListViewCompat";

	private SlideView mFocusedItemView;

	public ListViewCompat(Context context) {
		super(context);
	}

	public ListViewCompat(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ListViewCompat(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void shrinkListItem(int position) {
		View item = getChildAt(position);

		if (item != null) {
			try {
				((SlideView) item).shrink();
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			int x = (int) event.getX();
			int y = (int) event.getY();
			// ��ǰ�������һ��  
			int position = pointToPosition(x, y);
			LogUtil.i(TAG, "postion=" + position);
			/* 
			 * �õ���ǰ����е����ݴӶ�ȡ����ǰ�е�item
			 * �������˻��ɣ�ΪʲôҪ��ô�ɣ�Ϊʲô����getChildAt(position)��  
			 * ��ΪListView����л��棬����㲻��ô�ɣ���Щ�е�view���ǵò�����
			 */
			if (position != INVALID_POSITION) {
				AppUseStatics data = (AppUseStatics) getItemAtPosition(position);
				mFocusedItemView = data.getSlideView();
				LogUtil.i(TAG, "FocusedItemView=" + mFocusedItemView);
			}
		}
		default:
			break;
		}
		// ��ǰ�����view���ͻ����¼�������ʵ������SlideView������
		if (mFocusedItemView != null) {
			mFocusedItemView.onRequireTouchEvent(event);
		}

		return super.onTouchEvent(event);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev);
	}
}
