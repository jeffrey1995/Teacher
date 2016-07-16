package hc.teacher.application;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.text.ParseException;

import hc.teacher.entity.User;
import io.rong.imkit.RongIM;

import hc.teacher.data.DatabaseHelper;
import hc.teacher.utils.MethodUtils;

/**
 * Created by Administrator on 2015/10/15 0015.
 */
public class MyApplication extends Application {
    private static RequestQueue queue;

    public static DatabaseHelper dbHelper;
    public static boolean hasLoggedIn = false;

    //所有与图片相关的本地文件都在此目录下
    public static final String PATH_LOCAL_IMG = Environment.getExternalStorageDirectory() + "/localImg";
    public static final String PATH_USER_ICON = "/UserIcon.png";
    public static final String PATH_REAL_HEAD = "/RealHead.png";

    @Override
    public void onCreate() {
        super.onCreate();
        //httpRequest
        queue = Volley.newRequestQueue(getApplicationContext());

        // 创建/获取本地数据库
        dbHelper = new DatabaseHelper(this, "tutor", null, 1);

        //如果当前登录状态为未登录
        if(!hasLoggedIn){
            //检查本地数据库是否有保存密码
            if(MethodUtils.checkLocalUserExist()){
                Log.w("cry", "检查到保存密码，尝试自动登陆");

                try {
                    //如果有尝试登陆，更新hasLoggedIn
                    User user = MethodUtils.getLocalUser();
                    String account = user.getAccount();
                    String password = user.getPassword();
                    MethodUtils.updateHasLoggedIn(account, password, this);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        /*init rongcloud*/
        RongIM.init(this);
        MethodUtils.getRongIMConnection();
    }


    public static RequestQueue getHttpQueue() {return queue;}

}
