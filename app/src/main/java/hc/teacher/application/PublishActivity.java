package hc.teacher.application;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;

import hc.teacher.entity.User;
import hc.teacher.fragment.Publish_Do_Fragment;
import hc.teacher.fragment.Publish_Find_Fragment;
import hc.teacher.utils.MethodUtils;


public class PublishActivity extends AppCompatActivity{

    private Fragment fra_Find, fra_Do, current;
    private FragmentManager mFragmentManager;
    private ImageButton btn_GoBack;
    private TextView tv_Title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        initView();
        getFragment();

        //根据用户身份加载发布界面
        int identity = -1;
        try {
            identity = MethodUtils.getLocalUser().getIdentity();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(identity == User.IDENTITY_FIND){
            tv_Title.setText("找家教");
            switchContent(current, fra_Find);
        }
        else if(identity == User.IDENTITY_DO){
            tv_Title.setText("做家教");
            switchContent(current, fra_Do);
        }else {
            Toast.makeText(getApplication(), "本地数据错误", Toast.LENGTH_SHORT).show();
        }

        //返回按钮点击事件
        btn_GoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PublishActivity.this.finish();
            }
        });
    }

    public void initView() {
        tv_Title = (TextView) findViewById(R.id.tv_Title);
        btn_GoBack = (ImageButton) findViewById(R.id.btn_GoBack);
    }

    public void getFragment() {
        mFragmentManager = getFragmentManager();

        fra_Find = new Publish_Find_Fragment();
        fra_Do = new Publish_Do_Fragment();

        mFragmentManager.beginTransaction().add(R.id.fragment_container, fra_Find).commit();
        current = fra_Find;
    }

    public void switchContent(Fragment from, Fragment to) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (current != to) {
            current = to;
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from);
                transaction.add(R.id.fragment_container, to);
                transaction.commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from); // 隐藏当前的fragment，显示下一个
                transaction.show(to);
                transaction.commit();
            }
        }
    }

}
