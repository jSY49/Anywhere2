package com.example.anywhere;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anywhere.databinding.ActivityCommunityBinding;

import java.util.ArrayList;

class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private String[] mDataset;
    private ArrayList<String> dbset,timeset,docname;

    public MyAdapter() {

    }


    //아이템 클릭 리스너 인터페이스
    public interface OnItemClickListener{
        void onItemClick(View v, int position); //뷰와 포지션값
    }
    //리스너 객체 참조 변수
    private OnItemClickListener mListener = null;
    //리스너 객체 참조를 어댑터에 전달 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    // 리사이클러뷰 안의 뷰를 참조하는 메소드
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView postTitle, postTime;

        public MyViewHolder(@NonNull View v) {
            super(v);
            postTitle = v.findViewById(R.id.postNm);
            postTime = v.findViewById(R.id.postTm);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO : process click event.
                    int pos= getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        if(mListener!=null){
                            mListener.onItemClick(v,pos);
                        }
                    }
                    Intent intent =new Intent(((Activity)v.getContext()).getApplicationContext(),DetailPostActivity.class);
                    intent.putExtra("DocId",docname.get(pos));
                    ((Activity)v.getContext()).startActivity(intent);
                }
            });

        }
    }

    // 배열 데이타를 받는 생성자
    public MyAdapter(String[] myDataset) {
        mDataset = myDataset;
    }
    public MyAdapter(ArrayList<String> dbSet,ArrayList<String> timeSet,ArrayList<String> docName) {
        dbset=dbSet;
        timeset=timeSet;
        docname=docName;
    }

    // 새로운 뷰 생성
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // 뷰 생성
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.postslistview, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // 뷰 내용 수정
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // 현재 포지션에 해당하는 데이타 값을 가져옴
        // 해당 값을 뷰에 적용

//        holder.postTitle.setText(position+". ");
//        holder.postTime.setText(mDataset[position]);
        holder.postTitle.setText(dbset.get(position));
        holder.postTime.setText(timeset.get(position));

    }

    // 표현 할 뷰의 갯수 지정
    // 여기선 데이타 배열 크기 만큼 뷰가 생성
    @Override
    public int getItemCount() {
//        return mDataset.length;

        return dbset.size();
    }
}