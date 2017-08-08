package com.johan.viewtoolkit;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.johan.library.viewtoolkit.flowlayout.FlowAdapter;
import com.johan.library.viewtoolkit.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by johan on 2017/8/8.
 */

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
        FlowLayout flowLayout2 = (FlowLayout) findViewById(R.id.flow_layout2);
        FlowLayoutAdapter adapter2 = new FlowLayoutAdapter(dataList);
        flowLayout2.setAdapter(adapter2);
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
