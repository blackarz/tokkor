package com.tokkor.news60words;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // List of slider items
    List<SliderItems> sliderItems = new ArrayList<>();

    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> desc = new ArrayList<>();
    ArrayList<String> images = new ArrayList<>();
    ArrayList<String> newslinks = new ArrayList<>();
    ArrayList<String> heads = new ArrayList<>();

    // Firebase database reference
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final VerticalViewPager verticalViewPager = findViewById(R.id.verticalViewPager);

        // Firebase database reference
        mRef = FirebaseDatabase.getInstance().getReference("News");

        // Fetching data from Firebase
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    // Retrieve data and log image URLs for testing
                    titles.add(ds.child("tittle").getValue(String.class));
                    desc.add(ds.child("desc").getValue(String.class));
                    String imageUrl = ds.child("imagelink").getValue(String.class);
                    Log.d("ImageURL", "Image URL: " + imageUrl);  // Log the image URL
                    images.add(imageUrl);
                    newslinks.add(ds.child("newslink").getValue(String.class));
                    heads.add(ds.child("head").getValue(String.class));
                }

                for (int i = 0; i < images.size(); i++) {
                    sliderItems.add(new SliderItems(images.get(i)));
                }

                verticalViewPager.setAdapter(new ViewPagerAdapter(MainActivity.this, sliderItems, titles, desc, newslinks, heads, verticalViewPager));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error fetching data", error.toException());
            }
        });
    }
}
