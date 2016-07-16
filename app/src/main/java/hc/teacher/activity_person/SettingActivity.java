package hc.teacher.activity_person;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.utils.MethodUtils;

public class SettingActivity extends AppCompatActivity {

    //UI引用
    private Button btn_QuitAccount;
    private ImageButton btn_GoBack;
    private LinearLayout ll_ModifyPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();

        btn_GoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ll_ModifyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MyApplication.hasLoggedIn){
                    startActivity(new Intent(SettingActivity.this, ModifyPasswordActivity.class));
                }else{
                    Toast.makeText(getApplication(), "您还没有登录", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //退出登录按钮
        btn_QuitAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //向服务器发送退出登录请求
                Map<String, String> req = new HashMap<String, String>();
                try {
                    req.put("id", MethodUtils.getLocalUser().getId().toString());
                    volley_Post("exit", req);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //清空本地数据库中user表
                MethodUtils.clearLocalUser();

                //删除本地头像文件
                MethodUtils.getLocalImageFile(MyApplication.PATH_USER_ICON).delete();

                //本地状态置为未登录
                MyApplication.hasLoggedIn = false;

                finish();
            }
        });

        if (MyApplication.hasLoggedIn) {
            btn_QuitAccount.setClickable(true);
            btn_QuitAccount.setBackgroundResource(R.drawable.round_button);
        } else {
            btn_QuitAccount.setClickable(false);
            btn_QuitAccount.setBackgroundResource(R.drawable.round_button_disabled);
        }

    }

    private void initView() {
        btn_GoBack = (ImageButton) findViewById(R.id.btn_GoBack);
        btn_QuitAccount = (Button) findViewById(R.id.btn_QuitAccount);
        ll_ModifyPassword = (LinearLayout) findViewById(R.id.ll_ModifyPassword);
    }

    /**
     * 网络请求处理
     *
     * @param url_add 附加的相对url
     * @param req     请求map
     */
    private void volley_Post(String url_add, final Map<String, String> req) {
        String url = getString(R.string.url);
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //请求失败处理
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
