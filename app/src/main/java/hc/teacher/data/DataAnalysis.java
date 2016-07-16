package hc.teacher.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/10/26 0026.
 */
public class DataAnalysis {
    public static List<Map<String, Object>> listKeyMaps(String jsonString) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            Gson gson = new Gson();
            list = gson.fromJson(jsonString,
                    new TypeToken<List<Map<String, Object>>>() {
                    }.getType());
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("数据解析错误（List）");
        }
        return list;
    }
    public static Map<String,Object> mapKey(String jsonString)
    {
        Map<String,Object> map = new HashMap<String,Object>();
        try {
            Gson gson = new Gson();
            map = gson.fromJson(jsonString,
                    new TypeToken<Map<String, Object>>() {
                    }.getType());
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("数据解析错误（Map）");
        }
        return map;
    }
}
