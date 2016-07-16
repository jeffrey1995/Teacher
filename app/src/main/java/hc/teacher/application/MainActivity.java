package hc.teacher.application;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import hc.teacher.fragment.CircleFragment;
import hc.teacher.fragment.HomepageFragment;
import hc.teacher.fragment.Person_centerFragment;
import hc.teacher.fragment.TeacherCenterFragment;

public class MainActivity extends Activity implements OnClickListener
{
    private LinearLayout homepageLayout,teacher_centerLayout,circleLayout,person_centerLayout;
    private TextView textView01,textView02,textView03,textView04;
    private ImageView imageView01,imageView02,imageView03,imageView04;
    private Fragment homepage,teacher_center,circle,person_center,current;
    private FragmentManager mFragmentMan;
    private static Boolean isExit = false;// 回退两次退出应用

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        getFragment();
    }

    public Fragment getTeacherCenter()
    {
        return this.teacher_center;
    }

    /**
     * 由其他fragment向TeacherCenter跳转时传递数据
     * @param text  要向TeacherCenter传递的数据
     */
    public void setTeacherCenterArgument(String text)
    {
        this.teacher_center = TeacherCenterFragment.newInstance(text);
    }


    public void initView()
    {
        homepageLayout = (LinearLayout)findViewById(R.id.homepage_ll);
        teacher_centerLayout = (LinearLayout)findViewById(R.id.teacher_center_ll);
        circleLayout = (LinearLayout)findViewById(R.id.circle_ll);
        person_centerLayout = (LinearLayout)findViewById(R.id.person_center_ll);

        imageView01 = (ImageView)findViewById(R.id.mainImageView01);
        imageView02 = (ImageView)findViewById(R.id.mainImageView02);
        imageView03 = (ImageView)findViewById(R.id.mainImageView03);
        imageView04 = (ImageView)findViewById(R.id.mainImageView04);

        textView01 = (TextView)findViewById(R.id.mainTextView01);
        textView02 = (TextView)findViewById(R.id.mainTextView02);
        textView03 = (TextView)findViewById(R.id.mainTextView03);
        textView04 = (TextView)findViewById(R.id.mainTextView04);

        imageView01.setBackgroundResource(R.drawable.homepage_selected);
        imageView02.setBackgroundResource(R.drawable.tutor_center_unselected);
        imageView03.setBackgroundResource(R.drawable.circle_unselected);
        imageView04.setBackgroundResource(R.drawable.person_center_unselected);

        textView01.setTextColor(getApplication().getResources().getColor(R.color.theme_color));
        textView02.setTextColor(getApplication().getResources().getColor(R.color.main_chars));
        textView03.setTextColor(getApplication().getResources().getColor(R.color.main_chars));
        textView04.setTextColor(getApplication().getResources().getColor(R.color.main_chars));

        homepageLayout.setOnClickListener(this);
        teacher_centerLayout.setOnClickListener(this);
        circleLayout.setOnClickListener(this);
        person_centerLayout.setOnClickListener(this);
    }
    public void getFragment()
    {
        mFragmentMan = getFragmentManager();
        homepage = new HomepageFragment();
        teacher_center = new TeacherCenterFragment();
        circle = new CircleFragment();
        person_center = new Person_centerFragment();
        mFragmentMan.beginTransaction().add(R.id.main_middle,homepage).commit();
        current = homepage;
    }
    @Override
    public void onClick(View arg0)
    {
        switch (arg0.getId())
        {
            case R.id.homepage_ll:
                switchContent(current, homepage);
                imageView01.setBackgroundResource(R.drawable.homepage_selected);
                imageView02.setBackgroundResource(R.drawable.tutor_center_unselected);
                imageView03.setBackgroundResource(R.drawable.circle_unselected);
                imageView04.setBackgroundResource(R.drawable.person_center_unselected);
                textView01.setTextColor(getApplication().getResources().getColor(R.color.theme_color));
                textView02.setTextColor(getApplication().getResources().getColor(R.color.main_chars));
                textView03.setTextColor(getApplication().getResources().getColor(R.color.main_chars));
                textView04.setTextColor(getApplication().getResources().getColor(R.color.main_chars));
                break;
            case R.id.teacher_center_ll:
                switchContent(current, teacher_center);
                imageView01.setBackgroundResource(R.drawable.homepage_unselected);
                imageView02.setBackgroundResource(R.drawable.tutor_center_selected);
                imageView03.setBackgroundResource(R.drawable.circle_unselected);
                imageView04.setBackgroundResource(R.drawable.person_center_unselected);
                textView01.setTextColor(getApplication().getResources().getColor(R.color.main_chars));
                textView02.setTextColor(getApplication().getResources().getColor(R.color.theme_color));
                textView03.setTextColor(getApplication().getResources().getColor(R.color.main_chars));
                textView04.setTextColor(getApplication().getResources().getColor(R.color.main_chars));
                break;
            case R.id.circle_ll:
                if(MyApplication.hasLoggedIn)
                {
                    switchContent(current, circle);
                    imageView01.setBackgroundResource(R.drawable.homepage_unselected);
                    imageView02.setBackgroundResource(R.drawable.tutor_center_unselected);
                    imageView03.setBackgroundResource(R.drawable.circle_selected);
                    imageView04.setBackgroundResource(R.drawable.person_center_unselected);
                    textView01.setTextColor(getApplication().getResources().getColor(R.color.main_chars));
                    textView02.setTextColor(getApplication().getResources().getColor(R.color.main_chars));
                    textView03.setTextColor(getApplication().getResources().getColor(R.color.theme_color));
                    textView04.setTextColor(getApplication().getResources().getColor(R.color.main_chars));
                }
                else
                {
                    Intent intent = new Intent(getApplication(),LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.person_center_ll:
                switchContent(current, person_center);
                imageView01.setBackgroundResource(R.drawable.homepage_unselected);
                imageView02.setBackgroundResource(R.drawable.tutor_center_unselected);
                imageView03.setBackgroundResource(R.drawable.circle_unselected);
                imageView04.setBackgroundResource(R.drawable.person_center_selected);
                textView01.setTextColor(getApplication().getResources().getColor(R.color.main_chars));
                textView02.setTextColor(getApplication().getResources().getColor(R.color.main_chars));
                textView03.setTextColor(getApplication().getResources().getColor(R.color.main_chars));
                textView04.setTextColor(getApplication().getResources().getColor(R.color.theme_color));
                break;
        }
    }
    public void switchContent(Fragment from, Fragment to)
    {
        FragmentTransaction transaction = mFragmentMan.beginTransaction();
        if (current != to) {
            current = to;
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from);
                transaction.add(R.id.main_middle, to);
                transaction.commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from); // 隐藏当前的fragment，显示下一个
                transaction.show(to);
                transaction.commit();
            }
        }
    }

    /**
     * 点击两次退出应用
     *
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            exitBy2Click();
        }
        return false;
    }

    private void exitBy2Click() {
        // TODO Auto-generated method stub
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 1500); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
            System.exit(0);
        }
        }
    }
