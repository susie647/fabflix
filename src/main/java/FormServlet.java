package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
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

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedb,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "FormServlet", urlPatterns = "/form")
public class FormServlet extends HttpServlet {

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;


    // Use http GET
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Building page head with title
        //out.println("<html><head><title>MovieDBExample: Found Records</title></head>");

        // Building page body
        //out.println("<body><h1>MovieDB: Found Records</h1>");


        try {

            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Declare a new statement
            Statement statement = dbCon.createStatement();

            // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in index.html
            String title = "";
            title = request.getParameter("title");
            int year = -1;
            if(!request.getParameter("year").equals(""))
                year = Integer.parseInt(request.getParameter("year"));
            String director = "";
            director = request.getParameter("director");
            String star = "";
            star = request.getParameter("star");



            String temp = "";
            if (title.compareTo("")>0){
                temp+=String.format(" and m.title like '%s'",title);
            }
            if (year>-1){
                temp+=String.format(" and m.year like '%d'",year);
            }
            if (director.compareTo("")>0){
                temp+=String.format(" and m.director like '%s'", director);
            }
            if (star.compareTo("")>0){
                temp+=String.format(" and s.name like '%s'", star);
            }
            // Generate a SQL query
            String query = String.format("SELECT distinct m.id as movie_id, m.title as title, m.year as year, m.director as director, " +
                    "g.name as genre, r.rating as rating, s.id as star_id, s.name as star_name, s.birthYear as year_of_birth " +
                    "from movies as m, genres as g, genres_in_movies as gm, stars as s, stars_in_movies as sm, ratings as r " +
                    "where m.id=r.movieId and m.id=gm.movieId and gm.genreId=g.id and m.id=sm.movieId and sm.starId=s.id" +
                    "%s", temp);

                   // "SELECT * from stars where name like '%s'", star);

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

            /*
            // Create a html <table>
            out.println("<table border>");

            // Iterate through each row of rs and create a table row <tr>
            out.println("<tr><td>ID</td><td>Name</td></tr>");
            while (rs.next()) {
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                //String m_ID = rs.getString("ID");
                //String m_Name = rs.getString("name");
                out.println(String.format("<tr><td>%s</td><td>%s</td></tr>", movie_title, movie_year));
            }
            out.println("</table>");
*/

            // Close all structures
            rs.close();
            statement.close();
            dbCon.close();

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