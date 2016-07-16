package hc.teacher.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.text.ParseException;

import hc.teacher.activity_person.MyOrderActivity;
import hc.teacher.activity_person.MyPublishActivity;
import hc.teacher.activity_person.MyTaskActivity;
import hc.teacher.application.LoginActivity;
import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.activity_person.SelfInfoActivity;
import hc.teacher.activity_person.SettingActivity;
import hc.teacher.components.crop_image.gallery.Image;
import hc.teacher.entity.User;
import hc.teacher.utils.MethodUtils;

/**
 * Created by Administrator on 2015/10/6 0006.
 */
public class Person_centerFragment extends Fragment {
    RelativeLayout rl_Top;
    RelativeLayout person_myorder_rl;
    RelativeLayout person_mypublish_rl;
    RelativeLayout person_mytask_rl;
//    RelativeLayout person_mybag_rl;
//    RelativeLayout person_mygrade_rl;
//    RelativeLayout person_collect_rl;
    RelativeLayout person_setting_rl;
    ImageView head_img;
    ImageView person_myorder_img;
    ImageView person_mypublish_img;
    ImageView person_mytask_img;
//    ImageView person_mybag_img;
//    ImageView person_mygrade_img;
//    ImageView person_collect_img;
    ImageView person_setting_img;
    TextView tv_NickName;
    TextView tv_Description;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fra_person_center, null);
        initView(view);
        Log.w("cry", "一次onCreate");

        //触摸效果
        MyImageOnTouchListener myImageOnTouchListener = new MyImageOnTouchListener();
        person_myorder_rl.setOnTouchListener(myImageOnTouchListener);
        person_mypublish_rl.setOnTouchListener(myImageOnTouchListener);
        person_mytask_rl.setOnTouchListener(myImageOnTouchListener);
//        person_mybag_rl.setOnTouchListener(myImageOnTouchListener);
//        person_mygrade_rl.setOnTouchListener(myImageOnTouchListener);
//        person_collect_rl.setOnTouchListener(myImageOnTouchListener);
        person_setting_rl.setOnTouchListener(myImageOnTouchListener);



        //我的订单
        person_myorder_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.hasLoggedIn) {
                    Intent intent = new Intent(getActivity(), MyOrderActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity().getApplication(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        //我的发布
        person_mypublish_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.hasLoggedIn) {
                    Intent intent = new Intent(getActivity(), MyPublishActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity().getApplication(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        //我的任务
        person_mytask_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.hasLoggedIn) {
                    Intent intent = new Intent(getActivity(),MyTaskActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity().getApplication(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        //设置选项
        person_setting_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });

        //点击顶部个人信息
        rl_Top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果已经登录，进入个人资料
                if (MyApplication.hasLoggedIn) {
                    startActivity(new Intent(getActivity(), SelfInfoActivity.class));
                }
                //如果未登陆，跳转至登录界面
                else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        });

        //尝试加载用户信息
        try {
            loadUserInfo();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return view;
    }

    public void initView(View view) {
        rl_Top = (RelativeLayout) view.findViewById(R.id.top_relativelayout);
        person_myorder_rl = (RelativeLayout)view.findViewById(R.id.person_myorder_rl);
        person_mypublish_rl = (RelativeLayout)view.findViewById(R.id.person_mypublish_rl);
        person_mytask_rl = (RelativeLayout) view.findViewById(R.id.person_mytask_rl);
//        person_mybag_rl = (RelativeLayout) view.findViewById(R.id.person_mybag_rl);
//        person_mygrade_rl = (RelativeLayout) view.findViewById(R.id.person_mygrade_rl);
//        person_collect_rl = (RelativeLayout) view.findViewById(R.id.person_collect_rl);
        person_setting_rl = (RelativeLayout) view.findViewById(R.id.person_setting_rl);

        head_img = (ImageView) view.findViewById(R.id.head_img);
        person_myorder_img = (ImageView) view.findViewById(R.id.person_myorder_img);
        person_mypublish_img = (ImageView) view.findViewById(R.id.person_mypublish_img);
        person_mytask_img = (ImageView) view.findViewById(R.id.person_mytask_img);
//        person_mybag_img = (ImageView) view.findViewById(R.id.person_mybag_img);
//        person_mygrade_img = (ImageView) view.findViewById(R.id.person_mygrade_img);
//        person_collect_img = (ImageView) view.findViewById(R.id.person_collect_img);
        person_setting_img = (ImageView) view.findViewById(R.id.person_setting_img);

        tv_NickName = (TextView) view.findViewById(R.id.tv_UserName);
        tv_Description = (TextView) view.findViewById(R.id.tv_Description);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            loadUserInfo();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //触摸样式设置
    class MyImageOnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {   //当按钮按下时
                switch (v.getId()) {
                    case R.id.person_myorder_rl:
                        person_myorder_img.setBackgroundResource(R.drawable.person_myorder_selected);
                        break;
                    case R.id.person_mypublish_rl:
                        person_mypublish_img.setBackgroundResource(R.drawable.person_myspoor_selected);
                        break;
                    case R.id.person_mytask_rl:
                        person_mytask_img.setBackgroundResource(R.drawable.person_mytask_selected);
                        break;
//                    case R.id.person_mybag_rl:
//                        person_mybag_img.setBackgroundResource(R.drawable.person_mybag_selected);
//                        break;
//                    case R.id.person_mygrade_rl:
//                        person_mygrade_img.setBackgroundResource(R.drawable.person_mygrade_selected);
//                        break;
//                    case R.id.person_collect_rl:
//                        person_collect_img.setBackgroundResource(R.drawable.person_collect_selected);
//                        break;
                    case R.id.person_setting_rl:
                        person_setting_img.setBackgroundResource(R.drawable.person_setting_selected);
                        break;
                    default:
                        break;
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {   //当按钮弹起时
                switch (v.getId()) {
                    case R.id.person_myorder_rl:
                        person_myorder_img.setBackgroundResource(R.drawable.person_myorder_unselected);
                        break;
                    case R.id.person_mypublish_rl:
                        person_mypublish_img.setBackgroundResource(R.drawable.person_myspoor_unselected);
                        break;
                    case R.id.person_mytask_rl:
                        person_mytask_img.setBackgroundResource(R.drawable.person_mytask_unselected);
                        break;
//                    case R.id.person_mybag_rl:
//                        person_mybag_img.setBackgroundResource(R.drawable.person_mybag_unselected);
//                        break;
//                    case R.id.person_mygrade_rl:
//                        person_mygrade_img.setBackgroundResource(R.drawable.person_mygrade_unselected);
//                        break;
//                    case R.id.person_collect_rl:
//                        person_collect_img.setBackgroundResource(R.drawable.person_collect_unselected);
//                        break;
                    case R.id.person_setting_rl:
                        person_setting_img.setBackgroundResource(R.drawable.person_setting_unselected);
                        break;
                    default:
                        break;
                }
            }
            return false;
        }
    }

    /**
     * 从网络请求用户头像
     *
     * @throws ParseException
     */
    private void loadUserIconFromServer() throws ParseException {
        String url = getString(R.string.url);
        url = url + "public/images/" + MethodUtils.getLocalUser().getId() + ".png";
        //final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        final LruCache<String, Bitmap> lrucache = new LruCache<String, Bitmap>(20);
        ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache() {
            @Override
            public void putBitmap(String key, Bitmap value) {
                lrucache.put(key, value);
                Log.w("cry", "取到头像，尝试保存");
                MethodUtils.saveBitmap(value);
            }

            @Override
            public Bitmap getBitmap(String key) {
                return lrucache.get(key);
            }
        };
        ImageLoader imageLoader = new ImageLoader(MyApplication.getHttpQueue(), imageCache);
        ImageLoader.ImageListener listener = imageLoader.getImageListener(head_img, R.drawable.headimage01, R.drawable.headimage01);
        imageLoader.get(url, listener);
    }

    /**
     * 尝试加载用户信息到顶部显示用户信息的区域
     */

    private void loadUserInfo() throws ParseException {
        Log.w("cry", "尝试加载用户信息");
        //如果已登录
        if (MyApplication.hasLoggedIn) {
            User user = MethodUtils.getLocalUser();
            tv_NickName.setText(user.getNickname());
            String descStr = user.getDescription();
            if (descStr.length() > 10) {
                descStr = descStr.substring(0, 15) + "...";
            }
            tv_Description.setText(descStr);
            //显示当前用户头像
            if(MethodUtils.getLocalImageFile(MyApplication.PATH_USER_ICON).exists()){
                Log.w("cry", "从本地取头像");
                Bitmap bm = BitmapFactory.decodeFile(MethodUtils.getLocalImageFile(MyApplication.PATH_USER_ICON).getPath());
                head_img.setImageBitmap(bm);
            }
            else{
                //从网络
                Log.w("cry", "从网络取头像");
                loadUserIconFromServer();
            }
        }
        //如果未登录
        else {
            tv_NickName.setText("未登录");
            tv_Description.setText("");
            head_img.setImageResource(R.drawable.headimage01);
        }
    }
}



