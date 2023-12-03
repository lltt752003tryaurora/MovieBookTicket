package com.example.movieaapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.movieaapp.Adapters.ActorsListAdapter;
import com.example.movieaapp.Adapters.CategoryItemFilmListAdapter;
import com.example.movieaapp.Adapters.DateAdapter;
import com.example.movieaapp.Adapters.ShowtimeAdapter;
import com.example.movieaapp.Domain.FilmItem;
import com.example.movieaapp.Domain.Movie;
import com.example.movieaapp.Domain.Showtime;
import com.example.movieaapp.Domain.Theater;
import com.example.movieaapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private ProgressBar progressBar;
    private TextView titleTxt, movieRateTxt, movieTimeTxt, movieDate, movieSummaryInfo, movieActorsInfo;
    private int idFilm;
    private ImageView pic2, backImg;
    private RecyclerView.Adapter adapterActorList, adapterCategory;
    private RecyclerView recyclerViewActors, recyclerViewCategory;
    private NestedScrollView scrollView;
    private FloatingActionButton fab;
    private ShowtimeAdapter showtimeAdapter;
    private Showtime selectedShowtime;
    private List<Pair<RecyclerView, ShowtimeAdapter>> recyclerViewsAdapters = new ArrayList<>();
    private String selectedDate; // store Date
    private String selectedTheaterName; // Store theater

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        idFilm = getIntent().getIntExtra("id", 0);
        initView();
        sendRequest();

        RecyclerView dateRecyclerView = findViewById(R.id.dateRecyclerView);
        dateRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Date
        List<String> dates = getDates();
        DateAdapter dateAdapter = new DateAdapter(this, dates);
        dateAdapter.setOnDateClickListener(new DateAdapter.OnDateClickListener() {
            @Override
            public void onDateClick(String date) {
                selectedDate = date;
            }
        });
        dateRecyclerView.setAdapter(dateAdapter);

        // Load movies and find the selected movie
        List<Movie> movies = loadMoviesFromJson(this);
        Movie selectedMovie = findMovieById(movies, idFilm);

        if (selectedMovie != null) {
            updateTheatersUI(selectedMovie.getTheaters());
        }

        // navigate seat booking
        fab = findViewById(R.id.fabNavigate);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedDate == null || selectedShowtime == null || selectedTheaterName == null) {
                    Toast.makeText(DetailActivity.this, "Please choose suitable schedule !", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(DetailActivity.this, SeatBookingActivity.class);
                    intent.putExtra("movieId", idFilm);
                    intent.putExtra("selectedDate", selectedDate);
                    intent.putExtra("selectedTime", selectedShowtime.getTime());
                    intent.putExtra("selectedTheater", selectedTheaterName);
                    startActivity(intent);
                }
            }
        });
    }

    private void updateTheatersUI(List<Theater> theaters) {
        for (Theater theater : theaters) {
            // Assuming you have TextViews and RecyclerViews for each theater
            switch (theater.getName()) {
                case "Cinestar Cinemas":
                    updateTheaterUI(theater, R.id.cinestar, R.id.timeSlot1);
                    break;
                case "Lotte Cinemas":
                    updateTheaterUI(theater, R.id.CGV, R.id.timeSlot2);
                    break;
                case "CGV Cinemas":
                    updateTheaterUI(theater, R.id.Lotte, R.id.timeSlot3);
                    break;
                case "Galaxy Cinemas":
                    updateTheaterUI(theater, R.id.galaxy, R.id.timeSlot4);
                    break;
            }
        }
    }

    private void updateTheaterUI(Theater theater, int textViewId, int recyclerViewId) {
        TextView textView = findViewById(textViewId);
        textView.setText(theater.getName());

        RecyclerView recyclerView = findViewById(recyclerViewId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        showtimeAdapter = new ShowtimeAdapter(theater.getShowtimes(), new ShowtimeAdapter.OnShowtimeClickListener() {
            @Override
            public void onShowtimeClick(Showtime showtime) {
                if (!"unavailable".equals(showtime.getStatus())) {
                    selectedShowtime = showtime;
                    selectedTheaterName = theater.getName();
                    updateAllShowtimes();
                }
            }
        });

        recyclerView.setAdapter(showtimeAdapter);
        recyclerViewsAdapters.add(new Pair<>(recyclerView, showtimeAdapter));
    }

    private void updateAllShowtimes() {
        for (Pair<RecyclerView, ShowtimeAdapter> pair : recyclerViewsAdapters) {
            ShowtimeAdapter adapter = pair.second;
            for (Showtime st : adapter.getShowtimes()) {
                st.setSelected(st.equals(selectedShowtime));
            }
            adapter.notifyDataSetChanged();
        }
    }

    // Get now day and 6 day after this
    private List<String> getDates() {
        List<String> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            String date = new SimpleDateFormat("EEE dd", Locale.getDefault()).format(calendar.getTime());
            dates.add(date);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dates;
    }

    private void sendRequest() {
        mRequestQueue = Volley.newRequestQueue(this);
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        mStringRequest = new StringRequest(Request.Method.GET, "https://moviesapi.ir/api/v1/movies/" + idFilm, response -> {
            Gson gson = new Gson();
            progressBar.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);

            FilmItem item = gson.fromJson(response, FilmItem.class);

            Glide.with(DetailActivity.this)
                    .load(item.getPoster())
                    .into(pic2);
            titleTxt.setText(item.getTitle());
            movieRateTxt.setText(item.getImdbRating());
            movieTimeTxt.setText(item.getRuntime());
            movieDate.setText(item.getReleased());
            movieSummaryInfo.setText(item.getPlot());
            movieActorsInfo.setText(item.getActors());
            if (item.getImages() != null) {
                adapterActorList = new ActorsListAdapter(item.getImages());
                recyclerViewActors.setAdapter(adapterActorList);
            }
            if (item.getGenres() != null) {
                adapterCategory = new CategoryItemFilmListAdapter(item.getGenres());
                recyclerViewCategory.setAdapter(adapterCategory);
            }
        }, error -> progressBar.setVisibility(View.GONE));
        mRequestQueue.add(mStringRequest);
    }

    private void initView() {
        titleTxt = findViewById(R.id.movieNameTxt);
        progressBar = findViewById(R.id.progressBarDetail);
        scrollView = findViewById(R.id.scrollViewDetail);
        pic2 = findViewById(R.id.picDetail);
        movieRateTxt = findViewById(R.id.movieStar);
        movieTimeTxt = findViewById(R.id.movieTime);
        movieDate = findViewById(R.id.movieSchedule);
        movieSummaryInfo = findViewById(R.id.movieSummary);
        movieActorsInfo = findViewById(R.id.movieActors);
        backImg = findViewById(R.id.back2);
        recyclerViewCategory = findViewById(R.id.genreView);
        recyclerViewActors = findViewById(R.id.imageRecyclerview);
        recyclerViewActors.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // load json to get data
    public List<Movie> loadMoviesFromJson(Context context) {
        Gson gson = new Gson();
        Type movieListType = new TypeToken<ArrayList<Movie>>() {
        }.getType();
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("movies.json");
            Reader reader = new InputStreamReader(inputStream);
            return gson.fromJson(reader, movieListType);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Check movie which has id = idFilm => get information of film
    public Movie findMovieById(List<Movie> movies, int idFilm) {
        for (Movie movie : movies) {
            if (movie.getId().equals(idFilm)) {
                return movie;
            }
        }
        return null;
    }

}