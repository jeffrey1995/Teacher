package hc.teacher.activity_homepage;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hc.teacher.adapter.ViewAdapter;
import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.entity.Answer;
import hc.teacher.entity.Question;
import hc.teacher.utils.MethodUtils;

public class AnswerListActivity extends ListActivity {

    private int questionOwner;
    final Gson gson = MethodUtils.getGson();

    public static final int MINE = 0;
    public static final int OTHER = 1;

    private ListView listView;
    private Intent intent;
    private Question question;
    private ViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_list);

        listView = getListView();
        /*
        * Get intent and perform volley method, get answer list data
        * */
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        question = (Question) bundle.getSerializable("Question");

        getOwner();
        /*System.out.println("questionOwner" + questionOwner);*/
        Map<String, String> map = new HashMap<>();
        map.put("question_id", question.getId().toString());
        volley_Post("answer/getAnswerByQuestionId", map);

        /*
        不能使用Bundle
        Bundle owner = new Bundle();
        bundle.putInt("Owner", questionOwner);
        System.out.println(questionOwner + "ccccccccccccccccccccccc");*/

        adapter = new ViewAdapter(this, questionOwner, question, null,
                new int[]{
                        R.id.user,
                        R.id.questionContent,

                        R.id.myAnswerContent,
                        R.id.submitAnswer,

                        R.id.answerSqe,
                        R.id.isGet,
                        R.id.answerContent,
                        R.id.seeDetail,
                        R.id.furtherAsk
                });

        listView.setAdapter(adapter);
    }

    //Judge who the quetion is belonged to, Mine or Other
    private void getOwner() {
        String id = question.getUser_id().toString();
        String localId = null;
        try {
            localId = MethodUtils.getLocalUser().getId().toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("id" + id);
        System.out.println("localId" + localId);
        System.out.println(id == localId);
        if (id.equals(localId)) {
            questionOwner = MINE;
        } else {
            questionOwner = OTHER;
        }
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
                System.out.println(map);
                List<Map<String, Object>> result = gson.fromJson(map.get("answerList").toString(), List.class);
                System.out.println("result" + result);
                List<Answer> answers = new ArrayList<Answer>();
                for (int i = 0; i < result.size(); i ++){
                    answers.add(Answer.getAnswerByMap(result.get(i)));
                }
                System.out.println(answers);
                isChecked(answers);
                adapter.setMessage(answers);
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

    private void isChecked(List<Answer> answers){
        for(int i = 0; i < answers.size(); i ++){
            if (answers.get(i).getIsAccepted() == 1){
                Answer answer = answers.get(i);
                answers.remove(i);
                answers.add(0, answer);
                System.out.println(answer.getId() + "C");

                adapter.isChecked = true;
                return;
            }
        }
    }
}