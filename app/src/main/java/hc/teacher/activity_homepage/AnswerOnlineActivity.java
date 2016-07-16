package hc.teacher.activity_homepage;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import hc.teacher.application.R;

public class AnswerOnlineActivity extends Activity implements View.OnClickListener
{
    private TextView wait;      //等你回答
    private TextView ask;       //我要问题
    private TextView answer;    //查看答案

    private Fragment fragment1, fragment2, fragment3, current;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_online);

        wait = (TextView) findViewById(R.id.wait);
        ask = (TextView) findViewById(R.id.ask);
        answer = (TextView) findViewById(R.id.answer);
        wait.setTextColor(getResources().getColor(R.color.theme_color));

        getFragment();
        wait.setOnClickListener(this);
        ask.setOnClickListener(this);
        answer.setOnClickListener(this);

        findViewById(R.id.btn_return).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AnswerOnlineActivity.this.finish();
            }
        });
    }

    /**
     * 初始化各个fragment，当前的设为等你回答界面
     */
    private void getFragment()
    {
        fragment1 = new WaitAnswerFragment();
        fragment2 = new ToAskFragment();
        fragment3 = new CheckAnswerFragment();
        fm = getFragmentManager();
        fm.beginTransaction().add(R.id.main_pane, fragment1).commit();
        current = fragment1;
    }

    @Override
    public void onClick(View v)
    {
        //点击切换到对应fragment
        switch(v.getId())
        {
        case R.id.wait:
            switchContent(current, fragment1);
            wait.setTextColor(getResources().getColor(R.color.theme_color));
            ask.setTextColor(Color.BLACK);
            answer.setTextColor(Color.BLACK);
            break;
        case R.id.ask:
            switchContent(current, fragment2);
            wait.setTextColor(Color.BLACK);
            ask.setTextColor(getResources().getColor(R.color.theme_color));
            answer.setTextColor(Color.BLACK);
            break;
        case R.id.answer:
            switchContent(current, fragment3);
            wait.setTextColor(Color.BLACK);
            ask.setTextColor(Color.BLACK);
            answer.setTextColor(getResources().getColor(R.color.theme_color));
            break;
        }
    }

    /**
     * 切换fragment
     * @param from  当前fragment
     * @param to    要切换到的fragment
     */
    public void switchContent(Fragment from, Fragment to)
    {
        FragmentTransaction transaction = fm.beginTransaction();
        if (current != to)
        {
            current = to;
            if (!to.isAdded())
            {
                // 先判断是否被add过
                transaction.hide(from);
                transaction.add(R.id.main_pane, to);
                transaction.commit();           // 隐藏当前的fragment，add下一个到Activity中
            }
            else
            {
                transaction.hide(from);         // 隐藏当前的fragment，显示下一个
                transaction.show(to);
                transaction.commit();
            }
        }
    }
}