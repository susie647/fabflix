package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;



// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MovieListServlet", urlPatterns = "/cs122b/movie-list")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;

    private String checkSortingOrder(String sort, HttpSession session){
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
        else{
            return null;
        }

        return sortQuery;
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Time an event in a program to nanosecond precision
        long startTimeTS = System.nanoTime();
        long TJ = 0;

        JsonObject responseJsonObject = new JsonObject();
        response.setContentType("application/json"); // Response mime type

        // create/update movie list status
        HttpSession session = request.getSession(true);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
// the following few lines are for connection pooling
            // Obtain our environment naming context

            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null) {
//                out.println("envCtx is NULL");
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "envCtx is NULL");
                response.getWriter().write(responseJsonObject.toString());
            }

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

            // the following commented lines are direct connections without pooling
            //Class.forName("org.gjt.mm.mysql.Driver");
            //Class.forName("com.mysql.jdbc.Driver").newInstance();
            //Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            if (ds == null){
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "ds is null.");
                response.getWriter().write(responseJsonObject.toString());
            }
//                out.println("ds is null.");

            int page = Integer.parseInt(request.getParameter("page"));
            session.setAttribute("ML_page", page);

            int moviesPerPage = Integer.parseInt(request.getParameter("moviesPerPage"));
            session.setAttribute("ML_moviesPerPage", moviesPerPage);

            int offset = moviesPerPage * (page-1);

            String sort = request.getParameter("sort");
            String sortQuery = checkSortingOrder(sort, session);
            if(sortQuery == null) {
                throw new Exception("invalid sorting order");
            }

            // Time an event in a program to nanosecond precision
            long startTimeTJ = System.nanoTime();
            Connection dbcon = ds.getConnection();

            if (dbcon == null) {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "dbcon is null.");
                response.getWriter().write(responseJsonObject.toString());
//                out.println("dbcon is null.");
            }
            // Get a connection from dataSource
//            Connection dbcon = dataSource.getConnection();

            PreparedStatement statement;
            ResultSet rs;

            if(request.getParameter("genreId") != null){
                // Generate a SQL query
                String query = String.format("SELECT m.id as movie_id, m.title as title, m.year as year, m.director as director, " +
                                "g.name as genre, gm.genreId as genre_id, r.rating as rating, s.id as star_id, s.name as star_name, sc.count as count " +
                                "from movies as m, genres as g, genres_in_movies as gm, stars as s, stars_in_movies as sm, ratings as r, " +
                                "(select s_id, count(*) as count, sandm.movie_id as movie_id " +
                                "from (select s_table.s_id, sm.movieId as pmovie, s_table.movie_id as movie_id " +
                                "from (select s.id as s_id, sm.movieId as movie_id from (SELECT distinct m.id as movie_id, m.title, r.rating " +
                                "from movies as m, genres as g, genres_in_movies as gm, ratings as r " +
                                "where g.id = ? and gm.genreId=g.id and m.id=gm.movieId and m.id=r.movieId " +
                                "order by %s limit %d offset %d) as movieIDtable, stars_in_movies as sm, stars as s " +
                                "where sm.movieId = movieIDtable.movie_id and sm.starId=s.id) as s_table, stars_in_movies as sm " +
                                "where s_table.s_id = sm.starId) as sandm group by sandm.s_id, sandm.movie_id) as sc " +
                                "where m.id=sc.movie_id and m.id=r.movieId " +
                                "and m.id=gm.movieId and gm.genreId=g.id and m.id=sm.movieId and sm.starId=s.id and sc.s_id = s.id order by %s",
                        sortQuery, moviesPerPage, offset, sortQuery);

                statement = dbcon.prepareStatement(query);
                statement.setString(1, request.getParameter("genreId"));
                rs = statement.executeQuery();

                session.setAttribute("ML_status", "genreId="+request.getParameter("genreId"));
            }
            else if(request.getParameter("movieTitle") != null){
                // Generate a SQL query
                String movieTitle = request.getParameter("movieTitle");
                String temp;

                if (movieTitle.matches("[A-Z]")){
                    temp = "^[" + movieTitle + movieTitle.toLowerCase() + "]";
                }
                else if (movieTitle.matches("[0-9]")){
                    temp = "^" + movieTitle;
                }
                else if (movieTitle.equals("*")){
                    temp = "^[^0-9A-Za-z]";
                }
                else{
                    throw new Exception("invalid movie title");
                }

                String query = String.format("SELECT m.id as movie_id, m.title as title, m.year as year, m.director as director, " +
                                "g.name as genre, gm.genreId as genre_id, r.rating as rating, s.id as star_id, s.name as star_name, sc.count as count " +
                                "from movies as m, genres as g, genres_in_movies as gm, stars as s, stars_in_movies as sm, ratings as r, " +
                                "(select s_id, count(*) as count, sandm.movie_id as movie_id " +
                                "from (select s_table.s_id, sm.movieId as pmovie, s_table.movie_id as movie_id " +
                                "from (select s.id as s_id, sm.movieId as movie_id from " +
                                "(SELECT distinct m.id as movie_id, m.title, r.rating from movies as m, ratings as r where m.title regexp ? and m.id=r.movieId " +
                                "order by %s limit %d offset %d) as movieIDtable, stars_in_movies as sm, stars as s " +
                                "where sm.movieId = movieIDtable.movie_id and sm.starId=s.id) as s_table, stars_in_movies as sm " +
                                "where s_table.s_id = sm.starId) as sandm group by sandm.s_id, sandm.movie_id) as sc " +
                                "where m.id=sc.movie_id and m.id=r.movieId " +
                                "and m.id=gm.movieId and gm.genreId=g.id and m.id=sm.movieId and sm.starId=s.id and sc.s_id = s.id order by %s",
                        sortQuery, moviesPerPage, offset, sortQuery);

                statement = dbcon.prepareStatement(query);

                statement.setString(1, temp);
                rs = statement.executeQuery();

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

                    // full text search
//                    temp += " and MATCH (m.title) AGAINST (? IN BOOLEAN MODE)";

                    // full text search(match against) + fuzzy search(like + ed(SimilarTo))
                    temp += " and ( (MATCH (m.title) AGAINST (? IN BOOLEAN MODE)) OR (m.title like ?) OR (ed(m.title, ?) <= ?) )";
                }
                if (year > -1) {
                    temp += " and m.year= ? ";
                }
                if (director.compareTo("") > 0) {
                    temp += " and m.director like ? ";
                }
                if (star.compareTo("") > 0) {
                    temp += " and s.name like ? ";
                }

                // query to get all qualifying movieid
//                statement = dbcon.createStatement();
                // Generate a SQL query
                String query = String.format("SELECT m.id as movie_id, m.title as title, m.year as year, m.director as director, " +
                                "g.name as genre, gm.genreId as genre_id, r.rating as rating, s.id as star_id, s.name as star_name, sc.count as count " +
                                "from movies as m, genres as g, genres_in_movies as gm, stars as s, stars_in_movies as sm, ratings as r, " +
                                "(select s_id, count(*) as count, sandm.movie_id as movie_id " +
                                "from (select s_table.s_id, sm.movieId as pmovie, s_table.movie_id as movie_id " +
                                "from (select s.id as s_id, sm.movieId as movie_id from (SELECT distinct m.id as movie_id, m.title, r.rating " +
                                "from movies as m, stars as s, stars_in_movies as sm, ratings as r " +
                                "where m.id=sm.movieId and sm.starId=s.id and m.id=r.movieId " +
                                "%s order by %s limit %d offset %d) as movieIDtable, stars_in_movies as sm, stars as s " +
                                "where sm.movieId = movieIDtable.movie_id and sm.starId=s.id) as s_table, stars_in_movies as sm " +
                                "where s_table.s_id = sm.starId) as sandm group by sandm.s_id, sandm.movie_id) as sc " +
                                "where m.id=sc.movie_id and m.id=r.movieId " +
                                "and m.id=gm.movieId and gm.genreId=g.id and m.id=sm.movieId and sm.starId=s.id and sc.s_id = s.id order by %s",
                        temp, sortQuery, moviesPerPage, offset, sortQuery);


                statement = dbcon.prepareStatement(query);

                int num = 0;
                if (title.compareTo("") > 0) {

                    // full text search
                    //split search into array "good a" -> [good,a] -> '+good* +a*'
                    String [] titleArr = title.split(" ");
                    String ftTitle = "";
                    for (String word: titleArr){
                        ftTitle += "+"+word+"* ";
                    }
                    statement.setString(++num, ftTitle);

                    // fuzzy search
                    String likeItem = "%" + title + "%";
                    // allow users to make 1 typo if the length is less than 4
                    //                     2 typos if the length is greater than 3 and less than 7
                    //                     3 typos otherwise
                    Integer edNum = 1;
                    if(title.length() > 3 && title.length() < 7)
                        edNum = 2;
                    else if(title.length() > 6)
                        edNum = 3;
                    statement.setString(++num, likeItem);
                    statement.setString(++num, title);
                    statement.setInt(++num, edNum);
                }
                if (year > -1) {
                    statement.setInt(++num, year);
                }
                if (director.compareTo("") > 0) {
                    director = "%"+director+"%";
                    statement.setString(++num, director);
                }
                if (star.compareTo("") > 0) {
                    star = "%"+star+"%";
                    statement.setString(++num, star);
                }
                rs = statement.executeQuery();
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
                if(movie_rating.equals("-1")){
                    movie_rating = "N/A";
                }
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

            // end Timer
            long endTimeTJ = System.nanoTime();
            TJ = endTimeTJ - startTimeTJ; // elapsed time in nano seconds. Note: print the values in nano seconds

        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);

        }
        out.close();

        // end Timer
        long endTimeTS = System.nanoTime();
        long TS = endTimeTS - startTimeTS; // elapsed time in nano seconds. Note: print the values in nano seconds

        String contextPath = getServletContext().getRealPath("/");
        String xmlFilePath=contextPath+"log1.txt";
//        File myfile = new File(xmlFilePath);
//        myfile.createNewFile();
        FileWriter myWriter = new FileWriter(xmlFilePath, true);
        myWriter.write(String.format("%d | %d\n", TS, TJ));
        myWriter.close();
//        System.out.println("wrote");
    }
}