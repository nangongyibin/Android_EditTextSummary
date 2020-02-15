package com.ngyb.edittextsummary;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 作者：南宫燚滨
 * 描述：
 * 邮箱：nangongyibin@gmail.com
 * 日期：2020/2/15 10:04
 */
public class WrapListView extends ListView {
    private int width = 0;

    public WrapListView(Context context) {
        this(context, null);
    }

    public WrapListView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public WrapListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getMeasuredHeight();
        measureChildren(widthMeasureSpec,heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            int childWidth = getChildAt(i).getMeasuredWidth();
            width = Math.max(width,childWidth);
        }
        setMeasuredDimension(width,height);
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
