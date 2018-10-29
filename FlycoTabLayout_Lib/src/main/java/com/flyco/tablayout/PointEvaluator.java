package com.flyco.tablayout;

import android.animation.TypeEvaluator;

public class PointEvaluator implements TypeEvaluator<IndicatorPoint> {

    @Override
    public IndicatorPoint evaluate(float fraction, IndicatorPoint startValue, IndicatorPoint endValue) {
        float left = startValue.left + fraction * (endValue.left - startValue.left);
        float right = startValue.right + fraction * (endValue.right - startValue.right);
        IndicatorPoint point = new IndicatorPoint();
        point.left = left;
        point.right = right;
        return point;
    }
}
