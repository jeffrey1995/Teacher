package hc.teacher.components.dialog;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
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
import hc.teacher.data.DatabaseHelper;
import hc.teacher.entity.User;
import hc.teacher.utils.MethodUtils;


/**
 * Created by cry on 2015/11/22.
 */

public class GenderModifyDialog extends Dialog {

    public static String DEFAULT_TITLE = "Title";

    //网络数据处理
    Gson gson = MethodUtils.getGson();

    //传入数据
    private String type_name;
    private String title;

    //UI引用
    private Button btn_Male;
    private Button btn_FeMale;
    private TextView tv_Title;
    private Context mContext;
    private ModifyDialogListener mDialogListener;

    /**
     * GenderModifyDialog 的构造函数
     *
     * @param context 传入context
     * @param dl      传入监听器
     */
    public GenderModifyDialog(Context context, ModifyDialogListener dl) {
        super(context);
        this.mContext = context;
        this.mDialogListener = dl;
        this.type_name = "";
        this.title = DEFAULT_TITLE;
    }

    public GenderModifyDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public GenderModifyDialog setType_name(String type_name) {
        this.type_name = type_name;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_gender_modify);
        initView();

        tv_Title.setText(title);

        btn_Male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGender(User.GENDER_MALE);
                hide();
            }
        });

        btn_FeMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGender(User.GENDER_FEMALE);
                hide();
            }
        });

    }

    private void initView() {
        btn_Male = (Button) findViewById(R.id.btn_Male);
        btn_FeMale = (Button) findViewById(R.id.btn_FeMale);
        tv_Title = (TextView) findViewById(R.id.tv_Title);
    }

    private void setGender(int gender) {
        Map<String, String> req = new HashMap<String, String>();
        try {
            req.put("id", MethodUtils.getLocalUser().getId().toString());
            req.put(type_name, "" + gender);
            volley_Post("updateUserInfo", req, gender);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "本地数据已被破坏", Toast.LENGTH_SHORT).show();
        }
        hide();
    }

    private void volley_Post(String url_add, final Map<String, String> req, final int gender) {
        String url = mContext.getString(R.string.url);
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                Map<String, Object> res = gson.fromJson(s, Map.class);
                if (res.get("result").equals("0")) {
                    //修改本地数据
                    DatabaseHelper dbHelper = MyApplication.dbHelper;
                    SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put(type_name, "" + gender);
                    dbWrite.update("user", cv, null, null);
                    mDialogListener.onResponse(true);
                } else if (res.get("result").equals("1")){
                    Toast.makeText(mContext, "修改失败", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //请求失败处理
                Toast.makeText(mContext, "网络错误", Toast.LENGTH_LONG).show();
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
}
