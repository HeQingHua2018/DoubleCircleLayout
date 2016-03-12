package com.loommo.circlelayout.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import com.loommo.circlelayout.R;
import com.loommo.circlelayout.listener.OnMenuItemClickListener;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class DoubleCircleLayout extends ViewGroup {

    /*item的图片id*/
    private List<Integer> resIds;
    /*载入数据item数*/
    private int mMenuItemCount;

    /*容器宽度*/
    private int mRadius;

    /*child item的默认尺寸占radius百分比 */
    private static final float RADIO_DEFAULT_CHILD_DIMENSION = 220 / 1520f;
    private static final float RADIO_DEFAULT_CHILD_DIMENSION_WIDTH = 500 / 1520f;
    /*focusitem的默认尺寸占radius百分比 */
    private static final float RADIO_DEFAULT_FOCUSITEM_DIMENSION = 20 / 1520f;
    /*中心child的默认尺寸占radius百分比 */
    private static final float RADIO_DEFAULT_CENTERITEM_DIMENSION = 994 / 1520f;
    /*中心child圆点距离左侧距离占radius百分比*/
    private static final float RADIO_DEFAULT_CENTERITEM_LEFT = 259 / 1520f;

    /*item最大数目*/
    private static final int ITEM_MAX_CONNT = 8;

    /*如果移动角度达到该值，则屏蔽点击*/
    private static final int NOCLICK_VALUE = 3;
    /*回弹每秒滚动速度*/
    private static final int FLINGABLE_SPEED = 8;
    /* 外圈布局时的开始角度 */
    private double mOuterStartAngle = 0;
    /* 内圈布局时的开始角度 */
    private double mInnerStartAngle = 0;
    /*检测按下到抬起时旋转的角度*/
    private float mOuterTmpAngle;//内圈旋转角度
    private float mInnerTmpAngle;//外圈
    /*记录上一次的x，y坐标*/
    private float mLastX;
    private float mLastY;
    private float mOneAngle;
    private boolean isBig = true;
    /* 自动滚动的Runnable/外圈*/
    private OuterFlingRunnable mOuterFlingRunnable;
    /* 自动滚动的Runnable/内圈*/
    private InnerFlingRunnable mInnerFlingRunnable;

    /*MenuItem的点击事件接口*/
    private OnMenuItemClickListener mOnMenuItemClickListener;

    public DoubleCircleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPadding(0, 0, 0, 0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int layoutRadius = mRadius;

        int focusRadius = (int) (820 / 1520f * mRadius);

        int fWidth = (int) (layoutRadius * RADIO_DEFAULT_FOCUSITEM_DIMENSION);

        // Laying out the child views
        final int childCount = getChildCount();

        int left, top;
        // menu item 的尺寸
        int cWidth = (int) (layoutRadius * RADIO_DEFAULT_CHILD_DIMENSION_WIDTH);

        int cHeight = (int) (layoutRadius * RADIO_DEFAULT_CHILD_DIMENSION);

        // 根据menu item的个数，计算角度
        float angleDelay;

        if (getChildCount() > 2) {
            angleDelay = 360 / (getChildCount() - 2);
        } else {
            angleDelay = 360;
        }

        mOneAngle = angleDelay;

        // 遍历去设置menuitem的位置
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            // 中心child与focus跳过
            if (child.getId() == R.id.id_circle_menu_item_center || child.getId() == R.id.id_circle_menu_item_focus)
                continue;

            if (child.getVisibility() == GONE) {
                continue;
            }

            mOuterStartAngle %= 360;

            mInnerStartAngle %= 360;

            // 计算，中心点到menu item中心的距离
            float tmp = layoutRadius / 2f - cHeight / 2;

            // tmp cos 即menu item中心点的横坐标
            left = mRadius * 200 / 1520
                    + (int) Math.round(tmp
                    * Math.cos(Math.toRadians(mOuterStartAngle)) - 1 / 2f
                    * cHeight);
            // tmp sin 即menu item的纵坐标
            top = mRadius
                    / 2
                    + (int) Math.round(tmp
                    * Math.sin(Math.toRadians(mOuterStartAngle)) - 1 / 2f
                    * cHeight);

            child.layout(left, top, left + cWidth, top + cHeight);
            // 叠加尺寸
            mOuterStartAngle += angleDelay;
        }

        // focus位置
        View fView = findViewById(R.id.id_circle_menu_item_focus);
        if (fView != null) {

            float tmp = focusRadius / 2f;

            int fl = mRadius * 259 / 1520 + (int) Math.round(tmp
                    * Math.cos(Math.toRadians(mInnerStartAngle)) - 1 / 2f
                    * fWidth);
            int fr = mRadius / 2 + (int) Math.round(tmp
                    * Math.sin(Math.toRadians(mInnerStartAngle)) - 1 / 2f
                    * fWidth);
            fView.layout(fl, fr, fl + fWidth, fr + fWidth);
        }

        // 找到中心的view，如果存在设置onclick事件
        View cView = findViewById(R.id.id_circle_menu_item_center);
        if (cView != null) {
            cView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mOnMenuItemClickListener != null) {
                        mOnMenuItemClickListener.itemCenterClick(v);
                    }
                }
            });
            // 设置center item位置
            float cl = mRadius * RADIO_DEFAULT_CENTERITEM_LEFT - cView.getMeasuredWidth() / 2;
            int cr = mRadius / 2 - cView.getMeasuredWidth() / 2;
            cView.layout((int) cl, cr, (int) (cl + cView.getMeasuredWidth()), cr + cView.getMeasuredWidth());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int resWidth = 0;
        int resHeight = 0;

        // 根据传入的参数，分别获取测量模式和测量值
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        // 如果宽或者高的测量模式非精确值
        if (widthMode != MeasureSpec.EXACTLY
                || heightMode != MeasureSpec.EXACTLY) {
            // 主要设置为背景图的高度
            resWidth = getSuggestedMinimumWidth();
            // 如果未设置背景图片，则设置为屏幕宽高的默认值
            resWidth = resWidth == 0 ? getDefaultWidth() : resWidth;

            resHeight = getSuggestedMinimumHeight();
            // 如果未设置背景图片，则设置为屏幕宽高的默认值
            resHeight = resHeight == 0 ? getDefaultWidth() : resHeight;
        } else {
            // 若设置为精确值
            resWidth = width;
            resHeight = height;
        }

        setMeasuredDimension(resWidth, resHeight);

        // 获得半径
        mRadius = resHeight;

        // child数量
        final int count = getChildCount();
        // menu item尺寸
        int childSize = (int) (mRadius * RADIO_DEFAULT_CHILD_DIMENSION_WIDTH);
        // menu item测量模式
        int childMode = MeasureSpec.EXACTLY;

        // 迭代测量
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            // 计算menu item的尺寸；以及和设置好的模式，去对item进行测量
            int makeMeasureSpec = -1;

            if (child.getId() == R.id.id_circle_menu_item_center) {
                makeMeasureSpec = MeasureSpec.makeMeasureSpec(
                        (int) (mRadius * RADIO_DEFAULT_CENTERITEM_DIMENSION),
                        childMode);
            } else if (child.getId() == R.id.id_circle_menu_item_focus) {
                makeMeasureSpec = MeasureSpec.makeMeasureSpec((int) (mRadius * RADIO_DEFAULT_FOCUSITEM_DIMENSION),
                        childMode);
            } else {
                makeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize,
                        childMode);
            }
            child.measure(makeMeasureSpec, makeMeasureSpec);
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                // 初始点击xy值
                mLastX = x;
                mLastY = y;
                // 初始化
                mOuterTmpAngle = 0;
                mInnerTmpAngle = 0;
                // 判断内外圈
                isBig = isBigCircle(mLastX, mLastY);
                break;

            case MotionEvent.ACTION_MOVE:

                // 获得开始的角度
                float start = getAngle(mLastX, mLastY);
                // 获得当前的角度
                float end = getAngle(x, y);

                double tmpAngle;

                if (isBig) {
                    tmpAngle = mOuterStartAngle;
                } else {
                    tmpAngle = mInnerStartAngle;
                }

                // 如果是一、四象限，则直接end-start，角度值都是正值
                if (getQuadrant(x, y) == 1 || getQuadrant(x, y) == 4) {
                    tmpAngle += end - start;
                    mInnerTmpAngle += end - start;
                } else {
                    // 二、三象限，色角度值是负值
                    tmpAngle += start - end;
                    mInnerTmpAngle += start - end;
                }

                if (isBig) {
                    mOuterStartAngle = tmpAngle;
                } else {
                    mInnerStartAngle = tmpAngle;
                    mOuterTmpAngle += end - start;
                }
                // 重新布局
                requestLayout();

                // 同时设置中心圈角度
                setMenuCentreAngle(mOuterTmpAngle);

                mLastX = x;
                mLastY = y;

                break;

            case MotionEvent.ACTION_UP:

                // 处理当前角度为0~360
                double wCircleAngle = mOuterStartAngle;
                double nCircleAngle = mInnerStartAngle;
                wCircleAngle %= 360;
                wCircleAngle = wCircleAngle >= 0 ? wCircleAngle : (360 + wCircleAngle);
                nCircleAngle %= 360;
                nCircleAngle = nCircleAngle >= 0 ? nCircleAngle : (360 + nCircleAngle);

                // 判断内外圈是否match,在一份+-0.2均MATCH
                double dvalue = Math.abs(wCircleAngle - nCircleAngle) % mOneAngle;
                if (dvalue < mOneAngle * 0.05 | mOneAngle - dvalue < mOneAngle * 0.05) {
                    // match,更换中心图片
                    matchItem(wCircleAngle, nCircleAngle, isBig);
                } else if (isBig) {
                    // 外圈不匹配，回弹
                    float anglew = (float) (wCircleAngle % mOneAngle);
                    if (anglew < mOneAngle * 0.5) {
                        post(mOuterFlingRunnable = new OuterFlingRunnable(-anglew));
                    } else {
                        post(mOuterFlingRunnable = new OuterFlingRunnable(mOneAngle - anglew));
                    }
                    return true;
                } else {
                    // 内圈不匹配，回弹
                    float anglen = (float) (nCircleAngle % mOneAngle);
                    if (anglen < mOneAngle * 0.5) {
                        post(mInnerFlingRunnable = new InnerFlingRunnable(-anglen));
                    } else {
                        post(mInnerFlingRunnable = new InnerFlingRunnable(mOneAngle - anglen));
                    }
                    return true;
                }
                // 如果当前旋转角度超过NOCLICK_VALUE屏蔽点击
                if (Math.abs(mInnerTmpAngle) > NOCLICK_VALUE) {
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /*
    *设置MenuItem的点击事件
    * */
    public void setOnMenuItemClickListener(
            OnMenuItemClickListener mOnMenuItemClickListener) {
        this.mOnMenuItemClickListener = mOnMenuItemClickListener;
    }

    public void setMenuItemIconsAndTexts(List<Integer> resIds) {

        this.resIds = resIds;

        if (resIds == null) {
            throw new IllegalArgumentException("items error");
        } else {
            mMenuItemCount = resIds.size();
        }

        if (mMenuItemCount != 0 && mMenuItemCount < ITEM_MAX_CONNT) {
            addMenuItems(mMenuItemCount);
        } else {
            throw new IllegalArgumentException("the number of items can't be more than eight");
        }
    }

    private void addMenuItems(int number) {
        LayoutInflater mInflater = LayoutInflater.from(getContext());

        initItems();

        for (int i = 0; i < number; i++) {
            final int j = i;
            View view = mInflater.inflate(R.layout.item_circle_menu, this,
                    false);
            ImageView iv = (ImageView) view
                    .findViewById(R.id.id_circle_menu_item_image);
            TextView tv = (TextView) view
                    .findViewById(R.id.id_circle_menu_item_text);

            if (iv != null) {
                iv.setVisibility(View.VISIBLE);
                iv.setImageResource(resIds.get(i));
                iv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mOnMenuItemClickListener != null) {
                            mOnMenuItemClickListener.itemClick(v, j);
                        }
                    }
                });
            }
            if (tv != null) {
                tv.setVisibility(View.VISIBLE);
            }

            // 添加view到容器中
            addView(view);
        }

        setMenuCentre(0);

    }

    /*
    * 设置中心圆图片
    * */
    public void setMenuCentre(int imageNumber) {
        if (imageNumber >= mMenuItemCount) {
            return;
        }
        CircleImageView cv = (CircleImageView) findViewById(R.id.id_circle_menu_center_image);
        mOuterTmpAngle = 0;
        cv.setImageAngle(mOuterTmpAngle, false);
        cv.setImageResource(resIds.get(imageNumber));
    }

    /*
    * 设置中心圈转动角度
    * */
    public void setMenuCentreAngle(float imageAngle) {
        CircleImageView cv = (CircleImageView) findViewById(R.id.id_circle_menu_center_image);
        cv.setImageAngle(imageAngle, true);
    }
    
    /*
    * 处理item，避免重复添加
    * */
    private void initItems() {
        int childCount = getChildCount();
        if (childCount > 2) {
            for (int i = 2; i < childCount; i++) {
                removeViewAt(2);
            }
        }
    }

    private void matchItem(double wCircleAngle, double nCircleAngle, boolean isBig) {

        if (mMenuItemCount == 0) {
            return;
        }

        //计算当前item
        int w = (int) (Math.round(wCircleAngle / mOneAngle) % mMenuItemCount);
        if (w != 0) {
            w = getChildCount() - 2 - w;
        }
        int n = (int) (Math.round(nCircleAngle / mOneAngle) % mMenuItemCount);
        int item = w + n + mMenuItemCount;
        item = item % mMenuItemCount;// 当前对应item数
        setMenuCentre(item);
    }

    /*
    * 获得设备宽度
    * */
    private int getDefaultWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return Math.min(outMetrics.widthPixels, outMetrics.heightPixels);
    }

    /*
    * 计算象限
    * */
    private int getQuadrant(float x, float y) {
        int tmpX = (int) (x - mRadius * (259 / 1520d));
        int tmpY = (int) (y - mRadius / 2);
        if (tmpX >= 0) {
            return tmpY >= 0 ? 4 : 1;
        } else {
            return tmpY >= 0 ? 3 : 2;
        }

    }

    /*
    *根据触摸的位置，计算角度
    * */
    private float getAngle(float xTouch, float yTouch) {
        double x = xTouch - (mRadius * (259 / 1520d));
        double y = yTouch - (mRadius / 2d);
        return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
    }


    /*
     * 判断旋转外圈还是内圈
     * */
    private boolean isBigCircle(float xTouch, float yTouch) {
        double x = xTouch - (mRadius * (259 / 1520d));
        double y = yTouch - (mRadius / 2d);
        if (Math.hypot(x, y) > (mRadius * (994 / 1520f) / 2)) {
            return true;// 外圈
        }
        return false;// 内圈
    }

    /*
    *外圈自动滑动
    * */
    private class OuterFlingRunnable implements Runnable {

        private float angelPerSecond;
        private float angelSpeed;

        public OuterFlingRunnable(float velocity)//需移动角度
        {
            this.angelPerSecond = velocity;
            int i = 1;
            if (velocity < 0) {
                i = -1;
            }
            angelSpeed = i * FLINGABLE_SPEED;
        }

        public void run() {
            if (Math.abs(angelPerSecond) < 1) {
                double wCircleAngle = mOuterStartAngle;
                double nCircleAngle = mInnerStartAngle;
                wCircleAngle %= 360;
                wCircleAngle = wCircleAngle >= 0 ? wCircleAngle : (360 + wCircleAngle);
                nCircleAngle %= 360;
                nCircleAngle = nCircleAngle >= 0 ? nCircleAngle : (360 + nCircleAngle);
                matchItem(wCircleAngle, nCircleAngle, true);
                return;
            }
            if (Math.abs(angelPerSecond) < FLINGABLE_SPEED) {
                //需转动角度<速度
                mOuterStartAngle += angelPerSecond;
                angelPerSecond = 0;
            } else {
                mOuterStartAngle += angelSpeed;
                angelPerSecond -= angelSpeed;
                angelSpeed /= 1.0666F;
            }
            postDelayed(this, 30);
            // 重新布局
            requestLayout();
        }
    }

    /*
    *内圈自动滑动
    * */
    private class InnerFlingRunnable implements Runnable {
        private float angelPerSecond;
        private float angelSpeed;

        public InnerFlingRunnable(float velocity)//穿入需移动角度
        {
            this.angelPerSecond = velocity;
            int i = 1;
            if (velocity < 0) {
                i = -1;
            }
            angelSpeed = i * FLINGABLE_SPEED;
        }

        public void run() {
            if (Math.abs(angelPerSecond) < 1) {
                double wCircleAngle = mOuterStartAngle;
                double nCircleAngle = mInnerStartAngle;
                wCircleAngle %= 360;
                wCircleAngle = wCircleAngle >= 0 ? wCircleAngle : (360 + wCircleAngle);
                nCircleAngle %= 360;
                nCircleAngle = nCircleAngle >= 0 ? nCircleAngle : (360 + nCircleAngle);
                matchItem(wCircleAngle, nCircleAngle, false);
                return;
            }
            if (Math.abs(angelPerSecond) < FLINGABLE_SPEED) {
                // 需转动角度<速度
                mInnerStartAngle += angelPerSecond;
                mOuterTmpAngle += angelPerSecond;
                angelPerSecond = 0;
            } else {
                mInnerStartAngle += angelSpeed;
                mOuterTmpAngle += angelSpeed;
                angelPerSecond -= angelSpeed;
                angelSpeed /= 1.0666F;
            }
            postDelayed(this, 30);
            // 重新布局
            requestLayout();
            setMenuCentreAngle(mOuterTmpAngle);
        }
    }
}
