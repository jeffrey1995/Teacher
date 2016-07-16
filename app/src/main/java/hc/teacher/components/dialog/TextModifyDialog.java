package hc.teacher.components.dialog;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import hc.teacher.utils.MethodUtils;

/**
 * Created by cry on 2015/11/21.
 */
public class TextModifyDialog extends Dialog {

    public static int SHORT_TEXT_INPUT = 0;
    public static int LONG_TEXT_INPUT = 1;

    //网络数据处理
    Gson gson = MethodUtils.getGson();

    //传入数据
    private String initStr;
    private String type_name;
    private String title;
    private String hint;
    private int inputType;

    //UI引用
    private LinearLayout ll_MainContainer;
    private TextView tv_Title;
    private EditText et_Input;
    private Button btn_Cancel;
    private Button btn_Submit;
    private Context mContext;
    private ModifyDialogListener mDialogListener;

    /**
     * TextModifyDialog 的构造函数
     *
     * @param context 传入context
     * @param dl      传入监听器
     */
    public TextModifyDialog(Context context, ModifyDialogListener dl) {
        super(context);
        this.mContext = context;
        this.mDialogListener = dl;
        this.initStr = "";
        this.type_name = "";
        this.hint = "";
        this.inputType = SHORT_TEXT_INPUT;
    }

    /**
     * TextModifyDialog 的构造函数
     *
     * @param context   传入context
     * @param dl        传入监听器
     * @param initStr   待更改的初始字符串
     * @param type_name 待更改数据数据库中对应属性名
     */
    public TextModifyDialog(Context context, ModifyDialogListener dl, String initStr, String type_name) {
        super(context);
        this.mContext = context;
        this.mDialogListener = dl;
        this.initStr = initStr;
        this.type_name = type_name;
    }

    public TextModifyDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public TextModifyDialog setType_name(String type_name) {
        this.type_name = type_name;
        return this;
    }

    public TextModifyDialog setInitStr(String initStr) {
        this.initStr = initStr;
        return this;
    }

    public TextModifyDialog setHint(String hint) {
        this.hint = hint;
        return this;
    }

    public TextModifyDialog setInputType(int inputType) {
        this.inputType = inputType;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_text_modify);
        initView();

        tv_Title.setText(title);
        et_Input.setText(initStr);
        et_Input.setHint(hint);

        //设置布局样式
        if(inputType == SHORT_TEXT_INPUT){
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ll_MainContainer.getLayoutParams();
            layoutParams.height = MethodUtils.dip2px(mContext, 150);
            ll_MainContainer.setLayoutParams(layoutParams);
            et_Input.setSingleLine(true);
            et_Input.setMaxLines(1);
            et_Input.setGravity(Gravity.CENTER_VERTICAL);
            et_Input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        }else if(inputType == LONG_TEXT_INPUT){
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ll_MainContainer.getLayoutParams();
            layoutParams.height = MethodUtils.dip2px(mContext, 250);
            ll_MainContainer.setLayoutParams(layoutParams);
            et_Input.setSingleLine(false);
            et_Input.setMaxLines(5);
            et_Input.setGravity(Gravity.START);
            et_Input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(240)});
        }

        btn_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> req = new HashMap<String, String>();
                try {
                    req.put("id", MethodUtils.getLocalUser().getId().toString());
                    req.put(type_name, et_Input.getText().toString());
                    volley_Post("updateUserInfo", req);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "本地数据已被破坏", Toast.LENGTH_SHORT).show();
                }
                hide();
            }
        });

        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

    }

    private void initView() {
        ll_MainContainer = (LinearLayout) findViewById(R.id.ll_MainContainer);
        tv_Title = (TextView) findViewById(R.id.tv_Title);
        et_Input = (EditText) findViewById(R.id.et_Input);
        btn_Cancel = (Button) findViewById(R.id.btn_Cancel);
        btn_Submit = (Button) findViewById(R.id.btn_Submit);
    }

    public void showKeyboard() {
        et_Input.requestFocus();
        InputMethodManager imm = (InputMethodManager) et_Input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }

    private void volley_Post(String url_add, final Map<String, String> req) {
        String url = mContext.getString(R.string.url);
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                Map<String, Object> res = gson.fromJson(s, Map.class);
                if (res.get("result").equals("0"))  {
                    //修改本地数据
                    DatabaseHelper dbHelper = MyApplication.dbHelper;
                    SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put(type_name, et_Input.getText().toString());
                    dbWrite.update("user", cv, null, null);
                    mDialogListener.onResponse(true);
                } else if (res.get("result").equals("1"))  {
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
