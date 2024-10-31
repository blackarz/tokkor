package com.tokkor.news60words;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private VerticalViewPager verticalViewPager;
    private ViewPagerAdapter adapter;
    private List<SliderItems> sliderItems;
    private ArrayList<String> titles, desc, newslinks, heads;
    private DatabaseReference databaseReference;
    private TabLayout tabLayout;
    private final Map<String, String> categories = new LinkedHashMap<>(); // Use LinkedHashMap to maintain order

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        verticalViewPager = view.findViewById(R.id.verticalViewPager);
        databaseReference = FirebaseDatabase.getInstance().getReference("news");

        setupCategories();

        for (String category : categories.keySet()) {
            tabLayout.addTab(tabLayout.newTab().setText(category));
        }

        // Check if category argument is passed and select tab accordingly
        String categoryToSelect = getArguments() != null ? getArguments().getString("category") : "Home";
        int tabIndex = new ArrayList<>(categories.keySet()).indexOf(categoryToSelect);
        if (tabIndex >= 0) {
            tabLayout.getTabAt(tabIndex).select();
            loadCategoryData(categories.get(categoryToSelect));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadCategoryData(categories.get(tab.getText().toString()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }


    private void setupCategories() {
        categories.put("Home", "Home"); // "Home" tab first
        categories.put("Business", "Business");
        categories.put("Israel-Hamas-War", "Israel-Hamas-War");
        categories.put("Bangladesh", "Bangladesh");
        categories.put("Sports", "Sports");
        categories.put("International", "International");
        categories.put("Entertainment", "Entertainment");
        categories.put("Startups", "Startups");
    }

    private void loadCategoryData(String category) {
        sliderItems = new ArrayList<>();
        titles = new ArrayList<>();
        desc = new ArrayList<>();
        newslinks = new ArrayList<>();
        heads = new ArrayList<>();

        databaseReference.child(category).child("all_news_data").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String title = dataSnapshot.child("tittle").getValue(String.class);
                    String description = dataSnapshot.child("desc").getValue(String.class);
                    String imageUrl = dataSnapshot.child("imagelink").getValue(String.class);
                    String newsLink = dataSnapshot.child("newslink").getValue(String.class);

                    titles.add(title != null ? title : "No Title");
                    desc.add(description != null ? description : "No Description");
                    sliderItems.add(new SliderItems(imageUrl != null ? imageUrl : ""));
                    newslinks.add(newsLink != null ? newsLink : "");
                    heads.add(title);
                }

                adapter = new ViewPagerAdapter(HomeFragment.this, sliderItems, titles, desc, newslinks, heads, verticalViewPager);
                verticalViewPager.setAdapter(adapter);

                if (adapter.getCount() == 0) {
                    Toast.makeText(getContext(), "No items to display", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to open NewsDetailsFragment with the selected tab position
    public void openNewsDetails(String url) {
        int selectedTabPosition = tabLayout.getSelectedTabPosition();
        NewsDetailsFragment fragment = NewsDetailsFragment.newInstance(url);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .addToBackStack(null)
                .commit();
    }

    // Method to set the tab position when returning to HomeFragment
    public void setTabPosition(int position) {
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        if (tab != null) {
            tab.select();
        }
    }
}
