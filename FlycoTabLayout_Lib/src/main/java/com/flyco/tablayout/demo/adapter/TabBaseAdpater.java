package com.flyco.tablayout.demo.adapter;

import android.view.View;
import android.view.ViewGroup;

public abstract class TabBaseAdpater<T> {

    public abstract View getView(ViewGroup parent, int position);

    public abstract int getCount();

    public abstract T getItem(int position);

}