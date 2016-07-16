package hc.teacher.activity_person;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hc.teacher.activity_teacher_center.MakeOrderActivity;
import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.data.DataAnalysis;
import hc.teacher.entity.RealInfo;
import hc.teacher.entity.User;
import hc.teacher.user_defined_widget.RoundImageView;
import hc.teacher.utils.MethodUtils;

public class SignUpInfoActivity extends Activity
{
    private ListView listView;
    private List<Map<String, Object>> list = new ArrayList<>();
    private SignUpInfoAdapter adapter;

    private Map<String, Object> map;        //储存上一个activity传过来的数据

    private Gson gson = MethodUtils.getGson();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_info);

        //读取从上一个activity传来的数据
        Intent it = getIntent();
        Bundle bundle = it.getExtras();
        map = (HashMap)bundle.getSerializable("data");

        listView = (ListView)findViewById(R.id.listView);
        getData();
        adapter = new SignUpInfoAdapter(this, list, R.layout.sign_up_list,
                new String [] {"name", "school"},
                new int [] {R.id.name, R.id.school});
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //将被点击item对应的用户真实信息发送到下一界面
                Intent intent = new Intent();
                intent.setClass(SignUpInfoActivity.this, ReadyMakeOrderActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("data", (Serializable)list.get(position));
                bundle.putSerializable("info", (Serializable)map);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //返回上一界面
        findViewById(R.id.btn_return).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SignUpInfoActivity.this.finish();
            }
        });
    }

    private void getData()
    {
        //根据infoID查询出此信息对应的报名信息
        double infoId = (Double)map.get("ID");
        Map<String, String> req = new HashMap<>();
        req.put("info_id", (int) infoId + "");
        volley_Post("application/getApplicationByInfoId", req);
    }

    /**
     * 向服务器请求申请者列表
     * @param url_add   添加的请求url
     * @param req       请求数据
     */
    private void volley_Post(final String url_add, final Map<String,String> req)
    {
        String url = getString(R.string.url);
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                //请求成功
                Map<String,Object> response = gson.fromJson(s,Map.class);

                List<Map<String, Object>> result = new ArrayList<>();
                if((boolean) response.get("result"))
                {
                    result = new DataAnalysis().listKeyMaps(response.get("applicantList").toString());
                }
                list.addAll(result);
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                //请求失败处理
                Toast.makeText(SignUpInfoActivity.this, "连接服务器失败，请刷新重试！", Toast.LENGTH_LONG).show();
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
     * 自定义Adapter
     */
    private class SignUpInfoAdapter extends SimpleAdapter
    {
        private Context context;
        private int resource;
        public SignUpInfoAdapter(Context context, List<Map<String, Object>> data, int resource, String[] from, int[] to)
        {
            super(context, data, resource, from, to);
            this.context = context;
            this.resource = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LinearLayout newView;

            Map<String, Object> item = (Map<String, Object>)getItem(position);

            if(convertView == null)
            {
                newView = new LinearLayout(context);
                String inflater = Context.LAYOUT_INFLATER_SERVICE;
                LayoutInflater li;
                li = (LayoutInflater)context.getSystemService(inflater);
                li.inflate(resource, newView, true);
            }
            else
            {
                newView = (LinearLayout)convertView;
            }

            RoundImageView personHead = (RoundImageView)newView.findViewById(R.id.person_head);
            TextView name = (TextView)newView.findViewById(R.id.name);
            TextView school = (TextView)newView.findViewById(R.id.school);
            LinearLayout ll = (LinearLayout)newView.findViewById(R.id.identity);

            //显示头像
            double id = (double)item.get("ID");
            String url = context.getResources().getString(R.string.url) + "public/images/" + (int)id + ".png";
            netWorkImageViewVolley(url, personHead);

            //判断是否已经通过认证
            int identify = (int)(double)item.get("IDENTIFY_STATE");
            //若未认证则显示未认证
            if(identify == RealInfo.STATE_IS_CHECKING || identify == User.REALINFO_VERIFY_NO)
            {
                name.setText("未认证");
                ll.setVisibility(View.GONE);
            }
            //若已认证则请求该用户的真实信息
            else
            {
                Map<String, Object> temp = (Map) item.get("REALINFO");
                name.setText(temp.get("NAME").toString());
                school.setText(temp.get("SCHOOL").toString());
            }
            return newView;
        }
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
}
