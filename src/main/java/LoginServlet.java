package main.java;

import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonObject responseJsonObject = new JsonObject();
        try {

            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Declare a new statement
            Statement statement = dbCon.createStatement();

            // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in index.html
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            // Generate a SQL query
            String query = String.format("SELECT password from customers where email = '%s'", email);

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            // Iterate through each row of rs and create a table row <tr>
            //out.println("<tr><td>ID</td><td>Name</td></tr>");
            if (rs.next()) {
                if(password.equals(rs.getString("password"))){
                    request.getSession().setAttribute("user", new User(email));

                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                }
                else{
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "incorrect password");
                }
            }
            else{
                responseJsonObject.addProperty("message", "user " + email + " doesn't exist");
            }

            // Close all structures
            rs.close();
            statement.close();
            dbCon.close();

        } catch (Exception ex) {
            responseJsonObject.addProperty("message", "Sql error");
            // Output Error Massage to html
            //out.println(String.format("<html><head><title>MovieDBExample: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", ex.getMessage()));
            return;
        }
        response.getWriter().write(responseJsonObject.toString());
    }
}
