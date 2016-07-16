package hc.teacher.activity_circle;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import hc.teacher.entity.User;
import hc.teacher.fragment.Circle_AddressBookFragment;
import hc.teacher.utils.MethodUtils;

public class Circle_NewFriendActivity extends AppCompatActivity {
    Gson gson = MethodUtils.getGson();
    ImageView return_img;
    Button add_friend;
    EditText friend_edt;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle__newfriend);
        return_img = (ImageView)findViewById(R.id.return_img);
        add_friend = (Button)findViewById(R.id.add_friend);
        friend_edt = (EditText)findViewById(R.id.friend_edt);
        //返回按钮
        return_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回按钮的点击监听
                Circle_NewFriendActivity.this.finish();
            }
        });
        //添加好友按钮
        add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_friend = friend_edt.getText().toString();
                User user;
                try {
                    user = MethodUtils.getLocalUser();
                    Map<String,String> req = new HashMap<String, String>();
                    req.put("first_id", user.getId().toString());//当前用户id
                    req.put("second_name",str_friend);//被添加的好友账号
                    volley_Post("circle/apply", req);
                } catch (ParseException e) {
                    Log.i("newFriend","exception");
                    e.printStackTrace();
                }

            }
        });
    }

    private void volley_Post(String url_add, final Map<String, String> req) {
        String url = getString(R.string.url);
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                System.out.println(s);
                Map<String,Object> response = gson.fromJson(s,Map.class);
                if((boolean)response.get("result"))
                {
                    Toast.makeText(getApplication(), "成功添加好友！", Toast.LENGTH_LONG).show();
                    Circle_NewFriendActivity.this.finish();
                }
                else
                {
                    Toast.makeText(getApplication(), "添加失败！", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //请求失败处理
                Toast.makeText(getApplication(), "网络错误", Toast.LENGTH_LONG).show();
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
