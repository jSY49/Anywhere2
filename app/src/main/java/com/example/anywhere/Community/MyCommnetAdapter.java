package com.example.anywhere.Community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anywhere.R;

import java.util.ArrayList;

class MyCommnetAdapter extends RecyclerView.Adapter<MyCommnetAdapter.MyViewHolder> {
    private String[] mDataset;
    private ArrayList<String> user,timeset,Comment;

    public MyCommnetAdapter() {

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
        public TextView cBody,cUser,cTime;

        public MyViewHolder(@NonNull View v) {
            super(v);

            cBody = v.findViewById(R.id.commentBody);
            cUser = v.findViewById(R.id.commentUser);
            cTime= v.findViewById(R.id.commentTm);

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

                }
            });

        }
    }

    // 배열 데이타를 받는 생성자
    public MyCommnetAdapter(String[] myDataset) {
        mDataset = myDataset;
    }
    public MyCommnetAdapter(ArrayList<String> cUser, ArrayList<String> timeSet, ArrayList<String> comment) {

        user=cUser;
        timeset=timeSet;
        Comment=comment;
    }

    // 새로운 뷰 생성
    @Override
    public MyCommnetAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // 뷰 생성
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.commnet_recycler, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // 뷰 내용 수정
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // 현재 포지션에 해당하는 데이타 값을 가져옴
        // 해당 값을 뷰에 적용

        holder.cBody.setText(Comment.get(position));
        holder.cUser.setText(user.get(position));
        holder.cTime.setText(timeset.get(position));

    }

    // 표현 할 뷰의 갯수 지정
    // 여기선 데이타 배열 크기 만큼 뷰가 생성
    @Override
    public int getItemCount() {
        return Comment.size();
    }
}