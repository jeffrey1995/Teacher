package hc.teacher.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import hc.teacher.application.MyApplication;


/**
 * Created by Administrator on 2015/10/25 0025.
 */
public class NetConnection {
    final Gson gson = new Gson();
    String request_data = "";
    private static String response_data = "";

    public String getResponse_data() {
        return response_data;
    }
    public String getRequest_data() {
        return request_data;
    }

    public void volley_Get()
    {
        String url = "";
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
    public void volley_Post(String url_add,Map<String,Object> req)
    {
        String url = "http://192.168.11.17:8080/";
        url = url + url_add;
        request_data = gson.toJson(req);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                System.out.println("success!"+s);
                response_data = s;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //请求失败处理
                response_data = "net failed!";
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("request",request_data);
                return map;
            }
        };
        request.setTag("doPost");
        MyApplication.getHttpQueue().add(request);
//        return response_data;
    }
}
