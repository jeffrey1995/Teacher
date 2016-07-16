package hc.teacher.utils;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import java.net.HttpURLConnection;
import java.net.URL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.data.DatabaseHelper;
import hc.teacher.entity.ContactInfo;
import hc.teacher.entity.User;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

//ToDo:工具类:
public class MethodUtils {

    //数据处理
    final private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();

    public static Gson getGson() {
        return gson;
    }

    public final static List<Map<String, Object>> listKeyMaps(String jsonString) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            list = gson.fromJson(jsonString,
                    new TypeToken<List<Map<String, Object>>>() {
                    }.getType());
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("数据解析错误（List）");
        }
        return list;
    }

    public final static Map<String, Object> mapKey(String jsonString) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            map = gson.fromJson(jsonString,
                    new TypeToken<Map<String, Object>>() {
                    }.getType());
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("数据解析错误（Map）");
        }
        return map;
    }


    /**
     * 通过url获取图片，主要用在listview
     */
    public static Bitmap getBitmap(String path) throws IOException {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
            InputStream inputStream = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }
        return null;
    }

    /**
     * get setter by parameter's name
     *
     * @param para
     * @return setter.tostring();
     */
    public static String getSetMethodNameByParam(String para) {

        return "set" + para.substring(0, 1).toUpperCase() + para.substring(1, para.length());
    }

    /**
     * invoke setter method by reflection
     *
     * @param className
     * @param entity    entity_class
     * @param param     method's parameters
     */

    public static void call(String className, Object entity, String methodName, Object param) {

        try {
            Class clazz = Class.forName(className);
            Method method = null;
            if (param instanceof java.sql.Date)
                method = clazz.getDeclaredMethod(methodName, java.sql.Date.class);
            else
                method = clazz.getDeclaredMethod(methodName, param.getClass());

            method.invoke(entity, param);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 写文件
     *
     * @param filePath 文件路径
     * @param s        要写入的字符串
     * @throws IOException
     */
    public static void writeFile(String filePath, String s) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        PrintWriter out = new PrintWriter(fw);
        out.write(s);
        out.println();
        fw.close();
        out.close();
    }

    /**
     * 读取文件，返回文件内容的字符串
     *
     * @param path 文件路径
     * @return 文件内容的字符串
     */
    public static String ReadFile(String path) {
        File file = new File(path);
        BufferedReader reader = null;
        String ret_Str = "";
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                ret_Str = ret_Str + tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return ret_Str;
    }

    /**
     * 从本地数据库中取出当前登陆者user实例
     * 如果未登陆，会返回一个默认构造的user实例（id为-1）
     *
     * @return 当前登录user
     */
    public static User getLocalUser() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //连接到数据库
        DatabaseHelper dbHelper = MyApplication.dbHelper;
        SQLiteDatabase dbRead = dbHelper.getReadableDatabase();
        //获得用户
        Cursor c = dbRead.query("user", null, null, null, null, null, null);
        User user = new User();
        while (c.moveToNext()) {
            user.setId(c.getInt(c.getColumnIndex("id")));
            user.setAccount(c.getString(c.getColumnIndex("account")));
            user.setPassword(c.getString(c.getColumnIndex("password")));
            user.setIdentity(c.getInt(c.getColumnIndex("identity")));
            user.setNickname(c.getString(c.getColumnIndex("nickname")));
            user.setGender(c.getInt(c.getColumnIndex("gender")));
            user.setAddress(c.getString(c.getColumnIndex("address")));
            user.setEmail(c.getString(c.getColumnIndex("email")));
            user.setQQNumber(c.getString(c.getColumnIndex("qqnumber")));
            user.setTel(c.getString(c.getColumnIndex("tel")));
            user.setIsPublic(c.getInt(c.getColumnIndex("ispublic")));
            user.setDescription(c.getString(c.getColumnIndex("description")));
            user.setHead(c.getString(c.getColumnIndex("head")));
            user.setToken(c.getString(c.getColumnIndex("token")));
            user.setIdentify_state(c.getInt(c.getColumnIndex("identify_state")));
            java.util.Date utilRegisterDate = simpleDateFormat.parse(c.getString(c.getColumnIndex("register_date")));
            user.setRegister_date(new java.sql.Date(utilRegisterDate.getTime()));
        }
        c.close();
        dbRead.close();
        return user;
    }

    /**
     * 清空本地数据库中的user表
     */
    public static void clearLocalUser() {
        //连接到数据库
        DatabaseHelper dbHelper = MyApplication.dbHelper;
        SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();

        //清空之前的数据
        dbWrite.delete("user", null, null);
        dbWrite.close();
        dbHelper.close();
    }

    /**
     * 检查客户端数据库是否已有保存的账号
     *
     * @return true表示已有，false表示没有
     */
    public static boolean checkLocalUserExist() {
        //连接到数据库
        DatabaseHelper dbHelper = MyApplication.dbHelper;
        SQLiteDatabase dbRead = dbHelper.getReadableDatabase();
        Cursor c = dbRead.query("user", null, null, null, null, null, null);
        int cnt = 0;
        cnt = c.getCount();
        c.close();
        dbRead.close();
        Log.w("cry", "本地user表条目数： " + cnt);
        return cnt > 0;
    }

    /**
     * 将一个contactInfo对象实例插入为本地contact_info数据表中的一条记录（插入至contact_info表）
     *
     * @param contactInfo contactInfo对象
     */
    public static void insertContactInfoToLocalDB(ContactInfo contactInfo) {

        //连接到数据库
        DatabaseHelper dbHelper = MyApplication.dbHelper;
        SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();

        //插入数据
        ContentValues cv = new ContentValues();
        cv.put("id", contactInfo.getId());
        cv.put("user_id", contactInfo.getUserId());
        cv.put("name", contactInfo.getContactName());
        cv.put("tel", contactInfo.getTel());
        cv.put("address", contactInfo.getAddress());
        dbWrite.insert("contact_info", null, cv);

        //断开数据库连接
        dbWrite.close();
        dbHelper.close();
    }

    /**
     * 将一个contactInfo对象实例转换成本地contact_info数据表中的一条记录（插入至contact_info表）
     *
     * @param contactInfo contactInfo对象
     */
    public static void updateContactInfoInLocalDB(ContactInfo contactInfo) {

        //连接到数据库
        DatabaseHelper dbHelper = MyApplication.dbHelper;
        SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();

        //更新数据
        ContentValues cv = new ContentValues();
        cv.put("user_id", contactInfo.getUserId());
        cv.put("name", contactInfo.getContactName());
        cv.put("tel", contactInfo.getTel());
        cv.put("address", contactInfo.getAddress());
        dbWrite.update("contact_info", cv, "id = ?", new String[]{"" + contactInfo.getId()});

        //断开数据库连接
        dbWrite.close();
        dbHelper.close();
    }

    /**
     * 根据 contactInfo 的 ID 从本地数据库中取出对应contactInfo实例
     * 如果没有，会返回一个默认构造的contactInfo实例
     *
     * @return 当前登录user
     */
    public static ContactInfo getLocalContactInfoById(int contactInfoId) {
        //连接到数据库
        DatabaseHelper dbHelper = MyApplication.dbHelper;
        SQLiteDatabase dbRead = dbHelper.getReadableDatabase();
        //获得contactInfo
        Cursor c = dbRead.query("contact_info", null, "id=?", new String[]{"" + contactInfoId}, null, null, null);
        ContactInfo contactInfo = new ContactInfo();
        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndex("id"));
            int user_id = c.getInt(c.getColumnIndex("user_id"));
            String name = c.getString(c.getColumnIndex("name"));
            String tel = c.getString(c.getColumnIndex("tel"));
            String address = c.getString(c.getColumnIndex("address"));
            contactInfo = new ContactInfo(id, user_id, name, tel, address);
        }
        c.close();
        dbRead.close();
        return contactInfo;
    }

    /**
     * 向服务器发起帐密登录验证请求，以更新本地是否账户在线标识
     *
     * @param account 账户名
     * @param password 密码
     */
    public static void updateHasLoggedIn(String account, String password, final Context context) {
        Log.w("cry", "正在重新更新本地登录状态");
        final Map<String, String> req = new HashMap<String, String>();
        req.put("account", account);
        req.put("password", password);

        String url = context.getString(R.string.url);
        url = url + "login";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                Map<String, Object> res = gson.fromJson(s, Map.class);
                if ((Boolean) res.get("result")) {
                    MyApplication.hasLoggedIn = true;
                    //重新请求一次用户头像
                    try {
                        loadImageVolley(context);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Log.w("cry", "状态更新为已登录");
                } else {
                    MyApplication.hasLoggedIn = false;
                    Log.w("cry", "状态更新为未登录");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //请求失败处理
                MyApplication.hasLoggedIn = false;
                Log.w("cry", "网络错误");
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

    public static void getRongIMConnection() {

        /**
         * IMKit SDK调用第二步
         *
         * 建立与服务器的连接
         *
         */
        String token = "";
        try {
            //本地读取用户token
            token = MethodUtils.getLocalUser().getToken().toString();
            Log.i("token:-----------", token);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (!token.equals("")) {
            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {
                    //当token不正确时，应该重新向服务器请求新的Token
                    Log.i("token", "incorrect");
                }

                @Override
                public void onSuccess(String s) {
                    Log.i("token", s);
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    //融云连接失败，返回相应错误代码
                    Log.i("token", "error");
                }
            });
        }
    }

    /**
     * 获得用户的头像文件
     *
     * @return 用户的头像文件对象，但硬盘上这个文件此时不一定存在
     */
    public static File getLocalImageFile(String path) {
        File tmpDir = new File(MyApplication.PATH_LOCAL_IMG);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        String imgAbsolutePath = tmpDir.getAbsolutePath() + path;
        return new File(imgAbsolutePath);
    }

    /**
     * 获得文件
     *
     * @param absolutePath 绝对路径
     * @return 该路径对应的文件
     */
    public static File getFile(String absolutePath) {
        return new File(absolutePath);
    }

    /**
     * 清除本地临时拍照文件
     */
    public static void deleteLocalTempImage(Context mContext) {
        MethodUtils.getFile(MyApplication.PATH_LOCAL_IMG
                + mContext.getString(R.string.path_temp_image)).delete();
    }

    /**
     * 请求用户的头像
     */
    public static void loadImageVolley(Context context) throws ParseException {
        String url = context.getString(R.string.url);
        url = url + "public/images/" + MethodUtils.getLocalUser().getId() + ".png";
        final LruCache<String, Bitmap> lrucache = new LruCache<String, Bitmap>(20);
        ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache() {
            @Override
            public void putBitmap(String key, Bitmap value) {
                lrucache.put(key, value);
                saveBitmap(value);
            }

            @Override
            public Bitmap getBitmap(String key) {
                return lrucache.get(key);
            }
        };

        //// TODO: 2015/11/19 删掉这个异步加载器
        ImageLoader imageLoader = new ImageLoader(MyApplication.getHttpQueue(), imageCache);
        ImageLoader.ImageListener listener = imageLoader.getImageListener(new ImageView(context), R.drawable.headimage01, R.drawable.headimage01);
        imageLoader.get(url, listener);
    }

    /**
     * 将Bitmap保存为本地PNG图片，返回保存文件的URI
     *
     * @param bm 输入的Bitmap
     * @return 保存结果的文件URI
     */
    public static Uri saveBitmap(Bitmap bm) {

        File img = MethodUtils.getLocalImageFile(MyApplication.PATH_USER_ICON);
        try {
            FileOutputStream fos = new FileOutputStream(img);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fos);
            fos.flush();
            fos.close();
            return Uri.fromFile(img);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 获得base64格式图片字符串
     *
     * @return 输出base64格式图片字符串
     */
    public static String getBase64String(File ImageFile) {
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = new FileInputStream(ImageFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String retStr = Base64.encodeToString(data, Base64.DEFAULT);
        return retStr;
    }
}
