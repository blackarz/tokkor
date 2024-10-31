package com.tokkor.news60words;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.messaging.FirebaseMessaging;

public class AccountFragment extends Fragment {

    private Button btnShareApp, btnRateApp, btnTermsConditions, btnPrivacyPolicy;
    private Switch switchNotifications;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize views
        btnShareApp = view.findViewById(R.id.btn_share_app);
        btnRateApp = view.findViewById(R.id.btn_rate_app);
        btnTermsConditions = view.findViewById(R.id.btn_terms_conditions);
        btnPrivacyPolicy = view.findViewById(R.id.btn_privacy_policy);
        switchNotifications = view.findViewById(R.id.notificationSwitch);

        // Set up listeners
        btnShareApp.setOnClickListener(v -> shareApp());
        btnRateApp.setOnClickListener(v -> rateApp());
        btnTermsConditions.setOnClickListener(v -> openWebPage("https://www.yourapp.com/terms"));
        btnPrivacyPolicy.setOnClickListener(v -> openWebPage("https://www.yourapp.com/privacy"));
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> handleNotifications(isChecked));

        return view;
    }

    private void shareApp() {
        String shareText = "Check out this amazing app: https://play.google.com/store/apps/details?id=" + requireActivity().getPackageName();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + requireActivity().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + requireActivity().getPackageName())));
        }
    }

    private void openWebPage(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }


    //Handle Notifications Here
    private void handleNotifications(boolean isEnabled) {
        // Save notification state using SharedPreferences
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("notifications_enabled", isEnabled);
        editor.apply();

        if (isEnabled) {
            // Subscribe to Firebase topic for notifications
            FirebaseMessaging.getInstance().subscribeToTopic("all_users")
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Notifications Enabled", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to enable notifications", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Unsubscribe from Firebase topic to disable notifications
            FirebaseMessaging.getInstance().unsubscribeFromTopic("all_users")
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Notifications Disabled", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to disable notifications", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
