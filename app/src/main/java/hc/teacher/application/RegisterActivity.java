package hc.teacher.application;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import hc.teacher.entity.User;
import hc.teacher.utils.MethodUtils;


public class RegisterActivity extends AppCompatActivity {
    //数据处理对象
    Gson gson = MethodUtils.getGson();
    // 电话号长度
    private int PHONE_NUMBER_LENGTH = 11;
    // 密码最小长度
    private int PWD_MIN_LEN = 6;

    // UI引用
    private ImageButton btn_GoBack;
    private EditText et_Account;
    private EditText et_Password;
    private EditText et_VerificationCode;
    private RadioGroup rg_Identity;
    private Button btn_Register;
    private CheckBox cb_Agreement;

    // 输入框文本长度监视器
    MyTextWatcher mTextWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //初始化视图
        initView();

        //创建使用自定义文本长度监视器
        mTextWatcher = new MyTextWatcher();
        et_Account.addTextChangedListener(mTextWatcher);
        et_Password.addTextChangedListener(mTextWatcher);
        et_VerificationCode.addTextChangedListener(mTextWatcher);

        //点击同意协议判断注册按钮是否可用
        cb_Agreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRegisterButtonClickable();
            }
        });

        //返回按钮事件
        btn_GoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.finish();
            }
        });

        //验证用户名是否存在
        et_Account.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkAccountExist(et_Account.getText().toString());
                }
            }
        });

        //发送注册信息
        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

        //设置注册按钮是否允许点击
        setRegisterButtonClickable();
    }

    void initView() {
        btn_GoBack = (ImageButton) findViewById(R.id.btn_GoBack);
        et_Account = (EditText) findViewById(R.id.et_Account);
        btn_Register = (Button) findViewById(R.id.btn_Register);
        rg_Identity = (RadioGroup) findViewById(R.id.rg_Identity);
        et_Password = (EditText) findViewById(R.id.et_Password);
        cb_Agreement = (CheckBox) findViewById(R.id.cb_Agreement);
        et_VerificationCode = (EditText) findViewById(R.id.et_VerificationCode);
    }

    private void attemptRegister() {
        String account = et_Account.getText().toString();
        String password = et_Password.getText().toString();

        //如果手机号位数不够
        if (account.length() < PHONE_NUMBER_LENGTH) {
            Toast.makeText(getApplication(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        //如果密码位数不够
        if (password.length() < PWD_MIN_LEN) {
            Toast.makeText(getApplication(), "密码至少为6位", Toast.LENGTH_SHORT).show();
            return;
        }

        //发送注册信息
        postRegisterInfo(account, password);
    }

    private void postRegisterInfo(String account, String password) {
        User user = new User();
        user.setAccount(account);
        user.setPassword(password);
        int identity = rg_Identity.getCheckedRadioButtonId();
        switch (identity) {
            case R.id.rb_FindTeacher:
                user.setIdentity(User.IDENTITY_FIND);
                break;
            case R.id.rb_DoTeacher:
                user.setIdentity(User.IDENTITY_DO);
                break;
        }
        Map<String, String> req = new HashMap<String, String>();
        req.put("user", gson.toJson(user));
        volley_Post("register", req);
    }

    private void checkAccountExist(String account) {
        Map<String, String> req = new HashMap<String, String>();
        req.put("account", account);
        volley_Post("exist", req);
    }

    private String volley_Post(final String url_add, final Map<String, String> req) {
        String url = getString(R.string.url);
        url = url + url_add;
        String response_data = "";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                Gson gson = new Gson();
                Map<String, Object> res = gson.fromJson(s, Map.class);
                switch (url_add) {
                    //判断用户名是否已存在
                    case "exist":
                        if ((Boolean) res.get("result")) {
                            Toast.makeText(getApplication(), "用户名已存在", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    //发送注册信息
                    case "register":
                        int i = Integer.parseInt((String) res.get("result"));
                        if (i == 0) {
                            Toast.makeText(getApplication(), "注册成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (i == 1) {
                            Toast.makeText(getApplication(), "注册失败", Toast.LENGTH_SHORT).show();
                        } else if (i == 2) {
                            Toast.makeText(getApplication(), "用户名已存在", Toast.LENGTH_SHORT).show();
                        }
                        break;
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
        request.setTag("register_post");
        MyApplication.getHttpQueue().add(request);
        return response_data;
    }

    /**
     * 设置注册按钮是否可以点击
     *
     * @return
     */
    private void setRegisterButtonClickable() {
        if (et_Password.getText().toString().equals("") ||
                et_Account.getText().toString().equals("") ||
                et_VerificationCode.getText().toString().equals("") ||
                !cb_Agreement.isChecked()) {
            btn_Register.setClickable(false);
            btn_Register.setBackgroundResource(R.drawable.round_button_disabled);
        } else {
            btn_Register.setClickable(true);
            btn_Register.setBackgroundResource(R.drawable.round_button);
        }
    }

    /**
     * 文本长度监视器，任何一个输入框为空都将导致注册按钮不可点击
     */
    class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            setRegisterButtonClickable();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            setRegisterButtonClickable();
        }
    }
}
