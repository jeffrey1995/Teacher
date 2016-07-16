package hc.teacher.activity_person;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.tencent.lbssearch.object.result.TransitResultObject;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.entity.User;
import hc.teacher.utils.MethodUtils;

public class CheckOrderActivity extends Activity
{
    private TextView contactName;
    private TextView contactTel;
    private TextView contactAddress;

    private TextView subject;
    private TextView grade;
    private TextView time;
    private TextView salary;
    private TextView period;
    private TextView remark;

    private Button acceptBtn;
    private Button refuseBtn;

    private LinearLayout bottom;

    private Map<String, Object> map;        //接受从上一个activity传过来的数据
    private Gson gson = MethodUtils.getGson();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_order);
        initData();

        acceptBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //向服务器发送更新请求，将订单状态改为已接受
                Map<String, String> req = new HashMap<String, String>();
                double id = (Double)map.get("ID");
                req.put("id", (int)id + "");
                req.put("state", 3 + "");
                volley_Post("order/updateOrder", req);
            }
        });

        refuseBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //向服务器发送更新请求，将订单状态改为已拒绝
                Map<String, String> req = new HashMap<String, String>();
                double id = (Double)map.get("ID");
                req.put("id", (int)id + "");
                req.put("state", 4 + "");
                volley_Post("order/updateOrder", req);
            }
        });
    }

    /**
     * 初始化数据
     */
    public void initData()
    {
        contactName = (TextView)findViewById(R.id.contact_name);
        contactTel = (TextView)findViewById(R.id.contact_tel);
        contactAddress = (TextView)findViewById(R.id.contact_address);

        subject = (TextView)findViewById(R.id.subject);
        grade = (TextView)findViewById(R.id.grade);
        time = (TextView)findViewById(R.id.time);
        salary = (TextView)findViewById(R.id.salary);
        period = (TextView)findViewById(R.id.period);
        remark = (TextView)findViewById(R.id.remark);

        acceptBtn = (Button)findViewById(R.id.accept_btn);
        refuseBtn = (Button)findViewById(R.id.refused_btn);

        bottom = (LinearLayout)findViewById(R.id.bottom);

        //将从上一个界面传来的数据填充到各个控件中
        Intent it = getIntent();
        Bundle bundle = it.getExtras();
        map = (HashMap)bundle.getSerializable("data");

        contactName.setText(map.get("CONTACT_NAME").toString());
        contactTel.setText(map.get("CONTACT_TEL").toString());
        contactAddress.setText(map.get("CONTACT_ADDRESS").toString());

        subject.setText(map.get("SUBJECT").toString());
        grade.setText(map.get("GRADE").toString());
        time.setText(map.get("WORK_TIME").toString());
        salary.setText(map.get("SALARY").toString());
        period.setText(map.get("SECTION").toString());
        remark.setText(map.get("DESCRIPTION").toString());

        //如果用户身份为找家教，则下方的接受和拒绝按钮不可见
        try
        {
            if(MethodUtils.getLocalUser().getIdentity() == User.IDENTITY_FIND)
            {
                bottom.setVisibility(View.GONE);
            }
        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }

        //如果订单状态为“已接受”、“已拒绝”、“已完成”或“已失效”，则下方的接受和拒绝按钮不可见
        double state = (Double)map.get("STATE");
        if(state == 3 || state == 4 || state == 5 || state == 6)
        {
            bottom.setVisibility(View.GONE);
        }
    }

    /**
     * 请求服务器数据
     * @param url_add   添加的请求url
     * @param req       请求数据
     */
    private void volley_Post(String url_add, final Map<String,String> req)
    {
        String url = getString(R.string.url);
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                //请求成功
                Map<String,Object> response = gson.fromJson(s,Map.class);
                //从服务器传回来的信息解析出来数据
                if((boolean)response.get("result"))
                {
                    Toast.makeText(CheckOrderActivity.this, "操作成功！", Toast.LENGTH_SHORT).show();
                    CheckOrderActivity.this.finish();
                }
                else
                {
                    Toast.makeText(CheckOrderActivity.this, "操作失败，请重试！",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                //请求失败处理
                Toast.makeText(CheckOrderActivity.this,"连接服务器失败，请刷新重试！",Toast.LENGTH_LONG).show();
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
