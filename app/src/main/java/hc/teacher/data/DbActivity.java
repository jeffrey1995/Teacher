package hc.teacher.data;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SyncStatusObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;

import hc.teacher.application.R;

/**
 * Created by mrtian on 2015/9/24.
 */
public class DbActivity extends Activity {
    public String db_name = "hc_teacher";
    public String table_name = "pic";
    //辅助类
   // final DatabaseHelper helper = new DatabaseHelper(this,db_name,null,1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_test);
        Button button_01 = (Button)findViewById(R.id.button_01);

        DB db = new DB(this);
        SQLiteDatabase dbWrite = db.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name","小张");
        cv.put("sex","男");
        dbWrite.insert("user",null,cv);
        cv.put("name","小李");
        cv.put("sex","女");
        dbWrite.insert("user",null,cv);

        SQLiteDatabase dbRead = db.getReadableDatabase();
        //query用法：参数:表名，返回列（String[]{"name"}）,查询条件（"name="?"）,条件参数(new String[]{"小张"})，group by,having,orderBy)

        Cursor c = dbRead.query("user",null,null,null,null,null,null);

        while(c.moveToNext())
        {
            String name = c.getString(c.getColumnIndex("name"));
            String sex = c.getString(c.getColumnIndex("sex"));
            System.out.println(String.format("name=%s,sex=%s",name,sex));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    //初始化表
    public void initDatabase(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();

        cv.put("fileName", "pic1.jpg");
        cv.put("description", "图片1");
        db.insert(table_name, "", cv);

        cv.put("fileName", "pic2.jpg");
        cv.put("description", "图片2");
        db.insert(table_name, "", cv);

        cv.put("fileName", "pic3.jpg");
        cv.put("description", "图片3");
        db.insert(table_name, "", cv);

        cv.put("fileName", "pic4.jpg");
        cv.put("description", "图片4");
        db.insert(table_name, "", cv);

    }
}
