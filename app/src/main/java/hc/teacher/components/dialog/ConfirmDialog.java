package hc.teacher.components.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import hc.teacher.application.R;
import hc.teacher.utils.MethodUtils;


/**
 * Created by cry on 2015/11/22.
 */

public class ConfirmDialog extends Dialog {

    public static String DEFAULT_TITLE = "Title";

    //网络数据处理
    Gson gson = MethodUtils.getGson();

    //传入数据
    private String type_name;
    private String title;

    //UI引用
    private Button btn_ok;
    private Button btn_cancel;
    private TextView tv_Title;
    private Context mContext;
    private ModifyDialogListener mDialogListener;

    /**
     * GenderModifyDialog 的构造函数
     *
     * @param context 传入context
     * @param dl      传入监听器
     */
    public ConfirmDialog(Context context, ModifyDialogListener dl) {
        super(context);
        this.mContext = context;
        this.mDialogListener = dl;
        this.type_name = "";
        this.title = DEFAULT_TITLE;
    }

    public ConfirmDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm);
        initView();

        tv_Title.setText(title);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogListener.onResponse(true);
                hide();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogListener.onResponse(false);
                hide();
            }
        });

    }

    private void initView() {
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        tv_Title = (TextView) findViewById(R.id.tv_Title);
    }
}
