package hc.teacher.components.location;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import hc.teacher.application.R;

public class SelectCityActivity extends AppCompatActivity {

    //每列显示数量
    public static final int ROW_COUNT = 3;
    //结尾空白数据数量
    public static final int BLANK_DATA_COUNT = 13;

    private GridLayoutManager glm;

    //索引列表
    private static final String[] indexStr = {"热门", "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z"};

    private RecyclerView rv;
    private CityCellData[] data;
    private String[] city_name;
    private HashMap<String, Integer> index_map = new HashMap<>();

    //UI引用
    LinearLayout ll_IndexTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        initView();

        getData();
        getIndexView();

        glm = new GridLayoutManager(this, ROW_COUNT, LinearLayoutManager.VERTICAL, false);

        rv.setLayoutManager(glm);
        rv.setAdapter(new CityAdapter());

    }


    private void initView() {
        ll_IndexTv = (LinearLayout) findViewById(R.id.ll_index_tv);
        rv = (RecyclerView) findViewById(R.id.rv_City);
    }

    /**
     * 从城市xml文件中得到填充到recycler view中的数据，存放到String[] city_name中
     */
    private void getData() {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(getAssets().open("china_city.xml"));
            Element root = document.getDocumentElement();
            NodeList city_list = root.getElementsByTagName("name");
            //根据大小分配string数组空间
            int length = city_list.getLength();
            city_name = new String[length];
            for (int i = 0; i < length; i++) {
                city_name[i] = city_list.item(i).getTextContent();
            }
            generateCellData();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据文件中读到的城市名，生成一个celldata数组，对分隔线等的处理逻辑在此方法中。
     */
    private void generateCellData() {
        Queue<String> q = new LinkedList<>();
        int p_index = 0;
        int cnt = 0;
        boolean first = true;
        for (int i = 0; i < city_name.length; i++) {
            String[] temp = city_name[i].split("_");
            String index = temp[0];
            String name = temp[1];
            if (index.equals(indexStr[p_index])) {
                if (first) {
                    //插入空白单元格
                    int blank_cnt = (ROW_COUNT - cnt % ROW_COUNT) % ROW_COUNT;
                    while (blank_cnt > 0) {
                        q.add("");
                        blank_cnt--;
                    }
                    cnt = 0;
                    //插入分隔线
                    q.add(indexStr[p_index]);
                    for (int j = 1; j < ROW_COUNT; j++) q.add("");
                    first = false;
                }
                q.add(name);
                cnt++;
            } else {
                first = true;
                while (!index.equals(indexStr[p_index])) p_index++;
                i--;
            }
        }
        data = new CityCellData[q.size() + BLANK_DATA_COUNT];
        int p = 0;
        while (!q.isEmpty()) {
            String name = q.poll();
            boolean isIndex = false;
            //如果是索引标题单元格，则用map记录
            for (int i = 0; i < indexStr.length; i++) {
                if (indexStr[i].equals(name)) {
                    index_map.put(name, p);
                    isIndex = true;
                }
            }
            if (isIndex) {
                //分隔线使用分隔线布局
                data[p++] = new CityCellData(name, CityCellData.TYPE_CUTLINE);
                for (int i = 0; i < ROW_COUNT - 1; i++) {
                    String tmp = q.poll();
                    data[p++] = new CityCellData(tmp, CityCellData.TYPE_CUTLINE);
                }
            } else {
                //非分隔线使用名称布局或者空白布局
                if (name.isEmpty()) {
                    data[p++] = new CityCellData(name, CityCellData.TYPE_BLANK);
                } else {
                    data[p++] = new CityCellData(name, CityCellData.TYPE_NAME);
                }
            }
        }
        for (int i = 0; i < BLANK_DATA_COUNT; i++) {
            data[p++] = new CityCellData("", CityCellData.TYPE_BLANK);
        }
    }


    /**
     * 生成右侧索引列表
     */
    private void getIndexView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, 0);
        params.weight = 1;
        for (int i = 0; i < indexStr.length; i++) {
            final TextView tv = new TextView(this);
            tv.setLayoutParams(params);
            tv.setText(indexStr[i]);
            tv.setPadding(10, 0, 10, 0);
            ll_IndexTv.addView(tv);
        }

        ll_IndexTv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //变色
                int motionEvent = event.getAction();
                if (motionEvent == MotionEvent.ACTION_DOWN || motionEvent == MotionEvent.ACTION_MOVE) {
                    ll_IndexTv.setBackgroundColor(Color.parseColor("#B0B0B0"));
                }
                if (motionEvent == MotionEvent.ACTION_UP) {
                    ll_IndexTv.setBackgroundColor(Color.parseColor("#CFCFCF"));
                }

                float y = event.getY();
                int index = (int) (y / (ll_IndexTv.getHeight() / indexStr.length));
                //防止越界
                if (index < 0 || index >= indexStr.length)
                    return false;
                //如果获取到为空，即不在map中，则找到离它最近的
                while (index_map.get(indexStr[index]) == null && index >= 0) index--;
                if (index_map.get(indexStr[index]) == null) return false;
                int pos = index_map.get(indexStr[index]);
                rv.getScrollState();
                glm.scrollToPositionWithOffset(pos, 5);
                return true;
            }

        });

    }

    /**
     * city RecyclerView 的数据适配器
     */
    private class CityAdapter extends RecyclerView.Adapter {

        /**
         * 构造方法
         */
        public CityAdapter() {
        }


        /**
         * 城市名称的ViewHolder
         */
        class NameItemHolder extends RecyclerView.ViewHolder {

            private Button btn_CityName;

            public NameItemHolder(View root) {
                super(root);
                btn_CityName = (Button) root.findViewById(R.id.btn_CityName);
            }

            public Button getBtnCityName() {
                return btn_CityName;
            }
        }

        /**
         * 向城市名称Item绑定数据
         *
         * @param vh 名称类型ViewHolder
         * @param cd 单元数据CellData
         */
        private void bindNameItem(NameItemHolder vh, CityCellData cd) {
            vh.getBtnCityName().setText(cd.title);
        }

        /**
         * 分隔线的ViewHolder
         */
        class CutLineItemHolder extends RecyclerView.ViewHolder {

            private TextView tv_Title;

            public CutLineItemHolder(View root) {
                super(root);
                tv_Title = (TextView) root.findViewById(R.id.tv_Title);
            }

            public TextView getTv_Title() {
                return tv_Title;
            }

        }

        /**
         * 向分隔线Item绑定数据
         *
         * @param vh 分隔线类型ViewHolder
         * @param cd 单元数据CellData
         */
        private void bindCutLineItem(CutLineItemHolder vh, CityCellData cd) {
            vh.getTv_Title().setText(cd.title);
        }

        /**
         * 空白的ViewHolder
         */
        class BlankItemHolder extends RecyclerView.ViewHolder {
            public BlankItemHolder(View root) {
                super(root);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            //设定一个城市 cell 的 item 点击事件
            if (viewType == CityCellData.TYPE_NAME) {
                View v = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.city_cell_name, null);

                final Button btn_CityName = (Button) v.findViewById(R.id.btn_CityName);
                btn_CityName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("CITY_NAME", btn_CityName.getText().toString());
                        setResult(0, intent);
                        finish();
                    }
                });

                return new NameItemHolder(v);

            } else if (viewType == CityCellData.TYPE_CUTLINE) {
                return new CutLineItemHolder(LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.city_cell_cutline, null));
            } else {
                return new BlankItemHolder(LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.city_cell_blank, null));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder instanceof NameItemHolder) {
                bindNameItem((NameItemHolder) viewHolder, data[i]);
            } else if (viewHolder instanceof CutLineItemHolder) {
                bindCutLineItem((CutLineItemHolder) viewHolder, data[i]);
            }
        }

        @Override
        public int getItemCount() {
            //读取数组长度大小的数据
            return data.length;
        }

        @Override
        public int getItemViewType(int position) {
            CityCellData cd = data[position];
            return cd.type;
        }
    }

}
