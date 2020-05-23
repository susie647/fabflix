package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainPage extends ActionBarActivity {

    private EditText search_box;
//    private EditText password;
    private TextView message;
    private Button searchButton;
    private String url;
    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // upon creation, inflate and initialize the layout
        setContentView(R.layout.main);
        search_box = findViewById(R.id.search_box);
        message = findViewById(R.id.message);
        searchButton = findViewById(R.id.search_button);
        /**
         * In Android, localhost is the address of the device or the emulator.
         * To connect to your machine, you need to use the below IP address
         * **/
        url = "http://10.0.2.2:8080/cs122b-spring20-team125/cs122b/";

        //assign a listener to call a function to handle the user request when clicking a button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
    }

    public void search() {

        message.setText("Trying to search");
        // Use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        String query = "?title=" + search_box.getText().toString() + "&year=&director=&star=&page=1&moviesPerPage=10&sort=tara";

        //request type is GET
        final StringRequest searchRequest = new StringRequest(Request.Method.GET, url + "movie-list"+query, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //TODO should parse the json response to redirect to appropriate functions.
                try {

                    jsonArray = new JSONArray(response);
                    Log.d("search.success", response);
                    //initialize the activity(page)/destination
                    Intent listPage = new Intent(MainPage.this, ListViewActivity.class);
                    //without starting the activity/page, nothing would happen
                    startActivity(listPage);


//                    for (int i=0; i<jsonArray.length(); i++) {
//                        JSONObject item = jsonArray.getJSONObject(i);
//                        String movie_id = item.getString("movie_id");
//                        String movie_title = item.getString("movie_title");
//                        String movie_year = item.getString("movie_year");
//                        String movie_director = item.getString("movie_director");
//                        String movie_genre = item.getString("movie_genre");
//                        String movie_genre_id = item.getString("movie_genre_id");
//                        String movie_rating = item.getString("movie_rating");
//                        String star_id = item.getString("star_id");
//                        String star_name = item.getString("star_name");
//                        String star_played_count = item.getString("star_played_count");
//                    }


//                    String status = reader.getString("status");
//                    if(status.equals("success")) {
//                        Log.d("search.success", response);
//                        //initialize the activity(page)/destination
//                        Intent listPage = new Intent(MainPage.this, ListViewActivity.class);
//                        //without starting the activity/page, nothing would happen
//                        startActivity(listPage);
//                    }
//                    else{
//                        String message = reader.getString("message");
//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    String message = response;
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("search.error", error.toString());
                    }
                }) {
//            @Override
//            protected Map<String, String> getParams() {
//                // Post request form data
//                final Map<String, String> params = new HashMap<>();
////                params.put("email", username.getText().toString());
////                params.put("password", password.getText().toString());
////                params.put("identity", "user");
////                "?title=" + search_box.getText().toString() + "&year=&director=&star=&page=1&moviesPerPage=10&sort=tara"
//                params.put("title", search_box.getText().toString());
//
//                params.put("year", "");
//                params.put("director", "");
//                params.put("star", "");
//                params.put("page", "1");
//                params.put("moviesPerPage", "10");
//                params.put("sort", "tara");
//
//                return params;
//            }
        };

        // !important: queue.add is where the search request is actually sent
        queue.add(searchRequest);

    }
}