package hc.teacher.activity_teacher_center;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import hc.teacher.application.R;
import hc.teacher.entity.User;
import hc.teacher.utils.MethodUtils;

public class EmployInfoActivity extends Activity
{
    private TextView title;
    private TextView subject1;
    private TextView time;
    private TextView grade1;
    private TextView sex;
    private TextView grade2;
    private TextView subject2;
    private TextView sexComm;
    private TextView area;
    private TextView when;
    private TextView salary;
    private TextView contractName;
    private TextView phone;
    private TextView request;

    private LinearLayout bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employ_info);
        //初始化数据
        initData();
        //返回上一界面
        findViewById(R.id.return_imbtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EmployInfoActivity.this.finish();
            }
        });
    }

    private void initData()
    {
        title = (TextView)findViewById(R.id.title);
        subject1 = (TextView)findViewById(R.id.subject1);
        time = (TextView)findViewById(R.id.time);
        grade1 = (TextView)findViewById(R.id.grade1);
        sex = (TextView)findViewById(R.id.sex);
        grade2 = (TextView)findViewById(R.id.grade2);
        subject2 = (TextView)findViewById(R.id.subject2);
        sexComm = (TextView)findViewById(R.id.sex_comm);
        area = (TextView)findViewById(R.id.area);
        when = (TextView)findViewById(R.id.when);
        salary = (TextView)findViewById(R.id.salary);
        contractName = (TextView)findViewById(R.id.contract_name);
        phone = (TextView)findViewById(R.id.phone);
        request = (TextView)findViewById(R.id.request);

        bottom = (LinearLayout)findViewById(R.id.bottom);

        //将从上一个界面传来的数据填充到各个控件中
        Intent it = getIntent();
        Bundle bundle = it.getExtras();
        Map<String, Object> map = (HashMap)bundle.getSerializable("data");
        title.setText(map.get("TITLE").toString());
        time.setText(map.get("COMMIT_DATE").toString().substring(5, 16));
        StringTokenizer tokenizer = new StringTokenizer(map.get("GRADE").toString(), "||");
        StringBuffer sb = new StringBuffer();
        while(tokenizer.hasMoreElements())
        {
            sb.append(tokenizer.nextToken() + " ");
        }
        grade1.setText(sb.toString().length() > 3 ? sb.toString().substring(0, 2) + "..." : sb.toString());
        grade2.setText(sb.toString());
        sex.setText(((Double)map.get("GENDER") == 1) ? "男" : "女");
        tokenizer = new StringTokenizer(map.get("SUBJECT").toString(), "||");
        sb = new StringBuffer();
        while(tokenizer.hasMoreElements())
        {
            sb.append(tokenizer.nextToken() + " ");
        }
        subject1.setText(sb.toString().length() > 3 ? sb.toString().substring(0, 2) + "..." : sb.toString());
        subject2.setText(sb.toString());
        sexComm.setText(((Double)map.get("NEED_GENDER") == 1) ? "男" : "女");
        area.setText(map.get("ADDRESS").toString());
        tokenizer = new StringTokenizer(map.get("WORKTIME").toString(), "||");
        sb = new StringBuffer();
        while(tokenizer.hasMoreElements())
        {
            sb.append(tokenizer.nextToken() + " ");
        }
        when.setText(sb.toString());
        salary.setText(map.get("SALARY").toString());
        contractName.setText(map.get("CONTACT_NAME").toString());
        phone.setText(map.get("TEL").toString());
        request.setText(map.get("DESCRIPTION").toString());

        //如果用户身份为“找家教”，那么最下方的“关注”和“我要报名”不可见
        try
        {
            if(MethodUtils.getLocalUser().getIdentity() == User.IDENTITY_FIND)
                bottom.setVisibility(View.GONE);
        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }
    }
}
