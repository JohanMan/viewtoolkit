package com.johan.library.viewtoolkit.flowlayout;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by johan on 2017/8/8.
 * 适配器 : Object -> View 通知更新
 */

public abstract class FlowAdapter <T> implements FlowPublisher {

    // 数据源
    private List<T> dataList;
    // 观察者
    private FlowObserver observer;

    public FlowAdapter(List<T> dataList) {
        this.dataList = dataList;
    }

    @Override
    public void register(FlowObserver observer) {
        this.observer = observer;
    }

    @Override
    public void unregister() {
        this.observer = null;
    }

    /**
     * 子类复写，由数据data获取View
     * @param position
     * @param data
     * @return
     */
    public abstract View getView(int position, T data);

    /**
     * 解析所有数据
     * @return
     */
    List<View> parseViews() {
        List<View> viewList = new ArrayList<>();
        for (int position = 0; position < dataList.size(); position++) {
            viewList.add(getView(position, dataList.get(position)));
        }
        return viewList;
    }

    /**
     * 通知更新
     */
    public void notifyChange() {
        if (observer != null) {
            observer.notifyChange(this);
        }
    }

}
