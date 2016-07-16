package hc.teacher.activity_teacher_center;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.entity.ContactInfo;
import hc.teacher.utils.MethodUtils;

public class AddNewContactActivity extends AppCompatActivity {

    //GSON
    Gson gson = MethodUtils.getGson();

    //UI引用
    private EditText et_Name;
    private EditText et_Phone;
    private EditText et_Address;
    private Button btn_Submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);

        //初始化控件视图
        initView();

        //点击确定按钮
        btn_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSubmit();
            }
        });
    }

    private void initView() {
        et_Name = (EditText) findViewById(R.id.et_Name);
        et_Phone = (EditText) findViewById(R.id.et_Phone);
        et_Address = (EditText) findViewById(R.id.et_Address);
        btn_Submit = (Button) findViewById(R.id.btn_Submit);
    }

    private void attemptSubmit() {
        String name = et_Name.getText().toString();
        String phone = et_Phone.getText().toString();
        String address = et_Address.getText().toString();

        if (name.equals("") || phone.equals("") || address.equals("")) {
            Toast.makeText(getApplication(), "填写项不能为空", Toast.LENGTH_SHORT).show();
            return;
        } else if (phone.length() < 11) {
            Toast.makeText(getApplication(), "请填写正确的电话号码", Toast.LENGTH_SHORT).show();
            return;
        }

        //尝试生成一个ContactInfo对象，并发送给服务器
        try {
            ContactInfo contactInfo = new ContactInfo(0, MethodUtils.getLocalUser().getId(), name, phone, address);
            insertVolleyPost(contactInfo);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String insertVolleyPost(final ContactInfo contactInfo) {
        final Map<String, String> req = new HashMap<>();
        req.put("contactInfo", gson.toJson(contactInfo));

        String url = getString(R.string.url);
        url = url + "contactInfo/insertContactInfo";
        String response_data = "";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                Map<String, Object> res = gson.fromJson(s, Map.class);
                if ((Boolean) res.get("result")) {
                    Toast.makeText(getApplication(), "添加成功", Toast.LENGTH_LONG).show();
                    //获得ID并设置到 contactInfo 对象
                    contactInfo.setId((int) Double.parseDouble(res.get("id").toString()));
                    //insert到本地数据库
                    MethodUtils.insertContactInfoToLocalDB(contactInfo);
                    //向启动 activity 返回 OK 返回码，提醒其刷新
                    setResult(RESULT_OK, new Intent());
                    finish();
                } else {
                    Toast.makeText(getApplication(), "请求失败", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //请求失败处理
                Toast.makeText(getApplication(), "网络错误", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return req;
            }
        };
        request.setTag("add_new_contactInfo_post");
        MyApplication.getHttpQueue().add(request);
        return response_data;
    }

}
