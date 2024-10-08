package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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
import java.sql.SQLException;
import java.sql.Statement;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MoviesServlet", urlPatterns = "/cs122b/browse")
public class BrowseServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject responseJsonObject = new JsonObject();
        response.setContentType("application/json"); // Response mime type

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

            Connection connection = ds.getConnection();
            if (connection == null) {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "dbcon is null.");
                response.getWriter().write(responseJsonObject.toString());
//                out.println("dbcon is null.");
            }
            // Get a connection from dataSource
//            Connection connection = dataSource.getConnection();
            // declare statement
            Statement statement = connection.createStatement();
            // prepare query
            String query = "SELECT g.id as genre_id, g.name as genre_name from genres as g";
            // execute query
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String starId = rs.getString("genre_id");
                String starName = rs.getString("genre_name");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("genre_id", starId);
                jsonObject.addProperty("genre_name", starName);

                jsonArray.add(jsonObject);
            }
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            connection.close();
        } catch (SQLException | NamingException e) {

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