package hc.teacher.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import hc.teacher.activity_homepage.AnswerListActivity;
import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.entity.Answer;
import hc.teacher.entity.Question;
import hc.teacher.utils.MethodUtils;

/**
 * Created by star on 2016/1/9.
 */
public class ViewAdapter extends BaseAdapter{
    final Gson gson = MethodUtils.getGson();

    private List<Answer> answers;
    private Question question;
    private LayoutInflater layoutInflater;
    private Context context;
    private int[] subViewId;

    public void setMessage(List<Answer> answers){
        this.answers = answers;
        notifyDataSetChanged();
    }
    /*
    * Save three layout Id(R.layout. )
    * */
    //private int[] resouceIds;

    /*
    * questionOwner's value
    * public static final int MINE = 0;
    * public static final int OTHER = 1;
    * */
    private int questionOwner;

    /*
    * Sava first answer sequence,there're two cases:
    * First: It is my question, the value is 1
    * second: It is other's question, the value is 0
    * */
    private int firstAnswerSeq;

    //Save all View reference
    public boolean isChecked = false;

    /*
    * The reference of all views in item_view_layout3 xml file
    * */
    private Item3ViewHolder item3ViewHolder;

    /*
    * The reference of all views in item_view_layout2 xml file
    * */
    private Item2ViewHolder item2ViewHolder;

    /*
    * The reference of all views in item_view_layout2 xml file
    * */
    private Item1ViewHolder item1ViewHolder;


    class Item1ViewHolder{
        TextView user;
        TextView questionContent;
    }

    class Item2ViewHolder{
        EditText myAnswerContent;
        Button submitAnswer;
    }

    class Item3ViewHolder {
        TextView answerSqe;
        TextView isGet;
        TextView answerContent;
        Button seeDetail;
        Button furTherAsk;

        /*
        * 4R.id.answerSqe,
        * 5R.id.isGet,
        * 6R.id.answerContent,
        * 7R.id.seeDetail,
        * 8R.id.furtherAsk
        * */
    }

    public ViewAdapter(Context context, int questionOwner, Question question,
                       List<Answer> answers, int[] subViewId){
        this.context = context;

        /*questionOwner = bundle.getInt("Owner");*/
        System.out.println("questionOwner" + questionOwner);
        switch (questionOwner){
            case AnswerListActivity.MINE:
                firstAnswerSeq = 1;
                break;
            case AnswerListActivity.OTHER:
                firstAnswerSeq = 2;
                break;
        }
        System.out.println(firstAnswerSeq);
        this.question = question;

        if (answers != null) {
            this.answers = answers;
        }
        else{
            this.answers = new ArrayList<Answer>();
        }
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.subViewId = new int[subViewId.length];

        System.arraycopy(subViewId, 0, this.subViewId, 0, subViewId.length);
    }

    @Override
    public int getCount() {
        return answers.size() + firstAnswerSeq;
    }

    @Override
    public Object getItem(int position) {
        return answers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position > firstAnswerSeq - 1){
            convertView = layoutInflater.inflate(R.layout.item_view_layout3, null);
            item3ViewHolder = new Item3ViewHolder();
            item3ViewHolder.answerSqe = (TextView)convertView.findViewById(subViewId[4]);
            item3ViewHolder.isGet = (TextView)convertView.findViewById(subViewId[5]);
            if (isChecked == true && position == firstAnswerSeq){
                item3ViewHolder.isGet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checked, 0, 0, 0);
            }
            item3ViewHolder.answerContent = (TextView) convertView.findViewById(subViewId[6]);
            item3ViewHolder.seeDetail = (Button) convertView.findViewById(subViewId[7]);
            item3ViewHolder.furTherAsk = (Button)convertView.findViewById(subViewId[8]);
            convertView.setTag(convertView);
            item3ViewHolder.isGet.setOnClickListener(new ViewClickListener(position));
            item3ViewHolder.seeDetail.setOnClickListener(new ViewClickListener(position));
            item3ViewHolder.answerSqe.setText("答案" + (position - firstAnswerSeq) + " :");
            Answer textContent = answers.get(position - (firstAnswerSeq - 1) - 1);
            if(textContent != null){
                String text = textContent.getDetails();
                item3ViewHolder.answerContent.setText(text);
            }
        }
        else{
            if (position == 0){
                /*
                * getQuestion()
                * */
                convertView = layoutInflater.inflate(R.layout.item_view_layout1, null);
                item1ViewHolder = new Item1ViewHolder();
                item1ViewHolder.user = (TextView)convertView.findViewById(subViewId[0]);
                item1ViewHolder.questionContent = (TextView)convertView.findViewById(subViewId[1]);
                convertView.setTag(item1ViewHolder);
                if(question != null){
                    item1ViewHolder.user.setText(question.getNickname());
                    String text = question.getDetails();
                    item1ViewHolder.questionContent.setText(text);
                }

            }
            else{
                /*
                * getMyAnswer()
                * */
                convertView = layoutInflater.inflate(R.layout.item_view_layout2, null);
                item2ViewHolder = new Item2ViewHolder();
                item2ViewHolder.myAnswerContent = (EditText)convertView.findViewById(subViewId[2]);
                item2ViewHolder.submitAnswer = (Button)convertView.findViewById(subViewId[3]);
                convertView.setTag(item2ViewHolder);
                item2ViewHolder.myAnswerContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setFocusable(true);
                    }
                });
                item2ViewHolder.submitAnswer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*
                        * 获得焦距并隐藏软键盘
                        * */
                        item2ViewHolder.myAnswerContent.setFocusable(true);
                        InputMethodManager inputMethodManager =
                                (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(item2ViewHolder.myAnswerContent.getWindowToken(), 0);
                        try {
                            int localId = MethodUtils.getLocalUser().getId();
                            if (localId != 0 ){
                                String s = item2ViewHolder.myAnswerContent.getText().toString();
                                if (s != null) {
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("question_id", question.getId().toString());
                                    map.put("user_id", question.getUser_id().toString());
                                    map.put("details", s);
                                    volleyAnswerPost("answer/create", map);
                                }
                                else {
                                    Toast.makeText(context, "请填写答案", Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                Toast.makeText(context, "请先登录", Toast.LENGTH_LONG).show();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        return convertView;
    }

    class ViewClickListener implements View.OnClickListener{

        private int position;
        private boolean isGet = false;

        public ViewClickListener(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.isGet:
                    if(isGet == false) {
                        if (isChecked == false) {
                            ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(R.drawable.checked, 0, 0, 0);
                            isChecked = true;
                            Answer answer = answers.get(position - firstAnswerSeq);
                            answers.remove(position - firstAnswerSeq);
                            answers.add(0, answer);
                            System.out.println("A" + answer.getId());
                            System.out.println("B" + answers.get(0).getId());
                            notifyDataSetChanged();
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("id", answer.getId().toString());
                            map.put("question_id", answer.getQuestion_id().toString());
                            volleyGetPost("answer/accept", map);
                            /*
                            * volley
                            * */
                        }
                    }
                    break;
                case R.id.seeDetail:
                    Toast.makeText(context, "aaaaa", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void volleyGetPost(String url_add, final Map<String, String> req) {
        String url = "http://192.168.11.17:8080/";
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                System.out.println("success!");
                System.out.println("Get Answer" + s);
                System.out.println(s);
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

    public void volleyAnswerPost(String url_add, final Map<String, String> req) {
        String url = "http://192.168.11.17:8080/";
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                System.out.println("success!");
                System.out.println("Get Answer" + s);
                System.out.println(s);
                Map<String, Object> map = gson.fromJson(s, HashMap.class);

                Map<String, Object> result = gson.fromJson(map.get("answer").toString(), Map.class);
                System.out.println("result" + result);

                Answer answer = Answer.getAnswerByMap(result);
                System.out.println("Answer:" + answer);
                answers.add(answer);
                notifyDataSetChanged();

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
}