package com.flyco.tablayout.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.flyco.tablayout.R;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyco.tablayout.utils.UnreadMsgUtils;
import com.flyco.tablayout.widget.PointView;

import java.util.ArrayList;

/**
 * Icon 切换 TabLayout
 */
public class SingleIconTabLayout extends FrameLayout {

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
     * 监听Tab切换
     */
    private OnTabSelectListener mOnTabSelectListener;

    public SingleIconTabLayout(Context context) {
        this(context, null, 0);
    }

    public SingleIconTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleIconTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);//重写onDraw方法,需要调用这个方法来清除flag
        setClipChildren(false);
        setClipToPadding(false);

        this.mContext = context;
        mTabsContainer = new LinearLayout(context);
        addView(mTabsContainer);
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
            tabView = View.inflate(mContext, R.layout.layout_icon_tab, null);
            tabView.setTag(i);
            addTab(i, tabView);
        }
    }

    /**
     * 创建并向父控件 mTabsContainer 添加tab
     */
    private void addTab(final int position, View tabView) {
        ImageView iv_tab_icon = (ImageView) tabView.findViewById(R.id.iv_tab_icon);
        iv_tab_icon.setImageResource(mTabEntitys.get(position).getTabUnselectedIcon());

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
            ImageView iv_tab_icon = (ImageView) tabView.findViewById(R.id.iv_tab_icon);
            CustomTabEntity tabEntity = mTabEntitys.get(i);
            iv_tab_icon.setImageResource(isSelect ? tabEntity.getTabSelectedIcon() : tabEntity.getTabUnselectedIcon());
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
        invalidate();
    }


    /**
     * 显示未读消息
     *
     * @param position 显示tab位置
     * @param num      num小于等于0显示红点,num大于0显示数字
     */
    public void showMsg(int position, int num) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }

        View tabView = mTabsContainer.getChildAt(position);
        PointView tipView = (PointView) tabView.findViewById(R.id.point_view);
        if (tipView != null) {
            UnreadMsgUtils.show(tipView, num);
        }
    }

    /**
     * 显示未读红点
     *
     * @param position 显示tab位置
     */
    public void showDot(int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }
        showMsg(position, 0);
    }

    public void hideMsg(int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }

        View tabView = mTabsContainer.getChildAt(position);
        PointView tipView = (PointView) tabView.findViewById(R.id.point_view);
        if (tipView != null) {
            tipView.setVisibility(View.GONE);
        }
    }

}
