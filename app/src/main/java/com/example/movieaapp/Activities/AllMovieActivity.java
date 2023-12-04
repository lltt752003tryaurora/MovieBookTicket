package com.example.movieaapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.movieaapp.Adapters.MoviesAdapter;
import com.example.movieaapp.Domain.Movie;
import com.example.movieaapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AllMovieActivity extends AppCompatActivity {
    private TextView btnBack;
    private RecyclerView moviesRecyclerView;
    private MoviesAdapter adapter;
    private List<Movie> movieList;
    private EditText searchEditText;
    private List<String> uniqueGenres;
    private List<Movie> filteredMovieList;
    private Spinner genreSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_movie);
        searchEditText = findViewById(R.id.edtSearch);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        // RecyclerView
        moviesRecyclerView = findViewById(R.id.moviesRecyclerView);
        moviesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        Gson gson = new Gson();
        Type movieListType = new TypeToken<ArrayList<Movie>>() {
        }.getType();
        movieList = gson.fromJson(loadJSONFromAsset(), movieListType);

        // Establish Adapter
        adapter = new MoviesAdapter(this, movieList);
        moviesRecyclerView.setAdapter(adapter);

        // Spinner filter genres
        genreSpinner = findViewById(R.id.genreSpinner);

        TextView filterTextView = findViewById(R.id.textView9);
        filterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (genreSpinner.getVisibility() == View.VISIBLE) {
                    genreSpinner.setVisibility(View.GONE);
                } else {
                    genreSpinner.setVisibility(View.VISIBLE);
                }
            }
        });

        uniqueGenres = getUniqueGenres(movieList);
        filteredMovieList = new ArrayList<>(movieList);

        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, uniqueGenres);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(genreAdapter);

        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedGenre = genreSpinner.getSelectedItem().toString();
                filterMoviesByGenre(selectedGenre);

                TextView filterTextView = findViewById(R.id.textView9);
                filterTextView.setText(selectedGenre);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        filterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (genreSpinner.getVisibility() == View.VISIBLE) {
                    genreSpinner.setVisibility(View.GONE);
                } else {
                    genreSpinner.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //Filter film about genres
    private void filterMoviesByGenre(String genre) {
        filteredMovieList.clear();
        for (Movie movie : movieList) {
            if (movie.getGenres().contains(genre)) {
                filteredMovieList.add(movie);
            }
        }
        adapter.updateList(filteredMovieList);
    }

    private List<String> getUniqueGenres(List<Movie> movies) {
        List<String> genres = new ArrayList<>();
        for (Movie movie : movies) {
            for (String genre : movie.getGenres()) {
                if (!genres.contains(genre)) {
                    genres.add(genre);
                }
            }
        }
        return genres;
    }

    // Filter film about name
    private void filter(String text) {
        List<Movie> filteredList = new ArrayList<>();

        for (Movie movie : movieList) {
            if (movie.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(movie);
            }
        }
        // Update new list after filter
        adapter.updateList(filteredList);
    }

    // Get data from movies.json
    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("movies.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}