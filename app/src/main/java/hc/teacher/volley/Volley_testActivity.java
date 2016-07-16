package hc.teacher.volley;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hc.teacher.application.MyApplication;
import hc.teacher.application.R;

public class Volley_testActivity extends AppCompatActivity {
    Button button;
    final Gson gson = new Gson();
    String json_data = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volley_test);
        button = (Button)findViewById(R.id.test_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> req = new HashMap<String, Object>();
                req.put("username","tom ");
                req.put("password","123456");
                volley_Post("login", req);
            }
        });
    }
    private void volley_Get()
    {
        String url = "http://ip:8080/teacher/login?page=1";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功返回
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //请求失败
            }
        });
        request.setTag("doGet");
        MyApplication.getHttpQueue().add(request);
    }
    private void volley_Post(String url_add,Map<String,Object> req)
    {
        String url = "http://192.168.11.17:8080/";
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                System.out.println("success!");
//                Map<String,Object> result = gson.fromJson(s, HashMap.class);
//                User user = (User)result.get("user");
//                user.setAddress("adadadad");
//                List<Map<String, Object>> list = listKeyMaps(s);
//                Map<String,Object> map = list.get(1);
//                User user = gson.fromJson(map.get("user").toString(), User.class);

                Toast.makeText(getApplication(),s, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //请求失败处理
                Toast.makeText(getApplication(),"failed!",Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String,String>();
                map.put("user_name","tom");
                map.put("password","123456");
                return map;
            }
        };
        request.setTag("doPost");
        MyApplication.getHttpQueue().add(request);
    }

    public static List<Map<String, Object>> listKeyMaps(String jsonString) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            Gson gson = new Gson();
            list = gson.fromJson(jsonString,
                    new TypeToken<List<Map<String, Object>>>() {
                    }.getType());
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("数据解析错误（List）");
        }
        return list;
    }
    public static Map<String,Object> mapKey(String jsonString)
    {
        Map<String,Object> map = new HashMap<String,Object>();
        try {
            Gson gson = new Gson();
            map = gson.fromJson(jsonString,
                    new TypeToken<Map<String, Object>>() {
                    }.getType());
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("数据解析错误（Map）");
        }
        return map;
    }
}
