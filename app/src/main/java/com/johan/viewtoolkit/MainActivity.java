package com.johan.viewtoolkit;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.johan.library.viewtoolkit.refreshlistview.RefreshListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < 20; i++) {
            dataList.add("Item Data " + i);
        }

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

    }

    private BaseAdapter adapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(MainActivity.this);
            textView.setText(dataList.get(position));
            textView.setTextColor(Color.GRAY);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setPadding(25, 35, 25, 35);
            return textView;
        }

    };

}
