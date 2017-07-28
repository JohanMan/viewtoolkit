package com.johan.library.viewtoolkit.refreshlistview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by johan on 2017/7/27.
 */

public abstract class AbsRefreshListView extends ListView implements AbsListView.OnScrollListener {

    /** 动画每毫秒移动距离 */
    private static final int ANIMATION_STEP = 1;

    /** 移动速率 */
    private static final float MOVE_RATIO = 0.4f;

    /** 刷新View最大高度 */
    private static final int REFRESH_VIEW_MAX_HEIGHT = 500;

    /** 不没有刷新 */
    private static final int STATE_NO_REFRESH = 1;
    /** 正在上拉或者下拉 */
    private static final int STATE_RELEASE_REFRESH = 2;
    /** 正在刷新 */
    private static final int STATE_REFRESHING = 3;

    /** 状态 */
    private int state = STATE_NO_REFRESH;

    /** 记录手指滑动的Y值 */
    private float currentY;

    /** headerView和footerView的原始值 */
    private int headerHeight, footerHeight;

    /** Header Footer View */
    private View headerView, footerView;

    /** 记录是否最顶和最底 */
    private boolean isTop = true, isBottom;

    /** 监听滑动的Listener，因为在AbsRefreshListView已经调用了setOnScrollListener，需要以另一种方式提供用户使用 */
    private OnScrollListener onScrollListener;

    /** 是否可以上拉和下拉 */
    private boolean canPullTop = true, canPullBottom = true;

    /** 判断是否在动画 */
    private boolean isAnimation;

    /** 记录动画时，上一次的值 */
    private int lastAnimationValue;

    public AbsRefreshListView(Context context) {
        super(context);
        init();
    }

    public AbsRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        headerView = initHeaderView();
        footerView = initFooterView();
        if (headerView != null) {
            // 获取headerView的高度
            headerView.measure(0, 0);
            headerHeight = headerView.getMeasuredHeight();
            post(new Runnable() {
                @Override
                public void run() {
                    // 设置headerView的高度
                    setViewHeight(headerView, 0);
                }
            });
            addHeaderView(headerView);
        } else {
            canPullTop = false;
        }
        if (footerView != null) {
            // 获取footerView的高度
            footerView.measure(0, 0);
            footerHeight = footerView.getMeasuredHeight();
            post(new Runnable() {
                @Override
                public void run() {
                    // 设置footerView的高度
                    setViewHeight(footerView, 0);
                }
            });
            addFooterView(footerView);
        } else {
            canPullBottom = false;
        }
        setOnScrollListener(this);
    }

    /**
     * 设置View的高度
     * Bug1 : 当height=0时，view会恢复原来的高度
     * Bug1解决办法 : 如果height=0时，隐藏view，并设为1
     * Bug2 : 当height=0时，分割线还会显示
     * Bug2解决办法 : 如果height=0时，隐藏分割线，还好ListView有设置是否显示分割线的方法
     * @param view
     * @param height
     */
    private void setViewHeight(View view, int height) {
        int visibility = height == 0 ? View.GONE : View.VISIBLE;
        view.setVisibility(visibility);
        boolean isShowDivider = height != 0;
        if (view == headerView) {
            setHeaderDividersEnabled(isShowDivider);
        } else {
            setFooterDividersEnabled(isShowDivider);
        }
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        height = height == 0 ? 1 : height;
        params.height = height;
        view.setLayoutParams(params);
    }

    /**
     * 初始化Header，子类实现
     * @return
     */
    protected abstract View initHeaderView();

    /**
     * 初始化Footer，子类实现
     * @return
     */
    protected abstract View initFooterView();

    /**
     * 重写onTouchEvent实现上拉和下拉功能
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isAnimation) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                currentY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE :
                // 单次移动的距离：手指移动的距离乘以移动速率，为了不让刷新的View显示太快
                float moveY = (event.getY() - currentY) * MOVE_RATIO;
                currentY = event.getY();
                // 上拉
                if (isTop && canPullTop) {
                    // 在最顶，如果上滑，不拦截
                    if (state == STATE_NO_REFRESH && moveY < 0) return super.onTouchEvent(event);
                    // 设置headerView高度
                    updateHeader((int) moveY);
                    // 如果不处理的话，下拉至刷新状态之后，不放手，然后上滑，发现设置不了headerView的高度
                    // 因为此时的ListView也会处理ACTION_MOVE事件下滑，所以我们要返回true，表示我们要处理事件
                    return true;
                }
                // 下拉
                if (isBottom && canPullBottom) {
                    // 在最底，如果下滑，不拦截
                    if (state == STATE_NO_REFRESH && moveY > 0) return super.onTouchEvent(event);
                    // 设置footerView高度
                    updateFooter((int) -moveY);
                    // 因为我们处理事件，所以ListView的滑动就停止了
                    // 所以，虽然我们设置了footerView的高度，但是还是显示不出来
                    // 因此，需要我们手动设置滑动
                    scrollBy(0, (int) -moveY);
                    // 表示处理事件
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP :
                currentY = 0;
                if (state == STATE_RELEASE_REFRESH) {
                    if (headerView.getHeight() >= headerHeight) {
                        // 上拉刷新
                        releaseRefresh();
                        state = STATE_REFRESHING;
                        onRefreshing(true);
                    } else if (footerView.getHeight() >= footerHeight) {
                        // 下拉加载
                        releaseRefresh();
                        state = STATE_REFRESHING;
                        onRefreshing(false);
                    } else {
                        // 没有刷新的话，回到原始状态
                        completeRefresh();
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 更新headerView
     * @param moveY
     */
    private void updateHeader(int moveY) {
        state = STATE_RELEASE_REFRESH;
        int height = headerView.getHeight() + moveY;
        height = Math.max(0, Math.min(REFRESH_VIEW_MAX_HEIGHT, height));
        setViewHeight(headerView, height);
        onProgress(true, height, headerHeight);
    }

    /**
     * 更新footerView
     * @param moveY
     */
    private void updateFooter(int moveY) {
        state = STATE_RELEASE_REFRESH;
        int height = footerView.getHeight() + moveY;
        height = Math.max(0, Math.min(REFRESH_VIEW_MAX_HEIGHT, height));
        setViewHeight(footerView, height);
        onProgress(false, footerView.getHeight(), footerHeight);
    }

    /**
     * 松手刷新
     */
    private void releaseRefresh() {
        final View refreshView = headerView.getHeight() > 1 ? headerView : footerView.getHeight() > 1 ? footerView : null;
        int refreshViewHeight = headerView.getHeight() > 1 ? headerHeight : footerView.getHeight() > 1 ? footerHeight : 0;
        if (refreshView == null) {
            return;
        }
        if (refreshViewHeight == 0) {
            return;
        }
        if (refreshView.getHeight() < 2) {
            return;
        }
        if (refreshView.getHeight() > refreshViewHeight) {
            lastAnimationValue = refreshView.getHeight();
            ValueAnimator animator = ValueAnimator.ofInt(refreshView.getHeight(), refreshViewHeight);
            animator.setDuration((refreshView.getHeight() - refreshViewHeight) / ANIMATION_STEP + 1);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int height = (int) animation.getAnimatedValue();
                    setViewHeight(refreshView, height);
                    // 因为上拉加载时，我们手动滚动了ListView，现在要恢复滚动的位置
                    if (isBottom) {
                        scrollBy(0, height - lastAnimationValue);
                        lastAnimationValue = height;
                    }
                }
            });
            animator.start();
        }
    }

    /**
     * 完成刷新
     * Bug1 : 手指正在滑动，打算取消刷新，而此时，外部调用了completeRefresh完成刷新，把refreshView重置了，
     *        造成手指还在滑动，refreshView突然不见
     * Bug1解决办法 : 根据 currentY != 0 判断手指是否在滑动，如果手指还在滑动，则不做任何操作，否则重置refreshView
     * 注意1 : 不能用 headerView.getHeight() != 0 这种方式判断是否显示有headerView，具体原因请看setViewHeight方法
     *         要用 headerView.getHeight() > 1 方式判断
     */
    public void completeRefresh() {
        if (currentY != 0) return;
        isAnimation = true;
        final View refreshView = headerView.getHeight() > 1 ? headerView : footerView.getHeight() > 1 ? footerView : null;
        if (refreshView == null) {
            isAnimation = false;
            return;
        }
        if (refreshView.getHeight() < 2) {
            isAnimation = false;
            return;
        }
        lastAnimationValue = refreshView.getHeight();
        ValueAnimator animator = ValueAnimator.ofInt(refreshView.getHeight(), 0);
        animator.setDuration(refreshView.getHeight() / ANIMATION_STEP + 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();
                setViewHeight(refreshView, height);
                // 因为上拉加载时，我们手动滚动了ListView，现在要恢复滚动的位置
                if (isBottom) {
                    scrollBy(0, height - lastAnimationValue);
                    lastAnimationValue = height;
                }
                if (height == 0) {
                    state = STATE_NO_REFRESH;
                    isAnimation = false;
                    onComplete(refreshView == headerView);
                }
            }
        });
        animator.start();
    }

    /**
     * 判断是否最顶或者最低或者都不是
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem == 0) {
            isTop = true;
            isBottom = false;
        } else if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
            isBottom = true;
            isTop = false;
        } else {
            isTop = false;
            isBottom = false;
        }
        if (onScrollListener != null) {
            onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (onScrollListener != null) {
            onScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    /**
     * 设置是否可以上拉
     * @param canPullTop
     */
    public void setCanPullTop(boolean canPullTop) {
        this.canPullTop = canPullTop;
    }

    /**
     * 设置是否可以下拉
     * @param canPullBottom
     */
    public void setCanPullBottom(boolean canPullBottom) {
        this.canPullBottom = canPullBottom;
    }

    /**
     * 是否在刷新
     * @return
     */
    public boolean isRefreshing() {
        return state == STATE_REFRESHING;
    }

    /**
     * 因为AbsRefreshListView已经调用了setOnScrollListener方法，不能在外面调用setOnScrollListener，否则会影响AbsRefreshListView
     * 请使用该方法，和setOnScrollListener的功能是一样的
     * @param onScrollListener
     */
    public void setScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    /**
     * 下拉上拉时调用
     * @param isTop
     * @param currentHeight
     * @param viewHeight
     */
    protected void onProgress(boolean isTop, int currentHeight, int viewHeight) {

    }

    /**
     * 刷新时调用，子类实现
     * @param isTop
     */
    protected abstract void onRefreshing(boolean isTop);

    /**
     * 刷新完成时调用
     * @param isTop
     */
    protected void onComplete(boolean isTop) {

    }

}
