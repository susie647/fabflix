package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
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
        url = "https://ec2-3-15-38-179.us-east-2.compute.amazonaws.com:8443/cs122b-spring20-team125/cs122b/";

        search_box.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode==KeyEvent.KEYCODE_ENTER) { //Whenever you got user click enter. Get text in edittext and check it equal test1. If it's true do your code in listenerevent of button3
                    search();
                }

                return false;
            }});
        //assign a listener to call a function to handle the user request when clicking a button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
    }

    public void search() {

        message.setText("Searching Movie Title...");
        // Use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        String searchtext = search_box.getText().toString();

        String query = "?title=" + searchtext + "&year=&director=&star=&page=1&moviesPerPage=20&sort=tara";

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
                    listPage.putExtra("movies", jsonArray.toString());
                    listPage.putExtra("searchText", searchtext);
                    listPage.putExtra("page", 1);
                    //without starting the activity/page, nothing would happen
                    startActivity(listPage);

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

        };

        // !important: queue.add is where the search request is actually sent
        queue.add(searchRequest);

    }
}