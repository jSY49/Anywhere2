package com.example.anywhere.Festival;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.anywhere.R;
import com.example.anywhere.itemDetail.AreaTripDetailActivity;

import java.util.ArrayList;

class FestivalAdapter extends RecyclerView.Adapter<FestivalAdapter.MyViewHolder> {
    private String[] mDataset;
    private ArrayList<FestivalList> list;
    private Context mContext = null ;


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
        public TextView title,addr,period;
        ImageView image;

        public MyViewHolder(@NonNull View v) {
            super(v);

            image=v.findViewById(R.id.festImg);
            title=v.findViewById(R.id.festTitle);
            addr=v.findViewById(R.id.festAddr);
            period=v.findViewById(R.id.festPeriod);

            v.setOnClickListener(v1 -> {
                // TODO : process click event.
                int pos= getAdapterPosition();
                if(pos!=RecyclerView.NO_POSITION){
                    if(mListener!=null){
                        mListener.onItemClick(v1,pos);
                    }
                }

                Intent intent =new Intent(((Activity) v1.getContext()).getApplicationContext(), AreaTripDetailActivity.class);
                intent.putExtra("contentId",list.get(pos).getId());
                ((Activity) v1.getContext()).startActivity(intent);
//                Log.d("listcId",list.get(pos).getId());


            });

        }
    }

    // 배열 데이타를 받는 생성자
    public FestivalAdapter(String[] myDataset) {
        mDataset = myDataset;
    }
    public FestivalAdapter(ArrayList<FestivalList> list, Context context) {

        this.list= list;
        mContext=context;
    }

    // 새로운 뷰 생성
    @Override
    public FestivalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // 뷰 생성
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_festival_recyclerview, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // 뷰 내용 수정
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // 현재 포지션에 해당하는 데이타 값을 가져옴
        // 해당 값을 뷰에 적용
        MultiTransformation multiOption = new MultiTransformation(
                new CenterCrop(),
                new RoundedCorners(25)
        );
        Glide.with(mContext).load(list.get(position).getImgUrl()).apply(RequestOptions.bitmapTransform(multiOption)).placeholder(R.drawable.brankimg).into(holder.image);
        holder.title.setText(list.get(position).getTitle());
        holder.addr.setText(list.get(position).getAddr());
        holder.period.setText(list.get(position).getStart()+"~"+list.get(position).getEnd());
    }

    // 표현 할 뷰의 갯수 지정
    // 여기선 데이타 배열 크기 만큼 뷰가 생성
    @Override
    public int getItemCount() {
        return list.size();
    }
}