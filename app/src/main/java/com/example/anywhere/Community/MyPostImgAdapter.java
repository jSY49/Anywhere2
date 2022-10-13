package com.example.anywhere.Community;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.anywhere.R;

import java.util.ArrayList;

public class MyPostImgAdapter extends RecyclerView.Adapter<MyPostImgAdapter.MyViewHolder> {
    private String[] mDataset;
    private ArrayList<ImageView> imgset;

    private ArrayList<Uri> mData = null ;
    private Context mContext = null ;

    public MyPostImgAdapter() {

    }
    // 생성자에서 데이터 리스트 객체, Context를 전달받음.
    MyPostImgAdapter(ArrayList<Uri> list, Context context) {
        mData = list ;
        mContext = context;
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
        ImageView image;

        MyViewHolder(View itemView) {
            super(itemView) ;
            // 뷰 객체에 대한 참조.
            image = itemView.findViewById(R.id.re_img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO : process click event.
                    int pos= getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        if(mListener!=null){
                            mListener.onItemClick(v,pos);
                        }
                    }
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
//                    float scale = (float) (1024/(float)bitmap.getWidth());
////                    int image_w = (int) (bitmap.getWidth() * scale);
////                    int image_h = (int) (bitmap.getHeight() * scale);
////                    Bitmap resize = Bitmap.createScaledBitmap(bitmap, image_w, image_h, true);
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                    byte[] byteArray = stream.toByteArray();
                    Intent intent =new Intent(((Activity)v.getContext()).getApplicationContext(), ImgFullScreenActivity.class);
                    intent.putExtra("image",mData.get(pos));
                    ((Activity)v.getContext()).startActivity(intent);



                }
            });

        }
    }

    // 배열 데이타를 받는 생성자
    public MyPostImgAdapter(String[] myDataset) {
        mDataset = myDataset;
    }
    public MyPostImgAdapter(ArrayList<ImageView> imgSet) {
        imgset=imgSet;
    }

    // 새로운 뷰 생성
    @NonNull
    @Override
    public MyPostImgAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // 뷰 생성
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_post_img_recyclerview, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // 뷰 내용 수정
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Uri image_uri = mData.get(position) ;
        Glide.with(mContext)
                .load(image_uri)
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .fitCenter()
//                .override(Target.SIZE_ORIGINAL)
                .into(holder.image);



    }

    // 표현 할 뷰의 갯수 지정
    // 여기선 데이타 배열 크기 만큼 뷰가 생성
    @Override
    public int getItemCount() {
        return mData.size();
    }
}