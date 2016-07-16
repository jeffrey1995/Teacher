package hc.teacher.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.map.geolocation.*;

import hc.teacher.activity_homepage.AnswerOnlineActivity;
import hc.teacher.activity_homepage.ConsultOnlineActivity;
import hc.teacher.application.MainActivity;
import hc.teacher.application.R;
import hc.teacher.components.location.SelectCityActivity;
import hc.teacher.utils.SubjectUtil;

public class HomepageFragment extends Fragment implements ViewPager.OnPageChangeListener, TencentLocationListener
{
    private LinearLayout chinese;
    private LinearLayout math;
    private LinearLayout english;
    private LinearLayout physics;
    private LinearLayout chem;
    private LinearLayout biology;
    private LinearLayout geography;
    private LinearLayout history;
    private LinearLayout politics;
    private LinearLayout other;
    private RelativeLayout enterConsult;
    private RelativeLayout enterAnswer;

    //操作栏相关控件
    private LinearLayout ll_City;
    private TextView txt_City;
    private ImageButton searchBtn;

    //腾讯地图相关
    private TencentLocationManager mLocationManager;

    //循环滚动图片相关控件
    private ViewPager viewPager;
    private ViewGroup viewGroup;
    private ImageView[] imageViews;
    private ImageView[] tips;
    private int[] imageId;
    private boolean isLoop = true;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //填充布局
        View view = inflater.inflate(R.layout.fra_homepage, container, false);
        initView(view);

        ll_City = (LinearLayout) view.findViewById(R.id.ll_City);
        searchBtn = (ImageButton) view.findViewById(R.id.search_imbtn);
        txt_City = (TextView)view.findViewById(R.id.txt_City);

        //城市定位按钮
        ll_City.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动选择城市Activity
                Intent intent = new Intent(getActivity(), SelectCityActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        // 获得地图管理器
        mLocationManager = TencentLocationManager.getInstance(getActivity());

        //开始定位
        startLocation();

        //搜索按钮
        searchBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO 完成搜索界面的跳转
                Toast.makeText(getActivity(), "跳转到搜索界面", Toast.LENGTH_SHORT).show();
            }
        });

        /*
        广告图自动播放
        每过5秒钟换下一张图片
         */
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (isLoop)
                {
                    SystemClock.sleep(5000);
                    handler.sendEmptyMessage(0);
                }
            }
        }).start();
        //手动滑动播放广告图
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewGroup = (ViewGroup) view.findViewById(R.id.viewGroup);

        //载入图片资源id
        imageId = new int[]{R.drawable.android1, R.drawable.android2, R.drawable.android3, R.drawable.android4};

        //将图片下面的点点加入到ViewGroup中
        tips = new ImageView[imageId.length];
        for (int i = 0; i < tips.length; i++)
        {
            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(10, 10));
            tips[i] = imageView;
            if (i == 0) {
                tips[i].setBackgroundResource(R.drawable.focused);
            } else
                tips[i].setBackgroundResource(R.drawable.unfocused);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            params.leftMargin = 5;
            params.rightMargin = 5;
            viewGroup.addView(imageView, params);
        }

        //将图片装载到数组中
        if (imageId.length == 2 || imageId.length == 3)
        {
            imageViews = new ImageView[imageId.length * 2];
            for (int i = 0; i < imageViews.length; i++) {
                ImageView imageView = new ImageView(getActivity());
                imageViews[i] = imageView;
                imageView.setBackgroundResource(imageId[(i > (imageId.length - 1)) ? i - imageId.length : i]);
            }
        } else {
            imageViews = new ImageView[imageId.length];
            for (int i = 0; i < imageViews.length; i++) {
                ImageView imageView = new ImageView(getActivity());
                imageViews[i] = imageView;
                imageView.setBackgroundResource(imageId[i]);
            }
        }

        //设置对应适配器和监听器
        viewPager.setAdapter(new ViewPagerAdapter());
        viewPager.setOnPageChangeListener(this);
        viewPager.setCurrentItem((imageViews.length) * 100);

        //点击对应科目
        SubjectListener subjectListener = new SubjectListener();
        chinese.setOnClickListener(subjectListener);
        math.setOnClickListener(subjectListener);
        english.setOnClickListener(subjectListener);
        physics.setOnClickListener(subjectListener);
        chem.setOnClickListener(subjectListener);
        biology.setOnClickListener(subjectListener);
        geography.setOnClickListener(subjectListener);
        history.setOnClickListener(subjectListener);
        politics.setOnClickListener(subjectListener);
        other.setOnClickListener(subjectListener);

        //进入在线咨询
        enterConsult.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent it = new Intent();
                it.setClass(getActivity(), ConsultOnlineActivity.class);
                startActivity(it);
            }
        });
        //进入在线答题
        enterAnswer.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent it = new Intent();
                it.setClass(getActivity(), AnswerOnlineActivity.class);
                startActivity(it);
            }
        });
        //进入风采展示
        view.findViewById(R.id.enter_mien).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(), "暂未开放，敬请期待", Toast.LENGTH_SHORT).show();
            }
        });
        //进入高考专题
        view.findViewById(R.id.enter_gaokao).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(), "暂未开放，敬请期待", Toast.LENGTH_SHORT).show();
            }
        });
        //进入论坛
        view.findViewById(R.id.enter_forum).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(), "暂未开放，敬请期待", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void initView(View view)
    {
        chinese = (LinearLayout)view.findViewById(R.id.chinese_ll);
        math = (LinearLayout)view.findViewById(R.id.math_ll);
        english = (LinearLayout)view.findViewById(R.id.english_ll);
        physics = (LinearLayout)view.findViewById(R.id.physics_ll);
        chem = (LinearLayout)view.findViewById(R.id.chem_ll);
        biology = (LinearLayout)view.findViewById(R.id.biology_ll);
        geography = (LinearLayout)view.findViewById(R.id.geography_ll);
        history = (LinearLayout)view.findViewById(R.id.history_ll);
        politics = (LinearLayout)view.findViewById(R.id.politics_ll);
        other = (LinearLayout)view.findViewById(R.id.other_ll);
        enterConsult = (RelativeLayout)view.findViewById(R.id.enter_consult);
        enterAnswer = (RelativeLayout)view.findViewById(R.id.enter_answer);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position)
    {
        //对应图片选中的时候要将对应的“点”的选中状态改变
        setImageBackground(position % imageViews.length);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isLoop = false;

        // 退出 activity 前一定要停止定位!
        stopLocation();
    }

    /**
     * 改变广告图下方“点点”的选中状态
     * @param item  对应选中图片位置
     */
    public void setImageBackground(int item)
    {
        for (int i = 0; i < tips.length; i++)
        {
            if (i == item)
                tips[i].setBackgroundResource(R.drawable.focused);
            else
                tips[i].setBackgroundResource(R.drawable.unfocused);
        }
    }

    /**
     * 当定位位置发生变化
     *
     * @param tencentLocation 获得的地址
     * @param error           返回的错误码
     * @param s               错误原因
     */
    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int error, String s) {
        Toast.makeText(getActivity(), "定位返回码：" + error, Toast.LENGTH_SHORT).show();
        Log.w("cry", "定位返回码：" + error);
        if (TencentLocation.ERROR_OK == error) {
            // 定位成功
            String city = tencentLocation.getCity();
            txt_City.setText(city);
        } else {
            // 网络问题引起的定位失败
            Toast.makeText(getActivity(), "定位城市失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 腾讯地图：当GPS, WiFi, Radio 等状态发生变化
     *
     * @param name   设备名, GPS, WIFI, RADIO 中的某个
     * @param status 状态码, STATUS_ENABLED, STATUS_DISABLED, STATUS_UNKNOWN 中的某个
     * @param desc   状态描述
     */
    @Override
    public void onStatusUpdate(String name, int status, String desc) {

    }

    /**
     * 自定义PagerAdapter类
     */
    public class ViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViews[position % imageViews.length]);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(imageViews[position % imageViews.length], 0);
            return imageViews[position % imageViews.length];
        }
    }

    /**
     * 处理被启动activity返回的结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null)return;

        if (data.hasExtra("CITY_NAME")) {
            String city = data.getStringExtra("CITY_NAME");
            if (city != null) {
                txt_City.setText(city);
            }
        }
    }

    public void startLocation() {
        // 创建一个定位请求
        TencentLocationRequest request = TencentLocationRequest.create();
        request.setInterval(0).setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA);
        // 执行请求
        mLocationManager.requestLocationUpdates(request, this);
        Log.w("cry", "已发起定位查询");
    }

    public void stopLocation() {
        mLocationManager.removeUpdates(this);
    }

    /**
     * 各个科目的监听器
     */
    private class SubjectListener implements OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            //点击科目进入家教中心界面，将对应科目id传给下一个fragment
            MainActivity ma = (MainActivity)getActivity();
            ma.setTeacherCenterArgument(SubjectUtil.getSubject(v.getId()));
            ma.switchContent(HomepageFragment.this, ma.getTeacherCenter());
            (getActivity().findViewById(R.id.mainImageView01)).setBackgroundResource(R.drawable.homepage_unselected);
            (getActivity().findViewById(R.id.mainImageView02)).setBackgroundResource(R.drawable.tutor_center_selected);
            ((TextView)getActivity().findViewById(R.id.mainTextView01)).setTextColor(getActivity().getApplication().getResources().getColor(R.color.main_chars));
            ((TextView)getActivity().findViewById(R.id.mainTextView02)).setTextColor(getActivity().getApplication().getResources().getColor(R.color.theme_color));
        }
    }
}


