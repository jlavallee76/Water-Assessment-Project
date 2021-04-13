package com.example.wap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import org.imaginativeworld.whynotimagecarousel.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.CarouselType;
import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.Utils;

import java.util.ArrayList;
import java.util.List;

public class CarouselActivity extends AppCompatActivity {
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carousel);

        context = this;
        ImageCarousel carousel = findViewById(R.id.carousel);
        carousel.setCarouselType(CarouselType.SHOWCASE);
        carousel.setScaleOnScroll(false);
        carousel.setScalingFactor(.15f);
        carousel.setShowTopShadow(false);
        carousel.setTopShadowAlpha(0.6f); // 0 to 1, 1 means 100%
        carousel.setTopShadowHeight(Utils.dpToPx(32, context)); // px value of dp
        carousel.setShowBottomShadow(false);
        carousel.setBottomShadowAlpha(0.6f); // 0 to 1, 1 means 100%
        carousel.setBottomShadowHeight(Utils.dpToPx(64, context)); // px value of dp
        carousel.setShowCaption(true);
        carousel.setCaptionMargin(Utils.dpToPx(16, context)); // px value of dp
        carousel.setCaptionTextSize(Utils.spToPx(16, context)); // px value of sp
        carousel.setIndicatorMargin(Utils.dpToPx(0, context)); // px value of dp
        carousel.setShowNavigationButtons(true);
        carousel.setImageScaleType(ImageView.ScaleType.CENTER);
        carousel.setCarouselBackground(new ColorDrawable(Color.parseColor("#05AFF2")));
        carousel.setCarouselType(CarouselType.SHOWCASE);
        carousel.setScaleOnScroll(true);
        carousel.setScalingFactor(.15f);
        carousel.setAutoWidthFixing(true);
        carousel.setAutoPlay(false);
        carousel.setAutoPlayDelay(3000); // Milliseconds

        List<CarouselItem> list = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            list.add(
                    new CarouselItem(
                            "https://images.unsplash.com/photo-1532581291347-9c39cf10a73c?w=1080",
                            "Photo by Aaron Wu on Unsplash"
                    )
            );

        }

        carousel.addData(list);
    }
}