package com.johan.library.viewtoolkit.flowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.johan.library.R;

import java.util.List;

/**
 * Created by johan on 2017/8/8.
 * 流式布局
 */

public class FlowLayout extends ViewGroup implements FlowObserver {

    /** 子View点击监听器 */
    private OnItemClickListener onItemClickListener;
    /** 子View之间水平距离 */
    private int horizontalSpace = 0;
    /** 子View之间垂直距离 */
    private int verticalSpace = 0;

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        horizontalSpace = array.getDimensionPixelOffset(R.styleable.FlowLayout_horizontal_space, 0);
        verticalSpace = array.getDimensionPixelOffset(R.styleable.FlowLayout_vertical_space, 0);
        array.recycle();
    }

    /**
     * 计算所有子View的高宽（测量）
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 取出宽高的模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        // 取出宽高的大小
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        // 子View的个数
        int childCount = getChildCount();
        // 计算宽度
        int calculateWidth = 0;
        // 计算高度
        int calculateHeight = 0;
        // 行宽度
        int lineWidth = 0;
        // 行高度
        int lineHeight = 0;
        // 计算，遍历所有子View
        for (int index = 0; index < childCount; index++) {
            View childView = getChildAt(index);
            // 测量子View
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            // 子View宽度
            int childWidth = childView.getMeasuredWidth() + horizontalSpace;
            // 子View高度
            int childHeight = childView.getMeasuredHeight();
            // 如果 当前行宽 + 当前子View宽度 > 最大宽度
            if (childWidth + lineWidth > widthSize) {
                // 换行
                // 更新计算宽度，如果当前行宽大于之前计算宽度，则计算宽度=当前行宽
                calculateWidth = Math.max(lineWidth, calculateWidth);
                // 计算高度累加当前行高
                calculateHeight += lineHeight + (calculateHeight == 0 ? 0 : verticalSpace);
                // 更新当前行宽
                lineWidth = childWidth;
                // 更新当前行高
                lineHeight = childHeight;
            } else {
                // 不换行
                // 累加当前行宽
                lineWidth += childWidth;
                // 更新当前行高，如果当前子View总高度大于当前行高，则当前行高=当前子View总高度
                lineHeight = Math.max(childHeight, lineHeight);
            }
            // 判断是否最后一个
            if (index == childCount - 1) {
                // 更新计算宽度，如果当前行宽大于之前计算宽度，则计算宽度=当前行宽
                calculateWidth = Math.max(lineWidth, calculateWidth);
                // 计算高度累加当前行高
                calculateHeight += lineHeight + (calculateHeight == 0 ? 0 : verticalSpace);
            }
        }
        // 如果是EXACTLY模式，直接设置用户指定的宽度
        int measureWidth = widthMode == MeasureSpec.EXACTLY ? widthSize : calculateWidth;
        // 如果是EXACTLY模式，直接设置用户指定的高度
        int measureHeight = heightMode == MeasureSpec.EXACTLY ? heightSize : calculateHeight;
        // 设置宽高
        setMeasuredDimension(measureWidth, measureHeight);
    }

    /**
     * 设置所有子View的位置（布局）
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // 子View的个数
        int childCount = getChildCount();
        // 获取最大宽度
        int maxWidth = getWidth();
        // 行宽
        int lineWidth = 0;
        // 行高
        int lineHeight = 0;
        // 记录高度
        int recordHeight = 0;
        // 布局，遍历所有子View
        for (int index = 0; index < childCount; index++) {
            View childView = getChildAt(index);
            int childWidth = childView.getMeasuredWidth() + horizontalSpace;
            int childHeight = childView.getMeasuredHeight();
            // 如果 当前行宽 + 当前子View总宽度 > 最大宽度
            if (lineWidth + childWidth > maxWidth) {
                // 换行
                // 记录高度累加当前行高，从第2行起，都要加上垂直距离
                recordHeight += lineHeight + (recordHeight == 0 ? 0 : verticalSpace);
                // 布局
                int childLeft = 0;
                int childTop = recordHeight + verticalSpace;
                int childRight = childWidth;
                int childBottom = recordHeight + childHeight + verticalSpace;
                childView.layout(childLeft, childTop, childRight, childBottom);
                // 更新行宽
                lineWidth = childWidth;
                // 更新行高
                lineHeight = childHeight;
                // 下一个
                continue;
            }
            // 不换行
            // 布局
            int childLeft = lineWidth;
            int childTop = recordHeight + (recordHeight == 0 ? 0 : verticalSpace);
            int childRight = lineWidth + childWidth;
            int childBottom = recordHeight + childHeight + (recordHeight == 0 ? 0 : verticalSpace);
            childView.layout(childLeft, childTop, childRight, childBottom);
            // 更新行宽
            lineWidth += childWidth;
            // 更新行高，取最大
            lineHeight = Math.max(childHeight, lineHeight);
        }
    }

    /**
     * 设置适配器
     * @param adapter
     */
    public void setAdapter(FlowAdapter adapter) {
        adapter.register(this);
        refresh(adapter);
    }

    /**
     * 观察者更新方法
     * @param publisher
     */
    @Override
    public void notifyChange(FlowPublisher publisher) {
        if (publisher instanceof FlowAdapter) {
            refresh((FlowAdapter)publisher);
        }
    }

    /**
     * 刷新
     */
    private void refresh(FlowAdapter adapter) {
        // 移除所有子View
        removeAllViews();
        // 获取FlowAdapter所有子View
        List<View> childViews = adapter.parseViews();
        // 添加到FlowLayout中
        for (int index = 0; index < childViews.size(); index++) {
            final View childView = childViews.get(index);
            // 设置点击事件
            final int position = index;
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position, childView);
                    }
                }
            });
            // 添加
            addView(childView);
        }
        // 更新
        requestLayout();
    }

    /**
     * 设置子View点击接口
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 子View点击接口
     */
    public interface OnItemClickListener {
        void onItemClick(int position, View view);
    }

}
