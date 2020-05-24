package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SingleListActivity extends Activity {
//    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        //this should be retrieved from the database and the backend server

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String movies = extras.getString("movies");
            //The key argument here must match that used in the other activity
            try {
                JSONArray jsonArray = new JSONArray(movies);
//                Toast.makeText(getApplicationContext(), movies, Toast.LENGTH_SHORT).show();
                final ArrayList<Movie> movieList = new ArrayList<>();

                HashMap<String, Movie> moviesHMap = new HashMap<String, Movie>();

                for (int i=0; i<jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    String movie_id = item.getString("movie_id");
                    String movie_title = item.getString("movie_title");
                    String movie_year = item.getString("movie_year");
                    String movie_director = item.getString("movie_director");
                    String movie_genre = item.getString("movie_genre");
                    String movie_genre_id = item.getString("movie_genre_id");
                    String movie_rating = item.getString("movie_rating");
                    String star_id = item.getString("star_id");
                    String star_name = item.getString("star_name");
                    String star_played_count = item.getString("star_played_count");

                    if(!moviesHMap.containsKey(movie_id)){
                        Movie newItem = new Movie(movie_id, movie_title, (short) Integer.parseInt(movie_year), movie_director, movie_rating);
                        newItem.addGenre(movie_genre, Integer.parseInt(movie_genre_id));
                        newItem.addStar(star_name, star_id, Integer.parseInt(star_played_count));
                        moviesHMap.put(movie_id, newItem);
                    }
                    else{
                        moviesHMap.get(movie_id).addStar(star_name, star_id, Integer.parseInt(star_played_count));
                        moviesHMap.get(movie_id).addGenre(movie_genre, Integer.parseInt(movie_genre_id));
                    }
                }

                Iterator it = moviesHMap.entrySet().iterator();
                while(it.hasNext()) {
                    Map.Entry entry = (Map.Entry)it.next();
                    movieList.add((Movie) entry.getValue());
                }
//        if (extras != null) {
//            String movies = extras.getString("movies");
//            //The key argument here must match that used in the other activity
//            try {
//                JSONArray jsonArray = new JSONArray(movies);
////                Toast.makeText(getApplicationContext(), movies, Toast.LENGTH_SHORT).show();
//                final ArrayList<Movie> movieList = new ArrayList<>();
//
//                HashMap<String, Movie> moviesHMap = new HashMap<String, Movie>();
//
//                for (int i=0; i<jsonArray.length(); i++) {
//                    JSONObject item = jsonArray.getJSONObject(i);
//                    String movie_id = item.getString("movie_id");
//                    String movie_title = item.getString("movie_title");
//                    String movie_year = item.getString("movie_year");
//                    String movie_director = item.getString("movie_director");
//                    String movie_genre = item.getString("movie_genre");
//                    String movie_genre_id = item.getString("movie_genre_id");
//                    String movie_rating = item.getString("movie_rating");
//                    String star_id = item.getString("star_id");
//                    String star_name = item.getString("star_name");
//                    String star_played_count = item.getString("star_played_count");
//
//                    if(!moviesHMap.containsKey(movie_id)){
//                        Movie newItem = new Movie(movie_id, movie_title, (short) Integer.parseInt(movie_year), movie_director, movie_rating);
//                        newItem.addGenre(movie_genre, Integer.parseInt(movie_genre_id));
//                        newItem.addStar(star_name, star_id, Integer.parseInt(star_played_count));
//                        moviesHMap.put(movie_id, newItem);
//                    }
//                    else{
//                        moviesHMap.get(movie_id).addStar(star_name, star_id, Integer.parseInt(star_played_count));
//                        moviesHMap.get(movie_id).addGenre(movie_genre, Integer.parseInt(movie_genre_id));
//                    }
//                }
//
//                Iterator it = moviesHMap.entrySet().iterator();
//                while(it.hasNext()) {
//                    Map.Entry entry = (Map.Entry)it.next();
//                    movieList.add((Movie) entry.getValue());
//                }

                MovieListViewAdapter adapter = new MovieListViewAdapter(movieList, this);

                ListView listView = findViewById(R.id.list);
                listView.setAdapter(adapter);

//                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Movie movie = movieList.get(position);
//
//                        Intent listPage = new Intent(SingleListActivity.this, SingleListActivity.class);
//                        listPage.putExtra("movies", jsonArray.toString());
//                        //without starting the activity/page, nothing would happen
//                        startActivity(listPage);
//
//
//
//                        String message = String.format("Clicked on position: %d, title: %s, %d", position, movie.getTitle(), movie.getYear());
//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//                    }
//                });

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "cannot convert to jsonArray", Toast.LENGTH_SHORT).show();
            }
        }

//        final ArrayList<Movie> movies = new ArrayList<>();
//        movies.add(new Movie("The Terminal", (short) 2004));
//        movies.add(new Movie("The Final Season", (short) 2007));

//        MovieListViewAdapter adapter = new MovieListViewAdapter(movies, this);

//        ListView listView = findViewById(R.id.list);
//        listView.setAdapter(adapter);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Movie movie = movies.get(position);
//                String message = String.format("Clicked on position: %d, title: %s, %d", position, movie.getTitle(), movie.getYear());
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//            }
//        });
    }


}