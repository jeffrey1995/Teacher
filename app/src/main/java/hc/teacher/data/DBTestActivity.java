package hc.teacher.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import hc.teacher.application.MyApplication;
import hc.teacher.application.R;

public class DBTestActivity extends AppCompatActivity {

    private Button btn_Add;
    private Button btn_Show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbtest);

        btn_Add = (Button) findViewById(R.id.btn_Add);
        btn_Show = (Button) findViewById(R.id.btn_Show);

        btn_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper db = MyApplication.dbHelper;
                Log.w("cry", "田田叼啊55");
                SQLiteDatabase dbWrite = db.getWritableDatabase();
                Log.w("cry", "田田叼啊66");
                ContentValues cv = new ContentValues();
                Log.w("cry", "田田叼啊77");
                cv.put("id", 1);
                Log.w("cry", "田田叼啊88");
                cv.put("name", "小张");
                Log.w("cry", "田田叼啊99");
                cv.put("sex", 1);
                Log.w("cry", "田田叼啊aaaaaaa");
                dbWrite.insert("user", null, cv);
                Log.w("cry", "田田叼啊astgswetgfasdgfa");
                cv.put("id", 2);
                cv.put("name","小李");
                cv.put("sex",2);
                dbWrite.insert("user", null, cv);
            }
        });

        btn_Show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper db = MyApplication.dbHelper;
                SQLiteDatabase dbRead = db.getReadableDatabase();
                //query用法：参数:表名，返回列（String[]{"name"}）,查询条件（"name="?"）,条件参数(new String[]{"小张"})，group by,having,orderBy)

                Cursor c = dbRead.query("user", null, null, null, null, null, null);

                while (c.moveToNext()) {
                    String name = c.getString(c.getColumnIndex("name"));
                    String sex = c.getString(c.getColumnIndex("sex"));
                    Log.w("cry", String.format("name=%s,sex=%s", name, sex));
                }
            }
        });


    }

}
