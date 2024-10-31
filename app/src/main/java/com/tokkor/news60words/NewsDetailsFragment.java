package com.tokkor.news60words;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

public class NewsDetailsFragment extends Fragment {

    private String newslink;
    private static int tabPosition;
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_details, container, false);

        if (getArguments() != null) {
            newslink = getArguments().getString("url");
            tabPosition = getArguments().getInt("tab_position", 0);
        }

        webView = view.findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(newslink);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    // Return to HomeFragment with the selected tab position
                    requireActivity().getSupportFragmentManager().popBackStack();
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).setTabPosition(tabPosition);
                    }
                }
            }
        });

        return view;
    }

    public static NewsDetailsFragment newInstance(String url) {
        NewsDetailsFragment fragment = new NewsDetailsFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putInt("tab_position", tabPosition);
        fragment.setArguments(args);
        return fragment;
    }
}