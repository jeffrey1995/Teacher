package hc.teacher.activity_person;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hc.teacher.activity_teacher_center.ApplyInfoActivity;
import hc.teacher.activity_teacher_center.EmployInfoActivity;
import hc.teacher.adapter.InformationAdapter;
import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.data.DataAnalysis;
import hc.teacher.entity.User;
import hc.teacher.utils.MethodUtils;

public class MyPublishActivity extends Activity
{
    private ListView listView;
    private List<Map<String, Object>> list = new ArrayList<>();
    private InformationAdapter adapter;

    private Gson gson = MethodUtils.getGson();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_publish);

        //初始化listView数据
        listView = (ListView)findViewById(R.id.listView);
        getData();
        adapter = new InformationAdapter(this, list, R.layout.information_list,
                new String[] {"TITLE", "SUBJECT", "COMMIT_DATE", "GRADE", "SALARY"},
                new int [] {R.id.title, R.id.subject, R.id.time, R.id.grade, R.id.salary});
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //将被点击的条目对应的数据包装在Bundle发送到下一个activity
                Intent intent = new Intent();
                try
                {
                    if(MethodUtils.getLocalUser().getIdentity() == User.IDENTITY_DO)
                        intent.setClass(MyPublishActivity.this, ApplyInfoActivity.class);
                    else
                    {
                        intent.setClass(MyPublishActivity.this, SignUpInfoActivity.class);
                    }
                }
                catch(ParseException e)
                {
                    e.printStackTrace();
                }

                Bundle bundle = new Bundle();
                Map<String, Object> map = (list.get(position));
                bundle.putSerializable("data", (Serializable) map);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

        //返回上一界面
        findViewById(R.id.btn_return).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MyPublishActivity.this.finish();
            }
        });
    }

    /**
     * 访问服务器获得自己已发布的信息列表
     */
    public void getData()
    {
        Map<String, String> req = new HashMap<>();
        try
        {
            req.put("userId", MethodUtils.getLocalUser().getId() + "");
        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }
        volley_Post("tutorCenter/findListByUserId", req);
    }

    public void volley_Post(String url_add, final Map<String, String> req)
    {
        String url = getString(R.string.url);
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                //请求成功
                Map<String,Object> response = gson.fromJson(s, Map.class);
                List<Map<String, Object>> result = new ArrayList<>();
                //从服务器传回来的信息解析出来数据
                if((boolean)response.get("result"))
                {
                    result = new DataAnalysis().listKeyMaps(response.get("infoList").toString());
                }
                list.addAll(result);
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                //请求失败处理
                Toast.makeText(MyPublishActivity.this, "连接服务器失败，请刷新重试！", Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                return req;
            }
        };
        request.setTag("doPost");
        MyApplication.getHttpQueue().add(request);
    }
}
