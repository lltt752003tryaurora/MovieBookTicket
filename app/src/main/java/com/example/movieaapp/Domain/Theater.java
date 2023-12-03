package com.example.movieaapp.Domain;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Theater {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("showtimes")
    @Expose
    private List<Showtime> showtimes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Showtime> getShowtimes() {
        return showtimes;
    }

    public void setShowtimes(List<Showtime> showtimes) {
        this.showtimes = showtimes;
    }

}
