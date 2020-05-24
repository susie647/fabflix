package edu.uci.ics.fabflixmobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Movie {
    private String title;
    private short year;
    private String id;
    private String director;
    private String rating;
    private HashMap<Integer, String> genres;
    private HashMap<String, Star> stars;

    public Movie(String id, String name, short year, String director, String rating) {
        this.id = id;
        this.title = name;
        this.year = year;
        this.director = director;
        this.rating = rating;
        this.genres = new HashMap<Integer, String>();
        this.stars = new HashMap<String, Star>();
    }

    public String getTitle() {
        return title;
    }

    public short getYear() {
        return year;
    }

    public String getId() { return id; }

    public String getRating() { return rating; }

    public String getDirector() { return director; }

    public void addGenre(String name, Integer gid) {
        if(!genres.containsKey(gid))
            genres.put(gid, name);
    }

    public HashMap<Integer, String> getGenres() { return genres; }

    public void addStar(String starId, String star, Integer playCount) {
        if(!this.stars.containsKey(starId))
            this.stars.put(starId, new Star(star, starId, playCount));
    }

    public HashMap<String, Star> getStars() { return stars; }
}