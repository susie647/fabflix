package edu.uci.ics.fabflixmobile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SingleMovieListViewAdapter extends ArrayAdapter<Movie> {
    private ArrayList<Movie> movies;

    public SingleMovieListViewAdapter(ArrayList<Movie> movies, Context context) {
        super(context, R.layout.row, movies);
        this.movies = movies;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.row, parent, false);

        Movie movie = movies.get(position);


//        TextView pageTitleView = view.findViewById(R.id.pageTitle);
        TextView titleView = view.findViewById(R.id.title);
        TextView yearView = view.findViewById(R.id.year);
        TextView directorView = view.findViewById(R.id.director);
        TextView genresView = view.findViewById(R.id.genres);
        TextView starsView = view.findViewById(R.id.stars);

//        pageTitleView.setText("Single Movie Page");
        titleView.setText(movie.getTitle());
        yearView.setText("Year: " + movie.getYear() + "");// need to cast the year to a string to set the label
        directorView.setText("Director: " + movie.getDirector());

        //parse genre, limit to 3
        String genreNames = movie.getGenres();
//        int index =0;
//        for (int i=0; i<3; i++) {
//            index = genreNames.indexOf(',', index+1);
//            if (index == -1) {//less than 3
//                break;
//            }
//        }
//        if (index>-1){genreNames = genreNames.substring(0,index);}

        genresView.setText("Genres: " + genreNames);



        //parse star, limit to 3
        String starNames = movie.getStars();
//        index =0;
//        for (int i=0; i<3; i++) {
//            index = starNames.indexOf(',', index+1);
//            if (index == -1) {//less than 3
//                break;
//            }
//        }
//        if(index==-1){starNames = starNames.substring(0,starNames.length()-1);}
//        else{starNames = starNames.substring(0,index);}
//        starsView.setText(starNames);

        starNames = starNames.substring(0,starNames.length()-2);
        starsView.setText("Stars: " + starNames);

        return view;
    }
}