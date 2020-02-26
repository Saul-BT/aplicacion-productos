package com.example.productmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Window window = getWindow();
        //window.setStatusBarColor(Color.parseColor("#FAFAFA"));

        final int MY_CAMERA_REQUEST_CODE = 100;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment navHost = getSupportFragmentManager().getPrimaryNavigationFragment();
        Fragment currentFragment = navHost.getChildFragmentManager().getFragments().get(0);

        if (!(currentFragment instanceof ProductsFragment)) {
            super.onBackPressed();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.finishAffinity();
        } else{
            this.finish();
            System.exit(0);
        }
    }
}
