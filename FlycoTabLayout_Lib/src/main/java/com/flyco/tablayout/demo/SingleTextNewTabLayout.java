package com.flyco.tablayout.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.flyco.tablayout.demo.adapter.TabAdapter;

/**
 * 文字切换 TabLayout Adapter
 */
public class SingleTextNewTabLayout extends FrameLayout {

    private TabAdapter mAdapter;
    private LinearLayout mTabsContainer;

    public SingleTextNewTabLayout(Context context) {
        this(context, null, 0);
    }

    public SingleTextNewTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleTextNewTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);//重写onDraw方法,需要调用这个方法来清除flag
        setClipChildren(false);
        setClipToPadding(false);

        mTabsContainer = new LinearLayout(context);
        addView(mTabsContainer);
    }

    public void setAdapter(TabAdapter adapter) {
        if (adapter == null) {
            throw new IllegalStateException("adapter is null!!");
        }
        mAdapter = adapter;
        init();
    }

    private void init() {
        mTabsContainer.removeAllViews();
        final int count = mAdapter.getCount();
        if (count <= 0) {
            return;
        }

        for (int i = 0; i < count; i++) {
            mAdapter.getView(mTabsContainer, i);
        }
    }

}
