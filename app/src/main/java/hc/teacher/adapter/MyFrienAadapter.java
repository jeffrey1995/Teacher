package hc.teacher.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.components.dialog.ConfirmDialog;
import hc.teacher.components.dialog.ModifyDialogListener;
import hc.teacher.entity.User;
import hc.teacher.fragment.Circle_AddressBookFragment;
import hc.teacher.user_defined_widget.RoundImageView;
import hc.teacher.utils.MethodUtils;

/**
 * Created by mrtian on 2015/11/9 0009.
 */
public class MyFrienAadapter extends SimpleAdapter {
    private Gson gson = MethodUtils.getGson();
    private LayoutInflater mInflater;
    private List<Map<String, Object>> list;
    private int layoutID;
    private String flag[];
    private int ItemIDs[];
    private Context context;
    Circle_AddressBookFragment circle_AddressBookFragment;

    public Circle_AddressBookFragment getCircle_AddressBookFragment() {
        return circle_AddressBookFragment;
    }

    public void setCircle_AddressBookFragment(Circle_AddressBookFragment circle_AddressBookFragment) {
        this.circle_AddressBookFragment = circle_AddressBookFragment;
    }



    public MyFrienAadapter(Context context, List<Map<String, Object>> list,
                           int layoutID, String flag[], int ItemIDs[]) {
        super(context,list,layoutID,flag,ItemIDs);
        Log.i("TAG", "构造方法");
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.list = list;
        this.layoutID = layoutID;
        this.flag = flag;
        this.ItemIDs = ItemIDs;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(layoutID, null);
        for (int i = 0; i < flag.length; i++) {//备注1
            if (convertView.findViewById(ItemIDs[i]) instanceof RoundImageView) {
                RoundImageView iv = (RoundImageView) convertView.findViewById(ItemIDs[i]);
//                iv.setBackgroundResource((Integer) list.get(position).get(
//                        flag[i]));
                netWorkImageViewVolley(list.get(position).get(flag[i]).toString(), iv);
            }
            else if (convertView.findViewById(ItemIDs[i]) instanceof TextView) {
                TextView tv = (TextView) convertView.findViewById(ItemIDs[i]);
                tv.setText((String) list.get(position).get(flag[i]));
            }
            else if (convertView.findViewById(ItemIDs[i]) instanceof ImageView)
            {
                ImageView iv = (ImageView)convertView.findViewById(ItemIDs[i]);
                addListener(iv,position);
            }
            else {
                //...备注2
                Log.i("TAG", "else");
            }
        }
        addListener(convertView,position);
        return convertView;
    }

    /**
     * 只需要将需要设置监听事件的组件写在下面这方法里就可以啦！
     *
     * 备注3
     */
    public void addListener(final View convertView, final int position) {
        ((ImageView)convertView.findViewById(R.id.delete_img)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDialog confirmDialog = new ConfirmDialog(convertView.getContext(), new ModifyDialogListener() {
                    @Override
                    public void onResponse(boolean result) {
                        if (result)
                        {
                            try {
                                User user = MethodUtils.getLocalUser();
                                if (user != null)
                                {
                                    Map<String,String> req = new HashMap<String, String>();
                                    req.put("first_id",user.getId().toString());
                                    req.put("second_id",list.get(position).get("ID").toString());
                                    Log.e("deletefriend_req",req.toString());
                                    volley_Post("circle/delete", req, convertView.getContext());
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                confirmDialog.setTitle("是否删除该好友?").show();
            }
        });
    }
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

    private void volley_Post(String url_add, final Map<String, String> req, final Context context) {
        String url = context.getString(R.string.url);
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                try {
                    Map<String,Object> response = gson.fromJson(s,Map.class);
                    if (response.get("result").toString().equals("0"))
                    {
                        Toast.makeText(context.getApplicationContext(), "删除好友成功", Toast.LENGTH_LONG).show();
                        circle_AddressBookFragment.refresh();
                    }
                    else
                    {
                        Toast.makeText(context.getApplicationContext(), "删除好友失败", Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e)
                {
                    Log.e("deletefriend",e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //请求失败处理
                Toast.makeText(context.getApplicationContext(), "网络错误", Toast.LENGTH_LONG).show();
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
