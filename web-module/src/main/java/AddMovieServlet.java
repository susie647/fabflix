package main.java;

import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.sql.Statement;


@WebServlet(name = "AddMovieServlet", urlPatterns = "/cs122b/add-movie")
public class AddMovieServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //HttpSession session = request.getSession(true);
        JsonObject responseJsonObject = new JsonObject();
        //PrintWriter out = response.getWriter();

        try {
            Connection dbCon = dataSource.getConnection();
            String movie_title = request.getParameter("movie_title");
            String movie_year = request.getParameter("movie_year");
            String movie_director = request.getParameter("movie_director");
            String movie_genre = request.getParameter("movie_genre");
            String star_name = request.getParameter("star_name");


            String call = "call add_movie(?, ?, ?, ?, ?)";
            PreparedStatement statement = dbCon.prepareStatement(call);
            statement.setString(1, movie_title);
            statement.setInt(2, Integer.parseInt(movie_year));
            statement.setString(3, movie_director);
            statement.setString(4, star_name);
            statement.setString(5, movie_genre);

            ResultSet rs = statement.executeQuery();
            String status = "";
            if(rs.next()){
//                status = Integer.parseInt(rs.getString("status"));
                status = rs.getString("status");
            }


            if(!status.equals("0")){
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
                responseJsonObject.addProperty("newIds", status);
            }
            else{
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "movie already exists!");
            }


            statement.close();
            rs.close();
            dbCon.close();
            response.setStatus(200);


        } catch (SQLException e) {
            e.printStackTrace();
            //response.getWriter().write("failure");
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "some errors occur");
            response.getWriter().write(responseJsonObject.toString());
            response.setStatus(500);
        }

        //out.close();
        //response.getWriter().write("success");
        response.getWriter().write(responseJsonObject.toString());
    }
}
