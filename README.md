# viewtoolkit
View的工具库

## 上拉刷新下拉加载ListView使用

复制Library到自己的项目中，并引用这个库。

### xml
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:refresh_view="http://schemas.android.com/apk/res-auto"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.johan.viewtoolkit.MainActivity">

    <com.johan.library.viewtoolkit.refreshlistview.RefreshListView
        android:id="@+id/list_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:dividerHeight="1px"
        android:divider="#cccccc"
        refresh_view:refresh_pull_icon="@drawable/my_refresh_icon"
        refresh_view:refresh_pull_tip="下拉刷新哦"
        refresh_view:refresh_pull_release_refresh_tip="松手刷新哦"
        refresh_view:refresh_pull_refreshing_tip="正在刷新哈"
        />

</LinearLayout>
```

### activity
```
final RefreshListView listView = (RefreshListView) findViewById(R.id.list_view);
listView.setAdapter(adapter);
listView.setLoadIcon(R.drawable.my_refresh_icon);
listView.setLoadTip("上拉加载哦");
listView.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
    @Override
    public void onPullRefreshing() {
        // 模拟延时3秒
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.completeRefresh();
            }
        }, 3000);
    }
    @Override
    public void onLoadRefreshing() {
        // 模拟延时3秒
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.completeRefresh();
            }
        }, 3000);
    }
});
```
