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
@WebServlet(name = "MovieListServlet", urlPatterns = "/cs122b/movie-list")
public class MovieListServlet extends HttpServlet {
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

            Statement statement;
            ResultSet rs;

            if(request.getParameter("genreId") != null){
                // query to get all qualifying movieid
                statement = dbcon.createStatement();
                // Generate a SQL query

                String query = String.format("SELECT m.id as movie_id, m.title as title, m.year as year, m.director as director, " +
                        "g.name as genre, r.rating as rating, s.id as star_id, s.name as star_name " +
                        "from movies as m, genres as g, genres_in_movies as gm, stars as s, stars_in_movies as sm, ratings as r, " +
                        "(SELECT distinct m.id as movie_id " +
                        "from movies as m, genres as g, genres_in_movies as gm " +
                        "where g.id = %s and gm.genreId=g.id and m.id=gm.movieId " +
                        "limit 20 offset 0) as movieIDtable " +
                        "where m.id=movieIDtable.movie_id and m.id=r.movieId and m.id=gm.movieId and " +
                        "gm.genreId=g.id and m.id=sm.movieId and sm.starId=s.id", request.getParameter("genreId"));
                rs = statement.executeQuery(query);
            }
            else if(request.getParameter("movieTitle") != null){
                // query to get all qualifying movieid
                statement = dbcon.createStatement();
                // Generate a SQL query
                String movieTitle = request.getParameter("movieTitle");
                String temp;

                if (movieTitle.matches("[A-Z]")){
                    temp = "where m.title LIKE '" + movieTitle.toLowerCase() + "%' OR m.title LIKE '"+ movieTitle + "%' ";
                }
                else if (movieTitle.matches("[0-9]")){
                    temp = "where m.title LIKE '" + movieTitle + "%' ";
                }
                else{
                    temp = "where m.title NOT LIKE '[a-z0-9A-Z]%' ";
                }
                String query = String.format("SELECT m.id as movie_id, m.title as title, m.year as year, m.director as director, " +
                        "g.name as genre, r.rating as rating, s.id as star_id, s.name as star_name " +
                        "from movies as m, genres as g, genres_in_movies as gm, stars as s, stars_in_movies as sm, ratings as r, " +
                        "(SELECT distinct m.id as movie_id from movies as m %s " +
                        "limit 20 offset 0) as movieIDtable " +
                        "where m.id=movieIDtable.movie_id and m.id=r.movieId and m.id=gm.movieId and " +
                        "gm.genreId=g.id and m.id=sm.movieId and sm.starId=s.id", temp);
                rs = statement.executeQuery(query);
            }
            else {
                // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in index.html
                String title = "";
                title = request.getParameter("title");
                int year = -1;
                if (!request.getParameter("year").equals(""))
                    year = Integer.parseInt(request.getParameter("year"));
                String director = "";
                director = request.getParameter("director");
                String star = "";
                star = request.getParameter("star");


                String temp = "";
                if (title.compareTo("") > 0) {
                    temp += String.format(" and m.title like '%s'", title);
                }
                if (year > -1) {
                    temp += String.format(" and m.year='%d'", year);
                }
                if (director.compareTo("") > 0) {
                    temp += String.format(" and m.director like '%s'", director);
                }
                if (star.compareTo("") > 0) {
                    temp += String.format(" and s.name like '%s'", star);
                }

                // query to get all qualifying movieid
                statement = dbcon.createStatement();
                // Generate a SQL query

                String query = String.format("SELECT m.id as movie_id, m.title as title, m.year as year, m.director as director, " +
                        "g.name as genre, r.rating as rating, s.id as star_id, s.name as star_name " +
                        "from movies as m, genres as g, genres_in_movies as gm, stars as s, stars_in_movies as sm, ratings as r, " +
                        "(SELECT distinct m.id as movie_id " +
                        "from movies as m, stars as s, stars_in_movies as sm " +
                        "where m.id=sm.movieId and sm.starId=s.id" +
                        "%s limit 20 offset 0) as movieIDtable " +
                        "where m.id=movieIDtable.movie_id and m.id=r.movieId and m.id=gm.movieId and " +
                        "gm.genreId=g.id and m.id=sm.movieId and sm.starId=s.id", temp);
                rs = statement.executeQuery(query);
            }


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