package hc.teacher.activity_homepage;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.utils.MethodUtils;

public class ToAskFragment extends Fragment
{
    private ImageView checkbox1, checkbox2;
    private boolean checked1, checked2;
    private EditText questionSum, questionDet;
    private Button submitQuestion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //填充布局
        View view = inflater.inflate(R.layout.fra_answer_online2, null);

        checkbox1 = (ImageView)view.findViewById(R.id.checkbox1);
        checkbox2 = (ImageView)view.findViewById(R.id.checkbox2);
        checkbox1.setOnClickListener(new MyImageViewClickListener());
        checkbox2.setOnClickListener(new MyImageViewClickListener());

        questionSum = (EditText)view.findViewById(R.id.question_summary);
        questionDet = (EditText)view.findViewById(R.id.question_detail);
        submitQuestion = (Button)view.findViewById(R.id.submit_question);
        submitQuestion.setOnClickListener(new MyImageViewClickListener());

        return view;
    }

    private class MyImageViewClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            switch(v.getId())
            {
            case R.id.checkbox1:
                if(checked1)
                {
                    checked1 = false;
                    checkbox1.setImageResource(R.drawable.unchecked);
                }
                else
                {
                    checked1 = true;
                    checkbox1.setImageResource(R.drawable.checked);
                }
                break;
            case R.id.checkbox2:
                if(checked2)
                {
                    checked2 = false;
                    checkbox2.setImageResource(R.drawable.unchecked);
                }
                else
                {
                    checked2 = true;
                    checkbox2.setImageResource(R.drawable.checked);
                }
                break;
                case R.id.submit_question:
                    try {
                        int  user_id = MethodUtils.getLocalUser().getId();
                        if(user_id != 0){
                            if (questionSum.getText().toString() == null){
                                Toast.makeText(getActivity(), "请填写问题概述", Toast.LENGTH_SHORT).show();
                            }
                            else if (questionDet.getText().toString() == null){
                                Toast.makeText(getActivity(),"请填写问题描述",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                HashMap<String, String> map = new HashMap<String, String>();
                                try {
                        /*
                        * It is best to new a question object
                        * */
                                    int userId = MethodUtils.getLocalUser().getId();
                                    map.put("user_id", userId + "");
                                    map.put("description", questionSum.getText().toString());
                                    map.put("details", questionDet.getText().toString());
                                    map.put("publish_time", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                                    //map.put("invalidate_time", "30 days");
                                    map.put("nickname", MethodUtils.getLocalUser().getNickname());
                                    System.out.println(map);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                volley_Post("question/insertQuestion", map);

                            }
                        }
                        else{
                            Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_LONG).show();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    public void volley_Post(String url_add,final Map<String,String> req){
        String url = getString(R.string.url);
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                System.out.println("success!");
                System.out.println(s);
                Toast.makeText(getActivity(), "问题提交成功", Toast.LENGTH_LONG);
                questionSum.setText("");
                questionDet.setText("");
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
