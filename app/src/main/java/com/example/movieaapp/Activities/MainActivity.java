package com.example.movieaapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.movieaapp.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnGetStarted = findViewById(R.id.btnGetStarted);
        btnGetStarted.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        Button btnMapCT = findViewById(R.id.btnMapCT);
        btnMapCT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

    }

    private void openMap() {
        double latitude = 10.817002200607995;
        double longitude = 106.71249665296965;
        String label = "Here is my location";

        String geoUri = String.format("geo:%f,%f?q=%f,%f(%s)", latitude, longitude, latitude, longitude, label);
        Uri geo = Uri.parse(geoUri);
        Intent intent = new Intent(Intent.ACTION_VIEW, geo);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}
