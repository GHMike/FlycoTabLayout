package com.flyco.tablayout.demo;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

public class HintImageView extends ImageView {

    /**
     * 点击颜色
     */
    private static final int CLICK_COLOR = Color.parseColor("#3D000000");

    /**
     * 默认态
     */
    private static final int DEFAULT_COLOR = Color.parseColor("#999999");

    /**
     * 不可点击颜色
     */
    private static final int DISENABLE_CLICK_COLOR = Color.parseColor("#333333");

    public HintImageView(Context context) {
        this(context, null);
    }

    public HintImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HintImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init() {
        int[] colors = new int[]{CLICK_COLOR, DISENABLE_CLICK_COLOR, DEFAULT_COLOR};
        int[][] states = new int[3][];
        states[0] = new int[]{android.R.attr.state_pressed};
        states[1] = new int[]{-android.R.attr.state_enabled};
        states[2] = new int[]{};
        StateListDrawable stateListDrawable = getStateListDrawable(getDrawable(), states);
        Drawable drawable = getStateDrawable(stateListDrawable, colors, states);
        setImageDrawable(drawable);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private StateListDrawable getStateListDrawable(Drawable drawable, int[][] states) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        for (int[] state : states) {
            stateListDrawable.addState(state, drawable);
        }
        return stateListDrawable;
    }

    private Drawable getStateDrawable(Drawable drawable, int[] colors, int[][] states) {
        ColorStateList colorList = new ColorStateList(states, colors);
        Drawable.ConstantState state = drawable.getConstantState();
        drawable = DrawableCompat.wrap(state == null ? drawable : state.newDrawable()).mutate();
        DrawableCompat.setTintList(drawable, colorList);
        return drawable;
    }

}
