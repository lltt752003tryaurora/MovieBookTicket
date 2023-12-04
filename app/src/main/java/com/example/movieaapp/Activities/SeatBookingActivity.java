package com.example.movieaapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.movieaapp.Adapters.SeatsAdapter;
import com.example.movieaapp.Domain.FilmItem;
import com.example.movieaapp.Domain.Seat;
import com.example.movieaapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SeatBookingActivity extends AppCompatActivity {
    private StringRequest mStringRequest;
    private int movieId;
    private String selectedDate, selectedTime, selectedTheater;
    private ImageView picSB, backSB;
    private RequestQueue mRequestQueue;
    private TextView name_film, date_film, time_film, theater_film;
    private GridView gridViewSeats;
    private List<Seat> seatsList;
    private SeatsAdapter seatsAdapter;
    private TextView textViewTicketCount;
    private TextView textViewTotalPrice;
    private FloatingActionButton fabNavigate;
    private int ticketCount = 0;
    private final int ticketPrice = 25;
    private int totalPrice1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_booking);

        Intent intent = getIntent();
        movieId = intent.getIntExtra("movieId", -1);
        selectedDate = intent.getStringExtra("selectedDate");
        selectedTime = intent.getStringExtra("selectedTime");
        selectedTheater = intent.getStringExtra("selectedTheater");
        initView();
        sendRequest();
        gridViewSeats.setAdapter(seatsAdapter);

        loadSeatData();
        seatsAdapter.setSeatClickListener(new SeatsAdapter.SeatClickListener() {
            @Override
            public void onSeatClicked(int selectedSeatsCount, int totalPrice) {
                updateTicketInfo(selectedSeatsCount, totalPrice);
            }
        });

        fabNavigate.setOnClickListener(v -> {
            if (totalPrice1 > 0) {
                Intent confirmIntent = new Intent(SeatBookingActivity.this, ConfirmTicket.class);
                confirmIntent.putExtra("id", movieId);
                confirmIntent.putExtra("selectedTheater", selectedTheater);
                startActivity(confirmIntent);
            } else {
                Toast.makeText(SeatBookingActivity.this, "You haven't book ticket yet.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        picSB = findViewById(R.id.picCB);
        backSB = findViewById(R.id.backCB);
        name_film = findViewById(R.id.name_film);
        date_film = findViewById(R.id.date_film);
        time_film = findViewById(R.id.time_film);
        theater_film = findViewById(R.id.theater_film);
        gridViewSeats = findViewById(R.id.gridViewSeats);
        seatsList = new ArrayList<>(); // init list seats
        seatsAdapter = new SeatsAdapter(this, seatsList); // init adapter null
        textViewTicketCount = findViewById(R.id.textViewTicketCount);
        textViewTotalPrice = findViewById(R.id.textViewTotalPrice);
        fabNavigate = findViewById(R.id.fabNavigate);
        backSB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void sendRequest() {
        mRequestQueue = Volley.newRequestQueue(this);
        mStringRequest = new StringRequest(Request.Method.GET, "https://moviesapi.ir/api/v1/movies/" + movieId, response -> {
            Gson gson = new Gson();
            FilmItem item = gson.fromJson(response, FilmItem.class);

            Glide.with(SeatBookingActivity.this)
                    .load(item.getPoster())
                    .into(picSB);
            name_film.setText(item.getTitle());
            date_film.setText(selectedDate);
            time_film.setText(selectedTime);
            theater_film.setText(selectedTheater);

        }, error -> {

        });
        mRequestQueue.add(mStringRequest);
    }

    private void loadSeatData() {

        String jsonFileString = loadJSONFromAsset(getApplicationContext(), "seats.json");
        List<Seat> newSeatsList = convertJSONToList(jsonFileString);

        seatsList.clear();
        seatsList.addAll(newSeatsList);
        seatsAdapter.notifyDataSetChanged();
    }

    public List<Seat> convertJSONToList(String json) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Seat>>() {
        }.getType();
        return gson.fromJson(json, listType);
    }

    public String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void updateTicketInfo(int selectedSeatsCount, int totalPrice) {
        textViewTicketCount.setText(String.valueOf(selectedSeatsCount));
        textViewTotalPrice.setText("$" + totalPrice);
        totalPrice1 = totalPrice;
    }

}