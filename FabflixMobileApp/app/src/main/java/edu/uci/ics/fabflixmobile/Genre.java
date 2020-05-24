package edu.uci.ics.fabflixmobile;

public class Genre {
    private int id;
    private String name;

    public Genre(String name, int id){
        this.name = name;
        this.id = id;
    }

    public int getGId() { return id; }

    public String getName() { return name; }
}
