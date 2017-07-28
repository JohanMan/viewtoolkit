package com.johan.library.viewtoolkit.refreshlistview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.johan.library.R;

/**
 * Created by johan on 2017/7/27.
 */

public class RefreshListView extends AbsRefreshListView {

    private ImageView headerIconView, footerIconView;
    private TextView headerContentView, footerContentView;

    private RefreshMessage refreshMessage;

    private ObjectAnimator rotateAnimator;

    private OnRefreshListener onRefreshListener = new OnRefreshListener() {
        @Override
        public void onPullRefreshing() {}
        @Override
        public void onLoadRefreshing() {}
    };

    public RefreshListView(Context context) {
        super(context);
        refreshMessage = new RefreshMessage();
        initView();
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        refreshMessage = new RefreshMessage();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RefreshListView);
        if (array.hasValue(R.styleable.RefreshListView_refresh_pull_icon)) {
            refreshMessage.pullIcon = array.getDrawable(R.styleable.RefreshListView_refresh_pull_icon);
        }
        if (array.hasValue(R.styleable.RefreshListView_refresh_pull_tip)) {
            refreshMessage.pullTip = array.getString(R.styleable.RefreshListView_refresh_pull_tip);
        }
        if (array.hasValue(R.styleable.RefreshListView_refresh_pull_release_refresh_tip)) {
            refreshMessage.pullReleaseRefreshTip = array.getString(R.styleable.RefreshListView_refresh_pull_release_refresh_tip);
        }
        if (array.hasValue(R.styleable.RefreshListView_refresh_pull_refreshing_tip)) {
            refreshMessage.pullRefreshingTip = array.getString(R.styleable.RefreshListView_refresh_pull_refreshing_tip);
        }
        if (array.hasValue(R.styleable.RefreshListView_refresh_load_icon)) {
            refreshMessage.loadIcon = array.getDrawable(R.styleable.RefreshListView_refresh_load_icon);
        }
        if (array.hasValue(R.styleable.RefreshListView_refresh_load_tip)) {
            refreshMessage.loadTip = array.getString(R.styleable.RefreshListView_refresh_load_tip);
        }
        if (array.hasValue(R.styleable.RefreshListView_refresh_load_release_refresh_tip)) {
            refreshMessage.loadReleaseRefreshTip = array.getString(R.styleable.RefreshListView_refresh_load_release_refresh_tip);
        }
        if (array.hasValue(R.styleable.RefreshListView_refresh_load_refreshing_tip)) {
            refreshMessage.loadRefreshingTip = array.getString(R.styleable.RefreshListView_refresh_load_refreshing_tip);
        }
        array.recycle();
        initView();
    }

    @Override
    protected View initHeaderView() {
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.header_default, null);
        headerIconView = (ImageView) headerView.findViewById(R.id.header_default_icon);
        headerContentView = (TextView) headerView.findViewById(R.id.header_default_content);
        return headerView;
    }

    @Override
    protected View initFooterView() {
        View footerView = LayoutInflater.from(getContext()).inflate(R.layout.footer_default, null);
        footerIconView = (ImageView) footerView.findViewById(R.id.footer_default_icon);
        footerContentView = (TextView) footerView.findViewById(R.id.footer_default_content);
        return footerView;
    }

    private void initView() {
        if (refreshMessage.pullIcon != null) {
            headerIconView.setImageDrawable(refreshMessage.pullIcon);
        }
        headerContentView.setText(refreshMessage.pullTip);
        if (refreshMessage.loadIcon != null) {
            footerIconView.setImageDrawable(refreshMessage.loadIcon);
        }
        footerContentView.setText(refreshMessage.loadTip);
    }

    @Override
    protected void onRefreshing(boolean isTop) {
        View refreshView = isTop ? headerIconView : footerIconView;
        startRotateAnimation(refreshView);
        if (isTop) {
            onRefreshListener.onPullRefreshing();
            headerContentView.setText(refreshMessage.pullRefreshingTip);
        } else {
            onRefreshListener.onLoadRefreshing();
            footerContentView.setText(refreshMessage.loadRefreshingTip);
        }
    }

    @Override
    protected void onProgress(boolean isTop, int currentHeight, int viewHeight) {
        if (isTop) {
            String headerContent = currentHeight >= viewHeight ? refreshMessage.pullReleaseRefreshTip : refreshMessage.pullTip;
            headerContentView.setText(headerContent);
            rotateIcon(headerIconView, 360 * (currentHeight % viewHeight) / viewHeight);
        } else {
            String footerContent = currentHeight >= viewHeight ? refreshMessage.loadReleaseRefreshTip : refreshMessage.loadTip;
            footerContentView.setText(footerContent);
            rotateIcon(footerIconView, 360 * (currentHeight % viewHeight) / viewHeight);
        }
        endRotateAnimation();
    }

    @Override
    protected void onComplete(boolean isTop) {
        endRotateAnimation();
    }

    private void startRotateAnimation(View view) {
        rotateAnimator = ObjectAnimator.ofFloat(view, "rotation", 0, 359.0f);
        rotateAnimator.setDuration(500);
        rotateAnimator.setRepeatCount(-1);
        rotateAnimator.setInterpolator(new LinearInterpolator());
        rotateAnimator.start();
    }

    private void endRotateAnimation() {
        if (rotateAnimator != null) {
            rotateAnimator.end();
            rotateAnimator = null;
        }
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public interface OnRefreshListener {
        void onPullRefreshing();
        void onLoadRefreshing();
    }

    private void rotateIcon(ImageView imageView, int angle) {
        imageView.setPivotX(imageView.getWidth() / 2);
        imageView.setPivotY(imageView.getHeight() / 2);
        imageView.setRotation(angle);
    }

    public void setPullTip(String pullTip) {
        refreshMessage.pullTip = pullTip;
    }

    public void setPullReleaseRefreshTip(String pullReleaseRefreshTip) {
        refreshMessage.pullReleaseRefreshTip = pullReleaseRefreshTip;
    }

    public void setPullRefreshingTip(String pullRefreshingTip) {
        refreshMessage.pullRefreshingTip = pullRefreshingTip;
    }

    public void setPullIcon(int pullIcon) {
        headerIconView.setImageResource(pullIcon);
    }

    public void setLoadTip(String loadTip) {
        refreshMessage.loadTip = loadTip;
    }

    public void setLoadReleaseRefreshTip(String loadReleaseRefreshTip) {
        refreshMessage.loadReleaseRefreshTip = loadReleaseRefreshTip;
    }

    public void setLoadRefreshingTip(String loadRefreshingTip) {
        refreshMessage.loadRefreshingTip = loadRefreshingTip;
    }

    public void setLoadIcon(int loadIcon) {
        footerIconView.setImageResource(loadIcon);
    }

    class RefreshMessage {
        public String pullTip = "下拉刷新";
        public String pullReleaseRefreshTip = "松手刷新";
        public String pullRefreshingTip = "正在刷新";
        public Drawable pullIcon = null;
        public String loadTip = "上拉加载";
        public String loadReleaseRefreshTip = "松手加载";
        public String loadRefreshingTip = "正在加载";
        public Drawable loadIcon = null;
    }

}
