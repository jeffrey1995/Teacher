package hc.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import hc.teacher.application.R;
import hc.teacher.entity.Question;

/**
 * Created by star on 2016/1/14.
 */
public class WaitAnswerAdapter extends BaseAdapter{

    private LayoutInflater layoutInflater;
    Context context;
    List<Question> itemMessage;
    String[] key;

    private class ViewHolder{
        TextView summary;
        TextView detail;
        TextView userName;
        TextView time;
    }

    public List<Question> getItemMessage() {
        return itemMessage;
    }

    public void addList(List<Question> questions){
        itemMessage.addAll(questions);
        for (int i = 0; i < itemMessage.size(); i ++){
            System.out.println(itemMessage.get(i).getId());
        }
    }

    public void setList(List<Question> list){
        itemMessage = list;
    }

   /* public void refreshData(List<Question> list){
        itemMessage.addAll(list.size(), list);
    }*/

    public WaitAnswerAdapter(Context context, List list){
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        itemMessage = list;
    }

    @Override
    public int getCount() {
        return itemMessage.size();
    }

    @Override
    public Object getItem(int i) {
        return itemMessage.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = layoutInflater.inflate(R.layout.wait_answer_question_list, null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.detail = (TextView)view.findViewById(R.id.detail);
        viewHolder.summary = (TextView)view.findViewById(R.id.summary);
        viewHolder.userName = (TextView)view.findViewById(R.id.nickname);
        viewHolder.time = (TextView)view.findViewById(R.id.time);

        /*
        * Should use key rather ....
        * */
        viewHolder.detail.setText(itemMessage.get(i).getDetails());
        viewHolder.summary.setText(itemMessage.get(i).getDescription());
        viewHolder.userName.setText(itemMessage.get(i).getNickname() + ":");
        viewHolder.time.setText(itemMessage.get(i).getPublish_time().toString());
        return view;
    }
}
