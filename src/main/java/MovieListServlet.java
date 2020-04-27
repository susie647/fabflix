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

        // create/update movie list status
        HttpSession session = request.getSession(true);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            Statement statement;
            ResultSet rs;

            int page = Integer.parseInt(request.getParameter("page"));
            session.setAttribute("ML_page", page);

            int moviesPerPage = Integer.parseInt(request.getParameter("moviesPerPage"));
            session.setAttribute("ML_moviesPerPage", moviesPerPage);

            int offset = moviesPerPage * (page-1);

            String sort = request.getParameter("sort");
            String sortQuery = "";
            if(sort.equals("tara")){
                sortQuery = "m.title ASC, r.rating ASC";
                session.setAttribute("ML_sort", "tara");
            }
            else if(sort.equals("tard")){
                sortQuery = "m.title ASC, r.rating DESC";
                session.setAttribute("ML_sort", "tard");
            }
            else if(sort.equals("tdra")){
                sortQuery = "m.title DESC, r.rating ASC";
                session.setAttribute("ML_sort", "tdra");
            }
            else if(sort.equals("tdrd")){
                sortQuery = "m.title DESC, r.rating DESC";
                session.setAttribute("ML_sort", "tdrd");
            }
            else if(sort.equals("rata")){
                sortQuery = "r.rating ASC, m.title ASC";
                session.setAttribute("ML_sort", "rata");
            }
            else if(sort.equals("ratd")){
                sortQuery = "r.rating ASC, m.title DESC";
                session.setAttribute("ML_sort", "ratd");
            }
            else if(sort.equals("rdta")){
                sortQuery = "r.rating DESC, m.title ASC";
                session.setAttribute("ML_sort", "rdta");
            }
            else if(sort.equals("rdtd")){
                sortQuery = "r.rating DESC, m.title DESC";
                session.setAttribute("ML_sort", "rdtd");
            }


            if(request.getParameter("genreId") != null){
                // query to get all qualifying movieid
                statement = dbcon.createStatement();
                // Generate a SQL query



                String query = String.format("SELECT m.id as movie_id, m.title as title, m.year as year, m.director as director, " +
                                "g.name as genre, gm.genreId as genre_id, r.rating as rating, s.id as star_id, s.name as star_name, sc.count as count " +
                                "from movies as m, genres as g, genres_in_movies as gm, stars as s, stars_in_movies as sm, ratings as r, " +
                                "(select s_id, count(*) as count, sandm.movie_id as movie_id " +
                                "from (select s_table.s_id, sm.movieId as pmovie, s_table.movie_id as movie_id " +
                                "from (select s.id as s_id, sm.movieId as movie_id from (SELECT m.id as movie_id " +
                                "from movies as m, genres as g, genres_in_movies as gm, ratings as r " +
                                "where g.id = %s and gm.genreId=g.id and m.id=gm.movieId and m.id=r.movieId " +
                                "order by %s limit %d offset %d) as movieIDtable, stars_in_movies as sm, stars as s " +
                                "where sm.movieId = movieIDtable.movie_id and sm.starId=s.id) as s_table, stars_in_movies as sm " +
                                "where s_table.s_id = sm.starId) as sandm group by sandm.s_id, sandm.movie_id) as sc " +
                                "where m.id=sc.movie_id and m.id=r.movieId " +
                                "and m.id=gm.movieId and gm.genreId=g.id and m.id=sm.movieId and sm.starId=s.id and sc.s_id = s.id",
                        request.getParameter("genreId"), sortQuery, moviesPerPage, offset);

                rs = statement.executeQuery(query);

                session.setAttribute("ML_status", "genreId="+request.getParameter("genreId"));
            }
            else if(request.getParameter("movieTitle") != null){
                // query to get all qualifying movieid
                statement = dbcon.createStatement();
                // Generate a SQL query
                String movieTitle = request.getParameter("movieTitle");
                String temp;

                if (movieTitle.matches("[A-Z]")){
                    temp = "where m.title LIKE '" + movieTitle + "%' ";
                }

                else if (movieTitle.matches("[0-9]")){
                    temp = "where m.title LIKE '" + movieTitle + "%' ";
                }

                else{
                    temp = "where m.title NOT LIKE '[a-z0-9A-Z]%' ";
                }



                String query = String.format("SELECT m.id as movie_id, m.title as title, m.year as year, m.director as director, " +
                                "g.name as genre, gm.genreId as genre_id, r.rating as rating, s.id as star_id, s.name as star_name, sc.count as count " +
                                "from movies as m, genres as g, genres_in_movies as gm, stars as s, stars_in_movies as sm, ratings as r, " +
                                "(select s_id, count(*) as count, sandm.movie_id as movie_id " +
                                "from (select s_table.s_id, sm.movieId as pmovie, s_table.movie_id as movie_id " +
                                "from (select s.id as s_id, sm.movieId as movie_id from " +
                                "(SELECT m.id as movie_id from movies as m, ratings as r %s and m.id=r.movieId " +
                                "order by %s limit %d offset %d) as movieIDtable, stars_in_movies as sm, stars as s " +
                                "where sm.movieId = movieIDtable.movie_id and sm.starId=s.id) as s_table, stars_in_movies as sm " +
                                "where s_table.s_id = sm.starId) as sandm group by sandm.s_id, sandm.movie_id) as sc " +
                                "where m.id=sc.movie_id and m.id=r.movieId " +
                                "and m.id=gm.movieId and gm.genreId=g.id and m.id=sm.movieId and sm.starId=s.id and sc.s_id = s.id",
                        temp, sortQuery, moviesPerPage, offset);

                rs = statement.executeQuery(query);

                session.setAttribute("ML_status", "movieTitle="+movieTitle);
            }
            else {
                // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in index.html

                String ML_status = "title=";

                String title = "";
                title = request.getParameter("title");
                ML_status+=title;

                ML_status+="&year=";
                int year = -1;
                if (!request.getParameter("year").equals("")) {
                    year = Integer.parseInt(request.getParameter("year"));
                    ML_status+=Integer.toString(year);
                }

                ML_status+="&director=";
                String director = "";
                director = request.getParameter("director");
                ML_status+=director;

                ML_status+="&star=";
                String star = "";
                star = request.getParameter("star");
                ML_status+=star;

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
                                "g.name as genre, gm.genreId as genre_id, r.rating as rating, s.id as star_id, s.name as star_name, sc.count as count " +
                                "from movies as m, genres as g, genres_in_movies as gm, stars as s, stars_in_movies as sm, ratings as r, " +
                                "(select s_id, count(*) as count, sandm.movie_id as movie_id " +
                                "from (select s_table.s_id, sm.movieId as pmovie, s_table.movie_id as movie_id " +
                                "from (select s.id as s_id, sm.movieId as movie_id from (SELECT m.id as movie_id " +
                                "from movies as m, stars as s, stars_in_movies as sm, ratings as r " +
                                "where m.id=sm.movieId and sm.starId=s.id and m.id=r.movieId " +
                                "%s order by %s limit %d offset %d) as movieIDtable, stars_in_movies as sm, stars as s " +
                                "where sm.movieId = movieIDtable.movie_id and sm.starId=s.id) as s_table, stars_in_movies as sm " +
                                "where s_table.s_id = sm.starId) as sandm group by sandm.s_id, sandm.movie_id) as sc " +
                                "where m.id=sc.movie_id and m.id=r.movieId " +
                                "and m.id=gm.movieId and gm.genreId=g.id and m.id=sm.movieId and sm.starId=s.id and sc.s_id = s.id",
                        temp, sortQuery, moviesPerPage, offset);
                rs = statement.executeQuery(query);

                session.setAttribute("ML_status", ML_status);

            }


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
}