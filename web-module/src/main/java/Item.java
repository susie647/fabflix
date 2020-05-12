package main.java;

import java.util.Random;

public class Item {
    private String movie_id;
    private String movie_title;
    private int quantity;
    private int price;
    private int unitPrice;

    public Item(String movie_id, String movie_title) {
        this.movie_id = movie_id;
        this.movie_title = movie_title;
        this.quantity = 1;
        this.unitPrice = new Random().nextInt(15)+5;
        this.price = unitPrice;
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

    public void updatePrice(boolean add) {
        if(add){
            this.price += unitPrice;
        }
        else{
            this.price -= unitPrice;
        }
    }
}
