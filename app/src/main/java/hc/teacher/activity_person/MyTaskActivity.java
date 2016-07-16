package hc.teacher.activity_person;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hc.teacher.adapter.MyTaskAdapter;
import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.utils.MethodUtils;
import hc.teacher.view.CalendarView;

/**
 *  我的任务
 *  @author mrtian
 */
public class MyTaskActivity extends AppCompatActivity {
    private Gson gson = MethodUtils.getGson();
    private CalendarView calendar;
    private ImageButton btn_goBack;
    private Button btn_lastMonth;
    private Button btn_nextMonth;
    private TextView tv_currentDate;
    private ListView listView;

    private MyTaskAdapter myTaskAdapter;
    private List<Map<String,Object>> listItems = new ArrayList<Map<String, Object>>();
    private String from[] = {"TITLE","DATE","TASK_DESCRIPTION","TEACHER_HEAD","TEACHER_NICKNAME"};
    private int to[] = {R.id.tv_title,R.id.tv_date,R.id.tv_content,R.id.iv_teacherHead,R.id.tv_teacherNickName};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_task);
        initView();
        setListener();
        tv_currentDate.setText(calendar.getYearAndmonth());
        setListView();
        refresh();
    }

    private void refresh() {
        String user_id = "";
        Map<String,String> req = new HashMap<String,String>();
        try {
            user_id = MethodUtils.getLocalUser().getId().toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        req.put("user_id",user_id);
        req.put("date",format.format(calendar.getSelectedStartDate()).toString());
        volley_Post("dailyTask/getTaskByIdAndDate",req);
    }

    private void setListView() {
        myTaskAdapter = new MyTaskAdapter(getBaseContext(),listItems,R.layout.mytask_listitem,from,to);
        listView.setAdapter(myTaskAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplication(), "click:" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setListener() {
        MyOnclickListener myOnclickListener = new MyOnclickListener();
        MyOnItemClickListener myOnItemClickListener = new MyOnItemClickListener();
        btn_goBack.setOnClickListener(myOnclickListener);
        btn_lastMonth.setOnClickListener(myOnclickListener);
        btn_nextMonth.setOnClickListener(myOnclickListener);
        calendar.setOnItemClickListener(myOnItemClickListener);
    }

    private void initView() {
        calendar = (CalendarView)findViewById(R.id.calendar);
        btn_goBack = (ImageButton)findViewById(R.id.btn_goBack);
        btn_lastMonth = (Button)findViewById(R.id.btn_lastMonth);
        btn_nextMonth = (Button)findViewById(R.id.btn_nextMonth);
        tv_currentDate = (TextView)findViewById(R.id.txt_currentDate);
        listView = (ListView)findViewById(R.id.lv_task);
    }

    class MyOnclickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
           switch(v.getId())
            {
                case R.id.btn_goBack:
                    finish();
                    break;
                case R.id.btn_lastMonth:
                    calendar.clickLeftMonth();
                    tv_currentDate.setText(calendar.getYearAndmonth());
                    break;
                case R.id.btn_nextMonth:
                    calendar.clickRightMonth();
                    tv_currentDate.setText(calendar.getYearAndmonth());
                    break;
                default:
                    break;
            }
        }
    }

    class MyOnItemClickListener implements CalendarView.OnItemClickListener
    {

        @Override
        public void OnItemClick(Date date) {
            refresh();

        }
    }

    private void volley_Post(String url_add, final Map<String, String> req) {
        String url = getString(R.string.url);
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                listItems.clear();
                //请求成功
                Map<String,Object> responseMap = gson.fromJson(s,Map.class);
                if(responseMap.get("result").toString().equals("true"))
                {
                    List<Map<String,Object>> list = gson.fromJson(responseMap.get("taskList").toString(),List.class);
                    if (list.size() != 0)
                    {
                        for(int i=0;i<list.size();i++)
                        {
                            double studentId_double = (Double) list.get(i).get("STUDENT_ID");
                            list.get(i).put("STUDENT_ID",(int)studentId_double);
                            list.get(i).put("STUDENT_HEAD", getString(R.string.url) + list.get(i).get("STUDENT_HEAD").toString());
                            double teacherId_double = (Double) list.get(i).get("TEACHER_ID");
                            list.get(i).put("TEACHER_ID",(int)teacherId_double);
                            list.get(i).put("TEACHER_HEAD", getString(R.string.url) + list.get(i).get("TEACHER_HEAD").toString());
                        }
                    }
                    Log.w("myTask-----",list.toString());
                    listItems.addAll(list);
                }
                else
                {
                    Toast.makeText(getApplication(),"这一天没有任务~",Toast.LENGTH_SHORT).show();
                }
                myTaskAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //请求失败处理
                Toast.makeText(getApplication().getApplicationContext(), "网络错误", Toast.LENGTH_LONG).show();
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

}
