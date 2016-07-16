package hc.teacher.activity_teacher_center;

import android.app.Activity;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import hc.teacher.activity_person.ReadyMakeOrderActivity;
import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.entity.RealInfo;
import hc.teacher.entity.User;
import hc.teacher.user_defined_widget.RoundImageView;
import hc.teacher.utils.MethodUtils;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class ApplyInfoActivity extends Activity implements RongIM.UserInfoProvider
{
    private TextView personName;
    private TextView personSchool;
    private RoundImageView personHead;
    private TextView subject;
    private TextView fitGrade;
    private TextView area;
    private TextView when;
    private TextView salary;

    //认证条目
    private TextView id1;
    private TextView id2;
    private TextView id3;
    private ImageView identity1;
    private ImageView identity2;
    private ImageView identity3;

    private RelativeLayout rl;      //查看真实信息
    private LinearLayout bottom;
    private Button chat_btn;
    private Button make_order_btn;

    private Map<String, Object> map;        //储存上一个activity传过来的数据
    private Map<String, Object> realInfo;   //储存访问得到的用户真实信息
    private Gson gson = MethodUtils.getGson();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_info);
        //初始化数据
        initData();
        //融云用户数据
        RongIM.setUserInfoProvider(this, true);
        //返回上一个界面
        findViewById(R.id.return_btn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ApplyInfoActivity.this.finish();
            }
        });

        //查看真实信息
        rl.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //将被点击item对应的用户真实信息发送到下一界面
                Intent intent = new Intent();
                intent.setClass(ApplyInfoActivity.this, ReadyMakeOrderActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("data", (Serializable)realInfo);
                bundle.putSerializable("info", (Serializable)map);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //我就要他
        make_order_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent it = new Intent();
                it.setClass(ApplyInfoActivity.this, MakeOrderActivity.class);

                Bundle bund = new Bundle();
                Map<String, Object> data = new HashMap<>();
                double person_id = (double)map.get("USER_ID");
                data.put("teacher_id", (int)person_id + "");
                Map<String, Object> temp = (Map)realInfo.get("REALINFO");
                if(temp.size() == 0)
                    data.put("teacher_name", " ");
                else
                    data.put("teacher_name", temp.get("NAME"));
                data.put("subject", map.get("SUBJECT"));
                data.put("grade", map.get("GRADE"));
                data.put("work_time", map.get("WORKTIME"));
                data.put("salary", map.get("SALARY"));
                bund.putSerializable("data", (Serializable) data);
                it.putExtras(bund);
                startActivity(it);
            }
        });

        //和他聊聊
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(RongIM.getInstance() != null)
                {
                    double person = (double)map.get("USER_ID");
                    int person_id = (int)person;
                    RongIM.getInstance().startPrivateChat(ApplyInfoActivity.this,person_id+"",null);
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData()
    {
        personName = (TextView)findViewById(R.id.person_name);
        personSchool = (TextView)findViewById(R.id.person_school);
        personHead = (RoundImageView)findViewById(R.id.person_head);
        subject = (TextView)findViewById(R.id.subject);
        fitGrade = (TextView)findViewById(R.id.fit_grade);
        area = (TextView)findViewById(R.id.area);
        when = (TextView)findViewById(R.id.when);
        salary = (TextView)findViewById(R.id.salary);

        id1 = (TextView)findViewById(R.id.id1);
        id2 = (TextView)findViewById(R.id.id2);
        id3 = (TextView)findViewById(R.id.id3);

        identity1 = (ImageView)findViewById(R.id.identity1);
        identity2 = (ImageView)findViewById(R.id.identity2);
        identity3 = (ImageView)findViewById(R.id.identity3);

        rl = (RelativeLayout)findViewById(R.id.check_real_info);
        bottom = (LinearLayout)findViewById(R.id.bottom);
        chat_btn = (Button)findViewById(R.id.chat_btn);
        make_order_btn = (Button)findViewById(R.id.make_order_btn);

        //将从上一个界面传来的数据填充到各个控件中
        Intent it = getIntent();
        Bundle bundle = it.getExtras();
        map = (HashMap)bundle.getSerializable("data");

        StringTokenizer tokenizer = new StringTokenizer(map.get("SUBJECT").toString(), "||");
        StringBuffer sb = new StringBuffer();
        while(tokenizer.hasMoreElements())
        {
            sb.append(tokenizer.nextToken() + " ");
        }
        subject.setText(sb.toString());

        tokenizer = new StringTokenizer(map.get("GRADE").toString(), "||");
        sb = new StringBuffer();
        while(tokenizer.hasMoreElements())
        {
            sb.append(tokenizer.nextToken() + " ");
        }
        fitGrade.setText(sb.toString());
        area.setText(map.get("ADDRESS").toString());

        tokenizer = new StringTokenizer(map.get("WORKTIME").toString(), "||");
        sb = new StringBuffer();
        while(tokenizer.hasMoreElements())
        {
            sb.append(tokenizer.nextToken() + " ");
        }
        when.setText(sb.toString());
        salary.setText(map.get("SALARY").toString());

        double person = (double)map.get("USER_ID");
        int person_id = (int)person;
        String url = getResources().getString(R.string.url) + "public/images/" + person_id + ".png";
        //访问数据库得到user信息
        Map<String, String> req = new HashMap<String, String>();
        req.put("id", person_id + "");
        volley_Post("getUserById", req);
        netWorkImageViewVolley(url, personHead);

        //如果用户身份为“做家教”，那么最下方的“和他聊聊”和“我就要他”不可见
        try
        {
            if(MethodUtils.getLocalUser().getIdentity() == User.IDENTITY_DO)
                bottom.setVisibility(View.GONE);
        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }
    }

    public void volley_Post(String url_add, final Map<String, String> req)
    {
        String url = getString(R.string.url);
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                //请求成功
                Map<String,Object> response = gson.fromJson(s, Map.class);

                realInfo = response;
                Map<String, Object> temp = (Map) response.get("REALINFO");
                if(temp.size() == 0)
                {
                    personName.setText("未认证");
                    id1.setVisibility(View.GONE);
                    id2.setVisibility(View.GONE);
                    id3.setVisibility(View.GONE);
                    identity1.setVisibility(View.GONE);
                    identity2.setVisibility(View.GONE);
                    identity3.setVisibility(View.GONE);
                }
                else
                {
                    personName.setText(temp.get("NAME").toString());
                    personSchool.setText(temp.get("SCHOOL").toString());
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                //请求失败处理
                Toast.makeText(ApplyInfoActivity.this, "连接服务器失败，请刷新重试！",Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                return req;
            }
        };
        request.setTag("doPost");
        MyApplication.getHttpQueue().add(request);
    }

    /**
     * 请求用户头像
     * @param imageUrl  头像url
     * @param personHead    要显示该头像的控件
     */
    public void netWorkImageViewVolley(String imageUrl, RoundImageView personHead)
    {
        final LruCache<String,Bitmap> lruCache = new LruCache<String,Bitmap>(20);
        ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache() {
            @Override
            public Bitmap getBitmap(String key)
            {
                return lruCache.get("key");
            }

            @Override
            public void putBitmap(String key, Bitmap value)
            {
                lruCache.put(key,value);
            }
        };
        ImageLoader imageLoader = new ImageLoader(MyApplication.getHttpQueue(),imageCache);
        ImageLoader.ImageListener listener = imageLoader.getImageListener(personHead, 0, 0);
        imageLoader.get(imageUrl, listener);
    }

    @Override
    public UserInfo getUserInfo(String s) {
        System.out.println("s:::"+s);
        if (map.get("ID").toString().equals(s))
        {
            System.out.println("to chat!!!!!!!"+map.get("NICKNAME").toString()+"---"+Uri.parse(map.get("HEAD").toString()));
            return new io.rong.imlib.model.UserInfo(s,map.get("NICKNAME").toString(), Uri.parse(map.get("HEAD").toString()));
        }

        //设置融云聊天当前用户和对方信息
        try {
            Log.e("getLocalUser   :", "...............");
            User user = MethodUtils.getLocalUser();
            Log.e("getLocalUserId   :",user.getId().toString());
            if(user.getId().toString().equals(s))
            {
                Log.e("LocalUser   :", user.toString());
                return new io.rong.imlib.model.UserInfo(user.getId().toString(),user.getNickname(), Uri.parse(getString(R.string.url) + user.getHead()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
