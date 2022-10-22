package com.example.anywhere.Booking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.anywhere.R;

import java.util.ArrayList;

class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.MyViewHolder> {
    private String[] mDataset;
//    private ArrayList<String> titleSet,priceSet,AddrSet,ImgSet;
    private ArrayList<BookingActivity.item> listSet;
    private Context mContext = null ;
    public String code;
    public BookingAdapter() {

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
        public TextView title,addr,price;
        public ImageView img;


        public MyViewHolder(@NonNull View v) {
            super(v);
           title = v.findViewById(R.id.Title);
            addr = v.findViewById(R.id.Addr);
            price = v.findViewById(R.id.Price);
            img = v.findViewById(R.id.Img);

            v.setOnClickListener(v1 -> {
                // TODO : process click event.
                int pos= getAdapterPosition();
                if(pos!=RecyclerView.NO_POSITION){
                    if(mListener!=null){
                        mListener.onItemClick(v1,pos);
                    }
                }
                code=listSet.get(pos).getCode();
                Intent intent =new Intent(((Activity)v.getContext()).getApplicationContext(), BookingDetailActivity.class);
                intent.putExtra("code", code);
                ((Activity)v.getContext()).startActivity(intent);
            });

        }
    }

    // 배열 데이타를 받는 생성자
    public BookingAdapter(String[] myDataset) {
        mDataset = myDataset;
    }
    public BookingAdapter(ArrayList<BookingActivity.item> list, Context context) {
//        this.titleSet =titleSet;
//        this.priceSet=priceSet;
//        this.AddrSet=AddrSet;
        this.listSet=list;
        mContext=context;
    }

    // 새로운 뷰 생성
    @Override
    public BookingAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // 뷰 생성
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_booking_recyclerview, parent, false);

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
        holder.title.setText(listSet.get(position).getTitle());
        holder.addr.setText(listSet.get(position).getAddr());
        holder.price.setText(listSet.get(position).getPrice());
        Glide.with(mContext).load(listSet.get(position).getImg()).centerCrop().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.img);
        Log.d("Booking Ada","imgUrl="+listSet.get(position).getImg());


    }

    // 표현 할 뷰의 갯수 지정
    // 여기선 데이타 배열 크기 만큼 뷰가 생성
    @Override
    public int getItemCount() {
//        return mDataset.length;
        return listSet.size();
    }
}