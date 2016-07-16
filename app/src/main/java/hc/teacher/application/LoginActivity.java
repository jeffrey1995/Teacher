package hc.teacher.application;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hc.teacher.data.DatabaseHelper;
import hc.teacher.entity.ContactInfo;
import hc.teacher.entity.User;
import hc.teacher.utils.MethodUtils;

/**
 * create by cryViking Oct 7th, 2015.
 */

public class LoginActivity extends AppCompatActivity {
    //数据处理
    Gson gson = MethodUtils.getGson();
    // 电话号长度
    private int PHONE_NUMBER_LENGTH = 11;
    // 密码最小长度
    private int PWD_MIN_LEN = 6;

    // UI引用
    private ImageButton ibtn_GoBack;
    private EditText mAccountEditView;
    private EditText mPasswordEditView;
    private Button mLoginButton;
    private Button mRegisterButton;

    // 表示账号密码是否合法
    private boolean isAccountOK = false;
    private boolean isPasswordOK = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 获取UI控件
        initView();

        //返回按钮
        ibtn_GoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 监听账户输入框输入字符长度变化
        mAccountEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().length() < PHONE_NUMBER_LENGTH) {
                    isAccountOK = false;
                    setLoginButtonClickable(false);
                } else {
                    isAccountOK = true;
                    setLoginButtonClickable(isPasswordOK);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() < PHONE_NUMBER_LENGTH) {
                    isAccountOK = false;
                    setLoginButtonClickable(false);
                } else {
                    isAccountOK = true;
                    setLoginButtonClickable(isPasswordOK);
                }
            }
        });

        // 监听密码输入框输入字符长度变化
        mPasswordEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().length() < PWD_MIN_LEN) {
                    isPasswordOK = false;
                    setLoginButtonClickable(false);
                } else {
                    isPasswordOK = true;
                    setLoginButtonClickable(isAccountOK);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() < PWD_MIN_LEN) {
                    isPasswordOK = false;
                    setLoginButtonClickable(false);
                } else {
                    isPasswordOK = true;
                    setLoginButtonClickable(isAccountOK);
                }
            }
        });

        // 响应登录事件
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();

            }
        });

        // 响应注册事件
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        // 登陆按钮设为不可点击
        mLoginButton.setClickable(false);

    }

    private void initView() {
        ibtn_GoBack = (ImageButton) findViewById(R.id.btn_GoBack);
        mAccountEditView = (EditText) findViewById(R.id.et_Account);
        mPasswordEditView = (EditText) findViewById(R.id.et_Password);
        mLoginButton = (Button) findViewById(R.id.btn_Login);
        mRegisterButton = (Button) findViewById(R.id.btn_Register);
    }

    /**
     * 设置登录按钮是否可以点击
     *
     * @param clickable
     * @return
     */
    private void setLoginButtonClickable(boolean clickable) {
        if (clickable) {
            mLoginButton.setClickable(true);
            mLoginButton.setBackgroundResource(R.drawable.round_button);
        } else {
            mLoginButton.setClickable(false);
            mLoginButton.setBackgroundResource(R.drawable.round_button_disabled);
        }
    }

    /**
     * 登陆尝试
     */
    public void attemptLogin() {
        // 存储当次尝试登录的值
        String account = mAccountEditView.getText().toString().trim();
        String password = mPasswordEditView.getText().toString();
        Map<String, String> req = new HashMap<String, String>();
        req.put("account", account);
        req.put("password", password);
        volley_Post("login", req);
    }

    private void volley_Post(String url_add, final Map<String, String> req) {
        String url = getString(R.string.url);
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                //请求成功
                Map<String, Object> res = gson.fromJson(s, Map.class);
                if ((Boolean) res.get("result")) {
                    //清空本地数据库
                    MethodUtils.clearLocalUser();
                    User user = gson.fromJson(res.get("user").toString(), User.class);
                    insertUserToLocalDB(user);
                    try {
                        loadImageVolley();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    MyApplication.hasLoggedIn = true;

                    /*在登陆成功后建立融云连接*/
                    MethodUtils.getRongIMConnection();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();

                    //获得contactInfoMapList，每个list元素是一个保存了contactInfo类型各种属性的map
                    List<Map<String, Object>> contactInfoMapList = gson.fromJson(res.get("contactInfo").toString(), List.class);
                    ContactInfo[] contactInfo = new ContactInfo[contactInfoMapList.size()];
                    for (int i = 0; i < contactInfoMapList.size(); i++) {
                        Map<String, Object> map = contactInfoMapList.get(i);
                        int id = (int) Double.parseDouble(map.get("ID").toString());
                        int user_id = (int) Double.parseDouble(map.get("USER_ID").toString());
                        String contact_name = map.get("CONTACT_NAME").toString();
                        String tel = map.get("TEL").toString();
                        String address = map.get("ADDRESS").toString();
                        contactInfo[i] = new ContactInfo(id, user_id, contact_name, tel, address);
                    }
                    //清空之前的数据
                    clearContactInfoTable();

                    for (int i = 0; i < contactInfo.length; i++) {
                        //插入到本地数据库
                        MethodUtils.insertContactInfoToLocalDB(contactInfo[i]);
                    }
                } else {
                    MyApplication.hasLoggedIn = false;
                    Toast.makeText(getApplication(), "用户名或密码错误", Toast.LENGTH_LONG).show();
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
     * 将一个user对象实例转换成本地user数据表中的一条记录（插入至User表）
     *
     * @param user
     */
    private void insertUserToLocalDB(User user) {

        //连接到数据库
        DatabaseHelper dbHelper = MyApplication.dbHelper;
        SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();

        //清空之前的数据
        dbWrite.delete("user", null, null);

        //插入数据
        ContentValues cv = new ContentValues();
        cv.put("id", user.getId());
        cv.put("account", user.getAccount());
        cv.put("password", user.getPassword());
        cv.put("identity", user.getIdentity());
        cv.put("nickname", user.getNickname());
        cv.put("gender", user.getGender());
        cv.put("address", user.getAddress());
        cv.put("email", user.getEmail());
        cv.put("qqnumber", user.getQqnumber());
        cv.put("tel", user.getTel());
        cv.put("ispublic", user.getIsPublic());
        cv.put("description", user.getDescription());
        cv.put("head", user.getHead());
        cv.put("register_date", user.getRegister_date().toString());
        cv.put("identify_state", user.getIdentify_state());
        cv.put("token", user.getToken().toString());
        dbWrite.insert("user", null, cv);
        dbWrite.close();
        dbHelper.close();
    }

    private void clearContactInfoTable() {
        //连接到数据库
        DatabaseHelper dbHelper = MyApplication.dbHelper;
        SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
        dbWrite.delete("contact_info", null, null);
        dbWrite.close();
        dbHelper.close();
    }

    /**
     * 请求用户的头像
     */
    public void loadImageVolley() throws ParseException {
        String url = getString(R.string.url);
        url = url + "public/images/" + MethodUtils.getLocalUser().getId() + ".png";
        final LruCache<String, Bitmap> lrucache = new LruCache<String, Bitmap>(20);
        ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache() {
            @Override
            public void putBitmap(String key, Bitmap value) {
                lrucache.put(key, value);
                saveBitmap(value);
            }

            @Override
            public Bitmap getBitmap(String key) {
                return lrucache.get(key);
            }
        };

        //// TODO: 2015/11/19 删掉这个异步加载器
        ImageLoader imageLoader = new ImageLoader(MyApplication.getHttpQueue(), imageCache);
        ImageLoader.ImageListener listener = imageLoader.getImageListener(new ImageView(this), R.drawable.headimage01, R.drawable.headimage01);
        imageLoader.get(url, listener);
    }

    /**
     * 将Bitmap保存为本地PNG图片，返回保存文件的URI
     *
     * @param bm 输入的Bitmap
     * @return 保存结果的文件URI
     */
    private Uri saveBitmap(Bitmap bm) {
        File img = MethodUtils.getLocalImageFile(MyApplication.PATH_USER_ICON);
        try {
            FileOutputStream fos = new FileOutputStream(img);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fos);
            fos.flush();
            fos.close();
            return Uri.fromFile(img);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
