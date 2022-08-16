package com.example.anywhere;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

//공부 필
public class ListViewAdapter extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;
    Context context;


    // ListViewAdapter의 생성자
    public ListViewAdapter() {

    }
    public void clearAll(){
        if(getCount()>0){
            listViewItemList.clear();
        }
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }


    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        context =parent.getContext();
        ListViewItem listViewItem =listViewItemList.get(position);

        // "listview_item" Layout을 inflate하여 convertView 참조.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.arealistview_item, parent, false);

        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조
        ImageView ImageView = (ImageView) convertView.findViewById(R.id.imageView1) ;
        TextView titleTextView = (TextView) convertView.findViewById(R.id.textView1) ;
        TextView descTextView = (TextView) convertView.findViewById(R.id.textView2) ;



        titleTextView.setText(listViewItem.getTitle());
        descTextView.setText(listViewItem.getaddr());
        //Picasso.get().load(listViewItem.getimgurl()).fit().placeholder(R.drawable.brankimg).into(ImageView);
        Glide.with(convertView).load(listViewItem.getimgurl()).centerCrop().placeholder(R.drawable.brankimg).into(ImageView);
        // .centerCrop()  혹은 .fitCenter()


        return convertView;
    }




    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(ListViewItem item) {
        listViewItemList.add(item);
    }


}