package hc.teacher.activity_person;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.components.crop_image.CropImageIntentBuilder;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import hc.teacher.entity.User;
import hc.teacher.utils.MediaStoreUtils;
import hc.teacher.utils.MethodUtils;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class UserIconActivity extends AppCompatActivity {
    //数据处理
    Gson gson = MethodUtils.getGson();

    ImageView iv_UserIcon;
    Button btn_ChangeUserIcon;

    private static int CAMERA_REQUEST_CODE = 1;
    private static int GALLERY_REQUEST_CODE = 2;
    private static int CROP_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_icon);

        initView();

        //显示当前用户头像
        Bitmap bm = BitmapFactory.decodeFile(MethodUtils.getLocalImageFile(MyApplication.PATH_USER_ICON).getPath());
        iv_UserIcon.setImageBitmap(bm);

        //点击更换头像按钮触发事件
        btn_ChangeUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(UserIconActivity.this)
                        .setTitle("更换头像")
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
                                            startActivityForResult(MediaStoreUtils.getPickImageIntent(UserIconActivity.this), GALLERY_REQUEST_CODE);
                                        }
                                    }
                                }).show();
            }
        });
    }

    private void initView() {
        iv_UserIcon = (ImageView) findViewById(R.id.iv_UserIcon);
        btn_ChangeUserIcon = (Button) findViewById(R.id.btn_ChangeUserIcon);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //创建剪裁完的图像
        File croppedImageFile = MethodUtils.getLocalImageFile(MyApplication.PATH_USER_ICON);
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

            //上传至服务器
            try {
                Map<String, String> req = new HashMap<>();
                req.put("id", MethodUtils.getLocalUser().getId().toString());
                req.put("account", MethodUtils.getLocalUser().getAccount());
                req.put("head", MethodUtils.getBase64String(MethodUtils.getLocalImageFile(MyApplication.PATH_USER_ICON)));
                volley_Post("updateHead", req);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            iv_UserIcon.setImageBitmap(bm);

            MethodUtils.deleteLocalTempImage(UserIconActivity.this);

        }

    }

    /** 启动图片剪裁
     *
     * @param uri 待裁剪图片文件的uri
     * @param croppedImageUri 剪裁完图片文件的uri
     */
    private void startImageZoom(Uri uri, Uri croppedImageUri) {

        //设置输出图片文件的Uri，裁剪框设为蓝色，并将裁切出的大小设置为200*200 像素大小的正方形

        CropImageIntentBuilder cropImage = new CropImageIntentBuilder(200, 200, croppedImageUri);
        cropImage.setOutlineColor(0xFF03A9F4);
        cropImage.setSourceImage(uri);

        startActivityForResult(cropImage.getIntent(this), CROP_REQUEST_CODE);
    }

    private void volley_Post(String url_add, final Map<String, String> req) {
        String url = getString(R.string.url);
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                Map<String, Object> res = gson.fromJson(s, Map.class);
                if ((Boolean) res.get("result")) {
                    Toast.makeText(getApplication(), "上传成功", Toast.LENGTH_SHORT).show();
                    /*设置融云聊天当前用户信息*/
                    finish();
                } else {
                    Toast.makeText(getApplication(), "上传失败", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //请求失败处理
                Toast.makeText(getApplication(), "网络错误", Toast.LENGTH_LONG).show();
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
