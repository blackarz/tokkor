package com.tokkor.news60words;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SearchFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        view.findViewById(R.id.MyFeed).setOnClickListener(v -> openCategory("Home"));
        view.findViewById(R.id.Bangladesh).setOnClickListener(v -> openCategory("Bangladesh"));
        view.findViewById(R.id.Sports).setOnClickListener(v -> openCategory("Sports"));
        view.findViewById(R.id.Business).setOnClickListener(v -> openCategory("Business"));
        view.findViewById(R.id.International).setOnClickListener(v -> openCategory("International"));
        view.findViewById(R.id.Entertainment).setOnClickListener(v -> openCategory("Entertainment"));
        view.findViewById(R.id.Startups).setOnClickListener(v -> openCategory("Startups"));
        view.findViewById(R.id.IsraelHamasWar).setOnClickListener(v -> openCategory("Israel-Hamas-War"));

        return view;
    }

    private void openCategory(String category) {
        HomeFragment homeFragment = new HomeFragment();

        Bundle args = new Bundle();
        args.putString("category", category);
        homeFragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, homeFragment)
                .addToBackStack(null)
                .commit();
    }
}
