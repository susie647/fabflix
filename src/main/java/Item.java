package main.java;

public class Item {
    private String movie_id;
    private String movie_title;
    private int quantity;
    private int price;

    public Item(String movie_id, String movie_title, int quantity, int price) {
        this.movie_id = movie_id;
        this.movie_title = movie_title;
        this.quantity = quantity;
        this.price = price;
    }

    public String getMovieTitle() { return this.movie_title; }

    public int getQuantity() {
        return this.quantity;
    }

    public String getMovieId() {
        return this.movie_id;
    }

    public int getPrice() { return this.price; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
