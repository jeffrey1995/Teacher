package hc.teacher.activity_person;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import hc.teacher.data.DatabaseHelper;
import hc.teacher.utils.MethodUtils;

public class ModifyPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    //数据处理
    Gson gson = MethodUtils.getGson();

    private ImageButton ibtn_GoBack;
    private Button btn_ModifyPassword;
    private EditText et_CurrentPwd;
    private EditText et_NewPwd;
    private EditText et_ConfirmNewPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);

        initView();

        ibtn_GoBack.setOnClickListener(this);
        btn_ModifyPassword.setOnClickListener(this);
    }

    private void initView(){
        ibtn_GoBack = (ImageButton) findViewById(R.id.btn_GoBack);
        btn_ModifyPassword = (Button) findViewById(R.id.btn_ModifyPassword);
        et_CurrentPwd = (EditText) findViewById(R.id.et_CurrentPwd);
        et_NewPwd = (EditText) findViewById(R.id.et_NewPwd);
        et_ConfirmNewPwd = (EditText) findViewById(R.id.et_ConfirmNewPwd);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_GoBack:
                finish();
                break;
            case R.id.btn_ModifyPassword:
                attemptModifyPassword();
                break;
        }
    }

    private void attemptModifyPassword(){
        String current_password = et_CurrentPwd.getText().toString();
        String new_password = et_NewPwd.getText().toString();
        String confirm_new_password = et_ConfirmNewPwd.getText().toString();
        if(current_password.equals("") || new_password.equals("") || confirm_new_password.equals("") ){
            Toast.makeText(getApplication(),"输入信息不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(new_password.length() < 6){
            Toast.makeText(getApplication(),"密码至少为6位",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!new_password.equals(confirm_new_password)){
            Toast.makeText(getApplication(),"两次输入密码不一致",Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Map<String, String> req = new HashMap<>();
            req.put("account", MethodUtils.getLocalUser().getAccount());
            req.put("password", current_password);
            volleyPostLogin(req, new_password);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getApplication(), "本地数据被破坏", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 旧密码验证的请求
     * @param req 请求的map
     * @param password 新的密码
     */
    private void volleyPostLogin(final Map<String, String> req, final String password) {
        String url = getString(R.string.url);
        url = url + "login";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                Map<String, Object> res = gson.fromJson(s, Map.class);
                if ((Boolean) res.get("result")) {
                    try {
                        req.put("id", MethodUtils.getLocalUser().getId().toString());
                        req.put("password", "" + password);
                        volleyPostModify(req, password);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplication(), "本地数据已被破坏", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplication(), "当前密码错误", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //请求失败处理
                MyApplication.hasLoggedIn = false;
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

    /**
     * 修改密码的网络请求
     * @param req 请求的map
     * @param password 新的密码
     */
    private void volleyPostModify(final Map<String, String> req, final String password) {
        String url = getString(R.string.url);
        url = url + "updateUserInfo";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                Map<String, Object> res = gson.fromJson(s, Map.class);
                if (res.get("result").equals("0")) {
                    //修改本地数据
                    DatabaseHelper dbHelper = MyApplication.dbHelper;
                    SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put("password", password);
                    dbWrite.update("user", cv, null, null);
                    dbWrite.close();
                    Toast.makeText(getApplication(), "修改成功", Toast.LENGTH_LONG).show();
                    finish();
                } else if (res.get("result").equals("1")) {
                    Toast.makeText(getApplication(), "修改失败", Toast.LENGTH_LONG).show();
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
