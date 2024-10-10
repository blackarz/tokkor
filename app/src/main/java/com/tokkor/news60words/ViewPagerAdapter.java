package com.tokkor.news60words;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    List<SliderItems> sliderItems;
    LayoutInflater mLayoutInflater;
    Context context;
    ArrayList<String> titles;
    ArrayList<String> desc;
    ArrayList<String> newslinks;
    ArrayList<String> heads;
    VerticalViewPager verticalViewPager;

    int newposition;
    float x1,x2;

    public ViewPagerAdapter(Context context, List<SliderItems> sliderItems, ArrayList<String> titles, ArrayList<String> desc, ArrayList<String> newslinks, ArrayList<String> heads, VerticalViewPager verticalViewPager) {
        this.context = context;
        this.sliderItems = sliderItems;
        this.titles = titles;
        this.desc = desc;
        this.newslinks = newslinks;
        this.heads = heads;
        this.verticalViewPager = verticalViewPager;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        View itemView = mLayoutInflater.inflate(R.layout.item_container, container, false);

        ImageView imageView = itemView.findViewById(R.id.imageView);
        ImageView imageView2 = itemView.findViewById(R.id.imageView2);
        TextView tittle = itemView.findViewById(R.id.headline);
        TextView desctv = itemView.findViewById(R.id.desc);
        TextView head = itemView.findViewById(R.id.head);

        // Set data from array lists to TextViews
        tittle.setText(titles.get(position));
        desctv.setText(desc.get(position));
        head.setText(heads.get(position));

        // Use Glide to load main image into imageView
        Glide.with(context)
                .load(sliderItems.get(position).getImage())  // Ensure this is a valid image URL
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)  // Placeholder while loading
                .error(R.drawable.ic_launcher_background)  // Error image if loading fails
                .into(imageView);

        // If imageView2 serves a different purpose, set it accordingly
        // For now, it's set to load the same image but with a resized override.
        // Apply blur effect on imageView2 using Glide Transformations
        Glide.with(context)
                .load(sliderItems.get(position).getImage())
                .centerCrop()
                .apply(RequestOptions.bitmapTransform(new jp.wasabeef.glide.transformations.BlurTransformation(25, 3))) // Blur effect
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(imageView2);

        verticalViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                newposition = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        verticalViewPager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        break;


                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        float deltaX = x1-x2;

                        if (deltaX > 300)
                        {
                            Intent i = new Intent(context, NewsDetailsActivity.class);
                            if (position==1)
                            {
                                //for first page
                                i.putExtra("url",newslinks.get(0));
                                context.startActivity(i);
                            }
                            else
                            {
                                //when page scrolled
                                i.putExtra("url",newslinks.get(newposition));
                                context.startActivity(i);
                            }
                        }
                        break;
                }
                return false;
            }
        });

        // Add the view to the container
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
