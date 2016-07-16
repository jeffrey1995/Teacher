package hc.teacher.activity_person;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import hc.teacher.activity_teacher_center.MakeOrderActivity;
import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.entity.User;
import hc.teacher.user_defined_widget.RoundImageView;
import hc.teacher.utils.MethodUtils;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class ReadyMakeOrderActivity extends Activity implements RongIM.UserInfoProvider
{
    private TextView tip;
    private TextView name;
    private TextView gender;
    private TextView school;
    private TextView idNumber;
    private TextView telNumber;
    private TextView description;

    private ImageView head;

    private LinearLayout info1;
    private LinearLayout info2;
    private LinearLayout info3;
    private LinearLayout info4;

    Map<String, Object> map = new HashMap<>();      //保存上一个activity传来的数据
    Map<String, Object> info = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready_make_order);

        initData();

        //融云用户数据
        RongIM.setUserInfoProvider(this, true);

        //返回上一界面
        findViewById(R.id.btn_return).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ReadyMakeOrderActivity.this.finish();
            }
        });

        //和他聊聊
        findViewById(R.id.chat_btn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(RongIM.getInstance() != null)
                {
                    Map<String, Object> temp = (Map) map.get("REALINFO");
                    double person = (double)temp.get("USER_ID");
                    int person_id = (int)person;
                    RongIM.getInstance().startPrivateChat(ReadyMakeOrderActivity.this,person_id+"",null);
                }
            }
        });

        //我就要他
        findViewById(R.id.make_order_btn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent it = new Intent();
                it.setClass(ReadyMakeOrderActivity.this, MakeOrderActivity.class);

                Bundle bund = new Bundle();
                Map<String, Object> data = new HashMap<>();
                //把下单所需信息传给下单界面
                double id = (double)map.get("ID");
                data.put("teacher_id", (int)id + "");
                Map<String, Object> temp = (Map)map.get("REALINFO");
                if(temp.size() == 0)
                    data.put("teacher_name", " ");
                else
                    data.put("teacher_name", name.getText());
                data.put("subject", info.get("SUBJECT"));
                data.put("grade", info.get("GRADE"));
                data.put("work_time", info.get("WORKTIME"));
                data.put("salary", info.get("SALARY"));
                bund.putSerializable("data", (Serializable) data);
                it.putExtras(bund);
                startActivity(it);
            }
        });
    }

    /**
     * 处理从上一个界面传来的数据
     */
    public void initData()
    {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        map = (Map)bundle.getSerializable("data");
        info = (Map)bundle.getSerializable("info");

        tip = (TextView)findViewById(R.id.tip);
        name = (TextView)findViewById(R.id.teacher_name);
        gender = (TextView)findViewById(R.id.teacher_gender);
        school = (TextView)findViewById(R.id.teacher_school);
        idNumber = (TextView)findViewById(R.id.teacher_id_number);
        telNumber = (TextView)findViewById(R.id.teacher_tel_number);
        description = (TextView)findViewById(R.id.teacher_description);

        head = (ImageView)findViewById(R.id.teacher_head);

        info1 = (LinearLayout)findViewById(R.id.info1);
        info2 = (LinearLayout)findViewById(R.id.info2);
        info3 = (LinearLayout)findViewById(R.id.info3);
        info4 = (LinearLayout)findViewById(R.id.info4);

        Map<String, Object> temp = (Map)map.get("REALINFO");

        //如果用户未进行真实信息认证
        if(temp.size() == 0)
        {
            info1.setVisibility(View.GONE);
            info2.setVisibility(View.GONE);
            info3.setVisibility(View.GONE);
            info4.setVisibility(View.GONE);
        }
        //若该用户已进行真实信息认证，则显示对应的真实信息
        else
        {
            //设置提示不可见
            tip.setVisibility(View.GONE);
            //设置文字信息
            name.setText(temp.get("NAME").toString());
            school.setText(temp.get("SCHOOL").toString());
            gender.setText(((double) temp.get("GENDER") == 1.0) ? "男" : "nv");
            idNumber.setText(temp.get("IDENTITY_NUMBER").toString());
            telNumber.setText(temp.get("TEL").toString());
            description.setText(temp.get("INTRODUCTION").toString());
            //设置头像
            String url = getResources().getString(R.string.url) + temp.get("REAL_HEAD");
            netWorkImageViewVolley(url, head);
        }
    }

    @Override
    public UserInfo getUserInfo(String s) {
        System.out.println("s:::"+s);
        if (map.get("ID").toString().equals(s))
        {
            System.out.println("to chat!!!!!!!"+map.get("NICKNAME").toString()+"---"+ Uri.parse(map.get("HEAD").toString()));
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

    /**
     * 请求用户头像
     * @param imageUrl  头像url
     * @param personHead    要显示该头像的控件
     */
    public void netWorkImageViewVolley(String imageUrl, ImageView personHead)
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
}
