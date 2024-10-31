package com.tokkor.news60words;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    private final HomeFragment fragmentContext;
    private final List<SliderItems> sliderItems;
    private final ArrayList<String> titles;
    private final ArrayList<String> desc;
    private final ArrayList<String> newslinks;
    private final ArrayList<String> heads;
    private final VerticalViewPager verticalViewPager;
    private float x1;

    public ViewPagerAdapter(HomeFragment fragmentContext, List<SliderItems> sliderItems, ArrayList<String> titles,
                            ArrayList<String> desc, ArrayList<String> newslinks, ArrayList<String> heads,
                            VerticalViewPager verticalViewPager) {
        this.fragmentContext = fragmentContext;
        this.sliderItems = sliderItems;
        this.titles = titles;
        this.desc = desc;
        this.newslinks = newslinks;
        this.heads = heads;
        this.verticalViewPager = verticalViewPager;
    }

    @Override
    public int getCount() {
        return sliderItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = LayoutInflater.from(fragmentContext.requireContext()).inflate(R.layout.item_container, container, false);

        ImageView imageView = itemView.findViewById(R.id.imageView);
        ImageView imageView2 = itemView.findViewById(R.id.imageView2);
        TextView headline = itemView.findViewById(R.id.headline);
        TextView description = itemView.findViewById(R.id.desc);
        TextView head = itemView.findViewById(R.id.head);

        // Set data to views
        headline.setText(titles.get(position));
        description.setText(desc.get(position));
        head.setText(heads.get(position));

        // Load images with Glide
        Glide.with(fragmentContext.requireContext())
                .load(sliderItems.get(position).getImage())
                .centerCrop()
                .into(imageView);

        Glide.with(fragmentContext.requireContext())
                .load(sliderItems.get(position).getImage())
                .centerCrop()
                .apply(RequestOptions.bitmapTransform(new jp.wasabeef.glide.transformations.BlurTransformation(25, 3)))
                .into(imageView2);

        Log.d("ViewPagerAdapter", "Loaded item at position: " + position);

        // Swipe gesture to open details
        verticalViewPager.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x1 = event.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    float x2 = event.getX();
                    float deltaX = x1 - x2;

                    if (deltaX > 300) {  // Swipe left detected
                        FragmentManager fragmentManager = fragmentContext.getParentFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();

                        // Check the correct frame layout ID in your layout file
                        transaction.replace(R.id.frameLayout, NewsDetailsFragment.newInstance(newslinks.get(position)));
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                    break;
            }
            return false;
        });

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}