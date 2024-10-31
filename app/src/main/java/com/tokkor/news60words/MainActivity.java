package com.tokkor.news60words;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.tokkor.news60words.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private FirebaseRemoteConfig remoteConfig;
    private ActivityMainBinding binding;
    private FrameLayout frameLayout;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Toast.makeText(this, "Notification permissions not granted", Toast.LENGTH_SHORT).show();
                }
            });

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        askNotificationPermission();

        LinearLayout layNonet = findViewById(R.id.layNonet);
        frameLayout = findViewById(R.id.frameLayout);

        setupRemoteConfig();

        frameLayout.setVisibility(View.VISIBLE);
        replaceFragment(new HomeFragment());

        // Set up bottom navigation
        binding.BottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.search:
                    selectedFragment = new SearchFragment();
                    break;
                case R.id.account:
                    selectedFragment = new AccountFragment();
                    break;
            }
            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
        Log.d("MainActivity", "Fragment replaced: " + fragment.getClass().getSimpleName());
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Notification permission granted");
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                new AlertDialog.Builder(this)
                        .setTitle("Notification Permission")
                        .setMessage("For better experience, we need Notification Permission.")
                        .setPositiveButton("Okay", (dialog, which) ->
                                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS))
                        .setNegativeButton("No Thanks", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void setupRemoteConfig() {
        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(2)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);

        remoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    String newVersionCode = remoteConfig.getString("newVersionCode");
                    try {
                        if (Integer.parseInt(newVersionCode) > getCurrentVersionCode()) {
                            showUpdateDialogBox();
                        }
                    } catch (NumberFormatException e) {
                        Log.e("MainActivity", "Invalid version code from Remote Config", e);
                    }
                } else {
                    Log.e("MainActivity", "Remote config fetch failed");
                }
            }
        });
    }

    private int getCurrentVersionCode() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("MainActivity", "Failed to get version code", e);
            return -1;
        }
    }

    // Method to set the tab position in HomeFragment
    public void setTabPosition(int tabPosition) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if (fragment instanceof HomeFragment) {
            ((HomeFragment) fragment).setTabPosition(tabPosition);
        }
    }

    private void showUpdateDialogBox() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.tokkor)
                .setTitle(getString(R.string.new_update))
                .setMessage(getString(R.string.new_update_text))
                .setCancelable(false)
                .setPositiveButton(Html.fromHtml("<h4>Update Now</h4>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.playStoreLink))));
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Failed to open update link", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }
}