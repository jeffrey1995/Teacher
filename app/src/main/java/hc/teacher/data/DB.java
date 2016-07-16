package hc.teacher.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper{
    public DB(Context context)
    {
        super(context,"db_teacher",null,1);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库后，对数据库的操作
        db.execSQL("CREATE TABLE user(" +
                    "name TEXT DEFAULT \"\"," +
                        "sex TEXT DEFAULT \"\")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //更改数据库版本的操作
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        //每次成功打开数据库解后首先被执行
    }
}
