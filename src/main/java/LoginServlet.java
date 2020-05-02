package main.java;

import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;

@WebServlet(name = "LoginServlet", urlPatterns = "/cs122b/login")
public class LoginServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    private static final long serialVersionUID = 1L;

    public String getServletInfo() {
        return "Servlet connects to MySQL database and displays result of a SELECT";
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonObject responseJsonObject = new JsonObject();

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

//         Verify reCAPTCHA
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "recaptcha verification error");
            response.getWriter().write(responseJsonObject.toString());
            return;
        }


        try {

            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in index.html
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            // Generate a SQL query
            String query = "SELECT password, id from customers where email = ?";

            PreparedStatement statement = dbCon.prepareStatement(query);

            statement.setString(1, email);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            // Iterate through each row of rs and create a table row <tr>
            //out.println("<tr><td>ID</td><td>Name</td></tr>");
            if (rs.next()) {
                if(password.equals(rs.getString("password"))){
                    HttpSession session = request.getSession(true);
                    session.setAttribute("user", new User(email));
                    session.setAttribute("login", true);// used to check login status
                    session.setAttribute("cid", rs.getString("id"));

                    // set up movie list status
                    session.setAttribute("ML_status", "genreId=1");
                    session.setAttribute("ML_page", 1);
                    session.setAttribute("ML_moviesPerPage", 10);
                    session.setAttribute("ML_sort", "tara");

                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");

                }
                else{
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "incorrect password");
                }
            }
            else{ // resultSet is empty
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "user " + email + " doesn't exist");
            }

            // Close all structures
            rs.close();
            statement.close();
            dbCon.close();

        } catch (Exception ex) {
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "Sql error");
            response.getWriter().write(responseJsonObject.toString());
            // Output Error Massage to html
            //out.println(String.format("<html><head><title>MovieDBExample: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", ex.getMessage()));
            return;
        }
        response.getWriter().write(responseJsonObject.toString());
    }
}
