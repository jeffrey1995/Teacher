package hc.teacher.activity_person;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hc.teacher.activity_homepage.ToAskFragment;
import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.data.DataAnalysis;
import hc.teacher.entity.User;
import hc.teacher.utils.MethodUtils;

public class MyOrderActivity extends Activity implements View.OnClickListener
{
    private TextView waitAccept;
    private TextView accepted;
    private TextView unaccepted;
    private TextView finished;

    private ListView orderList;
    private List<Map<String, Object>> list = new ArrayList<>();
    private SimpleAdapter adapter;

    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        waitAccept = (TextView)findViewById(R.id.wait_accept);
        accepted = (TextView)findViewById(R.id.accepted);
        unaccepted = (TextView)findViewById(R.id.unaccepted);
        finished = (TextView)findViewById(R.id.finished);
        waitAccept.setTextColor(getResources().getColor(R.color.theme_color));

        //获得订单列表
        orderList = (ListView)findViewById(R.id.order_list);
        getData(1);
        adapter = new SimpleAdapter(this, list, R.layout.order_list,
                new String[] {"TEACHER_NAME", "SUBJECT", "GRADE", "SALARY", "INIT_TIME"},
                new int [] {R.id.name, R.id.subject, R.id.grade, R.id.salary, R.id.date});
        orderList.setAdapter(adapter);

        //切换订单类型
        waitAccept.setOnClickListener(this);
        accepted.setOnClickListener(this);
        unaccepted.setOnClickListener(this);
        finished.setOnClickListener(this);
        //点击查看订单详细信息
        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent();
                intent.setClass(MyOrderActivity.this, CheckOrderActivity.class);

                Bundle bundle = new Bundle();
                Map<String, Object> map = list.get(position);
                bundle.putSerializable("data", (Serializable)map);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

        //返回上一个activity
        findViewById(R.id.btn_return).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MyOrderActivity.this.finish();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //重新加载listView内容
        getData(1);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
        case R.id.wait_accept:
            waitAccept.setTextColor(getResources().getColor(R.color.theme_color));
            accepted.setTextColor(Color.GRAY);
            unaccepted.setTextColor(Color.GRAY);
            finished.setTextColor(Color.GRAY);
            getData(1);
            adapter.notifyDataSetChanged();
            break;
        case R.id.accepted:
            accepted.setTextColor(getResources().getColor(R.color.theme_color));
            waitAccept.setTextColor(Color.GRAY);
            unaccepted.setTextColor(Color.GRAY);
            finished.setTextColor(Color.GRAY);
            getData(3);
            adapter.notifyDataSetChanged();
            break;
        case R.id.unaccepted:
            unaccepted.setTextColor(getResources().getColor(R.color.theme_color));
            accepted.setTextColor(Color.GRAY);
            waitAccept.setTextColor(Color.GRAY);
            finished.setTextColor(Color.GRAY);
            getData(4);
            adapter.notifyDataSetChanged();
            break;
        case R.id.finished:
            finished.setTextColor(getResources().getColor(R.color.theme_color));
            unaccepted.setTextColor(Color.GRAY);
            accepted.setTextColor(Color.GRAY);
            waitAccept.setTextColor(Color.GRAY);
            getData(5);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 访问服务器请求数据
     * @param position      请求数据类型
     * @return      服务器返回的数据
     */
    private void getData(int position)
    {
        Map<String, String> req = new HashMap<>();
        try
        {
            if(MethodUtils.getLocalUser().getIdentity() == User.IDENTITY_DO)
                req.put("type", "teacher");
            else if(MethodUtils.getLocalUser().getIdentity() == User.IDENTITY_FIND)
                req.put("type", "student");
            req.put("id", MethodUtils.getLocalUser().getId().toString());
        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }
        volley_Post("order/getOrder", req, position);
    }

    public void volley_Post(String url_add, final Map<String, String> req, final int position)
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
                    result = new DataAnalysis().listKeyMaps(response.get("orderList").toString());
                }
                list.clear();
                for(Map<String, Object> map : result)
                {
                    double state = (double)map.get("STATE");
                    if((int)state == position)
                    {
                        list.add(map);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                //请求失败处理
                Toast.makeText(MyOrderActivity.this, "连接服务器失败，请刷新重试！",Toast.LENGTH_LONG).show();
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
