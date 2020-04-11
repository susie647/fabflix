package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MoviesServlet", urlPatterns = "/cs122b/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement
            Statement statement = dbcon.createStatement();

            String query = "SELECT m.id as movie_id, m.title as title, m.year as year, m.director as director, g.name as genre, r.rating as rating," +
                    "s.id as star_id, s.name as star_name, s.birthYear as year_of_birth " +
                    "from movies as m, genres as g, genres_in_movies as gm, stars as s, stars_in_movies as sm, ratings as r " +
                    "where m.id=r.movieId and m.id=gm.movieId and gm.genreId=g.id and m.id=sm.movieId and sm.starId=s.id " +
                    "order by r.rating desc limit 184";
            //String query = "SELECT * from movies as m, genres as g, genres_in_movies as gm, stars as s, stars_in_movies as sm," +
            //                    "ratings as r where m.id=r.movieId and m.id=gm.movieId and gm.genreId=g.id and m.id=sm.movieId and " +
            //                    "sm.movieId=s.id order by r.rating desc limit 20";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movie_id = rs.getString("movie_id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_genre = rs.getString("genre");
                String movie_rating = rs.getString("rating");
                String star_id = rs.getString("star_id");
                String star_name = rs.getString("star_name");
                String star_year_of_birth = rs.getString("year_of_birth");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_genre", movie_genre);
                jsonObject.addProperty("movie_rating", movie_rating);
                jsonObject.addProperty("star_id", star_id);
                jsonObject.addProperty("star_name", star_name);
                jsonObject.addProperty("star_year_of_birth", star_year_of_birth);

                jsonArray.add(jsonObject);
            }

            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            dbcon.close();
        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);

        }
        out.close();

    }
}