package com.zp.quickaccess.animation;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * 
 * @file TVAnimation.java
 * @package com.zp.quickaccess.animation 
 * @comment ģ��TV�رն���Ч��
 * @author zp
 * @date 2015-12-30 ����5:20:55
 */
public class TVAnimation extends Animation {

	private int mCenterWidth;
    private int mCenterHeight;
    private Camera mCamera = new Camera();
    private float mRotateY = 0.0f;

    @Override
    public void initialize(int width,
                           int height,
                           int parentWidth,
                           int parentHeight) {

        super.initialize(width, height, parentWidth, parentHeight);
        // ����Ĭ��ʱ��
        setDuration(3000);
        // ������������״̬
        setFillAfter(true);
        // ����Ĭ�ϲ�ֵ��
        setInterpolator(new AccelerateInterpolator());
        mCenterWidth = width / 2;
        mCenterHeight = height / 2;
    }

    // ��¶�ӿ�-������ת�Ƕ�
    public void setRotateY(float rorateY) {
        mRotateY = rorateY;
    }

    @Override
    protected void applyTransformation(
            float interpolatedTime,
            Transformation t) {
        final Matrix matrix = t.getMatrix();
        matrix.preScale(1,
                1 - interpolatedTime,
                mCenterWidth,
                mCenterHeight);
    }
}
