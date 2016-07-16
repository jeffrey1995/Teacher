
package hc.teacher.fragment;

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.jingchen.pulltorefresh.PullableListView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hc.teacher.adapter.MyFrienAadapter;
import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.entity.User;

import hc.teacher.utils.MethodUtils;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Created by Administrator on 2015/10/19 0019.
 */
public class Circle_AddressBookFragment extends Fragment implements RongIM.UserInfoProvider{
    Gson gson = MethodUtils.getGson();
    PullableListView listView;
    PullToRefreshLayout refreshView;
    boolean isRefreshing = false;
    MyFrienAadapter myFriendAdapter;
    List<Map<String,Object>> listItems = new ArrayList<Map<String, Object>>();
    String from_02[] = {"HEAD","NICKNAME","DESCRIPTION"};
    int to_02[] = {R.id.friend_image,R.id.friend_name,R.id.friend_note,R.id.delete_img,R.id.friend_rl};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fra_circle_addressbook,null);
        myFriendAdapter = new MyFrienAadapter(view.getContext(),listItems,R.layout.circle_listview02,from_02,to_02);
        myFriendAdapter.setCircle_AddressBookFragment(this);
        refreshView = (PullToRefreshLayout) view.findViewById(R.id.rl_refresh);
        listView = (PullableListView) view.findViewById(R.id.listView);

        listView.setAdapter(myFriendAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView textView = (TextView) view.findViewById(R.id.friend_name);
//                String name = textView.getText().toString();
                Map<String, Object> map = listItems.get(position);
                String ID = map.get("ID").toString();
                System.out.println("ID_________" + ID);
                if (RongIM.getInstance() != null) {
                    RongIM.getInstance().startPrivateChat(getActivity(), ID, null);
                }
            }
        });
//        listView.setLoadEnable(false);

//        listView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                new AsyncTask<Void, Void, Void>() {
//                    @Override
//                    protected Void doInBackground(Void... params) {
//                        refresh();
//                        return null;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Void aVoid) {
//                        listView.onRefreshComplete();
//                    }
//                }.execute(null, null, null);
//            }
//        });

        refresh();
        
        refreshView.setOnPullListener(new PullToRefreshLayout.OnPullListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                isRefreshing = true;
                refresh();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

            }
        });
        RongIM.setUserInfoProvider(this, true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("friendFragment", "onstart");
    }

    private void stopRefreshing(int refreshResult){
        if(isRefreshing){
            refreshView.refreshFinish(refreshResult);
            isRefreshing = false;
        }
    }

    public void refresh() {
        Map<String,String> req = new HashMap<String,String>();
        String user_id = "";
        try {
            User user = MethodUtils.getLocalUser();
            user_id = user.getId().toString();

        } catch (ParseException e) {
            Log.i("getRongConnection","failed");
            stopRefreshing(PullToRefreshLayout.FAIL);
            e.printStackTrace();
        }
        req.put("first_id",user_id);
        volley_Post("circle/getAllFriend", req);
    }


    private void volley_Post(String url_add, final Map<String, String> req) {
        String url = getString(R.string.url);
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                List<Map<String,Object>> response = gson.fromJson(s,List.class);
                listItems.clear();
                if (response.size() != 0)
                {
                    for(int i=0;i<response.size();i++)
                    {
                        double id_double = (Double) response.get(i).get("ID");
                        response.get(i).put("ID",(int)id_double);
                        response.get(i).put("HEAD", getString(R.string.url) + response.get(i).get("HEAD").toString());
                    }
                    listItems.addAll(response);
                    myFriendAdapter.notifyDataSetChanged();
                    Log.w("cry","volly post 后，以成功状态结束刷新");
                    stopRefreshing(PullToRefreshLayout.SUCCEED);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //请求失败处理
                Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_LONG).show();
                stopRefreshing(PullToRefreshLayout.FAIL);
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


    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
        if (pos < firstListItemPosition || pos > lastListItemPosition )
        {
            return listView.getAdapter().getView(pos, null, listView);}
        else
        {
            return null;
        }
    }

    @Override
    public UserInfo getUserInfo(String s) {
        Log.e("Friend id   :",s);
        //设置融云聊天好友信息
        for(Map<String,Object> i : listItems)
        {
            if(i.get("ID").toString().equals(s))
            {
                Log.e("Friend   :",i.toString());
                return new io.rong.imlib.model.UserInfo(i.get("ID").toString(),i.get("NICKNAME").toString(), Uri.parse(i.get("HEAD").toString()));
            }
        }
        //设置融云聊天当前用户信息
        try {
            Log.e("getLocalUser   :","...............");
            User user = MethodUtils.getLocalUser();
            Log.e("getLocalUserId   :",user.getId().toString());
            if(user.getId().toString().equals(s))
            {
                Log.e("LocalUser   :",user.toString());
                return new io.rong.imlib.model.UserInfo(user.getId().toString(),user.getNickname(),Uri.parse(getString(R.string.url)+user.getHead()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}

