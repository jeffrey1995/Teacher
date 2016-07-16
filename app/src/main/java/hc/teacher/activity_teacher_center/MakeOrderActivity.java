package hc.teacher.activity_teacher_center;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.components.location.SelectCityActivity;
import hc.teacher.data.DataAnalysis;
import hc.teacher.entity.ContactInfo;
import hc.teacher.entity.Order;
import hc.teacher.utils.MethodUtils;

public class MakeOrderActivity extends Activity
{
    private TextView actionbarName;
    private TextView contactName;
    private TextView contactTel;
    private TextView contactAddress;
    private TextView subject;
    private TextView grade;
    private TextView time;
    private TextView salary;
    private TextView period;
    private TextView totalSalary;
    private TextView discountSalary;
    private TextView finalSalary;

    private RelativeLayout choAddress;
    private RelativeLayout choPeriod;
    private RelativeLayout choSubject;
    private RelativeLayout choGrade;
    private RelativeLayout choTime;

    private EditText remark;
    private Button makeSure;

    private Map<String, Object> map;            //上一个activity传来的数据
    private Gson gson = MethodUtils.getGson();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_makesure);
        initView();

        //选择科目、年级、时间
        MyDialogClickListener l = new MyDialogClickListener();
        choSubject.setOnClickListener(l);
        choGrade.setOnClickListener(l);
        choTime.setOnClickListener(l);

        //选择联系方式
        choAddress.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MakeOrderActivity.this, SelectContactActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        //选择课时
        choPeriod.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //生成一个对话框，对话款的数据根据上一个activity传来的数据设置
                final EditText ed = new EditText(MakeOrderActivity.this);
                ed.setHeight(60);
                ed.setInputType(InputType.TYPE_CLASS_NUMBER);
                AlertDialog.Builder builder = new AlertDialog.Builder(MakeOrderActivity.this).setTitle(R.string.please_input_period)
                        .setView(ed).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!ed.getText().toString().equals("")) {
                                    period.setText(ed.getText());
                                    totalSalary.setText((Integer.parseInt(salary.getText().toString()) *
                                            Integer.parseInt(period.getText().toString())) + "");
                                    discountSalary.setText("0");
                                    finalSalary.setText((Integer.parseInt(totalSalary.getText().toString()) -
                                            Integer.parseInt(discountSalary.getText().toString())) + "");
                                }
                            }
                        }).setNegativeButton("取消", null);
                builder.show();
            }
        });

        //确认下单
        makeSure.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    Order order = new Order(0, MethodUtils.getLocalUser().getId(), Integer.parseInt((String) map.get("teacher_id")),
                            (String) map.get("teacher_name"), contactName.getText().toString(), contactTel.getText().toString(),
                            contactAddress.getText().toString(), period.getText().toString(), subject.getText().toString(),
                            grade.getText().toString(), time.getText().toString(), finalSalary.getText().toString(),
                            remark.getText().toString(), 1, new Date(System.currentTimeMillis()), null, null);
                    Map<String, String> req = new HashMap<String, String>();
                    req.put("order", gson.toJson(order));
                    volley_Post("order/insertOrder", req);
                }catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 初始化界面
     */
    public void initView() {
        //找出各个控件
        actionbarName = (TextView) findViewById(R.id.actionbar_name);
        contactName = (TextView) findViewById(R.id.contact_name);
        contactTel = (TextView) findViewById(R.id.contact_tel);
        contactAddress = (TextView) findViewById(R.id.contact_address);
        subject = (TextView) findViewById(R.id.subject);
        grade = (TextView) findViewById(R.id.grade);
        time = (TextView) findViewById(R.id.time);
        salary = (TextView) findViewById(R.id.salary);
        period = (TextView) findViewById(R.id.period);
        totalSalary = (TextView) findViewById(R.id.total_salary);
        discountSalary = (TextView) findViewById(R.id.discount_salary);
        finalSalary = (TextView) findViewById(R.id.final_salary);

        choAddress = (RelativeLayout) findViewById(R.id.cho_address);
        choPeriod = (RelativeLayout) findViewById(R.id.cho_period);
        choSubject = (RelativeLayout) findViewById(R.id.cho_subject);
        choGrade = (RelativeLayout) findViewById(R.id.cho_grade);
        choTime = (RelativeLayout) findViewById(R.id.cho_time);

        remark = (EditText) findViewById(R.id.remark);
        makeSure = (Button) findViewById(R.id.make_sure);

        //解析从上一个activity传来的数据
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        map = (HashMap) bundle.getSerializable("data");

        actionbarName.setText(map.get("teacher_name").toString());

        //TODO 根据自己的id在数据库中查询contact_info信息，取出默认地址
        contactName.setText("兰石磊");
        contactTel.setText("18840833151");
        contactAddress.setText("大连理工大学软件学院");

        //初始赋值为“请选择”，之后根据传来的信息生成选择框，在用户选择后更新
        subject.setText(R.string.please_choose);
        grade.setText(R.string.please_choose);
        time.setText(R.string.please_choose);
        salary.setText(map.get("salary").toString());

        //课时、总价、折扣价以及最终价格要在用户选择完课时包后更新
        period.setText("1");
        totalSalary.setText((Integer.parseInt(salary.getText().toString()) *
                Integer.parseInt(period.getText().toString())) + "");
        discountSalary.setText("0");
        finalSalary.setText((Integer.parseInt(totalSalary.getText().toString()) -
                Integer.parseInt(discountSalary.getText().toString())) + "");
    }

    /**
     * 处理被启动activity返回的结果
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) return;

        if (data.hasExtra("CONTACT_INFO_ID")) {
            int id = data.getIntExtra("CONTACT_INFO_ID", 0);
            ContactInfo contactInfo = MethodUtils.getLocalContactInfoById(id);
            contactName.setText(contactInfo.getContactName());
            contactAddress.setText(contactInfo.getAddress());
            contactTel.setText(contactInfo.getTel());
        }
    }

    /**
     * 请求服务器数据
     *
     * @param url_add 添加的请求url
     * @param req     请求数据
     */
    private void volley_Post(String url_add, final Map<String, String> req)
    {
        String url = getString(R.string.url);
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s)
            {
                //请求成功
                Map<String, Object> response = gson.fromJson(s, Map.class);
                //从服务器传回来的信息解析出来数据
                if ((boolean) response.get("result")) {
                    Toast.makeText(MakeOrderActivity.this, "下单成功！", Toast.LENGTH_SHORT).show();
                    //下单成功后返回上一个界面
                    MakeOrderActivity.this.finish();
                } else {
                    Toast.makeText(MakeOrderActivity.this, "下单失败，请重试！", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //请求失败处理
                Toast.makeText(MakeOrderActivity.this, "连接服务器失败，请刷新重试！", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return req;
            }
        };
        request.setTag("doPost");
        MyApplication.getHttpQueue().add(request);
    }

    /**
     * 自定义选择条件监听器
     */
    private class MyDialogClickListener implements View.OnClickListener {
        String key;     //储存被点击的条件

        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.cho_subject:
                    key = "subject";
                    break;
                case R.id.cho_grade:
                    key = "grade";
                    break;
                case R.id.cho_time:
                    key = "work_time";
                    break;
            }

            //将接收到的string串解析成单个条件
            StringTokenizer tokenizer = new StringTokenizer(map.get(key).toString(), "||");
            final String[] item = new String[tokenizer.countTokens()];
            int i = 0;
            while (tokenizer.hasMoreElements()) {
                item[i++] = tokenizer.nextToken();
            }

            //生成选择框
            AlertDialog.Builder builder = new AlertDialog.Builder(MakeOrderActivity.this).setTitle(R.string.please_choose)
                    .setItems(item, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (v.getId()) {
                                case R.id.cho_subject:
                                    subject.setText(item[which]);
                                    break;
                                case R.id.cho_grade:
                                    grade.setText(item[which]);
                                    break;
                                case R.id.cho_time:
                                    time.setText(item[which]);
                            }
                        }
                    });
            builder.show();
        }
    }
}
