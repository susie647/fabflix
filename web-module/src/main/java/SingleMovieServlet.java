package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/cs122b/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("movieId");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Construct a query with parameter represented by "?"
            String query = "SELECT m.id as movie_id, m.title as title, m.year as year, m.director as director, g.name as genre, gm.genreId as genre_id, "+
                    "r.rating as rating, s.id as star_id, s.name as star_name, sc.count as count from movies as m, genres as g, "+
                    "genres_in_movies as gm, stars as s, stars_in_movies as sm, ratings as r, (select s_id, count(*) as count "+
                    "from (select s_table.s_id, sm.movieId from (select s.id as s_id from movies as m, stars_in_movies as sm, "+
                    "stars as s where m.id =? and m.id=sm.movieId and sm.starId=s.id) as s_table, stars_in_movies as sm "+
                    "where s_table.s_id = sm.starId) as sandm group by sandm.s_id) as sc where m.id=? and m.id=r.movieId "+
                    "and m.id=gm.movieId and gm.genreId=g.id and m.id=sm.movieId and sm.starId=s.id and sc.s_id = s.id";


            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);
            statement.setString(2, id);


            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();


            // Iterate through each row of rs
            while (rs.next()) {

                String movie_id = rs.getString("movie_id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_genre = rs.getString("genre");
                String movie_genre_id = rs.getString("genre_id");
                String movie_rating = rs.getString("rating");
                String star_id = rs.getString("star_id");
                String star_name = rs.getString("star_name");
                String star_played_count = rs.getString("count");

                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_genre", movie_genre);
                jsonObject.addProperty("movie_genre_id", movie_genre_id);
                jsonObject.addProperty("movie_rating", movie_rating);
                jsonObject.addProperty("star_id", star_id);
                jsonObject.addProperty("star_name", star_name);
                jsonObject.addProperty("star_played_count", star_played_count);

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


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonObject responseJsonObject = new JsonObject();
        try {
            HttpSession session = request.getSession(true);

            responseJsonObject.addProperty("ML_status", session.getAttribute("ML_status").toString());
            responseJsonObject.addProperty("ML_page", session.getAttribute("ML_page").toString());
            responseJsonObject.addProperty("ML_moviesPerPage", session.getAttribute("ML_moviesPerPage").toString());
            responseJsonObject.addProperty("ML_sort", session.getAttribute("ML_sort").toString());

            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "logout successfully");
        } catch (Exception ex) {
            responseJsonObject.addProperty("message", "session error");
            // Output Error Massage to html
            //out.println(String.format("<html><head><title>MovieDBExample: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", ex.getMessage()));
            return;
        }
        response.getWriter().write(responseJsonObject.toString());
    }

}

