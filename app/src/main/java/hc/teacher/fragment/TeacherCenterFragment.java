package hc.teacher.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hc.teacher.adapter.InformationAdapter;
import hc.teacher.activity_teacher_center.ApplyInfoActivity;
import hc.teacher.activity_teacher_center.EmployInfoActivity;
import hc.teacher.application.LoginActivity;
import hc.teacher.application.PublishActivity;
import hc.teacher.application.R;
import hc.teacher.data.DataAnalysis;
import hc.teacher.user_defined_widget.PullToRefreshListView;
import hc.teacher.application.MyApplication;
import hc.teacher.utils.MethodUtils;

public class TeacherCenterFragment extends Fragment
{
    private ViewPager pager;                                              //用来切换两个tab
    private PagerAdapter pagerAdapter;                                    //pager的适配器
    private ArrayList<View> viewList = new ArrayList<View>();             //装载tab所用界面资源
    private List<Map<String, Object>> listData1 = new ArrayList<>();     //装载找家教界面数据
    private List<Map<String, Object>> listData2 = new ArrayList<>();     //装载做家教界面数据
    private PullToRefreshListView listView1;                              //找家教对应ListView
    private PullToRefreshListView listView2;                              //做家教对应ListView
    private InformationAdapter listAdapter1;                              //listView1对应adapter
    private InformationAdapter listAdapter2;                              //listView2对应adapter
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();

    //筛选条件对应button
    private Button choSubjectBtn1;
    private Button choTimeBtn1;
    private Button choGradeBtn1;
    private Button choSubjectBtn2;
    private Button choTimeBtn2;
    private Button choGradeBtn2;

    //actionBar上的找家教和做家教两个tab
    private TextView findTv;
    private TextView doTv;

    //保存当前界面
    private int current = 1;

    //保存已选择的对应科目、工作时间、年级信息
    String subject = null;
    String workTime = null;
    String grade = null;

    //切换常规界面和搜索界面
    private RelativeLayout actionBar;
    private RelativeLayout searchBar;
    private TextView cancel;
    private EditText editText;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fra_teacher_center,null);

        //自定义操作栏
        actionBar = (RelativeLayout)view.findViewById(R.id .actionBar);
        ImageButton aroundBtn = (ImageButton)view.findViewById(R.id.around_imbtn);
        ImageButton publishBtn = (ImageButton)view.findViewById(R.id.publish_imbtn);
        findTv = (TextView)view.findViewById(R.id.find_tutor_tv);
        doTv = (TextView)view.findViewById(R.id.do_tutor_tv);

        //自定义搜索栏
        searchBar = (RelativeLayout)view.findViewById(R.id.searchBar);
        cancel = (TextView)view.findViewById(R.id.cancel);
        editText = (EditText)view.findViewById(R.id.editText);

        //进入搜索界面
        aroundBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //隐藏原actionBar，显示searchBar
                searchBar.setVisibility(View.VISIBLE);
                actionBar.setVisibility(View.INVISIBLE);
            }
        });

        //取消搜索
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //隐藏searchBar，显示actionBar
                searchBar.setVisibility(View.INVISIBLE);
                actionBar.setVisibility(View.VISIBLE);
            }
        });

        //输入完成按下“回车”进行搜索
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                //请求服务器
                Map<String, String> req = new HashMap<>();
                req.put("keyword", editText.getText().toString());
                if(current == 1)
                {
                    req.put("type", "2");
                }
                else
                {
                    req.put("type", "1");
                }
                volley_Post("tutorCenter/findInformationByKeyword", req, 2);

                //隐藏searchBar，显示actionBar
                searchBar.setVisibility(View.INVISIBLE);
                actionBar.setVisibility(View.VISIBLE);

                //清空editText内容
                editText.setText("");
                return true;
            }
        });

        //点击发布按钮,跳转至发布界面
        publishBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(MyApplication.hasLoggedIn)
                {
                    //执行操作
                    startActivity(new Intent(getActivity(), PublishActivity.class));
                }else{
                    //跳转至LoginActivity;
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }

            }
        });

        //点击切换两个Tab对应切换viewPager
        findTv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switchTab(0);
                pager.setCurrentItem(0);
                current = 1;
            }
        });

        doTv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switchTab(1);
                pager.setCurrentItem(1);
                current = 2;
            }
        });

        //实现“找家教”和“做家教”两个界面间的滑动效果
        pager = (ViewPager)view.findViewById(R.id.info_viewpager);

        View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.fra_information, null);
        View view2 = LayoutInflater.from(getActivity()).inflate(R.layout.fra_information, null);
        viewList.add(view1);
        viewList.add(view2);

        pagerAdapter = new MyViewPagerAdapter(viewList);
        pager.setAdapter(pagerAdapter);

        //根据user信息判断初始为找家教界面还是做家教界面
        try
        {
            if(MyApplication.hasLoggedIn)
            {
                switchTab(MethodUtils.getLocalUser().getIdentity() - 1);
                pager.setCurrentItem(MethodUtils.getLocalUser().getIdentity() - 1);
            }
            else
            {
                switchTab(0);
            }
        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }

            @Override
            public void onPageSelected(int position)
            {
                //滑动切换界面时改变对应tab
                switch(position)
                {
                case 0:
                    switchTab(0);
                    current = 1;
                    break;
                case 1:
                    switchTab(1);
                    current = 2;
                    break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
            }
        });

        //筛选条件
        choTimeBtn1 = (Button)view1.findViewById(R.id.choose_time_btn);
        choSubjectBtn1 = (Button)view1.findViewById(R.id.choose_subject_btn);
        choGradeBtn1 = (Button)view1.findViewById(R.id.choose_grade_btn);
        choTimeBtn2 = (Button)view2.findViewById(R.id.choose_time_btn);
        choSubjectBtn2 = (Button)view2.findViewById(R.id.choose_subject_btn);
        choGradeBtn2 = (Button)view2.findViewById(R.id.choose_grade_btn);

        //判断是否有从其他fragment传来的数据并初始化
        if(getArguments() != null)
        {
            subject = getArguments().getString("subject");
            choSubjectBtn1.setText(subject);
            choSubjectBtn2.setText(subject);
        }

        choTimeBtn1.setOnClickListener(new MyCheckBoxListener(1));
        choSubjectBtn1.setOnClickListener(new MyCheckBoxListener(1));
        choGradeBtn1.setOnClickListener(new MyCheckBoxListener(1));
        choTimeBtn2.setOnClickListener(new MyCheckBoxListener(2));
        choSubjectBtn2.setOnClickListener(new MyCheckBoxListener(2));
        choGradeBtn2.setOnClickListener(new MyCheckBoxListener(2));

        //已发布信息列表
        listView1 = (PullToRefreshListView)view1.findViewById(R.id.listView);
        listView2 = (PullToRefreshListView)view2.findViewById(R.id.listView);

        //访问数据库获取信息
        getData(1);
        getData(2);

        listAdapter1 = new InformationAdapter(getActivity(), listData1, R.layout.information_list,
                new String[] {"TITLE", "SUBJECT", "COMMIT_DATE", "GRADE", "SALARY"},
                new int [] {R.id.title, R.id.subject, R.id.time, R.id.grade, R.id.salary});
        listAdapter2 = new InformationAdapter(getActivity(), listData2, R.layout.information_list,
                new String[] {"TITLE", "SUBJECT", "COMMIT_DATE", "GRADE", "SALARY"},
                new int [] {R.id.title, R.id.subject, R.id.time, R.id.grade, R.id.salary});
        listView1.setAdapter(listAdapter1);
        listView2.setAdapter(listAdapter2);

        //为两个ListView设定点击监听器
        listView1.setOnItemClickListener(new MyListListener(ApplyInfoActivity.class));
        listView2.setOnItemClickListener(new MyListListener(EmployInfoActivity.class));

        //为两个ListView设定下拉刷新监听器
        listView1.setOnRefreshListener(new MyOnRefreshListener(1, listAdapter1, listView1));
        listView2.setOnRefreshListener(new MyOnRefreshListener(2, listAdapter2, listView2));

        //为两个ListVi设定上拉加载更多监听器
        listView1.setOnLoadListener(new MyOnLoadListener(1, listData1, listAdapter1, listView1));
        listView2.setOnLoadListener(new MyOnLoadListener(2, listData2, listAdapter2, listView2));

        return view;
    }

    /**
     * 切换操作栏tab，切换时改变对应背景和字体颜色
     * @param tab   要切换到的tab
     */
    public void switchTab(int tab)
    {
        if(tab == 0)
        {
            findTv.setBackgroundResource(R.drawable.teacher_center_find);
            doTv.setBackgroundColor(Color.TRANSPARENT);
            doTv.setTextColor(Color.WHITE);
            findTv.setTextColor(getResources().getColor(R.color.theme_color));
        }
        else
        {
            doTv.setBackgroundResource(R.drawable.teacher_center_do);
            findTv.setBackgroundColor(Color.TRANSPARENT);
            findTv.setTextColor(Color.WHITE);
            doTv.setTextColor(getResources().getColor(R.color.theme_color));
        }
    }

    /**
     * 从其他fragment跳转到该fragment时调用此方法生成对象并传递数据
     * @param text 从其他fragment传来的数据
     * @return  返回次fragment的一个实例
     */
    public static TeacherCenterFragment newInstance(String text)
    {
        TeacherCenterFragment fragment = new TeacherCenterFragment();
        Bundle args = new Bundle();
        args.putString("subject", text);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 初始化listView数据
     */
    private void getData(int tag)
    {
        Map<String, String> req = new HashMap<String, String>();
        //添加自己的筛选信息，并请求服务器数据
        req.put("type", tag + "");
        if(subject != null)
            req.put("subject", subject);
        if(workTime != null)
            req.put("worktime", workTime);
        if(grade != null)
            req.put("grade", grade);
        volley_Post("tutorCenter/findInformation", req, 0);
    }

    /**
     * 请求服务器数据
     * @param url_add   添加的请求url
     * @param req       请求数据 1为做家教，2为找家教
     * @param flag      标记请求 0为findInformation，1为findOlderInformation, 2为findInformationByKeyword
     */
    private void volley_Post(String url_add, final Map<String,String> req, final int flag)
    {
        String url = getString(R.string.url);
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                //请求成功
                Map<String,Object> response = gson.fromJson(s,Map.class);
                List<Map<String, Object>> list = new ArrayList<>();
                //从服务器传回来的信息解析出来数据
                if((boolean)response.get("result"))
                {
                    list = new DataAnalysis().listKeyMaps(response.get("infoList").toString());
                }
                //如果请求类型为2，改变找家教对应信息
                if(Integer.parseInt(req.get("type")) == 2)
                {
                    if(flag == 0 || flag == 2)
                        listData1.clear();
                    listData1.addAll(list);
                    listAdapter1.notifyDataSetChanged();
                    if(listData1.size() != 10)
                        listView1.setLoadEnable(false);
                }
                //如果请求类型为1，改变做家教对应信息
                else
                {
                    if(flag == 0 || flag == 2)
                        listData2.clear();
                    listData2.addAll(list);
                    listAdapter2.notifyDataSetChanged();
                    if(listData2.size() != 10)
                        listView2.setLoadEnable(false);
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                //请求失败处理
                Toast.makeText(getActivity(),"连接服务器失败，请刷新重试！",Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                return req;
            }
        };
        request.setTag("doPost");
        MyApplication.getHttpQueue().add(request);
    }

    /**
     *  自定义PagerAdapter类
     */
    private class MyViewPagerAdapter extends PagerAdapter
    {
        private ArrayList<View> views;

        public MyViewPagerAdapter(ArrayList<View> views)
        {
            this.views = views;
        }

        @Override
        public int getCount()
        {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            container.addView(views.get(position));
            return views.get(position);
        }
    }

    /**
     * 三个筛选条件监听器
     */
    private class MyCheckBoxListener implements View.OnClickListener
    {
        int id;         //保存被点击的button的id
        int tag;        //判断是找家教的button还是做家教的button
        private String [] items = null;
        private boolean [] itemState = null;
        private boolean [] itemState1 = new boolean [] {false, false, false, false, false};
        private boolean [] itemState2 = new boolean [] {false, false, false, false, false, false, false, false, false, false};
        private boolean [] itemState3 = new boolean [] {false, false, false, false, false, false, false, false};
        private String title = "";          //对话框标题
        private EditText ed = null;         //自定义输入框
        private ListView lv = null;         //

        /**
         * 构造方法
         * @param tag   标记是找家教还是做家教的button被点击
         */
        public MyCheckBoxListener(int tag)
        {
            this.tag = tag;
        }

        @Override
        public void onClick(View v)
        {
            id = v.getId();
            //根据点击的button创建对话框所需的内容
            switch(id)
            {
                case R.id.choose_time_btn:
                    items = new String [] {"全选", "按次辅导", "周末辅导", "晚上辅导", "长期辅导"};
                    itemState = itemState1;
                    title = "选择时间";
                    ed = new EditText(getActivity());
                    ed.setHint("请输入其它时间");
                    break;
                case R.id.choose_subject_btn:
                    items = new String [] {"全选", "语文", "数学", "英语", "物理", "化学", "生物", "政治", "历史", "地理"};
                    itemState = itemState2;
                    title = "选择科目";
                    ed = new EditText(getActivity());
                    ed.setHint("请输入其它科目");
                    break;
                case R.id.choose_grade_btn:
                    items = new String [] {"全选", "初一", "初二", "初三", "高一", "高二", "高三", "大学"};
                    itemState = itemState3;
                    title = "选择年级";
                    break;
            }

            //生成自定义的对话框
            final AlertDialog builder = new AlertDialog.Builder(getActivity()).setTitle(title)
                .setMultiChoiceItems(items, itemState, new DialogInterface.OnMultiChoiceClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked)
                    {
                        //如果选择了“全选”，改变其他选项的状态
                        if(which == 0)
                        {
                            for(int i = 1; i < itemState.length; i++)
                            {
                                lv.setItemChecked(i, isChecked);
                                itemState[i] = isChecked;
                            }
                        }
                        else        //如果其它选项全部为true，则“全选”也置为true
                        {
                            itemState[which] = isChecked;
                            boolean all = true;
                            for(int i = 1; i < itemState.length; i++)
                            {
                                if(!itemState[i]) all = false;
                            }
                            itemState[0] = all;
                            lv.setItemChecked(0, all);
                        }
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    //点击对话框确定按钮触发
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        /*
                        如果点击的是选择科目或时间，并且用户填写了自定义的条件
                        将用户填写的自定义目或时间条件信息保存
                         */
                        if(id != R.id.choose_grade_btn && !ed.getText().toString().equals(""))
                        {
                            switch(id)
                            {
                            case R.id.choose_subject_btn:
                                subject = ed.getText().toString();
                                if(tag == 1) choSubjectBtn1.setText(subject);
                                else choSubjectBtn2.setText(subject);
                                break;
                            case R.id.choose_time_btn:
                                workTime = ed.getText().toString();
                                if(tag == 1) choTimeBtn1.setText(workTime);
                                else choTimeBtn2.setText(workTime);
                                break;
                            }
                        }
                        /*
                        如果用户并没有填写自定义信息
                        将用户选择的条件对应保存
                         */
                        else
                        {
                            //找出用户选择了那些选项,各个条件间用“||”隔开
                            StringBuffer sb = new StringBuffer();
                            for(int i = 1; i < itemState.length; i++)
                            {
                                if(itemState[i]) sb.append(items[i] + "||");
                            }
                            //如果用户没有选择任何条件，将控件恢复到最初状态
                            if(sb.length() == 0)
                            {
                                switch(id)
                                {
                                case R.id.choose_subject_btn:
                                    subject = null;
                                    if(tag == 1) choSubjectBtn1.setText("选择科目");
                                    else choSubjectBtn2.setText("选择科目");
                                    break;
                                case R.id.choose_time_btn:
                                    workTime = null;
                                    if(tag == 1) choTimeBtn1.setText("选择时间");
                                    else choTimeBtn2.setText("选择时间");
                                    break;
                                case R.id.choose_grade_btn:
                                    grade = null;
                                    if(tag == 1) choGradeBtn1.setText("选择年级");
                                    else choGradeBtn2.setText("选择年级");
                                    break;
                                }
                            }
                            else
                            {
                                sb.delete(sb.length() - 2, sb.length());
                                //改变对应button的text，如果选择了多项，则置为“XXX...”
                                switch(id)
                                {
                                case R.id.choose_subject_btn:
                                    subject = sb.toString();
                                    if(tag == 1) choSubjectBtn1.setText(subject.contains("||") ? subject.substring(0, subject.indexOf("||")) + "..." : subject);
                                    else choSubjectBtn2.setText(subject.contains("||") ? subject.substring(0, subject.indexOf("||")) + "..." : subject);
                                    break;
                                case R.id.choose_grade_btn:
                                    grade = sb.toString();
                                    if(tag == 1) choGradeBtn1.setText(grade.contains("||") ? grade.substring(0, grade.indexOf("||")) + "..." : grade);
                                    else choGradeBtn2.setText(grade.contains("||") ? grade.substring(0, grade.indexOf("||")) + "..." : grade);
                                    break;
                                case R.id.choose_time_btn:
                                    workTime = sb.toString();
                                    if(tag == 1) choTimeBtn1.setText(workTime.contains("||") ? workTime.substring(0, workTime.indexOf("||")) + "..." : workTime);
                                    else choTimeBtn2.setText(workTime.contains("||") ? workTime.substring(0, workTime.indexOf("||")) + "..." : workTime);
                                    break;
                                }
                            }
                        }
                        //访问数据库更新信息
                        if(tag == 1)
                        {
                            getData(2);
                            listAdapter1.notifyDataSetChanged();
                        }
                        else
                        {
                            getData(1);
                            listAdapter2.notifyDataSetChanged();
                        }
                    }
                }).setNegativeButton("取消", null).setView(ed).create();

            lv = builder.getListView();
            builder.show();
        }
    }
    /**
     *  ListView是案件点击监听器
     */
    private class MyListListener implements AdapterView.OnItemClickListener
    {
        private Class toActivityClass;      //点击某个条目要跳转到的activity

        public MyListListener(Class toActivityClass)
        {
            this.toActivityClass = toActivityClass;
        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            if(MyApplication.hasLoggedIn)
            {
                //将被点击的条目对应的数据包装在Bundle发送到下一个activity
                Intent intent = new Intent();
                intent.setClass(getActivity(), toActivityClass);

                Bundle bundle = new Bundle();
                Map<String, Object> map = (toActivityClass == ApplyInfoActivity.class) ? listData1.get(position - 1) : listData2.get(position - 1);
                bundle.putSerializable("data", (Serializable) map);
                intent.putExtras(bundle);

                startActivity(intent);
            }
            else
            {
                Intent intent = new Intent(getActivity().getApplication(),LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    /**
     * 下拉刷新监听器
     */
    private class MyOnRefreshListener implements PullToRefreshListView.OnRefreshListener
    {
        int tag;
        private SimpleAdapter adapter;
        private PullToRefreshListView listView;

        /**
         * 构造方法
         * @param tag   要刷新数据的类型
         * @param v     对应的listVi
         * @param a     对应的adapter
         */
        public MyOnRefreshListener(int tag, SimpleAdapter a, PullToRefreshListView v)
        {
            this.tag = tag;
            this.adapter = a;
            this.listView = v;
        }
        @Override
        public void onRefresh()
        {
            new AsyncTask<Void, Void, Void>()
            {
                @Override
                protected Void doInBackground(Void... params)
                {
                    //访问数据库得到最新的消息记录
                    try
                    {
                        Thread.sleep(2000);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    if(tag == 1)
                        getData(2);
                    else
                        getData(1);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid)
                {
                    //刷新结束改变数据
                    adapter.notifyDataSetChanged();
                    listView.onRefreshComplete();
                }
            }.execute(null, null, null);
        }
    }

    /**
     * 上拉加载更多监听器
     */
    private class MyOnLoadListener implements PullToRefreshListView.OnLoadListener
    {
        int tag;
        private List<Map<String, Object>> listData;
        private SimpleAdapter adapter;
        private PullToRefreshListView listView;

        /**
         * 构造方法
         * @param tag   需要加载的数据类型
         * @param l     装载加载出来的数据
         * @param a     对应的adapter
         * @param v     对应的listv
         */
        public MyOnLoadListener(int tag, List<Map<String, Object>> l, SimpleAdapter a, PullToRefreshListView v)
        {
            this.tag = tag;
            this.listData = l;
            this.adapter = a;
            this.listView = v;
        }

        @Override
        public void onLoad()
        {
            new AsyncTask<Void, Void, Void>()
            {
                @Override
                protected Void doInBackground(Void... params)
                {
                    Map<String, String> req = new HashMap<String, String>();
                    //添加自己的筛选信息，并请求服务器数据
                    if(tag == 1)
                        req.put("tag", 2 + "");
                    else
                        req.put("tag", 1 + "");
                    if(subject != null)
                        req.put("subject", subject);
                    if(workTime != null)
                        req.put("worktime", workTime);
                    if(grade != null)
                        req.put("grade", grade);
                    req.put("commit_date", listData.get(9).get("COMMIT_DATE").toString());
                    volley_Post("tutorCenter/findOlderInformation", req, 1);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid)
                {
                    //通知数据已发生改变
                    adapter.notifyDataSetChanged();
                    listView.onLoadComplete();
                    //如果加载出来的数据不足10条，说明数据已经全部加载
                    if(listData.size() % 10 != 0)
                    {
                        listView.setLoadEnable(false);
                    }
                }
            }.execute(null, null, null);
        }
    }
}