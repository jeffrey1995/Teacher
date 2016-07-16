package hc.teacher.activity_homepage;


import android.app.Fragment;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.entity.Answer;
import hc.teacher.entity.Question;
import hc.teacher.utils.MethodUtils;

import static android.support.v7.widget.LinearLayoutManager.*;

public class CheckAnswerFragment extends Fragment
{

    final Gson gson = MethodUtils.getGson();
    RecyclerView listView;
    CheckQuestionAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //填充布局
        View view = inflater.inflate(R.layout.fra_answer_online3, null);

        Map<String, String> map = new HashMap<String, String>();

        try {
            map.put("user_id", MethodUtils.getLocalUser().getId().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        volley_Post("question/getQuestionsByUserID", map);

        //初始化listView
        listView = (RecyclerView) view.findViewById(R.id.question_list);
        listView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter = new CheckQuestionAdapter();
        listView.setAdapter(adapter);

        return view;
    }

    public void volley_Post(String url_add, final Map<String, String> req) {
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
                for(int i = 0; i < result.size(); i ++){
                    Question question = Question.getQuestionByMap(result.get(i));
                    questions.add(question);
                }
                adapter.setQuestions(questions);
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //请求失败处理
                System.out.println("wrong");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                System.out.println(req);
                return req;
            }
        };
        request.setTag("doPost");
        MyApplication.getHttpQueue().add(request);
    }

    /**
     * 自定义adapter
     */

    class CheckQuestionAdapter extends RecyclerView.Adapter{

        public void setQuestions(List<Question> questions) {
            this.questions = questions;
        }

        private List<Question> questions = new ArrayList<Question>();

        class ViewHolder extends RecyclerView.ViewHolder{

            public TextView getSummary() {
                return summary;
            }

            public ImageView getState() {
                return state;
            }

            private TextView summary;
            private ImageView state;

            public ViewHolder(View root) {
                super(root);
                summary = (TextView) root.findViewById(R.id.summary);
                state = (ImageView) root.findViewById(R.id.state);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_answer_question_list, parent, false);
            return new ViewHolder(root);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            Question question = questions.get(position);
            viewHolder.getSummary().setText(question.getDescription());
            if (question.getState() == 1){
                viewHolder.getState().setImageResource(R.drawable.already_answer);
            }
            else if(question.getState() == 2){
                viewHolder.getState().setImageResource(R.drawable.wait_to_answer);
            }
        }

        @Override
        public int getItemCount() {
            return questions.size();
        }
    }
}