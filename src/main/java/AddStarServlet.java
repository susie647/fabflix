package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;


@WebServlet(name = "AddStarServlet", urlPatterns = "/cs122b/add-star")
public class AddStarServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //HttpSession session = request.getSession(true);
        response.setContentType("application/json"); // Response mime type
        JsonObject responseJsonObject = new JsonObject();
        //PrintWriter out = response.getWriter();

        try {
            Connection dbCon = dataSource.getConnection();
            String star_name = request.getParameter("star_name");
            String star_birthYear = request.getParameter("star_birthYear");

            //find max star id
            Statement statement = dbCon.createStatement();
            String query = "SELECT max(s.id) as maxStarId from stars as s";
            ResultSet rsMax = statement.executeQuery(query);

            //retrieve and increment id
            String maxStarId = "";
            while(rsMax.next()){
                maxStarId = rsMax.getString("maxStarId");
            }
            String newStarId = maxStarId.substring(0,2) + Integer.parseInt(maxStarId.substring(2))+1;

            rsMax.close();
            statement.close();

            //prepared statement for add star
            String select = "INSERT INTO stars VALUES (?, ?, ?);";
            PreparedStatement update = dbCon.prepareStatement(select);

            update.setString(1, newStarId);
            update.setString(2, star_name);
            update.setString(3, star_birthYear);
            update.executeUpdate();

            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");

            update.close();
            dbCon.close();
            response.setStatus(200);


        } catch (SQLException e) {
            e.printStackTrace();
            //response.getWriter().write("failure");
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "fail");
            response.getWriter().write(responseJsonObject.toString());
            response.setStatus(500);
        }

        //out.close();
        //response.getWriter().write("success");
        response.getWriter().write(responseJsonObject.toString());
    }
}
