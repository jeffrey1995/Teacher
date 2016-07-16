package hc.teacher.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.entity.Information;
import hc.teacher.utils.MethodUtils;

/**
 * Create by cry on 2015/10/12.
 */

public class Publish_Do_Fragment extends Fragment {
    Gson gson = MethodUtils.getGson();

    //UI引用
    EditText et_Title;
    Button btn_Subject;
    Button btn_Grade;
    Button btn_Time;
    EditText et_Salary;
    EditText et_Description;
    EditText et_Address;
    EditText et_Phone;
    CheckBox cb_Permit;
    EditText et_Linkman;
    CheckBox cb_Agreement;
    Button btn_Submit;

    //存放多选弹窗对应ListView的临时lv
    ListView lv_Choose;

    public Publish_Do_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fra_publish_do, container, false);
        initView(view);

        //为选择科目设置多选弹窗
        final String[] items_Subject = {"语文", "数学", "英语", "物理", "化学", "生物", "政治", "历史", "地理", "全选"}; //科目名称数组
        final boolean[] item_Subject_State = generateBooleanArray(items_Subject.length); //科目选择状态数组
        setMultiChoice(btn_Subject, items_Subject, item_Subject_State, getString(R.string.select_title_subject));

        //为选择年级设置多选弹窗
        final String[] items_Grade = { "初一", "初二", "初三", "高一", "高二", "高三", "大学", "全选"}; //科目名称数组
        final boolean[] item_Grade_State = generateBooleanArray(items_Grade.length); //科目选择状态数组
        setMultiChoice(btn_Grade, items_Grade, item_Grade_State, getString(R.string.select_title_grade));

        //为选择工作时间设置多选弹窗
        final String[] items_Time = {"按次辅导", "周末辅导", "晚上辅导", "长期辅导", "全选"}; //科目名称数组
        final boolean[] item_Time_State = generateBooleanArray(items_Time.length); //科目选择状态数组
        setMultiChoice(btn_Time, items_Time, item_Time_State, getString(R.string.select_title_time));

        //点击提交时，调用表单信息发送方法
        btn_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allItemOK()) {
                    doInfoPost();
                }
            }
        });

        return view;
    }

    private void initView(View view) {
        btn_Subject = (Button) view.findViewById(R.id.btn_Subject);
        btn_Grade = (Button) view.findViewById(R.id.btn_Grade);
        btn_Time = (Button) view.findViewById(R.id.btn_Time);
        et_Title = (EditText) view.findViewById(R.id.et_Title);
        et_Salary = (EditText) view.findViewById(R.id.et_Salary);
        et_Description = (EditText) view.findViewById(R.id.et_Description);
        et_Phone = (EditText) view.findViewById(R.id.et_Phone);
        cb_Permit = (CheckBox) view.findViewById(R.id.cb_Permit);
        et_Linkman = (EditText) view.findViewById(R.id.et_Name);
        cb_Agreement = (CheckBox) view.findViewById(R.id.cb_Agreement);
        btn_Submit = (Button) view.findViewById(R.id.btn_Submit);
        et_Address = (EditText) view.findViewById(R.id.et_Address);
    }

    /**
     * 设置点击按钮弹出的多选框
     *
     * @param btn       传入的按钮
     * @param items     条目文本信息
     * @param itemState 条目选择状态
     * @param title     弹出框标题
     */
    private void setMultiChoice(final Button btn, final String[] items, final boolean[] itemState, final String title) {

        final int length = items.length;

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog builder = new AlertDialog.Builder(getActivity()).setTitle(title)
                        .setMultiChoiceItems(items, itemState, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                //如果选择的是全选
                                if (items[which].equals("全选")) {
                                    if (isChecked) {
                                        for (int i = 0; i < length; i++) {
                                            itemState[i] = true;
                                            lv_Choose.setItemChecked(i, true);
                                        }
                                    } else {
                                        for (int i = 0; i < length; i++) {
                                            itemState[i] = false;
                                            lv_Choose.setItemChecked(i, false);
                                        }
                                    }
                                }
                                //如果取消了其它某一项，则“全选”项置为空
                                else {
                                    if (!isChecked){
                                        itemState[length - 1] = false;
                                        lv_Choose.setItemChecked(length - 1, false);
                                    }
                                }
                            }
                        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean first = true;
                                String selected_items = "";
                                for (int i = 0; i < length; i++) {
                                    if (itemState[i] && !items[i].equals("全选")) {
                                        if (first) {
                                            selected_items += items[i];
                                            first = false;
                                        } else {
                                            selected_items += "、" + items[i];
                                        }
                                    }
                                }
                                btn.setText(selected_items);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                btn.setText(getString(R.string.option_click_to_choose));
                            }
                        }).create();

                lv_Choose = builder.getListView();
                builder.show();
            }
        });
    }

    /**
     * 此方法根据长度返回一个boolean型空数组
     *
     * @param length 数组长度
     * @return boolean型空数组
     */
    private boolean[] generateBooleanArray(int length) {
        final boolean[] ret = new boolean[length];
        for (int i = 0; i < length; i++) {
            ret[i] = false;
        }
        return ret;
    }

    /**
     * 检查所有填写项是否合法
     *
     * @return true合法，false不合法
     */
    private boolean allItemOK() {
        if (et_Title.getText().toString().equals("")) {
            warnToast("请输入求职标题");
            return false;
        }
        if (btn_Subject.getText().toString().equals(getString(R.string.option_click_to_choose))) {
            warnToast("请至少选择一个科目");
            return false;
        }
        if (btn_Grade.getText().toString().equals(getString(R.string.option_click_to_choose))) {
            warnToast("请至少选择一个年级");
            return false;
        }
        if (btn_Time.getText().toString().equals(getString(R.string.option_click_to_choose))) {
            warnToast("请至少选择一个工作时间");
            return false;
        }
        if (et_Salary.getText().toString().equals("")) {
            warnToast("请输入工资需求");
            return false;
        }
        if (et_Address.getText().toString().equals("")) {
            warnToast("请输入工作地址");
            return false;
        }
        if (et_Phone.getText().toString().equals("")) {
            warnToast("请输入联系电话");
            return false;
        }
        if (et_Linkman.getText().toString().equals("")) {
            warnToast("请输入联系人");
            return false;
        }
        if (!cb_Agreement.isChecked()) {
            warnToast("请阅读并同意人人家教用户协议");
            return false;
        }
        return true;
    }

    /**
     * 将"、"分割的字符串转换为"||"分割的字符串
     *
     * @param inputStr "、"分割的字符串
     * @return "||"分割的字符串
     */
    private String changeToServerFormat(String inputStr) {
        String[] strs = inputStr.split("、");
        String retStr = strs[0];
        for (int i = 1; i < strs.length; i++) {
            retStr = retStr + "||" + strs[i];
        }
        return retStr;
    }

    /**
     * 发送做家教信息填写完成表单给服务器
     */
    private void doInfoPost() {
        Information info = new Information();

        //这是做家教信息
        info.setType(Information.TYPE_DO);

        //根据表单填写，设置info实例的各个属性
        try {
            info.setUser_Id(MethodUtils.getLocalUser().getId());
            info.setTitle(et_Title.getText().toString());
            info.setSubject(changeToServerFormat(btn_Subject.getText().toString()));
            info.setGrade(changeToServerFormat(btn_Grade.getText().toString()));
            info.setWorktime(changeToServerFormat(btn_Time.getText().toString()));
            info.setSalary(et_Salary.getText().toString());
            info.setDescription(et_Description.getText().toString());
            info.setAddress(et_Address.getText().toString());
            info.setTel(et_Phone.getText().toString());
            info.setNickname(MethodUtils.getLocalUser().getNickname());
            if (cb_Permit.isChecked()) {
                info.setPermit(0);
            } else {
                info.setPermit(1);
            }
            info.setContactname(et_Linkman.getText().toString());

            String obj = gson.toJson(info);
            Map<String, String> req = new HashMap<>();
            req.put("information", obj);
            volley_Post("tutorCenter/insert", req);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void volley_Post(String url_add, final Map<String, String> req) {
        String url = getString(R.string.url);
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                Map<String, Object> res = gson.fromJson(s, Map.class);
                if (res.get("result").toString().equals("0")) {
                    Toast.makeText(getActivity(), "发表成功", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "发表失败", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //请求失败处理
                Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return req;
            }
        };
        request.setTag("doPost");
        MyApplication.getHttpQueue().add(request);
    }

    private void warnToast(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }
}
