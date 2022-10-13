package com.example.anywhere.Community;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.anywhere.databinding.ActivityImgFullScreenBinding;

public class ImgFullScreenActivity extends AppCompatActivity {


    Uri uri;
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityImgFullScreenBinding binding = ActivityImgFullScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent secondIntent = getIntent();
        uri = secondIntent.getParcelableExtra("image");

        // xml에 정의한 이미지뷰 찾고
        mImageView = (ImageView) binding.imageView;
        Glide.with(this)
                .load(uri)
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .fitCenter()
//                .override(Target.SIZE_ORIGINAL)
                .into(mImageView);


        // 스케일제스쳐 디텍터 인스턴스
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        //변수로 선언해 놓은 ScaleGestureDetector
        mScaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            // ScaleGestureDetector에서 factor를 받아 변수로 선언한 factor에 넣고
            mScaleFactor *= scaleGestureDetector.getScaleFactor();

            // 최대 10배, 최소 10배 줌 한계 설정
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

            // 이미지뷰 스케일에 적용
            mImageView.setScaleX(mScaleFactor);
            mImageView.setScaleY(mScaleFactor);

            return true;
        }
    }
}



