package hc.teacher.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hc.teacher.application.R;

/**
 * Created by Administrator on 2015/10/19 0019.
 */
public class Circle_MessageFragment extends Fragment {
    ListView listView;
    List<Map<String,Object>> listItems;
    String from_01[] = {"mess_head_image","mess_from_name_text","mess_content_text","mess_datetime"};
    int to_01[] = {R.id.mess_head_image,R.id.mess_from_name_text,R.id.mess_content_text,R.id.mess_datetime};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fra_circle_message,null);
        listView = (ListView)view.findViewById(R.id.listView);
        final SimpleAdapter myAdapter_01 = new SimpleAdapter(view.getContext(),this.getData_01(),R.layout.circle_listview01,from_01,to_01);
        listView.setAdapter(myAdapter_01);
        return view;
    }
    private List<?extends Map<String,?>> getData_01(){
        listItems = new ArrayList<Map<String,Object>>();
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("mess_head_image",R.drawable.headimage01);
        map.put("mess_from_name_text","比克大魔王");
        map.put("mess_content_text","我要毁灭世界！！！");
        map.put("mess_datetime","2050-10-10");
        listItems.add(map);
        Map<String,Object> map1 = new HashMap<String,Object>();
        map1.put("mess_head_image",R.drawable.headimage02);
        map1.put("mess_from_name_text", "孙悟空");
        map1.put("mess_content_text", "我要拯救世界！！！");
        map1.put("mess_datetime", "2050-10-11");
        listItems.add(map1);
        return listItems;
    }
    private List<?extends Map<String,?>> getData_02(){
        listItems = new ArrayList<Map<String,Object>>();
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("friend_image",R.drawable.headimage01);
        map.put("friend_name","比克大魔王");
        map.put("friend_note", "世界尽在我手！！！");
        listItems.add(map);
        Map<String,Object> map1 = new HashMap<String,Object>();
        map1.put("friend_image",R.drawable.headimage04);
        map1.put("friend_name","比克小魔王");
        map1.put("friend_note","我也要毁灭世界！！！");
        listItems.add(map1);
        return listItems;
    }
}
