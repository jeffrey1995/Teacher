package hc.teacher.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by mrtian on 2015/9/24.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory cursorFactory, int version) {
        super(context, name, cursorFactory, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {   //创建数据库后，对数据库的操作
        //创建user表的ddl语句
        String sql_CreateUserTable = "CREATE TABLE user (" +
                "  id INTEGER PRIMARY KEY NOT NULL ," +
                "  account TEXT(11) NOT NULL ," +
                "  password TEXT(18) NOT NULL ," +
                "  identity INTEGER(1) NOT NULL ," +
                "  nickname TEXT(10) NOT NULL ," +
                "  gender INTEGER(1) NOT NULL ," +
                "  address TEXT(50) NOT NULL," +
                "  email TEXT(50) NOT NULL," +
                "  qqnumber TEXT(15) NOT NULL," +
                "  tel TEXT(11) NOT NULL," +
                "  ispublic INTEGER(1) NOT NULL ," +
                "  description TEXT(255) NOT NULL ," +
                "  head TEXT(255) NOT NULL ," +
                "  register_date TEXT(50) NOT NULL , " +
                "  identify_state INTEGER NOT NULL ," +
                "  token TEXT(100) NOT NULL" +
                ") ";
        db.execSQL(sql_CreateUserTable);

        String sql_CreateContactInfoTable = "CREATE TABLE contact_info (" +
                "  id INTEGER PRIMARY KEY NOT NULL ," +
                "  user_id INTEGER NOT NULL ," +
                "  name TEXT(10) NOT NULL ," +
                "  tel TEXT(11) NOT NULL," +
                "  address TEXT(50) NOT NULL" +
                ") ";
        db.execSQL(sql_CreateContactInfoTable);
    }

    /**
     * 如果本地已有一个数据库，则会调用此方法，用以更新本地数据库，即
     * 用于应用程序的更新。
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {   //更改数据库版本的操作
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        //每次成功打开数据库解后首先被执行
    }
}
