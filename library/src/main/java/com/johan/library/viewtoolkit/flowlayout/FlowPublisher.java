package com.johan.library.viewtoolkit.flowlayout;

/**
 * Created by johan on 2017/8/8.
 */

public interface FlowPublisher {
    void register(FlowObserver observer);
    void unregister();
}
