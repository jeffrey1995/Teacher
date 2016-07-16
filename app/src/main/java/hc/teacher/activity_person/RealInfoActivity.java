package hc.teacher.activity_person;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.jingchen.pulltorefresh.PullToRefreshLayout;

import java.io.File;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.components.crop_image.CropImageIntentBuilder;
import hc.teacher.data.DatabaseHelper;
import hc.teacher.entity.RealInfo;
import hc.teacher.entity.User;
import hc.teacher.utils.MediaStoreUtils;
import hc.teacher.utils.MethodUtils;

public class RealInfoActivity extends AppCompatActivity implements View.OnClickListener {
    //gson对象
    Gson gson = MethodUtils.getGson();

    private static final int CAMERA_REQUEST_CODE = 0;
    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int CROP_REQUEST_CODE = 2;

    private PullToRefreshLayout refreshView;

    //是否正在进行下拉刷新
    private boolean isPullRefreshing = false;

    //是否允许修改照片
    private boolean canEditPhoto = true;

    private ImageButton ibtn_GoBack;
    private EditText et_Name;
    private RadioButton rb_Male;
    private RadioButton rb_Female;
    private EditText et_School;
    private EditText et_IdentityNumber;
    private EditText et_Phone;
    private EditText et_Description;
    private Button btn_Submit;
    private ImageView iv_Head;
    private TextView tv_IdentifyState;

    private ProgressDialog pd_Waiting = null;

    //是否选择了真实照片
    private boolean hasSelectPhoto = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_info);

        //初始化，获得UI控件
        initView();

        //返回按钮监听
        ibtn_GoBack.setOnClickListener(this);
        //提交按钮监听
        btn_Submit.setOnClickListener(this);
        //上传照片监听
        iv_Head.setOnClickListener(this);

        //下拉layout监听
        refreshView.setOnPullListener(new PullToRefreshLayout.OnPullListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                isPullRefreshing = true;
                getRealInfo();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

            }
        });

        //设置当前的信息状态为未认证（默认）
        setInfoState(User.REALINFO_VERIFY_NO, null);

        //获得用户的认证信息
        getRealInfo();
    }

    private void initView() {
        //设置等待dialog样式
        pd_Waiting = new ProgressDialog(this, AlertDialog.THEME_HOLO_DARK);
        pd_Waiting.setCancelable(true);
        pd_Waiting.setIndeterminate(true);

        //获得可下拉layout，设为不可上拉
        refreshView = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        refreshView.setPullUpEnable(false);

        ibtn_GoBack = (ImageButton) findViewById(R.id.btn_GoBack);
        rb_Male = (RadioButton) findViewById(R.id.rb_Male);
        rb_Female = (RadioButton) findViewById(R.id.rb_Female);
        et_Name = (EditText) findViewById(R.id.et_Name);
        et_School = (EditText) findViewById(R.id.et_School);
        et_IdentityNumber = (EditText) findViewById(R.id.et_IdentityNumber);
        et_Phone = (EditText) findViewById(R.id.et_Phone);
        et_Description = (EditText) findViewById(R.id.et_Description);
        btn_Submit = (Button) findViewById(R.id.btn_Submit);
        iv_Head = (ImageView) findViewById(R.id.iv_Head);

        tv_IdentifyState = (TextView) findViewById(R.id.tv_IndentifyState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回按钮
            case R.id.btn_GoBack:
                finish();
                break;
            //提交按钮
            case R.id.btn_Submit:
                try {
                    attemptSubmit();
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplication(), "本地数据被破坏", Toast.LENGTH_LONG).show();
                }
                break;
            //点击头像
            case R.id.iv_Head:
                //如果不允许编辑头像则不做反应
                if (!canEditPhoto) break;
                //弹出提示用户拍照或选择一张照片
                new AlertDialog.Builder(RealInfoActivity.this)
                        .setTitle("选择照片")
                        .setItems(new String[]{"拍照", "从相册选择"},
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //选择拍照
                                        if (which == 0) {
                                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            Uri uri = Uri.fromFile(MethodUtils.getFile(MyApplication.PATH_LOCAL_IMG
                                                    + getString(R.string.path_temp_image)));
                                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                            startActivityForResult(intent, CAMERA_REQUEST_CODE);
                                        }
                                        //选择从相册选择
                                        else if (which == 1) {
                                            startActivityForResult(MediaStoreUtils.getPickImageIntent(RealInfoActivity.this), GALLERY_REQUEST_CODE);
                                        }
                                    }
                                }).show();
                break;
        }
    }

    private void stopPullRefreshing(int refreshResult) {
        if (isPullRefreshing) {
            refreshView.refreshFinish(refreshResult);
            isPullRefreshing = false;
        }
    }

    private void getRealInfo() {
        try {
            //如果当前登录用户提交过真实认证，则向服务器请求认证信息并显示
            if (MethodUtils.getLocalUser().getIdentify_state() != User.REALINFO_VERIFY_NO) {
                //向服务器发送请求
                Map<String, String> req = new HashMap<>();
                req.put("user_id", MethodUtils.getLocalUser().getId().toString());
                volley_PostGetRealInfo(req);
            } else {
                setInfoState(User.REALINFO_VERIFY_NO, null);
                stopPullRefreshing(PullToRefreshLayout.SUCCEED);
            }
        } catch (ParseException e) {
            Toast.makeText(getApplication(), "本地数据被破坏", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //创建剪裁完的图像
        File croppedImageFile = MethodUtils.getLocalImageFile(MyApplication.PATH_REAL_HEAD);
        Uri croppedImageUri = Uri.fromFile(croppedImageFile);

        //拍照请求的返回处理
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = Uri.fromFile(MethodUtils.getFile(MyApplication.PATH_LOCAL_IMG
                    + getString(R.string.path_temp_image)));
            startImageZoom(uri, croppedImageUri);
        }
        //图库请求的返回处理
        else if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            //当用户从图库挑选完一张图片，启动 CropImage Activity
            startImageZoom(data.getData(), croppedImageUri);
        }
        //裁剪请求的返回处理
        else if (requestCode == CROP_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap bm = BitmapFactory.decodeFile(croppedImageFile.getAbsolutePath());
            MethodUtils.deleteLocalTempImage(RealInfoActivity.this);
            iv_Head.setImageBitmap(bm);
            hasSelectPhoto = true;
        }

    }

    /**
     * 启动图片剪裁
     *
     * @param uri             待裁剪图片文件的uri
     * @param croppedImageUri 剪裁完图片文件的uri
     */
    private void startImageZoom(Uri uri, Uri croppedImageUri) {

        //设置输出图片文件的Uri，裁剪框设为蓝色

        CropImageIntentBuilder cropImage = new CropImageIntentBuilder(500, 700, croppedImageUri);
        cropImage.setOutlineColor(0xFF03A9F4);
        cropImage.setSourceImage(uri);

        startActivityForResult(cropImage.getIntent(this), CROP_REQUEST_CODE);
    }

    private void attemptSubmit() throws ParseException {
        RealInfo realInfo = new RealInfo();
        realInfo.setUser_id(MethodUtils.getLocalUser().getId());
        realInfo.setName(et_Name.getText().toString());
        realInfo.setGender(rb_Male.isChecked() ? 1 : 2);
        realInfo.setSchool(et_School.getText().toString());
        realInfo.setIdentity_number(et_IdentityNumber.getText().toString());
        realInfo.setTel(et_Phone.getText().toString());
        realInfo.setIntroduction(et_Description.getText().toString());

        //填写项目不能为空
        if (realInfo.getName().equals("") || realInfo.getSchool().equals("") ||
                realInfo.getIdentity_number().equals("") || realInfo.getTel().equals("") ||
                realInfo.getIntroduction().equals("")) {
            Toast.makeText(getApplication(), "填写项目不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        //身份证号必须有效
        if (realInfo.getIdentity_number().length() != 18) {
            Toast.makeText(getApplication(), "身份证号无效", Toast.LENGTH_SHORT).show();
            return;
        }
        //手机必须有效
        if (realInfo.getTel().length() != 11) {
            Toast.makeText(getApplication(), "手机号码无效", Toast.LENGTH_SHORT).show();
            return;
        }
        //必须选择一张照片
        if (!hasSelectPhoto) {
            Toast.makeText(getApplication(), "请上传真实照片", Toast.LENGTH_SHORT).show();
            return;
        }

        //向服务器发送数据
        Map<String, String> req = new HashMap<>();
        req.put("realInfo", gson.toJson(realInfo));
        req.put("head", MethodUtils.getBase64String(MethodUtils.getLocalImageFile(MyApplication.PATH_REAL_HEAD)));
        volley_PostSubmit(req);
    }

    /**
     * 提交填写数据的 volly_post
     *
     * @param req
     */
    private void volley_PostSubmit(final Map<String, String> req) {
        pd_Waiting.setMessage("正在提交");
        pd_Waiting.show();

        String url = getString(R.string.url);
        url = url + "realInfo/submitRealInfo";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                Map<String, Object> res = gson.fromJson(s, Map.class);
                if ((Boolean) res.get("result")) {
                    Toast.makeText(getApplication(), "提交成功", Toast.LENGTH_SHORT).show();
                    //修改本地数据
                    DatabaseHelper dbHelper = MyApplication.dbHelper;
                    SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
                    //User表中审核状态变为审核中
                    ContentValues cv = new ContentValues();
                    cv.put("identify_state", User.REALINFO_VERIFY_IS_CHECKING);
                    dbWrite.update("user", cv, null, null);
                    //当前界面显示状态变为审核中
                    setInfoState(RealInfo.STATE_IS_CHECKING, null);
                } else {
                    Toast.makeText(getApplication(), "提交失败", Toast.LENGTH_SHORT).show();
                }
                pd_Waiting.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //请求失败处理
                Toast.makeText(getApplication(), "提交失败", Toast.LENGTH_SHORT).show();
                pd_Waiting.dismiss();
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

    /**
     * 获得用户最新数据的 volly_post
     *
     * @param req
     */
    private void volley_PostGetRealInfo(final Map<String, String> req) {
        pd_Waiting.setMessage("请稍候");
        pd_Waiting.show();
        String url = getString(R.string.url);
        url = url + "realInfo/getRealInfo";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                Map<String, Object> res = gson.fromJson(s, Map.class);
                if ((Boolean) res.get("result")) {
                    //取出realInfo实例
                    RealInfo realInfo = gson.fromJson(res.get("realInfo").toString(), RealInfo.class);
                    setInfoState(realInfo.getState(), realInfo);
                    stopPullRefreshing(PullToRefreshLayout.SUCCEED);
                } else {
                    Toast.makeText(getApplication(), "服务器出现故障", Toast.LENGTH_SHORT).show();
                    stopPullRefreshing(PullToRefreshLayout.FAIL);
                }
                pd_Waiting.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //请求失败处理
                Toast.makeText(getApplication(), "网络错误", Toast.LENGTH_LONG).show();
                pd_Waiting.dismiss();
                stopPullRefreshing(PullToRefreshLayout.FAIL);
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

    /**
     * 设置各条目是否为可编辑状态
     *
     * @param canEdit true:可编辑 false:不可编辑
     */
    private void setEditable(boolean canEdit) {
        et_Name.setCursorVisible(canEdit);
        et_Name.setClickable(canEdit);
        et_Name.setEnabled(canEdit);
        rb_Male.setClickable(canEdit);
        rb_Female.setClickable(canEdit);
        et_School.setCursorVisible(canEdit);
        et_School.setClickable(canEdit);
        et_School.setEnabled(canEdit);
        et_IdentityNumber.setCursorVisible(canEdit);
        et_IdentityNumber.setClickable(canEdit);
        et_IdentityNumber.setEnabled(canEdit);
        et_Phone.setCursorVisible(canEdit);
        et_Phone.setClickable(canEdit);
        et_Phone.setEnabled(canEdit);
        et_Description.setCursorVisible(canEdit);
        et_Description.setClickable(canEdit);
        et_Description.setEnabled(canEdit);

        canEditPhoto = canEdit;
    }

    /**
     * 根据用户当前真实信息的不同状态，呈现界面
     *
     * @param state 真实信息状态
     */
    private void setInfoState(int state, RealInfo realInfo) {
        switch (state) {
            //未认证
            case User.REALINFO_VERIFY_NO:
                btn_Submit.setVisibility(View.VISIBLE);
                tv_IdentifyState.setText("未认证");
                stopPullRefreshing(PullToRefreshLayout.SUCCEED);
                setEditable(true);
                break;
            //审核中
            case RealInfo.STATE_IS_CHECKING:
                btn_Submit.setVisibility(View.INVISIBLE);
                if (realInfo != null) loadRealInfo(realInfo);
                tv_IdentifyState.setText("审核中");
                setEditable(false);
                break;
            //已审核
            case RealInfo.STATE_HAS_CHECKED:
                btn_Submit.setVisibility(View.INVISIBLE);
                if (realInfo != null) loadRealInfo(realInfo);
                tv_IdentifyState.setText("已认证");
                setEditable(false);
                break;
        }
    }

    private void loadRealInfo(RealInfo realInfo) {
        et_Name.setText(realInfo.getName());
        et_Phone.setText(realInfo.getTel());
        et_School.setText(realInfo.getSchool());
        et_Description.setText(realInfo.getIntroduction());
        et_IdentityNumber.setText(realInfo.getIdentity_number());
        if (realInfo.getGender() == RealInfo.GENDER_MALE) {
            rb_Male.setChecked(true);
        } else {
            rb_Female.setChecked(true);
        }

        //下载真实照片
        String url = getString(R.string.url);
        url = url + realInfo.getReal_head();
        final LruCache<String, Bitmap> lrucache = new LruCache<String, Bitmap>(20);
        ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache() {
            @Override
            public void putBitmap(String key, Bitmap value) {
                lrucache.put(key, value);
            }

            @Override
            public Bitmap getBitmap(String key) {
                return lrucache.get(key);
            }
        };
        ImageLoader imageLoader = new ImageLoader(MyApplication.getHttpQueue(), imageCache);
        ImageLoader.ImageListener listener = imageLoader.getImageListener(iv_Head, R.drawable.upload_real_head, R.drawable.upload_real_head);
        imageLoader.get(url, listener);
    }
}
