package hc.teacher.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;
import java.util.Map;

import hc.teacher.application.MyApplication;
import hc.teacher.application.R;
import hc.teacher.user_defined_widget.RoundImageView;

public class InformationAdapter extends SimpleAdapter
{
    private Context context;
    private int resource;
    public InformationAdapter(Context context, List<Map<String, Object>> data, int resource, String[] from, int[] to)
    {
        super(context, data, resource, from, to);
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LinearLayout newView;

        Map<String, Object> item = (Map<String, Object>)getItem(position);

        if(convertView == null)
        {
            newView = new LinearLayout(context);
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater)context.getSystemService(inflater);
            li.inflate(resource, newView, true);
        }
        else
        {
            newView = (LinearLayout)convertView;
        }

        TextView title = (TextView)newView.findViewById(R.id.title);
        TextView subject = (TextView)newView.findViewById(R.id.subject);
        TextView time = (TextView)newView.findViewById(R.id.time);
        TextView grade = (TextView)newView.findViewById(R.id.grade);
        TextView salary = (TextView)newView.findViewById(R.id.salary);
        RoundImageView personHead = (RoundImageView)newView.findViewById(R.id.person_head);

        String subjectStr = item.get("SUBJECT").toString();
        String timeStr = item.get("COMMIT_DATE").toString();
        String gradeStr = item.get("GRADE").toString();

        title.setText(item.get("TITLE").toString());
        subject.setText(subjectStr.length() > 2 ? subjectStr.substring(0, 2) + "..." : subjectStr);
        time.setText(timeStr.substring(5, 16));
        grade.setText(gradeStr.length() > 2 ? gradeStr.substring(0, 2) + "..." : gradeStr);
        salary.setText(item.get("SALARY").toString());

        double id = (double)item.get("USER_ID");
        String url = context.getResources().getString(R.string.url) + "public/images/" + (int)id + ".png";
        netWorkImageViewVolley(url, personHead);

        return newView;
    }
    public void netWorkImageViewVolley(String imageUrl, RoundImageView personHead)
    {
        final LruCache<String,Bitmap> lruCache = new LruCache<String,Bitmap>(20);
        ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache() {
            @Override
            public Bitmap getBitmap(String key)
            {
                return lruCache.get("key");
            }

            @Override
            public void putBitmap(String key, Bitmap value)
            {
                lruCache.put(key,value);
            }
        };
        ImageLoader imageLoader = new ImageLoader(MyApplication.getHttpQueue(),imageCache);
        ImageLoader.ImageListener listener = imageLoader.getImageListener(personHead, 0, 0);
        imageLoader.get(imageUrl, listener);
    }
}
