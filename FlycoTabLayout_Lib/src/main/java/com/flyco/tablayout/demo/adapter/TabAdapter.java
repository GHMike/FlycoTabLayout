package com.flyco.tablayout.demo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.tablayout.R;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

public class TabAdapter extends TabBaseAdpater<CustomTabEntity> implements View.OnClickListener {

    private static final String TAG = "TabAdapter";

    private List<CustomTabEntity> mTabEntitys = new ArrayList<>();

    private Context mContext;

    private OnTabSelectListener mOnTabSelectListener;

    private SparseArray<View> mTabViews;

    private ViewGroup mViewGroup;

    /**
     * 当前 Tab 位置，默认0，第一个
     */
    private int mCurrentTab;

    /**
     * 最后一次 Tab 位置
     */
    private int mLastTab;

    /**
     * 文字选中颜色
     */
    private int mTextSelectColor = Color.parseColor("#ffffff");

    /**
     * 文字未选中颜色
     */
    private int mTextUnselectColor = Color.parseColor("#AAffffff");

    public TabAdapter(Context context, ViewGroup viewGroup) {
        mContext = context;
        mViewGroup = viewGroup;
    }

    public void setOnTabSelectListener(OnTabSelectListener listener) {
        this.mOnTabSelectListener = listener;
    }

    public void setData(List<CustomTabEntity> list) {
        if (list == null || list.size() == 0) {
            throw new IllegalStateException("list is null!!");
        }
        if (this.mTabViews == null) {
            this.mTabViews = new SparseArray<>();
        }
        this.mTabViews.clear();
        this.mTabEntitys.clear();
        this.mTabEntitys.addAll(list);
    }

    @Override
    public View getView(ViewGroup parent, int position) {
        View tabView = View.inflate(mContext, R.layout.layout_text_tab, null);
        TextView title = (TextView) tabView.findViewById(R.id.tv_tab_title);
        title.setText(mTabEntitys.get(position).getTabTitle());
        tabView.setTag(position);
        tabView.setOnClickListener(this);
        addTab(parent, position, tabView);
        mTabViews.put(position, tabView);
        return tabView;
    }

    private void addTab(ViewGroup parent, final int position, View tabView) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.MATCH_PARENT, 1.0f);
        parent.addView(tabView, position, layoutParams);
    }

    @Override
    public int getCount() {
        return (mTabEntitys != null) ? mTabEntitys.size() : 0;
    }

    @Override
    public CustomTabEntity getItem(int position) {
        return mTabEntitys.get(position);
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
        mViewGroup.invalidate();
    }

    /**
     * 循环遍历所有Tab位置，设置状态，可以恢复选择某个Item态
     *
     * @param position
     */
    private void updateTabSelection(int position) {
        int count = getCount();
        for (int i = 0; i < count; ++i) {
            View tabView = mTabViews.get(i);
            final boolean isSelect = i == position;
            TextView title = (TextView) tabView.findViewById(R.id.tv_tab_title);
            title.setTextColor(isSelect ? mTextSelectColor : mTextUnselectColor);
        }
    }

    @Override
    public void onClick(View v) {
        final int position = (Integer) v.getTag();
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
}
