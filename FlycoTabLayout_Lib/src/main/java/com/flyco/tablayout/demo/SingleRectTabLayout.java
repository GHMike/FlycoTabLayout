package com.flyco.tablayout.demo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.tablayout.IndicatorPoint;
import com.flyco.tablayout.PointEvaluator;
import com.flyco.tablayout.R;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyco.tablayout.utils.ViewUtils;

import java.util.ArrayList;

/**
 * 只包含底部矩形
 */
public class SingleRectTabLayout extends FrameLayout implements ValueAnimator.AnimatorUpdateListener {

    private Context mContext;

    /**
     * 数据
     */
    private ArrayList<CustomTabEntity> mTabEntitys = new ArrayList<>();

    /**
     * Tab父控件
     */
    private LinearLayout mTabsContainer;

    /**
     * 当前 Tab 位置，默认0，第一个
     */
    private int mCurrentTab;

    /**
     * 最后一次 Tab 位置
     */
    private int mLastTab;

    /**
     * Tab Item 个数
     */
    private int mTabCount;

    /**
     * 记录当前Item位置 left right
     */
    private Rect mIndicatorCurrentRect = new Rect();

    private GradientDrawable mIndicatorDrawable = new GradientDrawable();

    /**
     * 指示器
     */
    private int mIndicatorColor = Color.WHITE;
    private float mIndicatorHeight = 5;//矩形高度
    private float mIndicatorWidth = 20;//矩形宽度

    /**
     * 动画相关定义
     */
    private boolean mIndicatorAnimEnable = true;//是否支持指示条动画
    private boolean mIndicatorBounceEnable = true;//是否支持反弹


    /**
     * 文字选中颜色
     */
    private int mTextSelectColor = Color.parseColor("#ffffff");

    /**
     * 文字未选中颜色
     */
    private int mTextUnselectColor = Color.parseColor("#AAffffff");

    /**
     * 动画
     */
    private ValueAnimator mValueAnimator;

    /**
     * 反弹动画
     */
    private OvershootInterpolator mOvershootInterpolator = new OvershootInterpolator(1.5f);

    /**
     * 当前指示器定义
     */
    private IndicatorPoint mCurrentPoint = new IndicatorPoint();

    /**
     * 上一个指示器定义
     */
    private IndicatorPoint mLastPoint = new IndicatorPoint();

    /**
     * 监听Tab切换
     */
    private OnTabSelectListener mOnTabSelectListener;

    public SingleRectTabLayout(Context context) {
        this(context, null, 0);
    }

    public SingleRectTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleRectTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);//重写onDraw方法,需要调用这个方法来清除flag
        setClipChildren(false);
        setClipToPadding(false);

        this.mContext = context;
        mTabsContainer = new LinearLayout(context);
        addView(mTabsContainer);
        mValueAnimator = ValueAnimator.ofObject(new PointEvaluator(), mLastPoint, mCurrentPoint);
        mValueAnimator.addUpdateListener(this);
    }

    public void setOnTabSelectListener(OnTabSelectListener listener) {
        this.mOnTabSelectListener = listener;
    }

    /**
     * 设置 Tab 数据
     *
     * @param tabEntitys
     */
    public void setTabData(ArrayList<CustomTabEntity> tabEntitys) {
        if (tabEntitys == null || tabEntitys.size() == 0) {
            throw new IllegalStateException("TabEntitys can not be NULL or EMPTY !");
        }

        this.mTabEntitys.clear();
        this.mTabEntitys.addAll(tabEntitys);

        notifyDataSetChanged();
    }

    /**
     * 刷新数据
     */
    public void notifyDataSetChanged() {
        mTabsContainer.removeAllViews();//清空父控件view
        this.mTabCount = mTabEntitys.size();
        View tabView;
        for (int i = 0; i < mTabCount; i++) {
            tabView = View.inflate(mContext, R.layout.layout_text_tab, null);
            tabView.setTag(i);
            addTab(i, tabView);
        }
    }

    /**
     * 创建并向父控件 mTabsContainer 添加tab
     */
    private void addTab(final int position, View tabView) {
        TextView tv_tab_title = (TextView) tabView.findViewById(R.id.tv_tab_title);
        tv_tab_title.setText(mTabEntitys.get(position).getTabTitle());
        tabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                if (mCurrentTab != position) {
                    setCurrentTab(position);
                    if (mOnTabSelectListener != null) {
                        mOnTabSelectListener.onTabSelect(position);
                    }
                } else {
                    if (mOnTabSelectListener != null) {
                        mOnTabSelectListener.onTabReselect(position);
                    }
                }
            }
        });
        LinearLayout.LayoutParams lp_tab = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
        mTabsContainer.addView(tabView, position, lp_tab);
    }

    /**
     * 循环遍历所有Tab位置，设置状态，可以恢复选择某个Item态
     *
     * @param position
     */
    private void updateTabSelection(int position) {
        for (int i = 0; i < mTabCount; ++i) {
            View tabView = mTabsContainer.getChildAt(i);
            final boolean isSelect = i == position;
            TextView tab_title = (TextView) tabView.findViewById(R.id.tv_tab_title);
            tab_title.setTextColor(isSelect ? mTextSelectColor : mTextUnselectColor);
        }
    }

    /**
     * 计算两个view之差，之后计算使用
     */
    private void calcOffset() {
        final View currentTabView = mTabsContainer.getChildAt(this.mCurrentTab);
        mCurrentPoint.left = currentTabView.getLeft();
        mCurrentPoint.right = currentTabView.getRight();

        final View lastTabView = mTabsContainer.getChildAt(this.mLastTab);
        mLastPoint.left = lastTabView.getLeft();
        mLastPoint.right = lastTabView.getRight();

        if (mLastPoint.left == mCurrentPoint.left && mLastPoint.right == mCurrentPoint.right) {
            invalidate();
        } else {
            mValueAnimator.setObjectValues(mLastPoint, mCurrentPoint);
            //TODO 设置反弹动画
            if (mIndicatorBounceEnable) {
                mValueAnimator.setInterpolator(mOvershootInterpolator);
            }
            mValueAnimator.setDuration(500);
            mValueAnimator.start();
        }
    }

    /**
     * 计算当前view位置，浮层
     */
    private void calcIndicatorRect() {
        View currentTabView = mTabsContainer.getChildAt(this.mCurrentTab);
        float left = currentTabView.getLeft();
        calcCurrentRect(left, currentTabView.getWidth());
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        View currentTabView = mTabsContainer.getChildAt(this.mCurrentTab);
        IndicatorPoint indicatorPoint = (IndicatorPoint) animation.getAnimatedValue();
        calcCurrentRect(indicatorPoint.left, currentTabView.getWidth());
        invalidate();
    }

    /**
     * 计算当前 mIndicatorCurrentRect left、right 位置
     *
     * @param left
     * @param width
     */
    private void calcCurrentRect(float left, int width) {
        float indicatorLeft = left + (width - mIndicatorWidth) / 2;
        mIndicatorCurrentRect.left = (int) indicatorLeft;
        mIndicatorCurrentRect.right = (int) (mIndicatorCurrentRect.left + mIndicatorWidth);
    }

    private boolean mIsFirstDraw = true;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || mTabCount <= 0) {
            return;
        }

        final int height = getHeight();//获取父控件高度
        final int paddingLeft = getPaddingLeft();//获取父控件left padding

        //TODO 绘制指示线
        if (mIndicatorAnimEnable) {
            if (mIsFirstDraw) {
                mIsFirstDraw = false;
                calcIndicatorRect();
            }
        } else {
            calcIndicatorRect();
        }
        drawUnderline(canvas, height, paddingLeft);
    }

    /**
     * 绘制下划线
     *
     * @param canvas
     * @param height
     * @param paddingLeft
     */
    private void drawUnderline(Canvas canvas, int height, int paddingLeft) {
        if (mIndicatorHeight > 0) {
            mIndicatorDrawable.setColor(mIndicatorColor);
            int left = paddingLeft + mIndicatorCurrentRect.left;
            int top = height - (int) mIndicatorHeight;
            int right = paddingLeft + mIndicatorCurrentRect.right;
            int bottom = height;
            mIndicatorDrawable.setBounds(left, top, right, bottom);
            mIndicatorDrawable.draw(canvas);
        }
    }

    /**
     * 设置当前Tab
     *
     * @param currentTab position
     */
    public void setCurrentTab(int currentTab) {
        mLastTab = this.mCurrentTab;
        this.mCurrentTab = currentTab;
        updateTabSelection(currentTab);
        if (mIndicatorAnimEnable) {
            calcOffset();
        } else {
            invalidate();
        }
    }

    protected int dp2px(float dp) {
        return ViewUtils.dp2px(mContext, dp);
    }

    protected int sp2px(float sp) {
        return ViewUtils.sp2px(mContext, sp);
    }

}
