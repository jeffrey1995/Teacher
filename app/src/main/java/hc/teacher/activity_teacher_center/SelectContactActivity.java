package hc.teacher.activity_teacher_center;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;

import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.components.dialog.ConfirmDialog;
import hc.teacher.components.dialog.ModifyDialogListener;
import hc.teacher.data.DatabaseHelper;
import hc.teacher.entity.Contact;
import hc.teacher.entity.ContactInfo;
import hc.teacher.utils.MethodUtils;

import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectContactActivity extends AppCompatActivity {

    private static final int ADD_NEW_REQUEST = 1;
    private static final int EDIT_REQUEST = 2;

    //网络数据相关
    Gson gson = MethodUtils.getGson();

    //填充列表的数据数组
    private ArrayList<ContactInfo> data;

    //UI引用
    RecyclerView rv_Contact;
    TextView tv_AddNewContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        //初始化视图
        initView();

        //设置 recyclerView 的布局
        GridLayoutManager glm = new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false);
        rv_Contact.setLayoutManager(glm);

        //获得联系方式数据
        getContactData();

        //为 recyclerView 绑定数据
        rv_Contact.setAdapter(new ContactAdapter());

        //跳转到新增联系方式
        tv_AddNewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SelectContactActivity.this, AddNewContactActivity.class), ADD_NEW_REQUEST);
            }
        });
    }

    private void initView() {
        rv_Contact = (RecyclerView) findViewById(R.id.rv_Contact);
        tv_AddNewContact = (TextView) findViewById(R.id.tv_AddNewContact);
    }

    /**
     * 当收到更改联系方式或者新增联系方式的Activity返回结果时，作出处理（刷新）
     *
     * @param requestCode 请求码
     * @param resultCode  返回码
     * @param data        传递的 intent 对象
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Log.w("cryDebug", "我该刷新一下");
            getContactData();
            ContactAdapter contactAdapter = (ContactAdapter) rv_Contact.getAdapter();
            contactAdapter.notifyItemRangeChanged(0, contactAdapter.getItemCount());
            contactAdapter.notifyDataSetChanged();
//            notifyAll();
        }
    }

    /**
     * 从本地数据库中读取出该人的联系方式
     */
    private void getContactData() {
        //连接到数据库
        DatabaseHelper dbHelper = MyApplication.dbHelper;
        SQLiteDatabase dbRead = dbHelper.getReadableDatabase();
        //获得联系方式
        Cursor c = dbRead.query("contact_info", null, null, null, null, null, null);
        data = new ArrayList<>();
        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndex("id"));
            int user_id = c.getInt(c.getColumnIndex("user_id"));
            String name = c.getString(c.getColumnIndex("name"));
            String tel = c.getString(c.getColumnIndex("tel"));
            String address = c.getString(c.getColumnIndex("address"));
            data.add(new ContactInfo(id, user_id, name, tel, address));
        }
        c.close();
        dbRead.close();
    }

    /**
     * 向服务器发起删除一个contactInfo的请求并执行相应操作
     *
     * @param contactInfoID 待删除的contactInfo的ID
     * @param position      待删除contactInfo在列表中的位置
     * @return
     */
    private String deleteVolleyPost(final int contactInfoID, final int position) {
        final Map<String, String> req = new HashMap<>();
        req.put("contactInfoId", "" + contactInfoID);
        String url = getString(R.string.url);
        url = url + "contactInfo/deleteContactInfo";
        String response_data = "";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                Map<String, Object> res = gson.fromJson(s, Map.class);
                if ((Boolean) res.get("result")) {
                    Toast.makeText(getApplication(), "删除成功", Toast.LENGTH_LONG).show();
                    //更新本地数据库
                    deleteContactInfoFromLocalDB(contactInfoID);
                    //更新列表显示
                    ContactAdapter contactAdapter = (ContactAdapter) rv_Contact.getAdapter();
                    contactAdapter.removeItem(position);
                } else {
                    MyApplication.hasLoggedIn = false;
                    Toast.makeText(getApplication(), "删除失败", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //请求失败处理
                Toast.makeText(getApplication(), "网络错误", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return req;
            }
        };
        request.setTag("add_new_contactInfo_post");
        MyApplication.getHttpQueue().add(request);
        return response_data;
    }

    /**
     * 根据 contactInfo 的 ID 从本地数据库中找到并删除该条 contactInfo
     *
     * @param contactInfoID 待删除的 contactInfo 的 ID
     */
    private void deleteContactInfoFromLocalDB(int contactInfoID) {
        //连接到数据库
        DatabaseHelper dbHelper = MyApplication.dbHelper;
        SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();

        //删除该条数据
        dbWrite.delete("contact_info", "id = ?", new String[]{"" + contactInfoID});

        //关闭数据库连接
        dbWrite.close();
        dbHelper.close();
    }

    /**
     * 联系方式的数据适配器
     */
    private class ContactAdapter extends RecyclerView.Adapter {

        /**
         * 一条Contact联系方式对应的holder
         */
        class ContactHolder extends RecyclerView.ViewHolder {

            //UI引用
            private TextView tv_Name;
            private TextView tv_Phone;
            private TextView tv_Address;
            private RelativeLayout rl_Item;
            private Button btn_Edit;

            public TextView getTv_Name() {
                return tv_Name;
            }

            public TextView getTv_Phone() {
                return tv_Phone;
            }

            public TextView getTv_Address() {
                return tv_Address;
            }

            public RelativeLayout getRl_Item() {
                return rl_Item;
            }

            public Button getBtn_Edit() {
                return btn_Edit;
            }

            public ContactHolder(View itemView) {
                super(itemView);

                tv_Name = (TextView) itemView.findViewById(R.id.tv_Name);
                tv_Phone = (TextView) itemView.findViewById(R.id.tv_Phone);
                tv_Address = (TextView) itemView.findViewById(R.id.tv_Address);
                rl_Item = (RelativeLayout) itemView.findViewById(R.id.rl_Item);
                btn_Edit = (Button) itemView.findViewById(R.id.btn_Edit);
            }
        }

        /**
         * 移除某一条目的数据和显示
         *
         * @param position 待删除项目的位置
         */
        public void removeItem(int position) {
            data.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount() - position);
        }

        /**
         * 将一个数据单元显示在对应 Contact holder 上
         *
         * @param contactHolder 待绑定holder
         * @param contactInfo   contactInfo实体类对象
         */
        private void bindContactItem(ContactHolder contactHolder, ContactInfo contactInfo) {
            contactHolder.getTv_Name().setText(contactInfo.getContactName());
            contactHolder.getTv_Phone().setText(contactInfo.getTel());
            contactHolder.getTv_Address().setText(contactInfo.getAddress());
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_cell, null);
            return new ContactHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            bindContactItem((ContactHolder) holder, data.get(position));
            View v = holder.itemView;

            ContactHolder contactHolder = (ContactHolder) holder;

            //点击选择
            contactHolder.getRl_Item().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContactInfo selectedContactInfo = data.get(position);
                    Intent intent = new Intent();
                    intent.putExtra("CONTACT_INFO_ID", selectedContactInfo.getId());
                    setResult(0, intent);
                    finish();
                }
            });

            //长按删除
            contactHolder.getRl_Item().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    ConfirmDialog confirmDialog = new ConfirmDialog(SelectContactActivity.this, new ModifyDialogListener() {
                        @Override
                        public void onResponse(boolean result) {
                            if (result) {
                                ContactInfo selectedContactInfo = data.get(position);
                                //向服务器发送删除请求
                                deleteVolleyPost(selectedContactInfo.getId(), position);
                            }
                        }
                    });
                    confirmDialog.setTitle("删除这个地址?").show();

                    return false;
                }
            });

            //点击编辑跳转到编辑页面
            contactHolder.getBtn_Edit().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContactInfo selectedContactInfo = data.get(position);
                    Intent intent = new Intent(SelectContactActivity.this, EditContactActivity.class);
                    intent.putExtra("CONTACT_INFO_ID", selectedContactInfo.getId());
                    startActivityForResult(intent, EDIT_REQUEST);
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
}
