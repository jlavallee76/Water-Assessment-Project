package com.example.wap;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.imaginativeworld.whynotimagecarousel.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.CarouselType;
import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CarouselFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarouselFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Context context;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CarouselFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CarouselFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CarouselFragment newInstance(String param1, String param2) {
        CarouselFragment fragment = new CarouselFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View carouselView = inflater.inflate(R.layout.fragment_carousel, container, false);

        context = getActivity();
        ImageCarousel carousel = carouselView.findViewById(R.id.carousel);
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

        //Get from database object.
        AppDatabase db = Room.databaseBuilder(getActivity(),
                AppDatabase.class, "image-links").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        ImageLinkDao imageLinkDao = db.imageLinkDao();

        List<ImageLink> imageLinks = imageLinkDao.getImageLinks();

        for(int i = 0; i < imageLinks.size(); i++) {
            String url = imageLinks.get(i).getLink();
            CarouselItem image = new CarouselItem(url, "Date");
            list.add(image);
        }
        carousel.addData(list);
        // Inflate the layout for this fragment
        return carouselView;
    }
}