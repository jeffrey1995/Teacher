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
import hc.teacher.user_defined_widget.RoundImageView;
import hc.teacher.utils.MethodUtils;

/**
 * Created by mrtian on 2016/3/14.
 */
public class MyTaskAdapter extends SimpleAdapter {
    private Gson gson = MethodUtils.getGson();
    private LayoutInflater mInflater;
    private List<Map<String, Object>> list;
    private int layoutID;
    private String from[];
    private int to[];
    private Context context;

    /**
     * Constructor
     *
     * @param context  The context where the View associated with this SimpleAdapter is running
     * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
     *                 Maps contain the data for each row, and should include all the entries specified in
     *                 "from"
     * @param layoutID Resource identifier of a view layout that defines the views for this list
     *                 item. The layout file should include at least those named views defined in "to"
     * @param from     A list of column names that will be added to the Map associated with each
     *                 item.
     * @param to       The views that should display column in the "from" parameter. These should all be
     *                 TextViews. The first N views in this list are given the values of the first N columns
     */
    public MyTaskAdapter(Context context, List<Map<String, Object>> data, int layoutID, String[] from, int[] to) {
        super(context, data, layoutID, from, to);
        Log.i("TAG", "构造方法");
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.list = data;
        this.layoutID = layoutID;
        this.from = from;
        this.to = to;
    }

    /**
     * 复写getView方法，添加按钮监听和图片加载
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(layoutID, null);
        for (int i = 0; i < from.length; i++) {
            if (convertView.findViewById(to[i]) instanceof TextView) {
                TextView tv = (TextView) convertView.findViewById(to[i]);
                tv.setText((String) list.get(position).get(from[i]));
            } else if (convertView.findViewById(to[i]) instanceof RoundImageView) {
                //这里是融云自定义图片，加载图片
                RoundImageView riv = (RoundImageView) convertView.findViewById(to[i]);
                netWorkImageViewVolley(list.get(position).get(from[i]).toString(),riv);

            } else if (convertView.findViewById(to[i]) instanceof ImageView) {
                ImageView im = (ImageView) convertView.findViewById(to[i]);
            } else {
                //其他情况
                Log.i("MaTask_getView", "else");
            }
        }
        return convertView;
    }

    /**
     * 给控件添加监听事件
     * @param convertView
     * @param position
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

    /**
     * 融云控件请求图片函数
     * @param imageUrl
     * @param personHead
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

    /**
     * volley网络请求函数
     * @param url_add
     * @param req
     * @param context
     */
    private void volley_Post(String url_add, final Map<String, String> req, final Context context) {
        String url = context.getString(R.string.url);
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
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
