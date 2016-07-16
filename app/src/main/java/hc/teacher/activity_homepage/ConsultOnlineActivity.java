package hc.teacher.activity_homepage;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hc.teacher.application.R;

public class ConsultOnlineActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //填充布局
        setContentView(R.layout.activity_consult_online);

        //返回上一界面
        findViewById(R.id.btn_return).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ConsultOnlineActivity.this.finish();
            }
        });

        //初始化listView
        ListView listView = (ListView)findViewById(R.id.consult_list);

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("object", "客服田田");
        map.put("description", "有问题请联系客服哦，保证解决您的问题。");
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("object", "心理咨询专家");
        map.put("description", "这里有最著名的心理咨询专家为您做最专业的辅导。");
        list.add(map);

        ListAdapter adapter = new SimpleAdapter(this, list, R.layout.consult_online_list,
                new String [] {"object", "description"}, new int [] {R.id.object, R.id.description});
        listView.setAdapter(adapter);
    }
}
