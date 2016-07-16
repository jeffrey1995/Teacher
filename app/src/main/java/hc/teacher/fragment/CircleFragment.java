package hc.teacher.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import hc.teacher.activity_circle.Circle_NewFriendActivity;
import hc.teacher.application.R;
import io.rong.imkit.RongIM;

/**
 * Created by Administrator on 2015/10/6 0006.
 */
public class CircleFragment extends Fragment{
    RelativeLayout circle_title_message;
    RelativeLayout circle_title_friend;
    /*做了一个fragment切换，现在用不上，留着以后用*/
    ImageView add_friend_btn;
    ImageView conversationlist_btn;
    Fragment circle_message,circle_addressbook,current;
    FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fra_circle,null);
//        circle_title_message = (RelativeLayout)view.findViewById(R.id.circle_title_message);
//        circle_title_friend = (RelativeLayout)view.findViewById(R.id.circle_title_friend);
        add_friend_btn = (ImageView)view.findViewById(R.id.add_friend_btn);
        conversationlist_btn = (ImageView)view.findViewById(R.id.conversationlist_btn);
        getFragment();
        //设置ActionBar
//      ActionBar actionBar = getActivity().getActionBar();
//      actionBar.setDisplayShowHomeEnabled(false);
//      actionBar.setDisplayShowCustomEnabled(true);
//      actionBar.setCustomView(R.layout.ab_circle);
//        circle_title_message.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "去消息", Toast.LENGTH_LONG).show();
//                switchContent(current, circle_message);
//                current = circle_message;
//            }
//        });
//        circle_title_friend.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "去通讯录", Toast.LENGTH_LONG).show();
//                switchContent(current, circle_addressbook);
//                current = circle_addressbook;
//            }
//        });
        add_friend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //新的好友按钮监听
                Intent intent = new Intent(getActivity(), Circle_NewFriendActivity.class);
                startActivity(intent);
            }
        });
        conversationlist_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //会话列表监听
                RongIM.getInstance().startConversationList(getActivity());
            }
        });
        return view;
    }

    private void getFragment() {
        fragmentManager = getFragmentManager();
        circle_message = new Circle_MessageFragment();
        circle_addressbook = new Circle_AddressBookFragment();
        fragmentManager.beginTransaction().add(R.id.content_ll,circle_addressbook).commit();
        current = circle_message;
    }



    public void switchContent(Fragment from, Fragment to) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (current != to) {
            current = to;
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from);
                transaction.add(R.id.content_ll, to);
                transaction.commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from); // 隐藏当前的fragment，显示下一个
                transaction.show(to);
                transaction.commit();
            }
        }
    }
}
