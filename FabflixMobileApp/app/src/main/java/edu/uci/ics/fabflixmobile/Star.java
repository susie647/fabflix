package edu.uci.ics.fabflixmobile;

public class Star {
    private String name;
    private String id;
    private int play_count;

    public Star(String name, String id, int play_count){
        this.name = name;
        this.id = id;
        this.play_count = play_count;
    }

    public String getName() { return name; }

    public String getSId() { return id; }

    public int getPlayCount() { return play_count; }
}
