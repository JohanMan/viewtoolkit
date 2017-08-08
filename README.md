# viewtoolkit
View的工具库

复制Library到自己的项目中，并引用这个库。

## 上拉刷新下拉加载ListView使用

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

## 流式布局FlowLayout使用

### xml
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:flow_layout="http://schemas.android.com/apk/res-auto"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#9c9c9c">

    <com.johan.library.viewtoolkit.flowlayout.FlowLayout
        android:id="@+id/flow_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="10dp"
        flow_layout:horizontal_space="10dp"
        flow_layout:vertical_space="10dp"
        android:background="@android:color/white"
        />

</LinearLayout>
```

### activity
```
public class FlowLayoutActivity extends Activity {

    private List<String> dataList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_layout);
        dataList.add("Java");
        dataList.add("Swift");
        dataList.add("Html");
        dataList.add("CSS");
        dataList.add("Go");
        dataList.add("C#");
        dataList.add("PHP");
        FlowLayout flowLayout = (FlowLayout) findViewById(R.id.flow_layout);
        final FlowLayoutAdapter adapter = new FlowLayoutAdapter(dataList);
        flowLayout.setAdapter(adapter);
        flowLayout.setOnItemClickListener(new FlowLayout.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                dataList.remove(position);
                adapter.notifyChange();
            }
        });
    }

    public class FlowLayoutAdapter extends FlowAdapter <String>  {
        public FlowLayoutAdapter(List<String> dataList) {
            super(dataList);
        }
        @Override
        public View getView(int position, String data) {
            View layout = LayoutInflater.from(FlowLayoutActivity.this).inflate(R.layout.item_flow_layout, null);
            TextView contentView = (TextView) layout.findViewById(R.id.item_content);
            contentView.setText(data);
            return layout;
        }
    }

}
```
