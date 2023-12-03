package com.example.movieaapp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
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
    int movieId;
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
        qrCodeImageView = findViewById(R.id.qrCodeImageView);

        btnConfirm = findViewById(R.id.btnConfirm);
        backCB = findViewById(R.id.backCB); // Gán giá trị cho backCB

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