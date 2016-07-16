package hc.teacher.activity_person;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.components.dialog.GenderModifyDialog;
import hc.teacher.components.dialog.TextModifyDialog;
import hc.teacher.components.dialog.ModifyDialogListener;
import hc.teacher.entity.User;
import hc.teacher.utils.MethodUtils;

public class SelfInfoActivity extends AppCompatActivity {

    private ImageButton ibtn_GoBack;
    private ImageView iv_UserIcon;
    private LinearLayout ll_UserIcon;
    private LinearLayout ll_NickName;
    private LinearLayout ll_Description;
    private LinearLayout ll_Gender;
    private LinearLayout ll_RealInfo;
    private TextView tv_NickName;
    private TextView tv_Description;
    private TextView tv_Account;
    private TextView tv_Gender;

    @Override
    protected void onStart() {
        super.onStart();

        //显示当前用户头像
        Bitmap bm = BitmapFactory.decodeFile(MethodUtils.getLocalImageFile(MyApplication.PATH_USER_ICON).getPath());
        iv_UserIcon.setImageBitmap(bm);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_info);

        initView();

        loadUserInfo();

        //返回按钮
        ibtn_GoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //点击更换头像
        ll_UserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelfInfoActivity.this, UserIconActivity.class));
            }
        });

        //点击修改昵称
        ll_NickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextModifyDialog myDialog = new TextModifyDialog(SelfInfoActivity.this, getDialogListener());
                myDialog.setInitStr(tv_NickName.getText().toString())
                        .setType_name("nickname")
                        .setTitle("昵称")
                        .setHint("请输入昵称")
                        .setInputType(TextModifyDialog.SHORT_TEXT_INPUT)
                        .show();

                //加载dialog 需要时间，所以弹出键盘需要延时
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        myDialog.showKeyboard();
                    }
                }, 200);
            }
        });

        //点击修改性别
        ll_Gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenderModifyDialog modifyDialog = new GenderModifyDialog(SelfInfoActivity.this, getDialogListener());
                modifyDialog.setType_name("gender")
                        .setTitle("性别")
                        .show();
            }
        });

        //点击修改签名
        ll_Description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextModifyDialog myDialog = new TextModifyDialog(SelfInfoActivity.this, getDialogListener());
                myDialog.setInitStr(tv_Description.getText().toString())
                        .setType_name("description")
                        .setTitle("个性签名")
                        .setHint("请输入签名")
                        .setInputType(TextModifyDialog.LONG_TEXT_INPUT)
                        .show();

                //加载dialog 需要时间，所以弹出键盘需要延时
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        myDialog.showKeyboard();
                    }
                }, 200);
            }
        });

        //点击进入真实资料
        ll_RealInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelfInfoActivity.this, RealInfoActivity.class));
            }
        });
    }

    private void initView() {
        ibtn_GoBack = (ImageButton) findViewById(R.id.btn_GoBack);
        iv_UserIcon = (ImageView) findViewById(R.id.iv_UserIcon);
        ll_Gender = (LinearLayout) findViewById(R.id.ll_Gender);
        ll_UserIcon = (LinearLayout) findViewById(R.id.ll_UserIcon);
        ll_NickName = (LinearLayout) findViewById(R.id.ll_NickName);
        ll_Description = (LinearLayout) findViewById(R.id.ll_Description);
        ll_RealInfo = (LinearLayout) findViewById(R.id.ll_RealInfo);
        tv_NickName = (TextView) findViewById(R.id.tv_NickName);
        tv_Description = (TextView) findViewById(R.id.tv_Description);
        tv_Account = (TextView) findViewById(R.id.tv_Account);
        tv_Gender = (TextView) findViewById(R.id.tv_Gender);
    }

    private void loadUserInfo() {
        //显示当前用户头像
        Bitmap bm = BitmapFactory.decodeFile(MethodUtils.getLocalImageFile(MyApplication.PATH_USER_ICON).getPath());
        iv_UserIcon.setImageBitmap(bm);

        try {
            User user = MethodUtils.getLocalUser();

            //显示昵称
            String name = user.getNickname();
            if (!name.equals("")) tv_NickName.setText(name);
            //显示账号
            String account = user.getAccount();
            tv_Account.setText(account);
            //显示性别
            if (user.getGender() == User.GENDER_MALE) {
                tv_Gender.setText("男");
            } else {
                tv_Gender.setText("女");
            }
            //显示签名
            String description = user.getDescription();
            if (!description.equals("")) tv_Description.setText(description);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    ModifyDialogListener getDialogListener() {
        return new ModifyDialogListener() {
            @Override
            public void onResponse(boolean result) {
                if (result) {
                    loadUserInfo();
                    Log.w("cry", "刷新数据");
                }
            }
        };
    }

}
