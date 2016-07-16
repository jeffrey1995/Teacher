package hc.teacher.activity_homepage;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hc.teacher.adapter.WaitAnswerAdapter;
import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.entity.Question;
import hc.teacher.user_defined_widget.PullToRefreshListView;
import hc.teacher.utils.MethodUtils;

/*
* 下拉刷新：每次刷新网络请求服务器中最新的十条数据替换本地的所有数据
* 上拉加载：每次加载网络请求服务器中十条旧数据填充到本地数据中，但有个最大值
* */

public class WaitAnswerFragment extends Fragment
{
    private PullToRefreshListView listView;
    private WaitAnswerAdapter adapter;
    private String url = "question/getQuestions";
    final Gson gson = MethodUtils.getGson();
    List<Question> list = new ArrayList<Question>();
    /*
    * 判断是否有新的数据
    * */
    private boolean isChanged = false;
    /*
    * DEFAULT:初始数据（10条）
    * REFRESH:下拉刷新
    * DOWNLOAD:上拉加载
    * */
    private final static int DEFAULT = 0;
    private final static int REFRESH = 1;
    private final static int DOWNLOAD = 2;
    private int state = DEFAULT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //填充布局
        View view = inflater.inflate(R.layout.fra_answer_online1, null);
        getData(url, null);
        //初始化listView
        listView = (PullToRefreshListView)view.findViewById(R.id.question_list);
        listView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        //访问数据库得到最新的消息记录
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        state = REFRESH;
                        //下拉刷新网络请求
                        getData("question/getQuestions", null);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        //刷新结束改变数据
                        if (isChanged == true) {
                            adapter.notifyDataSetChanged();
                        }
                        isChanged = false;
                        listView.onRefreshComplete();
                    }
                }.execute(null, null, null);
            }
        });
        listView.setOnLoadListener(new PullToRefreshListView.OnLoadListener() {
            @Override
            public void onLoad() {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        //访问数据库得到最新的消息记录
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        state = DOWNLOAD;
                        Map<String, String> data = new HashMap<String, String>();
                        String s = ((Question)adapter.getItem(adapter.getCount() - 1)).getPublish_time().toString();
                        data.put("publish_time", s.substring(0, s.length() - 2));

                        System.out.println(s.substring(0, s.length() - 2));
                        getData("question/getOlderQuestions", data);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        //刷新结束改变数据
                        if (isChanged == true) {
                            adapter.notifyDataSetChanged();
                        }
                        isChanged = false;
                        listView.onLoadComplete();
                    }
                }.execute(null, null, null);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), AnswerListActivity.class);
                Bundle bundle = new Bundle();
                System.out.println("position" + position);
                bundle.putSerializable("Question", (Question) adapter.getItem(position - 1));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        adapter = new WaitAnswerAdapter(getActivity(), list);
        listView.setAdapter(adapter);
        return view;
    }

    /*
    * Get Data
    * */

    void getData(String url_add,final Map<String,String> req){
        String url = getString(R.string.url);
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                System.out.println("success!");
                System.out.println("Get Answer" + s);
                System.out.println(s);
                Map<String, Object> map = gson.fromJson(s, HashMap.class);
                List<Map<String, Object>> result = gson.fromJson(map.get("questionList").toString(),List.class);
                List<Question> questions = new ArrayList<Question>();
                switch (state){
                    case DEFAULT:
                        for(int i = 0; i < result.size(); i ++){
                            Question question = Question.getQuestionByMap(result.get(i));
                            questions.add(question);
                        }
                        adapter.setList(questions);
                        adapter.notifyDataSetChanged();
                        break;
                    case REFRESH:
                        if (result.isEmpty() == false) {
                            isChanged = true;
                            for (int i = 0; i < result.size(); i++) {
                                Question question = Question.getQuestionByMap(result.get(i));
                                questions.add(question);
                            }
                            adapter.setList(questions);
                        }
                        /*adapter.notifyDataSetChanged();*/
                        break;
                    case DOWNLOAD:
                        if (result.isEmpty() == false) {
                            isChanged = true;
                            for (int i = 0; i < result.size(); i++) {
                                Question question = Question.getQuestionByMap(result.get(i));
                                questions.add(question);
                                System.out.println(question.getId());
                            }
                            adapter.addList(questions);
                        }
                        /*adapter.notifyDataSetChanged();*/
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //请求失败处理
                System.out.println("wrong");
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return req;
            }
        };
        request.setTag("doPost");
        MyApplication.getHttpQueue().add(request);
    }
}