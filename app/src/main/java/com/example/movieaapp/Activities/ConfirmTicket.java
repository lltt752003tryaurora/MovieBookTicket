package com.example.movieaapp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.movieaapp.Domain.FilmItem;
import com.example.movieaapp.R;

import android.graphics.Color;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class ConfirmTicket extends AppCompatActivity {
    private int movieId;
    private String theaterSelected;
    private ImageView qrCodeImageView;
    private StringRequest mStringRequest;
    private RequestQueue mRequestQueue;
    private String data;
    private ImageView backCB;
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_ticket);

        Intent intent = getIntent();
        movieId = intent.getIntExtra("movieId", 12);
        theaterSelected = intent.getStringExtra("selectedTheater");
        qrCodeImageView = findViewById(R.id.qrCodeImageView);

        btnConfirm = findViewById(R.id.btnConfirm);
        backCB = findViewById(R.id.backCB);

        btnConfirm.setOnClickListener(v -> {
            Toast.makeText(ConfirmTicket.this, "Booking Ticket Success", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> {
                Intent homeIntent = new Intent(ConfirmTicket.this, HomeActivity.class);
                startActivity(homeIntent);
                finish();
            }, 2000);
        });

        backCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRequestQueue = Volley.newRequestQueue(this);
        mStringRequest = new StringRequest(Request.Method.GET, "https://moviesapi.ir/api/v1/movies/" + movieId, response -> {
            Gson gson = new Gson();
            FilmItem item = gson.fromJson(response, FilmItem.class);

            ImageView picCB = findViewById(R.id.picCB);

            Glide.with(ConfirmTicket.this)
                    .load(item.getPoster())
                    .into(picCB);

            data = item.getTitle();

            runOnUiThread(() -> {
                try {
                    Bitmap qrCode = generateQRCode(data, 200, 200);
                    if (qrCode != null) {
                        qrCodeImageView.setImageBitmap(qrCode);
                    }
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            });
        }, error -> {
        });
        mRequestQueue.add(mStringRequest);


        Button btnMapCT = findViewById(R.id.btnMap);
        btnMapCT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap(theaterSelected, btnMapCT);
            }
        });
    }

    private void openMap(String theaterSelected, Button btnMapCT) {
        double latitudeCinestar = 10.81967913309342;
        double longitudeCinestar = 106.69927071538956;

        double latitudeLotte = 10.84466975997189;
        double longitudeLotte = 106.67224861474674;

        double latitudeCGV = 10.827768894541085;
        double longitudeCGV = 106.72124538180593;

        double latitudeGalaxy = 10.848838637745766;
        double longitudeGalaxy = 106.63456898436102;

        String goal = "Your destination is Cinestar Cinemas";
        String geoUri = String.format("geo:%f,%f?q=%f,%f(%s)", latitudeCinestar, longitudeCinestar, latitudeCinestar, longitudeCinestar, goal);
        btnMapCT.setText("Show map to Cinestar");

        if (theaterSelected == "Lotte Cinemas") {
            goal = "Your destination is Lotte Cinemas";
            geoUri = String.format("geo:%f,%f?q=%f,%f(%s)", latitudeLotte, longitudeLotte, latitudeLotte, longitudeLotte, goal);
            btnMapCT.setText("Show map to Lotte");
        } else if (theaterSelected == "CGV Cinemas") {
            goal = "Your destination is CGV Cinemas";
            geoUri = String.format("geo:%f,%f?q=%f,%f(%s)", latitudeCGV, longitudeCGV, latitudeCGV, longitudeCGV, goal);
            btnMapCT.setText("Show map to CGV");
        } else if (theaterSelected == "Galaxy Cinemas") {
            goal = "Your destination is Galaxy Cinemas";
            geoUri = String.format("geo:%f,%f?q=%f,%f(%s)", latitudeGalaxy, longitudeGalaxy, latitudeGalaxy, longitudeGalaxy, goal);
            btnMapCT.setText("Show map to Galaxy");
        }
        Uri geo = Uri.parse(geoUri);
        Intent intent = new Intent(Intent.ACTION_VIEW, geo);
        if (intent.resolveActivity(getPackageManager()) != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                }
            }, 3000);
        }
    }

    private Bitmap generateQRCode(String data, int width, int height) throws WriterException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, width, height);
        int matrixWidth = bitMatrix.getWidth();
        int matrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[matrixWidth * matrixHeight];

        for (int y = 0; y < matrixHeight; y++) {
            for (int x = 0; x < matrixWidth; x++) {
                pixels[y * matrixWidth + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight);

        return bitmap;
    }
}